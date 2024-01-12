package tests.chess_server_tests;

import chess_server_package.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestClient {
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
                        TestClient.this.message = message;
                        TestClient.this.messType = type;
                    }
                    case OPPONENT_MESSAGE -> {
                        TestClient.this.message = message;
                        TestClient.this.messType = type;
                    }
                    case MOVE -> {
                        TestClient.this.message = message;
                        TestClient.this.messType = type;
                    }
                    case CONFIRM -> {
                        TestClient.this.message = message;
                        TestClient.this.messType = type;
                    }
                    case REJECT -> {
                        TestClient.this.message = message;
                        TestClient.this.messType = type;
                    }
                    case INVITED -> {
                        TestClient.this.message = message;
                        TestClient.this.messType = type;
                    }
                    case GAME_ENDED -> {
                        TestClient.this.message = message;
                        TestClient.this.messType = type;
                    }
                    default -> {
                        TestClient.this.message = message;
                        TestClient.this.messType = null;
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
    void testConnection() throws InterruptedException, IOException {
        Dmc dmc = init();
        assertNotNull(dmc.cl.out);
        assertNotNull(dmc.cl.in);
        dmc.sr.server.close();
    }

    @Test
    void testListener() throws InterruptedException, IOException {
        Dmc dmc = init();
        assertNotNull(dmc.ml);

        dmc.sr.server.close();
    }
    @Test
    void testConfirmed() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.sr.connections.getFirst().sendMessage("Cm");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.CONFIRM, messType);

        dmc.sr.server.close();
    }
    @Test
    void testRejected() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.sr.connections.getFirst().sendMessage("Rm");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.REJECT, messType);

        dmc.sr.server.close();
    }
    @Test
    void testReceiveMessage() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.sr.connections.getFirst().sendMessage("Mm");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.OPPONENT_MESSAGE, messType);

        dmc.sr.server.close();
    }
    @Test
    void testReceiveSystemMessage() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.sr.connections.getFirst().sendMessage("Sm");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.SYSTEM_MESSAGE, messType);

        dmc.sr.server.close();
    }
    @Test
    void testReceiveMove() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.sr.connections.getFirst().sendMessage("Xm");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.MOVE, messType);

        dmc.sr.server.close();
    }
    @Test
    void testInvited() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.sr.connections.getFirst().sendMessage("Im");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.INVITED, messType);

        dmc.sr.server.close();
    }
    @Test
    void testEndGame() throws InterruptedException, IOException {
        Dmc dmc = init();
        dmc.cl.login("Kuba", "1234");
        Thread.sleep(1000);
        dmc.sr.connections.getFirst().sendMessage("Em");
        Thread.sleep(1000);
        assertEquals("m", message);
        assertEquals(MessType.GAME_ENDED, messType);

        dmc.sr.server.close();
    }
}
