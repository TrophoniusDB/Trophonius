package com.trophonius.Engines;

// OBJECT ENGINE
public class ObjectEngine extends Engine {

        public ObjectEngine() {

        super.setName("objectEngine");
        super.setBinaryFormat(true);
        super.setTableSuffix("tbl");
        super.setComment("Not suitable for large tables, but good for storing serializable java objects");


        }

}
