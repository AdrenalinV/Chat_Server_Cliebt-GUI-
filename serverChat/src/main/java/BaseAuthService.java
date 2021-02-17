
import java.util.ArrayList;
import java.util.List;

interface AuthService {
    void start();

    String getNickByLoginPass(String login, String pass);

    void stop();
}

public class BaseAuthService implements AuthService {

    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }

    }

    private List<Entry> entries;

    @Override
    public void start() {
        System.out.println("[DEBUG] сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("[DEBUG] сервис аутентификации остановлен");
    }

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("Vadim", "2013", "Vadim"));
        entries.add(new Entry("Alena", "2013", "Alena"));
        entries.add(new Entry("Ksyna", "2013", "Ksy"));
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (Entry en : entries) {
            if (en.login.equals(login) && en.pass.equals(pass)) return en.nick;
        }
        return null;
    }
}
