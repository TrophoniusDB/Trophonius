package com.trophonius.dbo;

public class ObjectEngine  {

    public ObjectEngine() {

        DBEngine engine = new DBEngine("engine");
        engine.setBinaryFormat(true);
        engine.setTableSuffix("tbl");
        engine.setComment("Not suitable for large tables, but good for storing serializable java objects");

    }






}
