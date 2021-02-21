public class UpdateNickRequest extends AbstractMSG{
    private String newNick;

    private UpdateNickRequest() { }

    public static UpdateNickRequest of (String newNick){
        UpdateNickRequest msg=new UpdateNickRequest();
        msg.newNick=newNick;
        return msg;
    }

    public String getNewNick() {
        return newNick;
    }
}
