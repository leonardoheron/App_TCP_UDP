package com.example.sae302_heron;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerTask extends AsyncTask<String, Void, Void> {

    private ServerSocket serverSocket;
    private StringBuilder sb;
    private String username;
    private TextView message;
    private ArrayList<SocketTask> SkT;
    private ArrayList<Socket> Sk;


    //Constructeur de la classe
    @SuppressLint("WrongThread")
    ServerTask(int port, StringBuilder sbe, String nameUser, TextView TVM){
        try {
            serverSocket = new ServerSocket(port);
            sb = sbe;
            username = nameUser;
            message = TVM;
            SkT = new ArrayList<>();
            Sk = new ArrayList<>();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Thread secondaire qui attend qu'un client se connecte, puis les ajoutes dans une liste pour qu'il puissent communiquer
    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(String... message) {
        while(true) {
            try {
                System.out.println("En attente de connexion d'un client");
                Socket socket = serverSocket.accept();
                Sk.add(socket);
                System.out.println("Client connecté");

                SocketTask T = new SocketTask(socket, username, sb, this.message,this);
                SkT.add(T);
                T.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Permet d'envoyer le message depuis un client présent ta la table vers tout les autres client.
    public void publishMessage(String message){
        for(SocketTask St : SkT){
            St.SendMessage(message);
        }
    }

}
