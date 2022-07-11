package com.hust.soict.socket.controller;

import com.google.gson.Gson;
import com.hust.soict.socket.ClientHandler;
import com.hust.soict.socket.SocketApplication;
import com.hust.soict.socket.domain.User;
import com.hust.soict.socket.mapping.ErrorMsg;
import com.hust.soict.socket.mapping.MyResponse;
import com.hust.soict.socket.response_object.FriendResponse;
import com.hust.soict.socket.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private Gson gson;
    public MyResponse login(String username, String password, PrintWriter dos, ClientHandler clientHandler){
        // TODO save client handler instead of dos to active client list
        System.out.println("Login handler...");
        try {
            User user = userService.login(username,password);
            // add this client to active clients list
            System.out.println("Adding this client to active client list when login...");
            SocketApplication.activeClient.put(user.getInGame(), dos);
            System.out.println("Added");
            // set USER of this client handler not null
            clientHandler.user = user;
            return new MyResponse(200,gson.toJson(user));
        } catch (Exception e){
            return new MyResponse(406,gson.toJson(new ErrorMsg(e.getMessage())));
        }
    }

    public MyResponse register(String username, String password, String inGame){
        System.out.println("Register handler...");
        try {
            userService.register(username,password, inGame);
            return new MyResponse(200,"OK");
        } catch (Exception e){
            return new MyResponse(406,gson.toJson(new ErrorMsg(e.getMessage())));
        }
    }

    public MyResponse logout(String ingame) {
        System.out.println("Logout handler...");

        try {
            if (!SocketApplication.activeClient.containsKey(ingame))
                throw new Exception("Not in active client list");
            System.out.println("Remove this client from active client list...");
            SocketApplication.activeClient.remove(ingame);
            System.out.println("Removed");
            return new MyResponse(200,"OK");
        } catch (Exception e){
            return new MyResponse(422,gson.toJson(new ErrorMsg(e.getMessage())));
        }
    }

    public MyResponse addFriend(String ingame, String from) {
        System.out.println("Add friend handler...");
        try {
            if (!SocketApplication.activeClient.containsKey(ingame))
                throw new Exception("User is not online");
            PrintWriter friendDos = SocketApplication.activeClient.get(ingame);
            // TODO real object instead of string
            friendDos.println("FRRQ {\"ingame\":\""+from+"\"}");
            System.out.println("Request sent");
            return new MyResponse(200,"OK");
        } catch (Exception e){
            return new MyResponse(422,gson.toJson(new ErrorMsg(e.getMessage())));
        }
    }

    public MyResponse acceptFriend(String ingame, ClientHandler clientHandler) {
        System.out.println("Accept friend handler...");
        try {
            String currentIngame = clientHandler.user.getInGame();
            userService.acceptFriend(currentIngame,ingame);
            System.out.println("Friend addded at UserFriend table...");
            return new MyResponse(200,"OK");
        } catch (Exception e){
            return new MyResponse(406,gson.toJson(new ErrorMsg(e.getMessage())));
        }
    }

    public MyResponse getFriend(ClientHandler clientHandler) {
        System.out.println("Get friend handler...");
        try {
            String currentIngame = clientHandler.user.getInGame();
            List<FriendResponse> list = userService.getFriend(currentIngame);
            return new MyResponse(200,gson.toJson(list));
        } catch (Exception e){
            return new MyResponse(406,gson.toJson(new ErrorMsg(e.getMessage())));
        }
    }
}
