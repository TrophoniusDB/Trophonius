package com.trophonius.dbo;


public class Engines {

        public Engines () {

        // OBJECT ENGINE
        DBEngine objectEngine = new DBEngine("objectEngine");
        objectEngine.setBinaryFormat(true);
        objectEngine.setTableSuffix("tbl");
        objectEngine.setComment("Not suitable for large tables, but good for storing serializable java objects");

        // CSV ENGINE
        DBEngine csvEngine = new DBEngine("csvEngine");
        objectEngine.setBinaryFormat(false);
        objectEngine.setTableSuffix("csv");
        objectEngine.setComment("Fast, but not feature rich");

        // BYTE ENGINE
        DBEngine byteEngine = new DBEngine("byteEngine");
        objectEngine.setBinaryFormat(true);
        objectEngine.setTableSuffix("dat");
        objectEngine.setComment("Fast and binary, but not feature rich");

        }

}
