package Messages;

import javax.crypto.SecretKey;
import java.util.Date;

public class KDCToUserEncryptedMessage {

    private byte[] key;
    private byte[] lifetime;
    private byte[] id;
    private byte[] nonce;

    public KDCToUserEncryptedMessage(
            byte[] key,
            byte[] lifetime,
            byte[] id,
            byte[] nonce
    ) {
        this.key = key;
        this.lifetime = lifetime;
        this.id = id;
        this.nonce = nonce;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getLifetime() {
        return lifetime;
    }

    public byte[] getId() {
        return id;
    }

    public byte[] getNonce() {
        return nonce;
    }
}
