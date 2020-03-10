package com.trophonius.sql;

import com.trophonius.dbo.*;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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

            // Check if table not found
            if (!java.nio.file.Files.isRegularFile(Paths.get("data/"+currentDB.getDbName()+"/"+tableName+".tbl"))) {
                // Table file not found. Return to sender
                System.out.println("Table not found.");
                return;
            } else {
                // Table exists - open it, construct row and append row to table.
                // System.out.println("Table exists");

                // Find field names from SQL
                String[] fieldNames = sql.substring(sql.indexOf("(")+1,sql.indexOf(")")).split("[,]");
                // Arrays.stream(fieldNames).forEach(System.out::println);

                // Find field values from SQL
                String valueString = sql.substring(sql.indexOf("values"),sql.length());
                String[] fieldValues = valueString.substring(valueString.indexOf("(")+1,valueString.indexOf(")")).split("[,]");
                //Arrays.stream(fieldValues).forEach(System.out::println);

                HashMap<String, String> valueMap = new HashMap<>();

                for(int i = 0; i < fieldNames.length;i++ ){
                    fieldNames[i] = fieldNames[i].strip();
                    fieldValues[i] = fieldValues[i].strip();

                    valueMap.put(fieldNames[i], fieldValues[i]);
                }

                // set currentTable
                currentDB.getTables().forEach((k, v) -> {
                    if (v.getTableName().equals(tableName)) {
                      //  v.printTableStructure();
                         currentTable = v;
                    }
                });


                // Check if all fields from SQL exists in tableStructure of currentTable

                Boolean found = currentTable.getFieldNames().containsAll(Arrays.asList(fieldNames));

                

                if(found == false) {
                // Not all field names were found in table structure. Print message and go back to prompt
                    System.out.println("ERROR: one or more field names is not present in table");
                    System.out.println("Fields in table: "+currentTable.getFieldNames());
                    System.out.println("Fields in SQL statement: "+Arrays.asList(fieldNames));

                } else {
                // All fieldNames where found in table structure, so continue to save row to file.

                // Save Row
                // put values in a row object and store in file.

                currentTable.getTableStructure().forEach((k,v) ->{
                    String storedFieldName = v.getName();
                    String storedDataTypeName = v.getDataType().getName();
                    String storedClassName = v.getDataType().getClassName();

                        // Iterate through each sql-supplied fieldname/fieldvalue pair and add to row + check if name equals name in tablestructure
                        valueMap.forEach((sk,sv) -> {

                        if(sk.equals(storedFieldName)) {

                            if (storedClassName.equals("String")) {
                                String value = new String(valueMap.get(storedFieldName));
                                row.add(storedFieldName, value);
                            }

                            if (storedClassName.equals("Integer") || storedClassName.equals("int") ) {
                                Integer value = Integer.parseInt(valueMap.get(storedFieldName));
                                row.add(storedFieldName, value);
                            }

                            if (storedClassName.equals("LocalDate")) {
                                String dateString = valueMap.get(storedFieldName).replaceAll("'","");
                                dateString = dateString.replaceAll("\"","");
                                try {
                                    LocalDate value = LocalDate.parse(dateString);
                                    row.add(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid date format:\n"+e.getMessage());
                                }

                            }

                            if (storedClassName.equals("LocalDateTime")) {
                                String dateTimeString = valueMap.get(storedFieldName).replaceAll("'","");
                                dateTimeString = dateTimeString.replaceAll("\"","");

                                try {
                                    LocalDateTime value = LocalDateTime.parse(dateTimeString);
                                    row.add(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid date format:\n"+e.getMessage());
                                }

                            }

                            if (storedClassName.equals("Double")) {
                                Double value = Double.valueOf(valueMap.get(storedFieldName));
                               //TODO
                                if(primaryKey) {
                                    row.addToPrimaryKey(storedFieldName, value);
                                } else {
                                    row.addToRow(storedFieldName, value);
                                }
                            }

                         } // end if
                    });


                        // No data from SQL, so add empty field:
                    if(!row.getRow().containsKey(storedFieldName))  {
                        row.addToRow(storedFieldName,null);
                    }

                });

                // Write row to console
                System.out.println(row.toString());
                // Write row to table file
                row.writeRowToDisk(currentDB.getDbName(), currentTable.getTableName());
            }

            } // end if allFieldsExists

        } // END INSERT INTO



            } // end parseSQL


}  // end class