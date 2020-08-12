package com.trophonius.Engines;

import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// OBJECT ENGINE
public class ObjectEngine implements Engine {

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
