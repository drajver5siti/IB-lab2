package Messages;

import java.util.Date;

public class UserToUserPlainMessage {
    private String fromId;
    private Date timestamp;

    public UserToUserPlainMessage(String fromId, Date timestamp) {
        this.fromId = fromId;
        this.timestamp = timestamp;
    }

    public String getFromId() {
        return fromId;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
