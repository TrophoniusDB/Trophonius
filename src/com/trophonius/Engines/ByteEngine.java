package com.trophonius.Engines;

// BYTE ENGINE
public class ByteEngine extends Engine {

    public ByteEngine() {

        super.setName("byteEngine");
        super.setBinaryFormat(true);
        super.setTableSuffix("dat");
        super.setComment("Fast and binary, but not feature rich");

    }
}
