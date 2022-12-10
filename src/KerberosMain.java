import Messages.KDCToUserEncryptedMessage;
import Messages.KDCToUserPlainMessage;
import Messages.UserToKDCRequestMessage;

import java.util.List;

public class KerberosMain {
    public static void main(String[] args) {

        MyKeyGenerator keyGenerator = new MyKeyGenerator();
        KDCServer server = new KDCServer(keyGenerator.generateKey());

        User alice = new User("Alice", keyGenerator.generateKey(), server);
        User bob = new User("Bob", keyGenerator.generateKey(), server);

        server.addUser(alice);
        server.addUser(bob);

        alice.sendMessage(bob, "Hello Test Message");

    }
}
