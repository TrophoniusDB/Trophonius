package com.trophonius.dbo;

import com.trophonius.Engines.Engine;
import com.trophonius.utils.HelperMethods;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Database implements Serializable {
    // to allow different new versions of this class to access the database
    private static final long serialVersionUID = 3353993616667352381L;
    private String dbName, charSet, collation;
    private Map users;
    private LocalDateTime created;
    private HashMap<String, Table> tables = new HashMap<>();
    private Engine engine;

    public Database() {
    }

    public Database(String dbName) {
        this.dbName = dbName;
    }

    public Database(String dbName, String charSet) {
        this.dbName = dbName;
        this.charSet = charSet;
    }

    public Database(String dbName, String charSet, String collation) {
        this.dbName = dbName;
        this.charSet = charSet;
        this.collation = collation;
    }

    public Database(String dbName, String charSet, String collation, Map users) {
        this.dbName = dbName;
        this.charSet = charSet;
        this.collation = collation;
        this.users = users;
    }

    public static void deleteDatabase(String dbName) {

        try {

            HelperMethods.recursiveDelete(new File("data/" + dbName));
            java.nio.file.Files.deleteIfExists(Paths.get("data/" + dbName+"/"));

            java.nio.file.Files.deleteIfExists(Paths.get(dbName));
            System.out.println("Database deleted.");

        } catch (IOException e) {
            System.out.println("Database " + dbName + " could not be deleted.");
            e.printStackTrace();
        }

    }

    public static void deleteTable(Database currentDB, String tableName) {

        String tableSuffix = currentDB.getTables().get(tableName).getEngine().getTableSuffix();

        try {
            // Delete table file
            java.nio.file.Files.delete(Paths.get("data/" + currentDB.getDbName() + "/" + tableName + "."+tableSuffix));
            System.out.println("Table file deleted from disk.");
        } catch (IOException e) {
            System.out.println("Table file " + tableName + " could not be deleted.");
            e.printStackTrace();
        }
        // Delete record of the table in database file
        try {
            currentDB.tables.remove(tableName);
        } catch (Exception e) {
            System.out.println("Table not found in database");
        }

        try {
            saveDatabase(currentDB);
            System.out.println("Table deleted. Database updated");
        } catch (IOException e) {
            System.out.println("Database not updated...");
            e.printStackTrace();
        }
    } // end delete table



    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Map getUsers() {
        return users;
    }

    public void setUsers(Map users) {
        this.users = users;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public HashMap<String, Table> getTables() {
        return tables;
    }

    public void setTables(HashMap<String, Table> tables) {
        this.tables = tables;
    }

    public void addTable(Database db, Table table) {

        try {
            db.tables.put(table.getTableName(), table);
            System.out.println("Added table to: " + db.getDbName());
            db.saveDatabase(db);
            System.out.println("Table created and Database Updated");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void printTables() {
        AtomicReference<Integer> i = new AtomicReference<>(1);
        if (tables.size() > 0) {
            System.out.println("+" + "-".repeat(65) + "+");
            System.out.printf("| %-3s | %-25s | %-15s | %-10s | %-10s |\n", "#", "Table Name", "Character set", "Collation","Engine");
            System.out.println("+" + "-".repeat(65) + "+");
            tables.forEach((k, v) -> System.out.printf("| %-3d | %-25s | %-15s | %-10s | %-10s |\n", i.getAndSet(i.get() + 1), v.getTableName(), v.getCharSet(), v.getCollation(),v.getEngine().getName()));
            System.out.println("+" + "-".repeat(65) + "+");
        } else {
            System.out.println("No tables found");
        }

    }

    public static void saveDatabase(Database outDB) throws IOException {

        try {

            // check if data directory exists
            if (!java.nio.file.Files.isDirectory(Paths.get("data"))) {
                // Create data directory
                java.nio.file.Files.createDirectory(Paths.get("data"));

            }

            // check if database directory exists in data directory
            if (!java.nio.file.Files.isDirectory(Paths.get("data/" + outDB.dbName))) {

                // Create directory for database files
                java.nio.file.Files.createDirectory(Paths.get("data/" + outDB.dbName));

            }

            // write .db file
            FileOutputStream dbFile = new FileOutputStream("data/" + outDB.dbName + "/" + outDB.dbName + ".db");
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(dbFile));
            os.writeObject(outDB);
            os.flush();
            os.close();
            dbFile.flush();
            dbFile.close();

        } catch (IOException e) {
            System.out.println("Not able to save database file. Message: " + e.getMessage());
            e.setStackTrace();
        }

    }

    public Database openDatabase(String dbName) {
        ObjectInputStream dbIs;
        Database openedDB = new Database();
        try (FileInputStream dbFile = new FileInputStream("data/" + dbName + "/" + dbName + ".db")) {
            dbIs = new ObjectInputStream(new BufferedInputStream(dbFile));
            openedDB = (Database) dbIs.readObject();
            dbIs.close();
        } catch (FileNotFoundException e) {
            System.out.println("Database Unknown");
            ;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return openedDB;
    }

    public void describeDatabase(String dbName, boolean full) throws IOException, ClassNotFoundException {

        ObjectInputStream is;
        try (FileInputStream dbFile = new FileInputStream("data/" + dbName + "/" + dbName + ".db")) {
            is = new ObjectInputStream(new BufferedInputStream(dbFile));
            Database db = (Database) is.readObject();

            System.out.println();
            System.out.println("+" + "-".repeat(27) + "+" + "-".repeat(32) + "+");
            System.out.printf("| %-25s | %-30s |\n", "Database Name ", db.dbName);
            System.out.println("+" + "-".repeat(27) + "+" + "-".repeat(32) + "+");
            System.out.printf("| %-25s | %-30s |\n", "Default Character Set", db.getCharSet());
            System.out.printf("| %-25s | %-30s |\n", "Default Collation ", db.getCollation());
            System.out.printf("| %-25s | %-30s |\n", "Default Engine ", db.getEngine().getName());
            System.out.printf("| %-25s | %-30s |\n", "Created ", db.getCreated());
            System.out.printf("| %-25s | %-30s |\n", "Tables", db.tables.size());
            System.out.println("+" + "-".repeat(27) + "+" + "-".repeat(32) + "+");
            System.out.println();

            // print table structures, if database has tables and sql = describe full
            is.close();
            if (db.tables.size() > 0) {
                System.out.println("Tables");
                db.printTables();
            }

            if (full && db.tables.size() > 0) {
                System.out.println();
                System.out.println("Table Structures\n");

                db.tables.forEach((k, v) -> {
                    v.printTableStructure();
                    System.out.println();
                });

            }
            is.close();

        } catch (IOException e) {
            System.out.println("Database unknown " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found " + e.getMessage());
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Database)) return false;
        Database database = (Database) o;
        return getDbName().equals(database.getDbName()) &&
                Objects.equals(getCharSet(), database.getCharSet()) &&
                Objects.equals(getCollation(), database.getCollation()) &&
                Objects.equals(getUsers(), database.getUsers()) &&
                Objects.equals(getCreated(), database.getCreated()) &&
                Objects.equals(getTables(), database.getTables());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDbName(), getCharSet(), getCollation(), getUsers(), getCreated(), getTables());
    }


}
