package ntu.scse.mdp2022.mainui.Chat;

public class Message {
    private static final int ROBOT = 2;
    private static final int REMOTE = 1;

    String message;
    int sender;

    public Message (String msg, int who) {
        this.message = msg;
        this.sender = who;
    }

    public int getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
