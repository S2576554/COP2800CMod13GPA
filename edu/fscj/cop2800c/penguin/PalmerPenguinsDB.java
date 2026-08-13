// PalmerPenguinsDB.java
// Chris O'Dell
// 08/13/2026
// Class for Palmer Penguins DB operations

package edu.fscj.cop2800c.penguin;

import java.sql.*;
import java.util.ArrayList;

public class PalmerPenguinsDB
{
    public static void createDB(ArrayList<Penguin> penguins) {

        final String DB_NAME = "PalmerPenguins";
        final String CLASS_NAME =
            "com.microsoft.sqlserver.jdbc.SQLServerDriver";

        final String CONN_URL =
            "jdbc:sqlserver://localhost:1433;integratedSecurity=true;";

        final String SQL_DROP_TABLE =
            "DROP TABLE Penguin";

        try {
            // Load SQL Server JDBC driver
            Class.forName(CLASS_NAME);

            try (Connection con = DriverManager.getConnection(CONN_URL);
                 Statement stmt = con.createStatement()) {

                // ---------------------------------------------------------
                // CREATE DATABASE
                // ---------------------------------------------------------
                try {
                    stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
                    System.out.println("DB created");
                } catch (SQLException e) {
                    System.out.println(
                        "could not create DB, already exists");
                }

                // ---------------------------------------------------------
                // SWITCH TO PalmerPenguins DATABASE
                // ---------------------------------------------------------
                stmt.executeUpdate("USE " + DB_NAME);

                // ---------------------------------------------------------
                // CREATE TABLE
                // ---------------------------------------------------------
                String createTable =
                    "CREATE TABLE Penguin " +
                    "(SAMPLENUM smallint PRIMARY KEY NOT NULL," +
                    "CULMENLEN float NOT NULL," +
                    "CULMENDEPTH float NOT NULL," +
                    "BODYMASS smallint NOT NULL," +
                    "SEX char(1) NOT NULL," +
                    "SPECIES varchar(20) NOT NULL," +
                    "FLIPPERLEN float NOT NULL)";

                stmt.executeUpdate(createTable);
                System.out.println("Table created");

                // ---------------------------------------------------------
                // INSERT PENGUIN DATA
                // ---------------------------------------------------------
                String insertQuery =
                    "INSERT INTO Penguin " +
                    "(SAMPLENUM, CULMENLEN, CULMENDEPTH, " +
                    "BODYMASS, SEX, SPECIES, FLIPPERLEN) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt =
                        con.prepareStatement(insertQuery)) {

                    for (Penguin penguin : penguins) {

                        pstmt.setInt(
                            1,
                            penguin.getSampleNum()
                        );

                        pstmt.setDouble(
                            2,
                            penguin.getCulmenLength()
                        );

                        pstmt.setDouble(
                            3,
                            penguin.getCulmenDepth()
                        );

                        pstmt.setInt(
                            4,
                            (int) penguin.getBodyMass()
                        );

                        String sex = penguin.getSex();

                        pstmt.setString(
                            5,
                            (sex != null && !sex.isEmpty())
                                ? sex.substring(0, 1)
                                : "?"
                        );

                        pstmt.setString(
                            6,
                            penguin.getSpecies().toString()
                        );

                        pstmt.setDouble(
                            7,
                            penguin.getFlipperLength()
                        );

                        pstmt.addBatch();
                    }

                    pstmt.executeBatch();

                    System.out.println("Data inserted");
                }

                // ---------------------------------------------------------
                // QUERY AND DISPLAY DATA
                // ---------------------------------------------------------
                try (ResultSet rs =
                        stmt.executeQuery("SELECT * FROM Penguin")) {

                    while (rs.next()) {

                        System.out.println(
                            rs.getInt("SAMPLENUM") + "," +
                            rs.getDouble("CULMENLEN") + "," +
                            rs.getDouble("CULMENDEPTH") + "," +
                            rs.getInt("BODYMASS") + "," +
                            rs.getString("SEX") + "," +
                            rs.getString("SPECIES") + "," +
                            rs.getDouble("FLIPPERLEN")
                        );
                    }
                }

                // ---------------------------------------------------------
                // DROP TABLE
                // ---------------------------------------------------------
                stmt.executeUpdate(SQL_DROP_TABLE);

                System.out.println("Penguin table dropped");

                // ---------------------------------------------------------
                // SWITCH BACK TO MASTER DATABASE
                //
                // This is important because SQL Server will not allow
                // PalmerPenguins to be dropped while this connection
                // is currently using PalmerPenguins.
                // ---------------------------------------------------------
                stmt.executeUpdate("USE master");

                // ---------------------------------------------------------
                // DROP DATABASE
                // ---------------------------------------------------------
                try {
                    stmt.executeUpdate(
                        "DROP DATABASE " + DB_NAME
                    );

                    System.out.println("DB dropped");

                } catch (SQLException e) {
                    System.out.println(
                        "could not drop DB, in use"
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}