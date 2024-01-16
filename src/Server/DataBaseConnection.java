package Server;

import java.sql.*;

/**
 * Klasa DataBaseConnection obsługuje połączenie z bazą danych oraz pobieranie i konwertowanie z niej wartości.
 */
public class DataBaseConnection {
    public Connection database;
    /**
     * Tworzy obiekt DataBaseConnection.
     */
    public DataBaseConnection() {
        try {
            //database = DriverManager.getConnection("jdbc:mysql://localhost:3306/ChessServer", "root", "");
            database = DriverManager.getConnection("jdbc:mysql://localhost:3306/chessapp", "root", "");
        } catch(Exception e) {
            System.out.println("database connection failed");
        }
    }

    /**
     * pomocnicza. Sprawdza czy dobre dane
     * @param n nazwa
     * @param p hasło
     * @return czy dobre
     */
    public boolean correctLogin(String n, String p) {
        try {
            Statement st = database.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM playerdata WHERE name='"+n+"' AND password='"+p+"'");
            return res.next();
        } catch(Exception e) {
            System.out.println("failed statement login");
            return false;
        }
    }

    /**
     * pomocnicza Sprawdza czy dobre dane
     * @param n nazwa
     * @param p hasło
     * @return czy dobre
     */
    public boolean correctRegister(String n, String p) {
        try {
            Statement st = database.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM playerdata WHERE name='"+n+"'");
            if(res.next()) {
                System.out.println("there is already such player");
                return false;
            }
            else {
                res = st.executeQuery("SELECT MAX(playerid) m FROM playerdata");
                res.next();
                int pid = res.getInt("m") + 1;
                PreparedStatement pr = database.prepareStatement("INSERT INTO playerdata (`playerid`, `name`, `password`) VALUES ('"+pid+"', '"+n+"', '"+p+"')");
                pr.execute();
                return true;
            }
        } catch(Exception e) {
            System.out.println("failed statement");
            return false;
        }
    }
    /**
     * Pobiera z bazy danych informacje o partiach gracza i zapisuje je w wygodnej postaci.
     * @param name nazwa użytkownika którego informacje nas interesują
     * @return lista list z grami
     */
    public String getPlayerGamesMessage(String name) {
        try {
            Statement st = database.createStatement();
            ResultSet res = st.executeQuery("SELECT \n" +
                    "gamedate, \n" +
                    "COALESCE((SELECT name FROM ChessServer.playerdata WHERE playerid=player2id), 'DELETED') opponent,\n" +
                    "COALESCE((SELECT name \n" +
                    "\tFROM ChessServer.playerdata WHERE (playerid=player1id AND winner=1) \n" +
                    "    OR (playerid=player2id) AND winner=2), 'REMIS') winner\n" +
                    "FROM ChessServer.gamedata\n" +
                    "WHERE player1id = (SELECT playerid FROM ChessServer.playerdata WHERE name='"+name+"');");
            String tmp = "";
            while(res.next()) {
                tmp += res.getString("gamedate") + ","
                        + res.getString("opponent")
                        + "," + res.getString("winner")
                        + "/";
            }
            tmp = "gamedate,opponent,winner/" + tmp;
            return tmp;

        } catch(Exception e) {
            System.out.println("statement fail get games");
        }
        return null;
    }
    /**
     * Pobiera z bazy danych informacje o statystykach wszystkich graczy i zapisuje je w wygodnej postaci.
     * @return lista list z statystykami graczy
     */
    public String getAllPlayersStatistics() {
        try {
            Statement st = database.createStatement();
            ResultSet res = st.executeQuery("SELECT \n" +
                    "(SELECT name FROM ChessServer.playerdata WHERE playerid=player1id) player,\n" +
                    "COUNT(*) gamecount,\n" +
                    "COUNT(IF(winner=1, 1, null)) wincount,\n" +
                    "COUNT(IF(winner=2, 1, null)) loosecount,\n" +
                    "COUNT(IF(winner=0, 1, null)) drawcount,\n" +
                    "(COUNT(IF(winner=1, 1, null)))/(COUNT(*)) winproportion\n" +
                    "FROM ChessServer.gamedata gamecount \n" +
                    "WHERE player1id IS NOT NULL\n" +
                    "GROUP BY player1id");
            String tmp = "";
            while(res.next()) {
                tmp += res.getString("player") + ","
                        + res.getString("gamecount")
                        + "," + res.getString("wincount")
                        + "," + res.getString("loosecount")
                        + "," + res.getString("drawcount")
                        + "," + res.getString("winproportion")
                        + "/";
            }
            tmp = "player,gamecount,wincount,loosecount,drawcount,winproportion/" + tmp;
            return tmp;
        } catch(Exception e) {
            System.out.println("failed getAllStats");
        }

        return null;
    }
    /**
     * Pobiera z bazy danych informacje o statystykach gracza o nazwie name i zapisuje je w wygodnej postaci.
     * @param name nazwa użytkownika
     * @return statystyki gracza (lista)
     */
    public String getPlayerStatistics(String name) {
        try {
            Statement st = database.createStatement();
            ResultSet res = st.executeQuery("SELECT \n" +
                    "(SELECT name FROM ChessServer.playerdata WHERE playerid=player1id) player,\n" +
                    "COUNT(*) gamecount,\n" +
                    "COUNT(IF(winner=1, 1, null)) wincount,\n" +
                    "COUNT(IF(winner=2, 1, null)) loosecount,\n" +
                    "COUNT(IF(winner=0, 1, null)) drawcount,\n" +
                    "(COUNT(IF(winner=1, 1, null)))/(COUNT(*)) winproportion\n" +
                    "FROM ChessServer.gamedata gamecount\n" +
                    "GROUP BY player1id\n" +
                    "HAVING player='"+name+"'");
            String tmp = "";
            while(res.next()) {
                tmp += res.getString("player") + ","
                        + res.getString("gamecount")
                        + "," + res.getString("wincount")
                        + "," + res.getString("loosecount")
                        + "," + res.getString("drawcount")
                        + "," + res.getString("winproportion");
            }
            tmp = "player,gamecount,wincount,loosecount,drawcount,winproportion/" + tmp;
            return tmp;
        } catch (Exception e) {
            System.out.println("failed getPStats");
        }
        return null;
    }

