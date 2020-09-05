package com.trophonius.Engines;

import com.trophonius.dbo.Row;
import com.trophonius.utils.AppendableObjectInputStream;
import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.trophonius.Main.currentDB;

/**
 * OBJECT ENGINE - a storage engine that stores serialized Java Objects.
 */
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

        /**
         * Creates the physical table file
         * @param dbName Name of the Database to which the table belongs
         * @param tableName Name of the Table for which to create a file
         */
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
        public void writeRowToDisk(String dbName, String tableName, Row row, boolean verbose) {

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
                                if(verbose) {
                                        System.out.println("Success: 1 row written to table: " + tableName);
                                }
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
        public List<Row> fetchRows(String tableName, List<String> fieldList, int limit, int offset) {

                List<Row> rows = new ArrayList<>();

                // Open Table file to find fields and check with SQL that all fields are present in table
                try {
                        FileInputStream dbFileIn = new FileInputStream("data/" + currentDB.getDbName() + "/" + tableName + "." + tableSuffix);
                        AppendableObjectInputStream is = new AppendableObjectInputStream(new BufferedInputStream(dbFileIn));


                        long rowCount = 0;
                        while (rowCount < limit) {
                                try {
                                        Row theRow = (Row) is.readObject();
                                        rows.add(theRow);
                                        rowCount++;
                                } catch (EOFException e) {
                                        break;
                                } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                }
                        } // end while

                        is.close();
                        dbFileIn.close();


                } catch (IOException e) {
                        System.out.println("Error! Could not open table file...");
                        e.printStackTrace();
                }
                return rows;
        } // end

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