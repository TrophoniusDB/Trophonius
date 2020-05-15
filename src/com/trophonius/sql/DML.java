package com.trophonius.sql;

import com.trophonius.dbo.*;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DML {

    public String prompt = "/";
    public Database currentDB;
    private String sql = "";
    private Table currentTable;
    private Field tableField;
    private Row<Serializable> row = new Row<>();

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
                    Boolean isPrimaryKey = v.isPrimaryKey();



                        // Iterate through each sql-supplied fieldname/fieldvalue pair and add to row + check if name equals name in tablestructure
                        valueMap.forEach((sk,sv) -> {

                        if(sk.equals(storedFieldName)) {

                            if (storedClassName.equals("String")) {
                                String value = new String(valueMap.get(storedFieldName));
                                row.addToRow(storedFieldName, value);
                                if(isPrimaryKey) {
                                    String pk = value;
                                }
                            }

                            if (storedClassName.equals("Integer") || storedClassName.equals("int") ) {
                                Integer value = Integer.parseInt(valueMap.get(storedFieldName));
                                row.addToRow(storedFieldName, value);
                            }

                            if (storedClassName.equals("LocalDate")) {
                                String dateString = valueMap.get(storedFieldName).replaceAll("'","");
                                dateString = dateString.replaceAll("\"","");
                                try {
                                    LocalDate value = LocalDate.parse(dateString);
                                    row.addToRow(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid date format:\n"+e.getMessage());
                                }

                            }

                            if (storedClassName.equals("LocalDateTime")) {
                                String dateTimeString = valueMap.get(storedFieldName).replaceAll("'","");
                                dateTimeString = dateTimeString.replaceAll("\"","");

                                try {
                                    LocalDateTime value = LocalDateTime.parse(dateTimeString);
                                    row.addToRow(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid date format:\n"+e.getMessage());
                                }

                            }

                            if (storedClassName.equals("Double")) {
                                Double value = Double.valueOf(valueMap.get(storedFieldName));
                                    row.addToRow(storedFieldName, value);
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
                row.writeRowsToDisk(pk, row, currentDB.getDbName(), currentTable.getTableName());
            }

            } // end if allFieldsExists

        } // END INSERT INTO


        // SQL SELECT <fields...> FROM <TABLENAME>
        if (sql.toLowerCase().startsWith("select") ) {

            String tableName = null;

            // Determine tablename
            for (int i = 0; i< words.length; i++) {
                if (words[i].toLowerCase().equals("from")) {
                    tableName = words[i+1];
                }
            }


            // Check if table not found
            if (!java.nio.file.Files.isRegularFile(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + ".tbl"))) {
                // Table file not found. Return to sender
                System.out.println("Table not found.");
                return;
            } else {
                // Table exists - open it, fetch row and return fields.
                 System.out.println("Table exists");
                final String[] primaryKeyDataType = new String[1];

                try {
                    FileInputStream dbFile = new FileInputStream("data/" + currentDB.getDbName() + "/" + tableName + ".tbl");
                    ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(dbFile));


                    try {
                        // Read fields from table

                        LinkedHashMap<String,Field> tableStructure = (LinkedHashMap<String, Field>) is.readObject();
                        tableStructure.forEach((k,v) -> {

                            System.out.println(k+ " "+v.getDataType().getClassName());
                            if(v.isPrimaryKey()) {
                                primaryKeyDataType[0] = v.getDataType().getClassName();
                                System.out.println("Primary key is of dataType: "+primaryKeyDataType[0]);
                            }

                        });
                        // list rows
                        TreeMap<Integer, Row> rows = (TreeMap<Integer, Row>) is.readObject();
                        System.out.println("Number of records in table: "+rows.size());
                        rows.forEach((k,v) -> {
                            System.out.println(k+" "+v);
                        });

                        is.close();



                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                } catch (IOException e) {
                    System.out.println("Error! Could not open table file...");
                    e.printStackTrace();
                }


                // Find field names from SQL
                //String[] fieldNames = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")")).split("[,]");
                // Arrays.stream(fieldNames).forEach(System.out::println);

            }
        } // end Select

            } // end parseSQL


}  // end class
