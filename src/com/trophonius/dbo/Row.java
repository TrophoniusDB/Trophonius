package com.trophonius.dbo;

import java.util.HashMap;

public class Row<E> {

    private HashMap<String, E> row = new HashMap<String, E>();

    public Row() {

    }

    public HashMap<String, E> getRow() {
        return row;
    }

    public void setRow(HashMap<String, E> row) {
        this.row = row;
    }

    public void add (String fieldName, E value ) {
        this.row.put(fieldName, value);
    }

    @Override
    public String toString() {
        return "Row{" + row + '}';
    }
}
