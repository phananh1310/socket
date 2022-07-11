package com.hust.soict.socket.controller;

import com.google.gson.Gson;
import com.hust.soict.socket.ClientHandler;
import com.hust.soict.socket.SocketApplication;
import com.hust.soict.socket.domain.GameType;
import com.hust.soict.socket.domain.Room;
import com.hust.soict.socket.mapping.MyResponse;
import com.hust.soict.socket.mapping.ServerResponse;
import com.hust.soict.socket.response_object.RoomResonse;
import com.hust.soict.socket.server_out_dto.JOINdto;
import com.hust.soict.socket.server_out_dto.OLVRdto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class RoomController {
    private Gson gson;

    public MyResponse createRoom(ClientHandler clientHandler, String id, String password, GameType type) throws Exception {
        System.out.println("Create Room handler...");
        if (SocketApplication.rooms.containsKey(id))
            throw new Exception("Room exist");
        Room room = new Room(id,clientHandler.user.getInGame(),null,true,password, type);
        System.out.println("Room created, adding this room to active room list");
        SocketApplication.rooms.put(id, room);
        System.out.println("Added");
        return new MyResponse(200,gson.toJson(room));
    }

    public MyResponse joinRoom(ClientHandler clientHandler, String roomid, String password) throws Exception {
        System.out.println("Join Room handler...");
        if (!SocketApplication.rooms.containsKey(roomid))
            throw new Exception("Room not exist");
        Room room = SocketApplication.rooms.get(roomid);
        System.out.println("Room found, checking password...");

        if (room.getPassword()!=null&&!room.getPassword().equals(password))
            throw new Exception("Wrong password");

        if (room.getBlack()!=null && room.getWhite()!=null)
            throw new Exception("Room is full");
        PrintWriter opponentDos;
        if (room.getWhite()==null) {
            room.setWhite(clientHandler.user.getInGame());
            opponentDos = SocketApplication.activeClient.get(room.getBlack());
        }
        else {
            room.setBlack(clientHandler.user.getInGame());
            opponentDos = SocketApplication.activeClient.get(room.getWhite());
        }
        System.out.println("Added");

        System.out.println("Sending new join room information to other component");
        JOINdto joiNdto = new JOINdto(clientHandler.user.getInGame());
        ServerResponse serverResponse = new ServerResponse("JOIN", gson.toJson(joiNdto));
        opponentDos.println(serverResponse.toString());

        return new MyResponse(200,gson.toJson(room));
    }

    public MyResponse getRooms() {
        System.out.println("Get Rooms handler...");
        List<RoomResonse> rooms = new ArrayList<>();
        SocketApplication.rooms.forEach((key, value) -> {
            Boolean isPassword = false;
            int total = 1;
            if (value.isFull())
                total = 2;
            if (value.getPassword()!=null)
                isPassword = true;
            rooms.add(new RoomResonse(key,isPassword, total));
        });
        return new MyResponse(200,gson.toJson(rooms));
    }

    public MyResponse leaveRoom(ClientHandler clientHandler, String roomid) throws Exception {
        System.out.println("Leave Room handler...");

        if (!SocketApplication.rooms.containsKey(roomid))
            throw new Exception("Room not exist");
        Room room = SocketApplication.rooms.get(roomid);
        if (room.isFull()){
            String opponent = null;
            System.out.println("Room has 2 players");
            // if owner = white and the one who leaves is white
            if (room.getWhite().equals(clientHandler.user.getInGame())){
                opponent = room.getBlack();
                room.setWhite(null);
                if (room.getIsWhiteOwner())
                    room.switchOwner();
            }
                // if owner = black and the one who leaves is black
            if (room.getBlack().equals(clientHandler.user.getInGame())){
                opponent = room.getWhite();
                room.setBlack(null);
                if (!room.getIsWhiteOwner())
                    room.switchOwner();
            }
            OLVRdto olvRdto = new OLVRdto(room.getIsWhiteOwner());
            ServerResponse serverResponse = new ServerResponse("OLVR", gson.toJson(olvRdto));
            PrintWriter opponentDos = SocketApplication.activeClient.get(opponent);
            opponentDos.println(serverResponse);

        } else {
          System.out.println("Room has only 1 player, remove the room from active rooms");
            SocketApplication.rooms.remove(roomid);
        }

        return new MyResponse(200,"OK");
    }
}
