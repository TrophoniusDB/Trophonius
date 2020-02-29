package com.trophonius.dbo;

import java.util.HashMap;
import java.util.TreeMap;

public class Row<E> {

    private TreeMap<String, E> row = new TreeMap<String, E>();

    public Row() {

    }

    public TreeMap<String, E> getRow() {
        return row;
    }

    public void setRow(TreeMap<String, E> row) {
        this.row = row;
    }

    public void add (String fieldName, E value ) {
        this.row.put(fieldName, value);
    }

    @Override
    public String toString() {
        return "Row=" + row ;
    }
}
