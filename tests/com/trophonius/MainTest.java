package com.trophonius;

import com.trophonius.dbo.Database;
import com.trophonius.sql.SqlParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.trophonius.Main.currentDB;
import static com.trophonius.Main.prompt;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @DisplayName("Test på en test")
    @Test
    void main() {
        int[] numbers = {0, 1, 2, 3, 4};
        assertAll("numbers",
                () -> assertEquals(numbers[0], 0),
                () -> assertEquals(numbers[3], 3),
                () -> assertEquals(numbers[4], 4)
        );
    }


@Test
    void sqlParserTestUse() {
    String inputText = "use testbase";
    String prompt = ">";
    //Database currentDB = new Database();
    SqlParser sql = new SqlParser(prompt, currentDB, inputText);
    assertAll("parseSQL",
            () -> assertEquals(sql.prompt, ">"),
            () -> assertEquals(sql.currentDB, currentDB),
            () -> assertEquals(inputText, "use testbase"),
            () -> {
//        if(assertNotEquals(currentDB.getDbName(),null)==true) {
//         assertEquals(currentDB.getDbName(), "testbase");
//        }
    }
    );

}




}