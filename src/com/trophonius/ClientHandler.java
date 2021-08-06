package com.trophonius;

import com.trophonius.dbo.Database;
import com.trophonius.sql.SqlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private int clientNUmber;
    public static String prompt = "";
    public static Database currentDB = new Database();
    private String inputText;

    Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients, int clientNumber) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        this.clientNUmber = clientNumber;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        //Server response
        try {
            inputText = in.readLine();
            SqlParser sql = new SqlParser(prompt, currentDB, inputText);
            prompt = sql.prompt;
            currentDB = sql.currentDB;

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
