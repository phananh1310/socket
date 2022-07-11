package com.hust.soict.socket;

import java.io.*;
import java.util.*;
import java.net.*;

import com.hust.soict.socket.domain.ActiveMatch;
import com.hust.soict.socket.domain.Matches;
import com.hust.soict.socket.domain.Room;
import com.hust.soict.socket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocketApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class, args);
    }
    @Autowired
    UserService userService;
    public static HashMap<String,PrintWriter> activeClient = new HashMap<>();
    public static HashMap<String, Room> rooms = new HashMap<>();
    public static HashMap<Long, ActiveMatch> activeMatches = new HashMap<>();


    @Override
    public void run(String... args) throws Exception {
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        // running infinite loop for getting
        // client request
        while (true)
        {
            // Accept the incoming request
            s = ss.accept();

            System.out.println("New client request received : " + s);

            BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter dos = new PrintWriter(s.getOutputStream(),true);

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s, dis, dos);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);



            // start the thread.
            t.start();



        }
    }
}
