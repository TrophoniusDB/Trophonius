package com.trophonius.utils;

import com.trophonius.sql.DML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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


    // print HTML-table with no header and footer
    public static void printHTMLTable(String[][] data) {
        String[] fields = new String[0];
        printHTMLTable(fields, data, false , false);
    } // end printHTMLTable


    // print HTML-table with optional header and footer
    public static void printHTMLTable(String[] fields, String[][] data, boolean header , boolean footer) {
        System.out.println("<table>");
        // Print Table head if header=true and field array has records
        if (header && fields.length>0) {
            System.out.println("<thead>");
            System.out.print("<tr>");
            Arrays.stream(fields).forEach(h -> {
                System.out.print("<th>" + h + "</th>");
            });
            System.out.println("</tr>");
            System.out.println("</thead>");
        }
        // Print table body
        System.out.println("<tbody>");
        Arrays.stream(data).forEach(r -> {
            System.out.print("<tr>");
            Arrays.stream(r).forEach(k -> System.out.print("<td>" + k + "</td>"));
            System.out.println("</tr>");
        });
        System.out.println("</tbody>");

        // Print Table foot if footer=true
        if (footer) {
            System.out.println("<tfoot>");
            System.out.print("<tr>");
            Arrays.stream(fields).forEach(h -> {
                System.out.print("<th>" + h + "</th>");
            });
            System.out.println("</tr>");
            System.out.println("</tfoot>");
        }

        System.out.println("</table>");

    } // end printHTMLTable




} // END CLASS


