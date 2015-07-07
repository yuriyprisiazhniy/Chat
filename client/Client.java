package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.*;
import javax.swing.*;

class Writer extends Thread {

    private String name;
    private Socket s;
    private String text;

    public Writer(String st, Socket s, String text) {
        name = st;
        this.s = s;
        this.text = text;
    }

    public void run() {

        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String str = name + ": " + text;
            out.writeUTF(str);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Reader extends Thread {

    private Socket s;
    private String text;
    public Reader(Socket s) {
        this.s = s;
    }
    public String getText(){
        return text;
    }
    public void run() {
        try {
            DataInputStream in = new DataInputStream(s.getInputStream());
             text =  in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Client extends KeyAdapter{
    private JFrame frame;
    private String name;
    private JTextArea textField;
    private JTextField edit;
    private Socket s;
    private JScrollPane scrollBar;
    public Client (String st){
        name = st;
        s = null;
        frame = new JFrame("Client");
        textField = new JTextArea();
        edit = new JTextField();
        scrollBar = new JScrollPane(textField);
    }
    public void launchFrame(){
        frame.setLayout(new BorderLayout());
        //frame.setResizable(false);
        textField.setPreferredSize(new Dimension(300,200));
        textField.setEditable(false);
        
        
        scrollBar.setVisible(true);
        scrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        frame.add(scrollBar,BorderLayout.EAST);
        edit.setPreferredSize(new Dimension(300,20));
        edit.addKeyListener(this);
        
        frame.add(textField,BorderLayout.WEST);
        frame.add(edit,BorderLayout.SOUTH);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.setVisible(true);
        while (true) 
            try {
                 s = new Socket("localhost", 5432);
                //Writer w = new Writer(name, s);
                //w.start();
                Reader r = new Reader(s);
                r.start();
                r.join();
                s.close();
                textField.setText(textField.getText()+"\n"+r.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    @Override
    public void keyPressed(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            Writer w = new Writer(name, s,edit.getText());
            w.start();
            edit.setText("");
        }
    }
    public static void main(String[] args) {
        Client c = new Client("Yura");
        c.launchFrame();
        /*while (true) 
            try {
                Socket s = new Socket("localhost", 5432);
                Writer w = new Writer(args[0], s);
                w.start();
                Reader r = new Reader(s);
                r.start();
                r.join();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        
    }
}
