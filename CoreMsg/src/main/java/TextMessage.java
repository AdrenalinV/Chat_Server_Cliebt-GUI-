public class TextMessage extends AbstractMSG {
    private String sender;
    private String recipient;
    private String message;

    private TextMessage() {
    }

    public static TextMessage of(String sender, String message) {
        TextMessage tMSG = new TextMessage();
        tMSG.sender = sender;
        tMSG.message = message;
        return tMSG;
    }

    public static TextMessage of(String sender, String recipient, String message) {
        TextMessage tMSG = new TextMessage();
        tMSG.sender = sender;
        tMSG.recipient = recipient;
        tMSG.message = message;
        return tMSG;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }
}
