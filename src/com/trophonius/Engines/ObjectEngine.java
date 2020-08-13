package com.trophonius.Engines;

import com.trophonius.dbo.Field;
import com.trophonius.dbo.Row;
import com.trophonius.utils.AppendableObjectInputStream;
import com.trophonius.utils.AppendableObjectOutputStream;
import com.trophonius.utils.HelperMethods;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.trophonius.Main.currentDB;

// OBJECT ENGINE
public class ObjectEngine implements Engine   {

        private static final long serialVersionUID = -2645684320509863174L;
        private String name;
        private String tableSuffix;
        private boolean binaryFormat = false;
        private String comment;


        public ObjectEngine() {

                this.setName("objectEngine");
                this.setBinaryFormat(true);
                this.setTableSuffix("tbl");
                this.setComment("Not suitable for large tables, but good for storing serializable java objects");

        } // END CONSTRUCTOR

        public void createTableFile(String dbName, String tableName) {

        // check that table file not  exists in data directory
        if (!Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + "."+ this.getTableSuffix()))) {

                try {
                        // create table file and write table stats
                        FileOutputStream dbFileOut = new FileOutputStream("data/" + dbName + "/" + tableName + "."+ this.getTableSuffix(), true);
                        AppendableObjectOutputStream oStr = new AppendableObjectOutputStream(new BufferedOutputStream(dbFileOut));
                        oStr.flush();
                        oStr.close();
                        dbFileOut.flush();
                        dbFileOut.close();
                } catch (
                        IOException e) {
                        System.out.println("Table could not we written to disk: ");
                        e.printStackTrace();
                }

        } else {
                // Table file exists
                System.out.println("Table \""+tableName+"\" already exists");
        }

        } // END  createTableFile


        @Override
        public void writeRowToDisk(String dbName, String tableName, Row row) {

                try {
                        // check that table file exists in data directory
                        if (java.nio.file.Files.isRegularFile(Paths.get("data/" + dbName + "/" + tableName + ".tbl"))) {

                                // Open table file for writing, and append map of primary key + row to the file

                                FileOutputStream dbFileOut = new FileOutputStream("data/" + dbName + "/" + tableName + ".tbl",true);
                                AppendableObjectOutputStream oStr = new AppendableObjectOutputStream(new BufferedOutputStream(dbFileOut));
                                oStr.writeObject(row);
                                oStr.flush();
                                oStr.close();
                                dbFileOut.flush();
                                dbFileOut.close();
                                System.out.println("Success: 1 row written to table: "+tableName);
                        } else  {
                                // Table file does not exists
                                System.out.println("Table file does not exist");
                                return;
                        }

                } catch (IOException e) {
                        System.out.println("Row could not we written to file for table: "+tableName);
                        e.printStackTrace();

                }

        }

        @Override
        public void fetchRows(String tableName, String sql) {
                // Split sql into separate words
                String[] words = sql.split("[= ]");

                // make a list to hold field names from sql
                List<String> fieldList = new ArrayList<>();


                // Open Table file to read in rows
                try {
                        FileInputStream dbFileIn = new FileInputStream("data/" + currentDB.getDbName() + "/" + tableName + "."+tableSuffix);
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
                                //TableStats stats = (TableStats) is.readObject();

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

        }

        @Override
        public String getName() {
                return name;
        }

        @Override
        public void setName(String name) {
                this.name = name;
        }

        @Override
        public String getTableSuffix() {
                return tableSuffix;
        }

        @Override
        public void setTableSuffix(String tableSuffix) {
                this.tableSuffix = tableSuffix;
        }

        @Override
        public boolean isBinaryFormat() {
                return binaryFormat;
        }

        @Override
        public void setBinaryFormat(boolean binaryFormat) {
                this.binaryFormat = binaryFormat;
        }

        @Override
        public String getComment() {
                return comment;
        }

        @Override
        public void setComment(String comment) {
                this.comment = comment;
        }


} // END CLASS
