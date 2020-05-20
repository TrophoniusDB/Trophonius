package com.trophonius.utils;

import com.trophonius.sql.DML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class HelperMethods {

    // Rounding doubles with decimals, like round() in sql
    public static double round(double n, int dec) {
        return Math.round(n * Math.pow(10, dec)) / (double) Math.pow(10, dec);
    }

    // Rounding floats with decimals, like round() in sql
    public static float round(float n, int dec) {
        return Math.round(n * Math.pow(10, dec)) / (float) Math.pow(10, dec);
    }

    public static void recursiveDelete(File file) {
        //to end the recursive loop
        if (!file.exists())
            return;

        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                recursiveDelete(f);
            }
        }
        //call delete to delete files and empty directory
        file.delete();
        System.out.println("Deleted file/folder: " + file.getAbsolutePath());
    }

    public static ArrayList<String> findDatabases() {

        ArrayList<String> dbNames = new ArrayList<>();

        if (java.nio.file.Files.isDirectory(Paths.get("data"))) {
            File rootDir = new File("data");

            for (File dir : rootDir.listFiles()) {
                if (dir.isDirectory()) {

                    dbNames.add(dir.getName());

                }
            }
        }

        return dbNames;
    }

    public static void populate(int rows, String filename) {
        try (FileWriter outFile = new FileWriter(filename)){
            Random rand = new Random();
            for (Integer i = 1; i <= rows ; i++) {
                outFile.append("insert into test (id,tall) values ("+i+","+rand.nextInt()+")\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



} // END CLASS


