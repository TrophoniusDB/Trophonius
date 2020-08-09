package com.trophonius.Engines;

// CSV ENGINE
public class CsvEngine extends Engine {

    public CsvEngine() {

        super.setName("csvEngine");
        super.setBinaryFormat(false);
        super.setTableSuffix("csv");
        super.setComment("Fast, but not feature rich");



    }
}
