package com.trophonius.sql;

import com.trophonius.dbo.Database;
import com.trophonius.dbo.Field;
import com.trophonius.dbo.Row;
import com.trophonius.dbo.Table;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.trophonius.Main.startTime;
import static com.trophonius.Main.timing;

/**
 * Class for SQL DML Statements - select, insert, update delete
 * @param <E> generic type for handling fields
 */
public class DML<E> {

    public String prompt = "/";
    public Database currentDB;
    private String sql = "";
    private Table currentTable;
    private Field tableField;
    private Row<Serializable> row = new Row<>();

    /***
     * Constructor that calls method to parse SQL
     * @param prompt prompt that will be changed when a database is selected
     * @param currentDB DBO selected by user
     * @param sql supplied SQL Statement
     */
    public DML(String prompt, Database currentDB, String sql) {
        this.sql = sql;
        this.prompt = prompt;
        this.currentDB = currentDB;
        parseSql(currentDB, sql);
    }

    private void parseSql(Database currentDB, String sql) {

        // Prepare SQL - Create Array of words and remove =
        String[] words = sql.split("[= ]");
        String charset = "", collation = "";

        // DML SQL METHODS

        // SQL INSERT <INTO> <TABLENAME>
        if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("insert into")) {
            // Method to insert new row into existing table
          Insert insert = new Insert(sql);

        } // END INSERT INTO

        //  \i or import - sql from <filename>
        if (sql.toLowerCase().startsWith("\\i") || sql.toLowerCase().startsWith("import")) {
            String fileName = words[1];
            importSql(fileName);

        } // end import from file


        // SQL SELECT <fields...> FROM <TABLENAME>
        if (sql.toLowerCase().startsWith("select")) {

            // Determine tablename
            String tableName = null;
            for (int i = 0; i < words.length; i++) {
                if (words[i].toLowerCase().equals("from")) {
                    tableName = words[i + 1];
                }
            }


            ;
            try {
                Table thisTable = currentDB.getTables().get(tableName);
                // Get table suffix to find the table
                String tableSuffix = currentDB.getTables().get(tableName).getEngine().getTableSuffix();

                // Check if table not found
                if (!java.nio.file.Files.isRegularFile(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + "."+tableSuffix))) {
                    // Table file not found. Return to sender
                    System.out.println("Table not found.");
                    return;
                } else {
                    // Table exists - open it, fetch row and return fields.
                    // If timing is on - set start time
                    if(timing) startTime = System.currentTimeMillis();
                    Select select = new Select(tableName, sql);
                    // Add timing info
                    if(timing) System.out.println(" in "+(System.currentTimeMillis()-startTime)+ " millis");
                }



            } catch (Exception e) {
                System.out.println("Table "+tableName+" is not in this database");
                return;
            }


        } // end Select

    } // end parseSQL

    /**
     * Method for importing a SQL file with only insert statements
     * @param filename Path to file with SQL statements
     */
    public void importSql(String filename) {

        // TODO check for other statements than insert
        try {
            Files.lines(Path.of(filename)).forEach(line -> {
                // insert into tableName
             Insert in = new Insert(line.toString());
            });

        } catch (IOException e) {
            System.out.println("SQL File not found");
            e.printStackTrace();
        }
        System.out.println("All rows from file inserted");
    } // end importSQL

    }  // end class
