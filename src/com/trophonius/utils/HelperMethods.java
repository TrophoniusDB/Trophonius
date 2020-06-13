package com.trophonius.utils;

import com.trophonius.dbo.Row;
import com.trophonius.sql.DML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
    public static <E> void printHTMLTable(E[][] data) {
        String[] fields = new String[0];
        printHTMLTable(fields, data, false , false);
    } // end printHTMLTable


    // Print ASCII Table from a list of fields names and a list of rows
    public static <E> void printAsciiTable(List<String> fieldList, List<Row> rows) {
        // Calculate field widths for length of ascii-box
        int maxlength = rows.stream()
                .map(a -> a.getRow().values())
                .mapToInt(b -> b.stream().mapToInt(c -> c.toString().length()).max().getAsInt())
                .max().getAsInt();


        // Calculate minimum field width from field names
        int minLength = fieldList.stream().mapToInt(a -> a.length()).max().getAsInt();

        // fix, if maxlength is less than minlength
        if (maxlength < minLength) {
            maxlength = minLength;
        }

        System.out.println("+" + "-".repeat((maxlength + 3) * fieldList.size()) + "+");
        System.out.print("| ");
        // copy maxlength to a final variabel for use in forEach()
        int finalMaxlength = maxlength;

        // Iterate through fieldList and print field names in the table header
        fieldList.forEach(k -> {
            // print field names
            System.out.printf(" %-" + finalMaxlength + "s |", k);
        });
        System.out.println();
        System.out.println("+" + "-".repeat((maxlength + 3) * fieldList.size()) + "+");

        // print rows, by first putting them into a LinkedHashMap: printList
        Map<String, E> printList = new LinkedHashMap<>();

        // Put each row in printList
        rows.forEach(a -> {
            System.out.print("| ");
            a.getRow().forEach((b, c) -> {
                // Check if field is in fieldList, i.e. should be returned
                if (fieldList.stream().map(d -> d.trim()).collect(Collectors.toList()).contains(b)) {
                    // put keys and values in a linkedHashMap - printList
                    printList.put(b.toString().trim(), (E) c);
                }
            });

            // print fields in the same order as in the sql
            fieldList.forEach(d -> {
                String b = d.trim();
                var theValue = printList.get(b);

                if (theValue.getClass().getName().contains("Integer")) {
                    System.out.printf(" %-" + finalMaxlength + "d |", theValue);
                } else {
                    System.out.printf(" %-" + finalMaxlength + "s |", theValue);
                }


            });

            System.out.println();
        });

        // Print table footer
        System.out.println("+" + "-".repeat((finalMaxlength + 3) * fieldList.size()) + "+");
        // print number of rows returned
        System.out.println(rows.size() > 1 ? rows.size() + " rows returned" : rows.size() + " row returned");

    } // end printAsciiTable






    // print HTML-table with optional header and footer
    public static <E> void printHTMLTable(String[] fields, E[][] data, boolean header , boolean footer) {
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


