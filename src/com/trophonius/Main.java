/**
 * Simple and efficient Database
 */
package com.trophonius;

import com.trophonius.dbo.Database;
import com.trophonius.sql.SqlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Class that controls input and dispatches commands to other classes
 * @author Terje Berg-Hansen
 * @version 0.1.10
 */
public class Main {
    public static boolean timing = true;
    public static long startTime = 0;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(20);
    private static int clientNumber = 0;


    public static void main(String[] args) throws IOException, ClassNotFoundException {

        final int PORT;
        if(args.length == 1) {
            PORT = Integer.parseInt(args[0]);
        } else {
            PORT = 9876;
        }


        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Trophonius started");

        ServerSocket listener = new ServerSocket(PORT);
        while (true) {
            logger.info("[SERVER] Server running on port " + PORT + ", waiting for connections... ");
            System.out.println("[SERVER] Server running on port " + PORT + ", waiting for connections... ");
            Socket client = listener.accept();
            logger.info("Client "+ ++clientNumber  +" connected at "+ LocalDateTime.now());
            ClientHandler clientThread = new ClientHandler(client, clients, clientNumber);
            clients.add(clientThread);
            pool.execute(clientThread);
        }



    }
}