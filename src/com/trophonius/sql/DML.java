package com.trophonius.sql;

import com.trophonius.dbo.*;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DML {

    public String prompt = "/";
    public Database currentDB;
    private String sql = "";
    private Table currentTable;
    private Field tableField;
    private Row row = new Row();

    public DML(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }

    private void parseSql(Database currentDB, String sql) {

        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        // DML SQL METHODS

        // SQL INSERT <INTO> <TABLENAME>
        if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("insert into")  ) {

            String tableName;

            // Determine tablename
            if(sql.toLowerCase().startsWith("insert into")) {
                tableName = words[2];
            } else {
                tableName = words[1];
            }

            // Check if table exists
            if (!java.nio.file.Files.isRegularFile(Paths.get("data/"+currentDB.getDbName()+"/"+tableName+".tbl"))) {
                // Table file not found. Return to sender
                System.out.println("Table not found.");
                return;
            } else {

                // Table exists - open it, construct row and append row to table.
                // System.out.println("Table exists");

                // Find field names from SQL
                String[] fieldNames = sql.substring(sql.indexOf("(")+1,sql.indexOf(")")).split("[, ]");
                // Arrays.stream(fieldNames).forEach(System.out::println);

                // Find field values from SQL
                String valueString = sql.substring(sql.indexOf("values"),sql.length());
                String[] fieldValues = valueString.substring(valueString.indexOf("(")+1,valueString.indexOf(")")).split("[, ]");
                //Arrays.stream(fieldValues).forEach(System.out::println);

                HashMap<String, String> valueMap = new HashMap<>();

                for(int i = 0; i < fieldNames.length;i++ ){
                    valueMap.put(fieldNames[i], fieldValues[i]);
                }


                currentDB.getTables().forEach((k, v) -> {
                    if (v.getTableName().equals(tableName)) {
                        v.printTableStructure();
                         currentTable = v;
                    }
                });


                // Save Row
                // put values a row and store row.

                currentTable.getTableStructure().forEach((k,v) ->{
                    String storedFieldName = v.getName();
                    String storedDataTypeName = v.getDataType().getName();
                    String storedClassName = v.getDataType().getClassName();

                    row.add("timestamp",LocalDateTime.now());

                    if (storedClassName.equals("String")) {
                        String value = new String(valueMap.get(storedFieldName));
                        row.add(storedFieldName, value);
                    }

                    if (storedClassName.equals("Integer")) {
                        Integer value = Integer.getInteger(valueMap.get(storedFieldName));
                        row.add(storedFieldName, value);
                    }

                    if (storedClassName.equals("LocalDate")) {
                        LocalDate value = LocalDate.parse(valueMap.get(storedFieldName));
                        row.add(storedFieldName, value);
                    }

                    if (storedClassName.equals("LocalDateTime")) {
                        LocalDateTime value = LocalDateTime.parse(valueMap.get(storedFieldName));
                        row.add(storedFieldName, value);
                    }

                    if (storedClassName.equals("Double")) {
                        Double value = Double.valueOf(valueMap.get(storedFieldName));
                        row.add(storedFieldName, value);
                    }

                });

                System.out.println(row.toString());



            }

        } // END INSERT INTO



            }


}
