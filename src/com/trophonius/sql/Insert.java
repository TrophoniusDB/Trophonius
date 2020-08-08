package com.trophonius.sql;

import com.trophonius.dbo.Row;
import com.trophonius.dbo.Table;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.trophonius.Main.currentDB;

public class Insert {

    public Insert() {
    }

    public Insert (String sql) {
        Row row = new Row();
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
            Table currentTable = currentDB.getTables().get(tableName);

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
                                    row.addToRow(storedFieldName, String.valueOf(value));
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
                                    row.addToRow(storedFieldName, String.valueOf(value));
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
                                    row.addToRow(storedFieldName, String.valueOf(value));
                                } catch (Exception e) {
                                    System.out.println("Not a valid datetime format:\n" + e.getMessage());
                                    validFields.set(false);
                                    return;
                                }

                            }

                            if (storedClassName.equals("Double")) {
                                try {
                                    Double value = Double.parseDouble(valueMap.get(storedFieldName));
                                    row.addToRow(storedFieldName, String.valueOf(value));
                                } catch (Exception e) {
                                    System.out.println("Not a valid Double format:\n" + e.getMessage());
                                    validFields.set(false);
                                    return;
                                }
                            }

                            if (storedClassName.equals("Float")) {
                                try {
                                    Float value = Float.parseFloat(valueMap.get(storedFieldName));
                                    row.addToRow(storedFieldName, String.valueOf(value));
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

    } // end insert

} // END CLASS
