import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class BaseAuthServiceTest {

    private static final String ADD_USER = "INSERT INTO users(login, pass, nickName) VALUES (?, ?, ?)";
    private static final String GET_NICK_BY_LOGIN_PASS = "SELECT nickName FROM users WHERE login=? AND pass=?";
    private static final String SUPER_SECRET_SALT = "MY_MOM_MAKES_COFFEE";
@Test
    public void createUserTest(){
        String userName = "Ksyna";
        String plainUserPassword = "Egor2013";
        createUser(userName, plainUserPassword);
    }

    private void createUser(String userName, String plainUserPassword){
        try(Connection connection = DataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(ADD_USER)){
            ps.setString(1, userName);
            ps.setString(2, getPassword(plainUserPassword));
            ps.setString(3, userName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getPassword(String plainPassword){
        String hashedPassword = null;
        String passwordWithSalt = plainPassword + SUPER_SECRET_SALT;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashedPassword = bytesToHex(md.digest(passwordWithSalt.getBytes()));
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

}
