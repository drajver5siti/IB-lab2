package Messages;

public class UserToKDCRequestMessage {

    private String fromID;
    private String toID;
    private byte[] nonce;

    public UserToKDCRequestMessage(String fromID, String toID, byte[] nonce) {
        this.fromID = fromID;
        this.toID = toID;
        this.nonce = nonce;
    }

    public String getFromID() {
        return fromID;
    }

    public String getToID() {
        return toID;
    }

    public byte[] getNonce() {
        return nonce;
    }
}
