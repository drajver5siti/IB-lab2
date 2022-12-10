package Messages;

import javax.crypto.SecretKey;
import java.util.Date;

public class KDCToUserPlainMessage {

    private SecretKey key;
    private Date lifetime;
    private String id;
    private byte[] nonce;

    public KDCToUserPlainMessage(
            SecretKey key,
            Date lifetime,
            String id,
            byte[] nonce
    ) {
        this.key = key;
        this.lifetime = lifetime;
        this.id = id;
        this.nonce = nonce;
    }

    public SecretKey getKey() {
        return key;
    }

    public Date getLifetime() {
        return lifetime;
    }

    public String getId() {
        return id;
    }

    public byte[] getNonce() {
        return nonce;
    }
}
