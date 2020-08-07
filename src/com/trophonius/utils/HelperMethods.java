package com.trophonius.utils;

import com.trophonius.dbo.Field;
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

        // Put field name and length in a HashMap
        Map<String,Integer> fieldsWithLength = new LinkedHashMap<>();
        for(String fieldName: fieldList) {
            fieldsWithLength.put(fieldName,fieldName.length());
        }

        // Go through rows tro see if length of value is greater than length of field name
        // and put the longest in HashMap fieldsWithLength

        // iterate each row
        rows.forEach(a-> {
        // iterate each object in row
            final int[] maxLength = {0};
            a.getRow().forEach((k,v) -> {
                int valueLength;
                try {
                    valueLength = v.toString().length();
                } catch (Exception e) {
                    valueLength = 4;
                }
            if (valueLength > maxLength[0]) maxLength[0] = valueLength;
            String fieldName = k.toString();

            int fieldLength = 0;
            try {
               fieldLength = fieldsWithLength.get(fieldName).intValue();
            } catch (Exception e) {
                fieldLength = 0;
            }

            // If length of value is greater than length of field name put it in fieldsWithLength HashMap
            if(maxLength[0] > fieldLength) {
                if(fieldsWithLength.containsKey(fieldName)) {
                    fieldsWithLength.put(fieldName, valueLength);
                }
            }
        });
    });
        // The HashMap fieldsWithLength now contains the name of the field and its max length

        // Calculate length of table (pad each field with 3 extra spaces)
        int tableLength = fieldsWithLength.values().stream().mapToInt(a->a+3).sum();

        // Print table header line
        System.out.println("+" + "-".repeat(tableLength)  + "+");
        System.out.print("| ");

        // Iterate through fieldList and print field names in the table header
        fieldsWithLength.forEach((k,v) -> {
            // print field names
            System.out.printf(" %-" + v + "s |", k);
        });

        System.out.println();
        System.out.println("+" + "-".repeat(tableLength)  + "+");

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

                // Check if row contains value for field
                try {
                    var theValue = printList.get(b);

                    if (theValue.getClass().getName().contains("Integer")) {
                        System.out.printf(" %-" + fieldsWithLength.get(d) + "d |", theValue);
                    } else {
                        System.out.printf(" %-" + fieldsWithLength.get(d) + "s |", theValue);
                    }
                } catch (Exception e) {
                 // No value for field, so print null
                    String theValue =  "null";
                    System.out.printf(" %-" + fieldsWithLength.get(d)+ "s |", theValue);
                }



            });

            System.out.println();
        });

        // Print table footer
        System.out.println("+" + "-".repeat(tableLength) + "+");
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


