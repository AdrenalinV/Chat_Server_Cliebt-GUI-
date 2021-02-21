import java.util.Collection;

public class UserListMSG extends AbstractMSG {
    private final Collection<String> users;

    public UserListMSG(Collection<String> users) {
        this.users = users;

    }

    public Collection<String> getUsers() {
        return users;
    }


}
