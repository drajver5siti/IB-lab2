import Exceptions.UnknownUserException;
import Messages.KDCToUserEncryptedMessage;
import Messages.KDCToUserPlainMessage;
import Messages.UserToKDCRequestMessage;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class KDCServer {
    private Map<String, SecretKey> keys;

    private SecretKey masterKey;

    public KDCServer(SecretKey masterKey) {
        this.masterKey = masterKey;
        this.keys = new HashMap<>();
    }

    public void addUser(User user) {
        keys.put(user.getID(), user.getPrivateKey());
    }

    public List<KDCToUserEncryptedMessage> receiveRequest(UserToKDCRequestMessage msg) {
        if(!this.hasUser(msg.getFromID())) {
            throw new UnknownUserException(msg.getFromID());
        }

        if(!this.hasUser(msg.getToID())) {
            throw new UnknownUserException(msg.getToID());
        }

        SecretKey sessionKey = KDCServer.deriveSessionKey(this.masterKey, msg.getNonce());
        Date lifetime = this.generateLifetime();

        this.addSessionKey(msg.getFromID(), msg.getToID(), sessionKey);

        SecretKey fromSecretKey = this.keys.get(msg.getFromID());
        SecretKey toSecretKey = this.keys.get(msg.getToID());

        KDCToUserEncryptedMessage fromReturnMsg = AES.encrypt(
                new KDCToUserPlainMessage(
                   sessionKey,
                   lifetime,
                   msg.getToID(),
                   msg.getNonce()
                ),
                fromSecretKey
        );

        KDCToUserEncryptedMessage toReturnMsg = AES.encrypt(
                new KDCToUserPlainMessage(
                        sessionKey,
                        lifetime,
                        msg.getFromID(),
                        null
                ),
                toSecretKey
        );


        List<KDCToUserEncryptedMessage> res = new ArrayList<>();
        res.add(fromReturnMsg);
        res.add(toReturnMsg);

        return res;
    }

    public SecretKey getSessionKey(User first, User second) {
        StringBuilder key = new StringBuilder("");

        if(first.getID().compareTo(second.getID()) < 0) {
            key.append(first.getID());
            key.append(second.getID());
        } else {
            key.append(second.getID());
            key.append(first.getID());
        }

        return this.keys.get(key.toString());
    }

    private static byte[] XOR(byte[] first, byte[] second) {
        byte[] result = new byte[first.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = (byte) (first[i] ^ second[i]);
        }

        return result;
    }

    private void addSessionKey(String first, String second, SecretKey sessionKey) {
        StringBuilder key = new StringBuilder("");

        if(first.compareTo(second) < 0) {
            key.append(first);
            key.append(second);
        } else {
            key.append(second);
            key.append(first);
        }

        this.keys.put(key.toString(), sessionKey);
    }

    private static SecretKey deriveSessionKey(SecretKey key, byte[] nonce) {
        return key;

//        byte[] keyEncoded = key.getEncoded();
//        byte[] XORed = KDCServer.XOR(keyEncoded, nonce);
//
//        SecretKeySpec secretKeySpec = new SecretKeySpec(XORed, "HmacSHA256");
//
//        try {
//            Mac mac = Mac.getInstance("HmacSHA256");
//            mac.init(secretKeySpec);
//            byte[] derived = mac.doFinal();
//            return new SecretKeySpec(derived, 0, derived.length, AES.algorithm);
//
//        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return key;
    }

    private static void byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray)
        {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());
    }

    private Date generateLifetime() {
        Calendar date = Calendar.getInstance();

        long time = date.getTimeInMillis();

        // 600 minutes
        return new Date(time + 36000000);
    }

    private boolean hasUser(String userID)
    {
        return this.keys.containsKey(userID);
    }



}
