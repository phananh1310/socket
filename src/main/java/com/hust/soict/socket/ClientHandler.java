package com.hust.soict.socket;

import com.hust.soict.socket.domain.User;
import com.hust.soict.socket.mapping.RequestMapper;
import com.hust.soict.socket.service.UserService;

import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable
{
    final BufferedReader dis;
    final PrintWriter dos;
    Socket s;

    public User user = null;

    private final RequestMapper requestMapper = SpringApplicationContext.getBean(RequestMapper.class);
    // constructor

    public ClientHandler(Socket s, BufferedReader dis, PrintWriter dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
    }

    @Override
    public void run() {
        String received = "";
        while (true)
        {
            try
            {
                // receive the string
                try {
                    received = dis.readLine();
                    if (received == null)
                        throw new Exception("Received null");
                } catch (Exception e) {
                    System.out.println("User "+s.getLocalSocketAddress()+" is disconnected");
                    // remove from active client if login
                    if (this.user!=null) {
                        System.out.println("Remove this client from active client hash map");
                        SocketApplication.activeClient.remove(user.getInGame());
                        this.user = null;
                    }
                    this.s.close();
                    break;
                }

                System.out.println("Receive from client "+s.getLocalSocketAddress()+": " +received);
                String response = requestMapper.handle(received, this.dos, this);
                // send response to client
                this.dos.println(response);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
        try
        {
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}