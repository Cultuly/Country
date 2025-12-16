package org.example;

import java.sql.*;
import java.util.List;
import java.util.Arrays;


public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Region ("
                    + "id INTEGER PRIMARY KEY, "
                    + "name TEXT UNIQUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Subregion ("
                    + "id INTEGER PRIMARY KEY, "
                    + "name TEXT UNIQUE, "
                    + "region_id INTEGER, "
                    + "FOREIGN KEY(region_id) REFERENCES Region(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Country ("
                    + "id INTEGER PRIMARY KEY, "
                    + "name TEXT, "
                    + "subregion_id INTEGER, "
                    + "internet_users INTEGER, "
                    + "population INTEGER, "
                    + "FOREIGN KEY(subregion_id) REFERENCES Subregion(id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(List<Region> regions, List<Subregion> subregions, List<Country> countries) {
        try {
            connection.setAutoCommit(false);

            String regionSql = "INSERT OR IGNORE INTO Region (name) VALUES (?)";
            try (PreparedStatement pstmt = connection.prepareStatement(regionSql)) {
                for (Region region : regions) {
                    pstmt.setString(1, region.getName());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            String subregionSql = "INSERT OR IGNORE INTO Subregion (name, region_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(subregionSql)) {
                for (Subregion subregion : subregions) {
                    pstmt.setString(1, subregion.getName());
                    pstmt.setInt(2, subregion.getRegionId());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            String countrySql = "INSERT INTO Country (name, subregion_id, internet_users, population) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(countrySql)) {
                for (Country country : countries) {
                    pstmt.setString(1, country.getName());
                    pstmt.setInt(2, country.getSubregionId());
                    pstmt.setLong(3, country.getInternetUsers());
                    pstmt.setLong(4, country.getPopulation());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void executeQueries() {
        try {
            String query1 = "SELECT s.name AS subregion, "
                    + "(SUM(c.internet_users) * 100.0 / SUM(c.population)) AS percentage "
                    + "FROM Country c "
                    + "JOIN Subregion s ON c.subregion_id = s.id "
                    + "WHERE c.population > 0 AND c.internet_users > 0 "
                    + "GROUP BY s.name "
                    + "ORDER BY percentage DESC";

            System.out.println("1. Процентное соотношение пользователей в интернете по субрегионам:");
            executeAndPrintQuery(query1);

            String query2 = "SELECT c.name, c.internet_users "
                    + "FROM Country c "
                    + "JOIN Subregion s ON c.subregion_id = s.id "
                    + "WHERE s.name = 'Eastern Europe' AND c.internet_users > 0 AND c.population > 0 "
                    + "ORDER BY c.internet_users ASC "
                    + "LIMIT 1";

            System.out.println("\n2. Страна с наименьшим кол-вом пользователей в Восточной Европе:");
            executeAndPrintQuery(query2);

            String query3 = "SELECT c.name, "
                    + "(c.internet_users * 100.0 / c.population) AS percentage "
                    + "FROM Country c "
                    + "WHERE c.population > 0 AND c.internet_users > 0 "
                    + "AND (c.internet_users * 100.0 / c.population) BETWEEN 75 AND 85 "
                    + "ORDER BY percentage";

            System.out.println("\n3. Страны с процентом пользователей от 75% до 85%:");
            executeAndPrintQuery(query3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeAndPrintQuery(String query) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-25s", metaData.getColumnName(i));
            }

            char[] dashes = new char[25 * columnCount];
            Arrays.fill(dashes, '-');
            System.out.println("\n" + new String(dashes));

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-25s", rs.getString(i));
                }
                System.out.println();
            }

            if (!hasData) {
                System.out.println("Нет данных");
            }
            System.out.println();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}