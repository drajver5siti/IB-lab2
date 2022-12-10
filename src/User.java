import Exceptions.InvalidLifetimeException;
import Exceptions.InvalidNonceException;
import Exceptions.InvalidTimeStampException;
import Exceptions.InvalidUserIDException;
import Messages.*;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.*;

public class User {

    private String ID;

    private SecretKey privateKey;

    private KDCServer server;

    public User(String ID, SecretKey privateKey, KDCServer server) {
        this.ID = ID;
        this.privateKey = privateKey;
        this.server = server;
    }

    public String getID() {
        return ID;
    }

    public SecretKey getPrivateKey() {
        return privateKey;
    }

    private byte[] createNonce() {
        byte[] nonce = new byte[32];
        Random random = new SecureRandom();
        random.nextBytes(nonce);

        return nonce;
    }

    private void verifyNonce(byte[] first, byte[] second) throws InvalidNonceException {

        if(first == null || second == null) return;

        if(!Arrays.equals(first, second)) {
            throw new InvalidNonceException();
        }
    }

    private void verifyUserId(String first, String second) throws InvalidUserIDException {
        if(!first.equals(second)) {
            throw new InvalidUserIDException();
        }
    }

    private void verifyLifetime(Date date) throws InvalidLifetimeException {

        // If currentDate is greater than Date, throw Exception

        Date currentDate = new Date();

        if(currentDate.compareTo(date) > 0) {
            throw new InvalidLifetimeException();
        }

    }

    private void verifyTimestamp(Date date) throws InvalidTimeStampException {
        // Check if date is within the last minute

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        Date oneMinuteBack = cal.getTime();

        // if date is before oneMinute, throw

        // The moment of receiving is one minute after moment of sending
        if(date.before(oneMinuteBack)) {
            throw new InvalidTimeStampException();
        }
    }

    private void verifyIncomingConnection(List<Object> messages) {
        UserToUserEncryptedMessage firstMsg = (UserToUserEncryptedMessage) messages.get(0);
        KDCToUserEncryptedMessage secondMsg = (KDCToUserEncryptedMessage) messages.get(1);

        KDCToUserPlainMessage secondMsgDecrypted = AES.decrypt(secondMsg, this.privateKey);
        SecretKey sessionKey = secondMsgDecrypted.getKey();

        System.out.println("Receiver session key: " + Base64.getEncoder().encodeToString(sessionKey.getEncoded()));

        UserToUserPlainMessage firstMsgDecrypted = AES.decrypt(firstMsg, sessionKey);

        this.verifyUserId(firstMsgDecrypted.getFromId(), secondMsgDecrypted.getId());
        this.verifyLifetime(secondMsgDecrypted.getLifetime());
        this.verifyTimestamp(firstMsgDecrypted.getTimestamp());
    }

    private void establishConnectionWith(User user) {
        String userId = user.getID();

        byte[] nonce = this.createNonce();
        UserToKDCRequestMessage msg = new UserToKDCRequestMessage(
                this.ID,
                userId,
                nonce
        );

        // Receives yA, yB from server
        // yA is for me, yB is to resend to the person I want to communicate with

        List<KDCToUserEncryptedMessage> fromKDC = this.server.receiveRequest(msg);
        KDCToUserEncryptedMessage firstMsg = fromKDC.get(0);
        KDCToUserEncryptedMessage secondMsg = fromKDC.get(1);

        KDCToUserPlainMessage firstMsgDecrypted = AES.decrypt(firstMsg, this.privateKey);

        this.verifyNonce(nonce, firstMsgDecrypted.getNonce());
        this.verifyUserId(userId, firstMsgDecrypted.getId());
        this.verifyLifetime(firstMsgDecrypted.getLifetime());

        Date timeStamp = new Date();

        UserToUserEncryptedMessage userToUserMsg = AES.encrypt(
                new UserToUserPlainMessage(
                    this.getID(),
                        timeStamp
                ),
                firstMsgDecrypted.getKey()
        );

        System.out.println("Sender session key: " + Base64.getEncoder().encodeToString(firstMsgDecrypted.getKey().getEncoded()));

        // Now send userToUser and secondMsg to BOB

        List<Object> userToUserMessages = new ArrayList();

        userToUserMessages.add(userToUserMsg);
        userToUserMessages.add(secondMsg);


        // Sending to Bob, if returns true, the connection is established
        // we both have a symmetric key
        user.verifyIncomingConnection(userToUserMessages);

        System.out.println("Connection can be established with: " + userId);
    }

    public void receiveMessage(User fromUser, byte [] encryptedMessage) {

        SecretKey sessionKey = this.server.getSessionKey(this, fromUser);

        String message = AES.decrypt(encryptedMessage, sessionKey);

        System.out.printf("---- %s ----\n", this.getID());
        System.out.println("Received message from: " + fromUser.getID());
        System.out.println("Message: " + message);
        System.out.println("-----------------------------\n");
    }

    public void sendMessage(User toUser, String message) {

        this.establishConnectionWith(toUser);

        SecretKey sessionKey = this.server.getSessionKey(this, toUser);

        byte[] encryptedMessage = AES.encrypt(message, sessionKey);

        System.out.printf("---- %s ----\n", this.getID());
        System.out.println("Sending message to: " + toUser.getID());
        System.out.println("Message: " + message);
        System.out.println("-----------------------------\n");

        toUser.receiveMessage(this, encryptedMessage);
    }

}
