package com.trophonius.sql;

import com.trophonius.dbo.*;
import com.trophonius.utils.HelperMethods;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DML<E> {

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
        if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("insert into")) {

            insertIntoTable(sql);

        } // END INSERT INTO

        //  \i or import sql from <filename>
        if (sql.toLowerCase().startsWith("\\i") || sql.toLowerCase().startsWith("import")) {
            String fileName = words[1];
            importSql(fileName);

        } // end import from file


        // SQL SELECT <fields...> FROM <TABLENAME>
        if (sql.toLowerCase().startsWith("select")) {

            // Determine tablename
            String tableName = null;
            for (int i = 0; i < words.length; i++) {
                if (words[i].toLowerCase().equals("from")) {
                    tableName = words[i + 1];
                }
            }

            // Check if table not found
            if (!java.nio.file.Files.isRegularFile(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + ".tbl"))) {
                // Table file not found. Return to sender
                System.out.println("Table not found.");
                return;
            } else {
                // Table exists - open it, fetch row and return fields.
                selectFromTable(tableName, sql);
            }

        } // end Select

    } // end parseSQL


    public void selectFromTable (String tableName, String sql) {
        // Split sql into separate words
        String[] words = sql.split("[= ]");

        // make a list to hold field names from sql
        List<String> fieldList = new ArrayList<>();

        try {
            FileInputStream dbFileIn = new FileInputStream("data/" + currentDB.getDbName() + "/" + tableName + ".tbl");
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(dbFileIn));

            try {
                // Read fieldNames and Fields from tableStructure stored in Table file as the first object
                LinkedHashMap<String, Field> tableStructure = (LinkedHashMap<String, Field>) is.readObject();

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

                if (rows.size() == 0) {
                    System.out.println("Table contains no rows...");
                    return;
                }

             // Print result
                HelperMethods.printAsciiTable (fieldList, rows);


            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Error! Could not open table file...");
            e.printStackTrace();
        }


    } // end selectFromTable


    public void insertIntoTable(String sql) {

        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        String tableName;

        // Determine tablename
        if (sql.toLowerCase().startsWith("insert into")) {
            tableName = words[2];
        } else {
            tableName = words[1];
        }

        // Check if table not found
        if (!java.nio.file.Files.isRegularFile(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + ".tbl"))) {
            // Table file not found. Return to sender
            System.out.println("Table not found.");
            return;
        } else {
            // Table exists - open it, construct row and append row to table.

            // Find field names from SQL
            String[] fieldNames = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")")).split("[,]");

            // Find field values from SQL
            String valueString = sql.substring(sql.indexOf("values"), sql.length());
            String[] fieldValues = valueString.substring(valueString.indexOf("(") + 1, valueString.indexOf(")")).split("[,]");
            //Arrays.stream(fieldValues).forEach(System.out::println);

            // Put field names and field values from SQL in a HashMap for later use
            HashMap<String, String> valueMap = new HashMap<>();
            for (int i = 0; i < fieldNames.length; i++) {
                fieldNames[i] = fieldNames[i].strip();
                fieldValues[i] = fieldValues[i].strip();
                valueMap.put(fieldNames[i], fieldValues[i]);
            }

            // set the value of currentTable
            currentDB.getTables().forEach((k, v) -> {
                if (v.getTableName().equals(tableName)) {
                    //  v.printTableStructure();
                    currentTable = v;
                }
            });

            // Check if all fields from SQL exists in tableStructure of currentTable
            Boolean found = currentTable.getFieldNames().containsAll(Arrays.asList(fieldNames));

            if (found == false) {
                // Not all field names were found in table structure. Print message and go back to prompt
                System.out.println("ERROR: One or more field names are not present in table");
                System.out.println("Fields in table: " + currentTable.getFieldNames());
                System.out.println("Fields in SQL statement: " + Arrays.asList(fieldNames));

            } else {
                // All fieldNames where found in table structure, so continue to save row to file.
                // variable to check if Field Value is valid
                AtomicReference<Boolean> validFields = new AtomicReference<>(true);

                // Save Row
                currentTable.getTableStructure().forEach((k, v) -> {
                    String storedFieldName = v.getName();
                    String storedDataTypeName = v.getDataType().getName();
                    String storedClassName = v.getDataType().getClassName();

                    // Iterate through each sql-supplied fieldname/fieldvalue pair and add to row + check if name equals name in tablestructure
                    valueMap.forEach((sk, sv) -> {

                        if (sk.equals(storedFieldName)) {

                            if (storedClassName.equals("String")) {
                                String value = new String(valueMap.get(storedFieldName));
                                row.addToRow(storedFieldName, value);
                            }

                            if (storedClassName.equals("Integer") || storedClassName.equals("int")) {
                                try {
                                    Integer value = Integer.parseInt(valueMap.get(storedFieldName));
                                    row.addToRow(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid Integer format:\n" + e.getMessage());
                                    validFields.set(false);
                                    return;
                                }

                            }

                            if (storedClassName.equals("LocalDate")) {
                                String dateString = valueMap.get(storedFieldName).replaceAll("'", "");
                                dateString = dateString.replaceAll("\"", "");
                                try {
                                    LocalDate value = LocalDate.parse(dateString);
                                    row.addToRow(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid date format:\n" + e.getMessage());
                                    validFields.set(false);
                                    return;
                                }
                            }

                            if (storedClassName.equals("LocalDateTime")) {
                                String dateTimeString = valueMap.get(storedFieldName).replaceAll("'", "");
                                dateTimeString = dateTimeString.replaceAll("\"", "");

                                try {
                                    LocalDateTime value = LocalDateTime.parse(dateTimeString);
                                    row.addToRow(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid datetime format:\n" + e.getMessage());
                                    validFields.set(false);
                                    return;
                                }

                            }

                            if (storedClassName.equals("Double")) {
                                try {
                                    Double value = Double.parseDouble(valueMap.get(storedFieldName));
                                    row.addToRow(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid Double format:\n" + e.getMessage());
                                    validFields.set(false);
                                    return;
                                }
                            }

                            if (storedClassName.equals("Float")) {
                                try {
                                    Float value = Float.parseFloat(valueMap.get(storedFieldName));
                                    row.addToRow(storedFieldName, value);
                                } catch (Exception e) {
                                    System.out.println("Not a valid Float format:\n" + e.getMessage());
                                    validFields.set(false);
                                    return;
                                }
                            }

                        } // end if
                    });


                    // No data from SQL, so add empty field:
                    if (!row.getRow().containsKey(storedFieldName)) {
                        row.addToRow(storedFieldName, null);
                    }

                });

                   /*
                     System.out.println("Primary Key field "+primaryKeyField);
                     System.out.println("Primary key value "+primaryKeyValue);
                    */

                // If all field values are valid, write row to table file
                if(validFields.get()) {
                    row.writeRowToDisk(row, currentDB.getDbName(), currentTable.getTableName());
                } else {
                    System.out.println("Row not saved in table");
                }
            }

        } // end if allFieldsExists

    } // end insertIntoTable


    public void importSql(String filename) {

        try {
            Files.lines(Path.of(filename)).forEach(line -> {
                // insert into tableName
                insertIntoTable(line.toString());
            });

        } catch (IOException e) {
            System.out.println("SQL File not found");
            e.printStackTrace();
        }
        System.out.println("All rows from file inserted");
    } // end importSQL

    }  // end class
