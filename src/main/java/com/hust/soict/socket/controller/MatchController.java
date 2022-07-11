package com.hust.soict.socket.controller;

import com.google.gson.Gson;
import com.hust.soict.socket.ClientHandler;
import com.hust.soict.socket.SocketApplication;
import com.hust.soict.socket.domain.ActiveMatch;
import com.hust.soict.socket.domain.Matches;
import com.hust.soict.socket.domain.Room;
import com.hust.soict.socket.mapping.MyResponse;
import com.hust.soict.socket.mapping.ServerResponse;
import com.hust.soict.socket.server_out_dto.OPMVdto;
import com.hust.soict.socket.service.MatchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;

@Controller
@AllArgsConstructor
public class MatchController {
    private final MatchService matchService;
    private Gson gson;
    public MyResponse play(ClientHandler clientHandler, String roomid) throws Exception {
        System.out.println("Play handler...");
        if (!SocketApplication.rooms.containsKey(roomid))
            throw new Exception("Room not exist");
        Room room = SocketApplication.rooms.get(roomid);
        if (room.getBlack()==null||room.getWhite()==null)
            throw new Exception("Not enough player");

        if (room.getIsWhiteOwner()){
            if (!clientHandler.user.getInGame().equals(room.getWhite()))
                throw new Exception("Only owner can start the game");
        }
        else
            if (!clientHandler.user.getInGame().equals(room.getBlack()))
                throw new Exception("Only owner can start the game");
        System.out.println("Room found with 2 player, creating match...");
        Matches matches = new Matches(null,room.getWhite(), room.getBlack(),"",null, room.getType());
        // save match and get id
        Matches savedMatches = matchService.saveMatch(matches);
        ActiveMatch activeMatch = new ActiveMatch(savedMatches, true);
        SocketApplication.activeMatches.put(savedMatches.getId(),activeMatch);
        System.out.println("Match info: " + gson.toJson(activeMatch));
        // sending information to component
        System.out.println("Send match info to other component");
        String componentIngame = room.getBlack();
        if (clientHandler.user.getInGame().equals(room.getBlack()))
            componentIngame = room.getWhite();
        PrintWriter componentDos = SocketApplication.activeClient.get(componentIngame);
        // TODO is raw value or object ?
        ServerResponse serverResponse = new ServerResponse("STRT", gson.toJson(activeMatch));
        componentDos.println(serverResponse.toString());
        return new MyResponse(200,gson.toJson(activeMatch));
    }

    public MyResponse move(ClientHandler clientHandler, Long matchid, String move) throws Exception {
        if (!SocketApplication.activeMatches.containsKey(matchid))
            throw new Exception("Match not found");
        ActiveMatch activeMatch = SocketApplication.activeMatches.get(matchid);
        // if current turn is white and the one who moves is black player
        if (activeMatch.getIsWhiteTurn()&&clientHandler.user.getInGame().equals(activeMatch.getMatch().getBlack()))
            throw new Exception("Not your turn");
        // if current turn is black and the one who moves is white player
        if (!activeMatch.getIsWhiteTurn()&&clientHandler.user.getInGame().equals(activeMatch.getMatch().getWhite()))
            throw new Exception("Not your turn");

        String opponent = activeMatch.getMatch().getWhite();
        if (activeMatch.getIsWhiteTurn())
            opponent = activeMatch.getMatch().getBlack();

        // TODO check valid move
        activeMatch.getMatch().addMove(move);
        // check move if win
        if (move.substring(move.length()-2).equals("++")){
            System.out.println("Checkmate");
            activeMatch.getMatch().setWinner(opponent);
            System.out.println("Save match history");
            matchService.saveMatch(activeMatch.getMatch());
        // remove the match from active matches
            SocketApplication.activeMatches.remove(matchid);
        }
        activeMatch.switchTurn();

        System.out.println("Send match info to opponent");
        PrintWriter opponentDos = SocketApplication.activeClient.get(opponent);
        OPMVdto opmVdto = new OPMVdto(move, activeMatch.getMatch().getState());
        ServerResponse serverResponse = new ServerResponse("OPMV", gson.toJson(opmVdto));
        opponentDos.println(serverResponse);
        return new MyResponse(200,gson.toJson(opmVdto));
    }
}
