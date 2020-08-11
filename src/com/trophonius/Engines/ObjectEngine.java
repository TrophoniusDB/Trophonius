package com.trophonius.Engines;

import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// OBJECT ENGINE
public class ObjectEngine implements Engine {

        public ObjectEngine() {

                setName("objectEngine");
                setBinaryFormat(true);
                setTableSuffix("tbl");
                setComment("Not suitable for large tables, but good for storing serializable java objects");

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
                return null;
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public String getTableSuffix() {
                return null;
        }

        @Override
        public void setTableSuffix(String tableSuffix) {

        }

        @Override
        public boolean isBinaryFormat() {
                return false;
        }

        @Override
        public void setBinaryFormat(boolean binaryFormat) {

        }

        @Override
        public String getComment() {
                return null;
        }

        @Override
        public void setComment(String comment) {

        }
} // END CLASS
