package tests.chess_server_tests;

import static org.junit.jupiter.api.Assertions.*;

import chess_server_package.*;

class TestDataBaseConnection {
    @org.junit.jupiter.api.Test
    void testConnection() throws InterruptedException {
        DataBaseConnection db = new DataBaseConnection();
        Thread.sleep(500);
        assertNotNull(db.database);
    }
    @org.junit.jupiter.api.Test
    void testCorrectRegister() throws InterruptedException {
        DataBaseConnection db = new DataBaseConnection();
        Thread.sleep(500);
        assertNotNull(db.database);
        assertFalse(db.correctRegister("Kuba", "1111"));
    }
    @org.junit.jupiter.api.Test
    void testCorrectLogin() throws InterruptedException {
        DataBaseConnection db = new DataBaseConnection();
        Thread.sleep(500);
        assertNotNull(db.database);
        assertFalse(db.correctLogin("Kuba", "1111"));
        assertFalse(db.correctLogin("Kuba", "1111"));
        assertTrue(db.correctLogin("Kuba", "1234"));
    }



    @org.junit.jupiter.api.Test
    void testGetPlayerGamesMessage() throws InterruptedException {
        DataBaseConnection db = new DataBaseConnection();
        Thread.sleep(500);
        assertNotNull(db.database);
        assertNotNull(db.getPlayerGamesMessage("Kuba"));
        assertNotNull(db.getPlayerStatistics("Gilbert"));
    }

    @org.junit.jupiter.api.Test
    void testGetAllPlayersStatistics() throws InterruptedException {
        DataBaseConnection db = new DataBaseConnection();
        Thread.sleep(500);
        assertNotNull(db.database);
        assertNotNull(db.getAllPlayersStatistics());
    }
}