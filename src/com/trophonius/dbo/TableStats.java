package com.trophonius.dbo;

import java.io.Serializable;

public class TableStats implements Serializable {
    private static final long serialVersionUID = 2559797051071867786L;
    private long numberOfRows;
    private long previousFileSize;

    public TableStats() {
    }

    public TableStats(long numberOfRows, long previousFileSize) {
        this.numberOfRows = numberOfRows;
        this.previousFileSize = previousFileSize;
    }

    public long getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(long numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public long getPreviousFileSize() {
        return previousFileSize;
    }

    public void setPreviousFileSize(long previousFileSize) {
        this.previousFileSize = previousFileSize;
    }

    @Override
    public String toString() {
        return "TableStats{" +
                "numberOfRows=" + numberOfRows +
                ", previousFileSize=" + previousFileSize +
                '}';
    }
}
