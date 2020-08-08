package com.trophonius.sql;

import com.trophonius.dbo.Field;
import com.trophonius.dbo.Row;
import com.trophonius.dbo.TableStats;
import com.trophonius.utils.AppendableObjectInputStream;
import com.trophonius.utils.HelperMethods;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.trophonius.Main.currentDB;

public class Select {

    public Select() {
    }

    public Select (String tableName, String sql) {
        // Split sql into separate words
        String[] words = sql.split("[= ]");

        // make a list to hold field names from sql
        List<String> fieldList = new ArrayList<>();

        // Open Table file to read in rows
        try {
            FileInputStream dbFileIn = new FileInputStream("data/" + currentDB.getDbName() + "/" + tableName + ".tbl");
            AppendableObjectInputStream is = new AppendableObjectInputStream(new BufferedInputStream(dbFileIn));

            try {
                // Read fieldNames and Fields from tableStructure
                LinkedHashMap<String, Field> tableStructure = currentDB.getTables().get(tableName).getTableStructure();

                // If "select *" then Fetch all fields by putting the whole keySet into the variable fieldList
                if (words[1].equals("*")) {
                    fieldList.addAll(tableStructure.keySet());
                } else {
                    // Or else determine fields to be fetched and put them into the variable fieldList
                    // First substring the fields frm sql
                    String fields = sql.toLowerCase()
                            .substring(sql.toLowerCase().indexOf("select") + 6, sql.toLowerCase().indexOf("from"));
                    // Then split the fields string and add Fields into fieldList
                    fieldList.addAll(Arrays.stream(fields.split(",")).map(a -> a.trim()).collect(Collectors.toList()));

                    // Check if all fields from SQL exists in tableStructure of currentTable
                    Boolean found = tableStructure.keySet().containsAll(fieldList);

                    if (found == false) {
                        // Not all field names were found in table structure. Print message and go back to prompt
                        System.out.println("ERROR: One or more field names are not present in table");
                        System.out.println("Fields in table: " + tableStructure.keySet());
                        System.out.println("Fields in SQL statement: " + fieldList);
                        return;
                    }
                }
                System.out.println("Fields to be fetched: " + fieldList);

                // get primary key field
                // String primaryKeyField = tableStructure
                        /*        .values().stream()
                                .filter(a -> a.isPrimaryKey())
                                .map(a -> a.getName())
                                .collect(Collectors.joining());
                        */

                // Read row objects from table file and put them into an ArrayList
                // Breaks graciously when no more records to read

                // Read in TableStats
                TableStats stats = (TableStats) is.readObject();

                // Read in Rows and put them in an ArrayList of Rows
                List<Row> rows = new ArrayList<>();
                while (true) {
                    try {
                        Row theRow = (Row) is.readObject();
                        rows.add(theRow);
                    } catch (EOFException e) {
                        break;
                    }
                } // end while

                is.close();
                dbFileIn.close();

                // If no rows are found, print message and return to prompt
                if (rows.size() == 0) {
                    System.out.println("Table contains no rows...");
                    return;
                }

                // Print result as Ascii Table through helper method printAsciiTable
                HelperMethods.printAsciiTable (fieldList, rows);

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Error! Could not open table file...");
            e.printStackTrace();
        }

    } // END SELECT

} // END CLASS
