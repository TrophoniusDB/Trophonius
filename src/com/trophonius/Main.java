package com.trophonius;

import com.trophonius.dbo.Database;
import com.trophonius.sql.SqlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static boolean timing = true;
    public static long startTime = 0;
    public static String prompt = "";
    public static Database currentDB = new Database();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Logger logger = LoggerFactory.getLogger(Main.class);

        System.out.println("Welcome to Trophonius - a Simple and Fast Database");
        System.out.println("Version 0.0.1");
        System.out.println("Type 'help' or '\\h' for help and 'quit' or '\\q' to exit");
        logger.info("Trophonius started");


        // Main Menu
        while (true) {
            // print prompt
            System.out.print(prompt + "> ");
            Scanner input = new Scanner(System.in);
            // Wait for input from user
            String inputText = input.nextLine();
            if (inputText.equals("quit") || inputText.equals("\\q") ) {
                // Say goodbye and exit
                System.out.println("Goodbye...");
                System.exit(0);
            } else {
                // Dispatch to SQL-parser
                SqlParser sql = new SqlParser(prompt, currentDB, inputText);
                prompt = sql.prompt;
                currentDB = sql.currentDB;
            }

        }

    }

}