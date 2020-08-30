package com.trophonius.sql;

import com.trophonius.Engines.Engine;
import com.trophonius.dbo.Field;
import com.trophonius.dbo.Row;
import com.trophonius.dbo.TableStats;
import com.trophonius.utils.AppendableObjectInputStream;
import com.trophonius.utils.HelperMethods;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.trophonius.Main.currentDB;

/**
 * Parses an SQL SELECT statement and dispatches it to the table's storage engine for retrieval
 */
public class Select {
    private int limit, offset;


    public Select() {
    }

    public Select (String tableName, String sql) {

        // Split sql into separate words
        String[] words = sql.split("[= ]");

        // make a list to hold field names from sql
        List<String> fieldList = new ArrayList<>();

        List<Row> rows = new LinkedList<>();

        // Set initial limit value to MAX_VAlUE and change it if LIMIT is in SQL
        limit = Integer.MAX_VALUE;

        // Check for LIMIT and set value
        for (int i = 0; i < words.length; i++) {
            if (words[i].toLowerCase().equals("limit")) {
                limit = Integer.valueOf(words[i + 1]);
            }
        }

        // set initial offset value to 0 and change it if OFFSET is in SQL
        offset = 0;

        // Check for offset and set value
        for (int i = 0; i < words.length; i++) {
            if (words[i].toLowerCase().equals("offset")) {
                offset = Integer.valueOf(words[i + 1]);
            }
        }


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

        }  catch (Exception e) {
            System.out.println("Table \""+ tableName  +"\" doesn't exist in this database");
        return;
        }

        // Find table engine
        try {
            Engine engine = currentDB.getTables().get(tableName).getEngine();
            rows = engine.fetchRows(tableName, fieldList, limit, offset);
            HelperMethods.printAsciiTable(fieldList,rows);

        } catch (Exception e) {
            System.out.println("ERROR: Table Storage Engine not found");
        }



    } // END SELECT

} // END CLASS
