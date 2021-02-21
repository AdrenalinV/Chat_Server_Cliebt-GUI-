

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DataSource {
    private static final String URL="jdbc:sqlite:users.db";

    static {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Database Driver initialization Error");
        }

    }

    private DataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);

    }

}
