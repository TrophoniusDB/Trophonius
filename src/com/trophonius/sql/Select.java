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
        String[] words = sql.split("[ ]");

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
        } // end parse words

        // Check for WHERE start and end
        if (sql.toLowerCase().contains("where")) {
            whereStart = sql.toLowerCase().indexOf("where")+6;
            if (sql.toLowerCase().contains("group")) {
                whereEnd = sql.toLowerCase().indexOf("group");
            } else if (sql.toLowerCase().contains("order")) {
                whereEnd = sql.toLowerCase().indexOf("order");
            } else if (sql.toLowerCase().contains("limit")) {
                whereEnd = sql.toLowerCase().indexOf("limit");
            } else {
                whereEnd = sql.length();
            }

        }

        String whereTerms = sql.toLowerCase().substring(whereStart,whereEnd);
        List<FilterTerm> filterTerms = new LinkedList<>();
        String relTerms ="";
        if(whereTerms.contains("=")) {

            whereTerms = whereTerms.replaceAll(" ","");
            String fieldName = whereTerms.substring(0,whereTerms.indexOf("="));
            String operand = "=";
            String value = whereTerms.substring(whereTerms.indexOf("=")+1);
            FilterTerm filter = new FilterTerm(fieldName,operand,value);
            filterTerms.add(filter);
        }

        // Check for functions
        // now()

        try {
            // Read fieldNames and Fields from tableStructure
            LinkedHashMap<String, Field> tableStructure = Main.currentDB.getTables().get(tableName).getTableStructure();

            // If "select *" then Fetch all fields by putting the whole keySet into the variable fieldList
            if (words[1].equals("*")) {
                fieldList.addAll(tableStructure.keySet());
            } else if (words[1].equals("count(*)")) {
                // get and print row count
                Engine engine = Main.currentDB.getTables().get(tableName).getEngine();
                long rowCount = engine.getRowCount(Main.currentDB.getDbName(),  tableName);
                HelperMethods.printAsciiTable(rowCount);
                return;
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
            rows = engine.fetchRows(tableName, fieldList, filterTerms, limit, offset);
            HelperMethods.printAsciiTable(fieldList, rows);
        } catch (Exception e) {
            System.out.println("ERROR: Table Storage Engine not found");
        }



    } // END SELECT

} // END CLASS
