package com.trophonius.Engines;

import com.trophonius.dbo.TableStats;
import com.trophonius.utils.AppendableObjectOutputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// OBJECT ENGINE
public class ObjectEngine extends Engine {

        public ObjectEngine() {

                super.setName("objectEngine");
                super.setBinaryFormat(true);
                super.setTableSuffix("tbl");
                super.setComment("Not suitable for large tables, but good for storing serializable java objects");

        }

        public void createTableOnDisc(String dbName, String tableName) {

                try {
                        // create table file and write table stats
                        FileOutputStream dbFileOut = new FileOutputStream("data/" + dbName + "/" + tableName + this.getTableSuffix(), true);
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

        } // END  createTableOnDisc



}
