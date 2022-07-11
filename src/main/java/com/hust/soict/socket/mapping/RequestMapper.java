package com.hust.soict.socket.mapping;

import com.google.gson.Gson;
import com.hust.soict.socket.ClientHandler;
import com.hust.soict.socket.SpringApplicationContext;
import com.hust.soict.socket.controller.MatchController;
import com.hust.soict.socket.controller.RoomController;
import com.hust.soict.socket.controller.UserController;
import com.hust.soict.socket.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Slf4j
@Component
public class RequestMapper {
    private final UserController userController = SpringApplicationContext.getBean(UserController.class);
    private final RoomController roomController = SpringApplicationContext.getBean(RoomController.class);
    private final MatchController matchController = SpringApplicationContext.getBean(MatchController.class);
    @Autowired
    Gson g;

    public String handle(String request, PrintWriter dos, ClientHandler clientHandler){
        try {
            String header = request.substring(0,4);
            String payload = request.substring(4);
            MyRequest myRequest = new MyRequest(header,payload);
            MyResponse myResponse;
            switch (myRequest.getType()) {
                case "LOGN" :
                    // when user login, he/she will be added to active client list at SocketApplication, the current thread PrintWiter will be send to that map
                    LOGNdto logNdto = g.fromJson(myRequest.getArguments(), LOGNdto.class);
                    myResponse = userController.login(logNdto.getUsername(), logNdto.getPassword(), dos, clientHandler);
                    break;
                case "LOUT" :
                    // when user logout, he/she will be removed to active client list at SocketApplication
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    LOUTdto louTdto = g.fromJson(myRequest.getArguments(), LOUTdto.class);
                    if (!louTdto.getIngame().equals(clientHandler.user.getInGame()))
                        throw new Exception("Wrong user session");
                    myResponse = userController.logout(clientHandler.user.getInGame());
                    break;

                case "REGT" :
                    REGTdto regTdto = g.fromJson(myRequest.getArguments(), REGTdto.class);
                    myResponse = userController.register(regTdto.getUsername(), regTdto.getPassword(), regTdto.getIngame());
                    break;

                case "ADFR" :
                    ADFRdto adfRdto = g.fromJson(myRequest.getArguments(), ADFRdto.class);
                    if (adfRdto.getIngame()==null)
                        throw new Exception("ADFR wrong format");
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    myResponse = userController.addFriend(adfRdto.getIngame(), clientHandler.user.getInGame());
                    break;

                case "ACFR" :
                    ACFRdto acfRdto = g.fromJson(myRequest.getArguments(), ACFRdto.class);
                    if (acfRdto.getIngame()==null)
                        throw new Exception("ACFR wrong format");
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    if (clientHandler.user.getInGame().equals(acfRdto.getIngame()))
                        throw new Exception("Invalid friend request");
                    myResponse = userController.acceptFriend(acfRdto.getIngame(), clientHandler);
                    break;

                case "FRND" :
                    FRNDdto frnDdto = g.fromJson(myRequest.getArguments(), FRNDdto.class);
                    if (frnDdto.getIngame()==null)
                        throw new Exception("FRND wrong format");
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    if (!frnDdto.getIngame().equals(clientHandler.user.getInGame()))
                        throw new Exception("Wrong user session");
                    myResponse = userController.getFriend(clientHandler);
                    break;

                case "CRRM" :
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    CRRMdto crrMdto = g.fromJson(myRequest.getArguments(), CRRMdto.class);
                    myResponse = roomController.createRoom(clientHandler, crrMdto.getId(), crrMdto.getPassword(), crrMdto.getType());
                    break;
                case "JNRM" :
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    JNRMdto jnrMdto = g.fromJson(myRequest.getArguments(), JNRMdto.class);
                    myResponse = roomController.joinRoom(clientHandler,jnrMdto.getRoomid(),jnrMdto.getPassword());
                    break;
                case "PLAY" :
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    PLAYdto plaYdto = g.fromJson(myRequest.getArguments(), PLAYdto.class);
                    myResponse = matchController.play(clientHandler,plaYdto.getRoomid());
                    break;
                case "MOVE" :
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    MOVEdto movEdto = g.fromJson(myRequest.getArguments(), MOVEdto.class);
                    myResponse = matchController.move(clientHandler,movEdto.getMatchid(), movEdto.getMove());
                    break;
                case "ROOM" :
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    myResponse = roomController.getRooms();
                    break;
                case "LVRM" :
                    if (clientHandler.user == null)
                        throw new Exception("Must login first");
                    LVRMdto lvrMdto = g.fromJson(myRequest.getArguments(), LVRMdto.class);

                    myResponse = roomController.leaveRoom(clientHandler,lvrMdto.getRoomid());
                    break;
                default:
                    return new MyResponse(400,g.toJson(new ErrorMsg("Message type not correct")),myRequest.getType()).toString();
            }
            myResponse.setType(myRequest.getType());
            return myResponse.toString();
        } catch (Exception e) {
            return new MyResponse(400,g.toJson(new ErrorMsg(e.getMessage())), "UNDF").toString();
        }

    }
}
