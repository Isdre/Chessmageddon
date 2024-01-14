package tests.chess_server_tests;

import Server.Server;
import chess_server_package.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestServerClientDatabaseConnection {
    class Dmc {
        public Server sr;
        public Client cl;
        public MyListener ml;
    }
    public String message = null;
    public MessType messType = null;
    Dmc init() throws InterruptedException {
        Dmc dmc = new Dmc();
        dmc.sr = new Server();
        Thread ts = new Thread(dmc.sr);
        ts.start();
        Thread.sleep(2000);
        dmc.ml = new MyListener() {
            @Override
            public void performed(String message, MessType type) {
                switch (type) {
                    case SYSTEM_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case OPPONENT_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case MOVE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case CONFIRM -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case REJECT -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case INVITED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case GAME_ENDED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    default -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = null;
                    }
                }
            }
        };
        dmc.cl = new Client(dmc.ml);
        assertNotNull(dmc.sr);
        assertNotNull(dmc.cl);
        assertNotNull(dmc.ml);
        Thread t = new Thread(dmc.cl);
        t.start();
        Thread.sleep(500);
        return dmc;
    }

    @Test
    void login() throws InterruptedException, IOException {
        Dmc dmc = init();
        assertFalse(dmc.cl.login("Kuba", "1111"));
        assertTrue(dmc.cl.login("Kuba", "1234"));
        dmc.sr.server.close();
    }

    @Test
    void register() throws InterruptedException, IOException {
        Dmc dmc = init();
        assertFalse(dmc.cl.register("Kuba", "1111"));
        dmc.sr.server.close();
    }
    @Test
    void testReceiveSystemMessage() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        assertNotNull(message);
        dmc.sr.connections.getFirst().sendMessage("Sm");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.SYSTEM_MESSAGE, messType);
        dmc.sr.server.close();
    }
    @Test
    void testPlayWith() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.cl.playWith("Gilbert");
        Thread.sleep(1000);
        assertEquals("There is no such player.", message);
        dmc.sr.server.close();
    }
    @Test
    void TestPlayWith() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        MyListener ml1 = new MyListener() {
            @Override
            public void performed(String message, MessType type) {
                switch (type) {
                    case SYSTEM_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case OPPONENT_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case MOVE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case CONFIRM -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case REJECT -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case INVITED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case GAME_ENDED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    default -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = null;
                    }
                }
            }
        };
        Client cl1 = new Client(ml1);
        Thread tc1 = new Thread(cl1);
        tc1.start();
        Thread.sleep(1000);
        cl1.login("Gilbert", "4321");

        cl1.playWith("Kuba");
        Thread.sleep(1000);
        assertNotNull(message);
        assertEquals("Gilbert", message);
        assertEquals(MessType.INVITED, messType);
        dmc.cl.reject();
        Thread.sleep(1000);
        assertEquals("unimportant", message);
        assertEquals(MessType.REJECT, messType);
        cl1.playWith("Kuba");
        Thread.sleep(1000);
        dmc.cl.confirm('b');
        Thread.sleep(1000);
        assertEquals("bGilbert", message);
        assertEquals(MessType.CONFIRM, messType);
        assertNotNull(dmc.sr.games.getFirst());
        dmc.sr.server.close();
    }
    @Test
    void TestSendReceiveMessage() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        MyListener ml1 = new MyListener() {
            @Override
            public void performed(String message, MessType type) {
                switch (type) {
                    case SYSTEM_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case OPPONENT_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case MOVE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case CONFIRM -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case REJECT -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case INVITED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case GAME_ENDED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    default -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = null;
                    }
                }
            }
        };
        Client cl1 = new Client(ml1);
        Thread tc1 = new Thread(cl1);
        tc1.start();
        Thread.sleep(1000);
        cl1.login("Gilbert", "4321");

        cl1.playWith("Kuba");
        Thread.sleep(1000);
        dmc.cl.confirm('b');
        Thread.sleep(1000);

        cl1.messageOpponent("elo");
        Thread.sleep(1000);
        assertEquals("Gilbert: elo", message);
        assertEquals(MessType.OPPONENT_MESSAGE, messType);
        dmc.sr.server.close();
    }
    @Test
    void testSendReceiveMove() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        MyListener ml1 = new MyListener() {
            @Override
            public void performed(String message, MessType type) {
                switch (type) {
                    case SYSTEM_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case OPPONENT_MESSAGE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case MOVE -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case CONFIRM -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case REJECT -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case INVITED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    case GAME_ENDED -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = type;
                    }
                    default -> {
                        TestServerClientDatabaseConnection.this.message = message;
                        TestServerClientDatabaseConnection.this.messType = null;
                    }
                }
            }
        };
        Client cl1 = new Client(ml1);
        Thread tc1 = new Thread(cl1);
        tc1.start();
        Thread.sleep(1000);
        cl1.login("Gilbert", "4321");

        cl1.playWith("Kuba");
        Thread.sleep(1000);
        dmc.cl.confirm('b');
        Thread.sleep(1000);

        cl1.makeMove("move");
        Thread.sleep(1000);
        assertEquals("move", message);
        assertEquals(MessType.MOVE, messType);
        dmc.sr.server.close();
    }

    @Test
    void testGetPlayerGames() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        assertNotNull(dmc.cl.getPlayerGames());
        dmc.sr.server.close();
    }
}