package com.trophonius.Engines;

// BYTE ENGINE
public class ByteEngine implements Engine {

    public ByteEngine() {

        setName("byteEngine");
        setBinaryFormat(true);
        setTableSuffix("dat");
        setComment("Fast and binary, but not feature rich");

    }

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
}
