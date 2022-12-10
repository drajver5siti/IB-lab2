package Messages;

public class UserToUserEncryptedMessage {
    private byte[] fromId;
    private byte[] timestamp;

    public UserToUserEncryptedMessage(byte[] fromId, byte[] timestamp) {
        this.fromId = fromId;
        this.timestamp = timestamp;
    }

    public byte[] getFromId() {
        return fromId;
    }

    public byte[] getTimestamp() {
        return timestamp;
    }
}
