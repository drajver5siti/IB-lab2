import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MyKeyGenerator {

    private KeyGenerator keyGenerator;

    public MyKeyGenerator()
    {
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");


            keyGenerator.init(256, secureRandom);

            this.keyGenerator = keyGenerator;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public SecretKey generateKey()
    {
        return this.keyGenerator.generateKey();
    }
}
