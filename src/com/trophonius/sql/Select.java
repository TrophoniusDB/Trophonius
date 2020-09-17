package com.trophonius.sql;

import com.trophonius.Engines.Engine;
import com.trophonius.Main;
import com.trophonius.dbo.Field;
import com.trophonius.dbo.Row;
import com.trophonius.utils.HelperMethods;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Parses an SQL SELECT statement and dispatches it to the table's storage engine for retrieval
 */
public class Select {
    private int limit, offset, whereStart=0, whereEnd=0;

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

        // set initial offset value to 0 and change it if OFFSET is in SQL
        offset = 0;

        //Parse words
        for (int i = 0; i < words.length; i++) {
            // Check for LIMIT and set value
            if (words[i].toLowerCase().equals("limit")) {
                limit = Integer.valueOf(words[i + 1]);
            }
            // Check for offset and set value
            if (words[i].toLowerCase().equals("offset")) {
                offset = Integer.valueOf(words[i + 1]);
            }
            // Check for WHERE start
            if (words[i].toLowerCase().equals("where")) {
                whereStart = i+1;
            }
            // Check for WHERE End
            if (words[i].toLowerCase().equals("group")) {
                whereEnd = i;
            }
            if (words[i].toLowerCase().equals("order") && whereEnd==0) {
                whereEnd = i;
            }
            if (words[i].toLowerCase().equals("limit") && whereEnd==0) {
                whereEnd = i;
            }


        }

        // if whereStart is present and no whereEnd is set, set it to the end of the SQL-sentence
        whereEnd = whereStart!=0 && whereEnd == 0 ? words.length : whereEnd;
        System.out.println("whereStart: "+whereStart);
        System.out.println("whereEnd: "+whereEnd);
        // Where-terms
        for(int i = whereStart; i< whereEnd;i++) {
            System.out.println(words[i]);
        }


        // Check for functions
        // now()

        try {
            // Read fieldNames and Fields from tableStructure
            LinkedHashMap<String, Field> tableStructure = Main.currentDB.getTables().get(tableName).getTableStructure();

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
            Engine engine = Main.currentDB.getTables().get(tableName).getEngine();
            // count(*)
            if(words[1].equals("count(*)")) {
                long rowCount = engine.getRowCount(Main.currentDB.getDbName(),  tableName);
                HelperMethods.printAsciiTable(rowCount);
            } else {
                rows = engine.fetchRows(tableName, fieldList, limit, offset);
                HelperMethods.printAsciiTable(fieldList, rows);
            }

        } catch (Exception e) {
            System.out.println("ERROR: Table Storage Engine not found");
        }



    } // END SELECT

} // END CLASS