    /**
     * dodaje grę użytkowników do bazy danych
     * @param player1 gracz1
     * @param player2 gracz2
     * @param winner który zwyciężył
     */
    public void addGame(String player1, String player2, int winner) {
        int looser;
        if(winner == 0) {
            looser = 0;
        }
        else {
            looser = winner == 1 ? 2 : 1;
        }
        try {
            PreparedStatement pr = database.prepareStatement("INSERT INTO ChessServer.gamedata\n" +
                    "(gameid, gamedate, player1id, player2id, winner)\n" +
                    "VALUES ((SELECT MAX(gameid)+1 FROM ChessServer.gamedata tmp), \n" +
                    "\t\tcurrent_date(),\n" +
                    "        (SELECT playerid FROM ChessServer.playerdata WHERE name = '"+player1+"'),\n" +
                    "\t\t(SELECT playerid FROM ChessServer.playerdata WHERE name = '"+player2+"'),\n" +
                    ""+winner+");\n");
            pr.execute();
            pr = database.prepareStatement("INSERT INTO ChessServer.gamedata\n" +
                    "(gameid, gamedate, player1id, player2id, winner)\n" +
                    "VALUES ((SELECT MAX(gameid)+1 FROM ChessServer.gamedata tmp), \n" +
                    "\t\tcurrent_date(),\n" +
                    "        (SELECT playerid FROM ChessServer.playerdata WHERE name = '"+player2+"'),\n" +
                    "\t\t(SELECT playerid FROM ChessServer.playerdata WHERE name = '"+player1+"'),\n" +
                    ""+looser+");");
            pr.execute();
        } catch (Exception e) {
            System.out.println("Error Dodawanie gier do bazy");
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) throws InterruptedException {
        DataBaseConnection d = new DataBaseConnection();
        Thread.sleep(1000);
        d.addGame("Kuba", "Gilbert", 2);
    }
}
