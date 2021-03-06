

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

interface AuthService {
    void start() throws SQLException;

    String getNickByLoginPass(String login, String pass);

    void createUser(String userName, String plainUserPassword);

    void updateNickName(String newNickName, String oldNickName);

    boolean existUser(String userName);

    void stop();
}


public class BaseAuthService implements AuthService {
    public static final Logger log = LogManager.getLogger(BaseAuthService.class.getName());
    private static final String INIT_DB = "CREATE TABLE IF NOT EXISTS users (id INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, login TEXT UNIQUE NOT NULL, pass TEXT NOT NULL, nickName TEXT UNIQUE NOT NULL)";
    private static final String EXIST_USER = "SELECT * FROM users WHERE login=?";
    private static final String ADD_USER = "INSERT INTO users(login, pass, nickName) VALUES (?, ?, ?)";
    private static final String GET_NICK_BY_LOGIN_PASS = "SELECT nickName FROM users WHERE login=? AND pass=?";
    private static final String UPDATE_NICKNAME = "UPDATE users SET nickName=? WHERE nickName=?";
    private static final String SUPER_SECRET_SALT = "MY_MOM_MAKES_COFFEE";

    public void createUser(String userName, String plainUserPassword) {
        log.trace("create new user: {}", userName);
        try (Connection connection = DataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(ADD_USER)) {
            ps.setString(1, userName);
            ps.setString(2, getPassword(plainUserPassword));
            ps.setString(3, userName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateNickName(String newNickName, String oldNickName) {
        log.trace("Update nickName user: {} newNikName: {}", oldNickName, newNickName);
        try (Connection con = DataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_NICKNAME)) {
            ps.setString(1, newNickName);
            ps.setString(2, oldNickName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getPassword(String plainPassword) {
        String hashedPassword = null;
        String passwordWithSalt = plainPassword + SUPER_SECRET_SALT;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashedPassword = bytesToHex(md.digest(passwordWithSalt.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    private String getNickName(String userName, String cipheredPassword) {
        log.trace("get nickName user: {}", userName);
        String nickName = null;
        try (Connection connection = DataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_NICK_BY_LOGIN_PASS)) {
            ps.setString(1, userName);
            ps.setString(2, cipheredPassword);
            ResultSet rs = ps.executeQuery();
            rs.next();
            nickName = rs.getString("nickName");
            rs.close();
        } catch (SQLException e) {
            return null;
        }
        return nickName;
    }

    @Override
    public boolean existUser(String userName) {
        log.trace("exist nickName user: {}", userName);
        boolean isExist = false;
        try (Connection con = DataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(EXIST_USER)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            isExist = rs.next();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isExist;
    }


    @Override
    public void start() throws SQLException {
        try (Connection con = DataSource.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(INIT_DB);
            log.debug("сервис аутентификации запущен");
        }

    }

    @Override
    public void stop() {
        log.debug("сервис аутентификации остановлен");
    }


    @Override
    public String getNickByLoginPass(String login, String pass) {
        return getNickName(login, getPassword(pass));
    }
}
