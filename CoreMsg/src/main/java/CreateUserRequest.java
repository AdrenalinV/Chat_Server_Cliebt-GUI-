public class CreateUserRequest extends AbstractMSG{
    private String login;
    private String password;

    private CreateUserRequest(){

    }

    public static CreateUserRequest of(){
        return new CreateUserRequest();
    }

    public static CreateUserRequest of(String userName, String password){
        CreateUserRequest msg=new CreateUserRequest();
        msg.login=userName;
        msg.password=password;
        return msg;
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
