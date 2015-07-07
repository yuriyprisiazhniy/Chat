package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.io.*;

class ClientService extends Thread {
    
    private Socket s;
    public static int count;
    public ClientService(Socket s) {
        this.s = s;
    }

    private synchronized void sendMessage(String str) throws IOException{
        for(Socket s:Server.activeSockets){
            DataOutputStream bw = new DataOutputStream(s.getOutputStream());
            bw.writeUTF(str);
            bw.flush();
        }
    }

    @Override
    public void run() {
        try {
            DataInputStream br = new DataInputStream(s.getInputStream());
            String str = br.readUTF();
            sendMessage(str);
            count++;
            System.out.println("Сервер отримав :"+count+" " + str);
            if(Server.activeSockets.contains(s))
                Server.activeSockets.remove(s);
            //s.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Server {

    private ServerSocket s;
    private ClientService service;
    public static  List<Socket> activeSockets;
    public Server(int port) {
        activeSockets = new ArrayList<>();
        service = null;
        s = null;
        try {
            s = new ServerSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public void start() {
        ClientService.count = 0;
        while (true) {
            try {
                Socket s1 = s.accept();
                activeSockets.add(s1);
                service = new ClientService(s1);
                service.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        Server serv = new Server(5432);
        serv.start();
    }
}
