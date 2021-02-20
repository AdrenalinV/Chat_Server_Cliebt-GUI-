public class AuthenticationRequest extends AbstractMSG {
    private String login;
    private String pass;
    private String nick;
    private Boolean stat;
    private int newUser;

    public void setNewUser(int newUser) {
        this.newUser = newUser;
    }

    public int getNewUser() {
        return newUser;
    }

    public AuthenticationRequest() {
        this.stat = false;
        newUser=0;
    }

    public AuthenticationRequest(String login, String pass) {
        this.login = login;
        this.pass = pass;
        this.stat = false;
        newUser=0;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public String getNick() {
        return nick;
    }

    public Boolean isStat() {
        return stat;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setStat(boolean stat) {
        this.stat = stat;
    }
}
