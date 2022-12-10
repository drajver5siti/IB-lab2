import Messages.KDCToUserPlainMessage;
import Messages.KDCToUserEncryptedMessage;
import Messages.UserToUserEncryptedMessage;
import Messages.UserToUserPlainMessage;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AES {

    public static String algorithm = "AES";

    private static byte[] _encryptKey(SecretKey key, SecretKey privateKey) {
        return AES._encryptBytes(key.getEncoded(), privateKey);
    }

    private static SecretKey _decryptKey(byte[] key, SecretKey privateKey) {
        byte[] decryptedBytes = AES._decryptBytes(key, privateKey);
        assert decryptedBytes != null;
        return new SecretKeySpec(decryptedBytes, 0, decryptedBytes.length, AES.algorithm);
    }

    private static byte[] _encryptDate(Date date, SecretKey privateKey) {
        return AES._encryptString(date.toString(), privateKey);
    }

    private static Date _decryptDate(byte[] date, SecretKey privateKey) {
        byte[] decryptedBytes = AES._decryptBytes(date, privateKey);
        assert decryptedBytes != null;
        String decryptedString = new String(decryptedBytes);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        try {
            return formatter.parse(decryptedString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static byte[] _encryptString(String str, SecretKey privateKey) {
        return AES._encryptBytes(str.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    private static String _decryptString(byte[] str, SecretKey privateKey) {
        byte[] decryptedBytes = AES._decryptBytes(str, privateKey);

        assert decryptedBytes != null;
        return new String(decryptedBytes);
    }

    private static byte[] _encryptBytes(byte[] bytes, SecretKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(AES.algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            return cipher.doFinal(bytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] _decryptBytes(byte[] bytes, SecretKey privateKey) {

        try {
            Cipher cipher = Cipher.getInstance(AES.algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(bytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UserToUserPlainMessage decrypt(UserToUserEncryptedMessage encryptedMessage, SecretKey privateKey) {

        return new UserToUserPlainMessage(
                AES._decryptString(encryptedMessage.getFromId(), privateKey),
                AES._decryptDate(encryptedMessage.getTimestamp(), privateKey)
        );
    }

    public static KDCToUserPlainMessage decrypt(KDCToUserEncryptedMessage encryptedMessage, SecretKey privateKey) {

        return new KDCToUserPlainMessage(
                AES._decryptKey(encryptedMessage.getKey(), privateKey),
                AES._decryptDate(encryptedMessage.getLifetime(), privateKey),
                AES._decryptString(encryptedMessage.getId(), privateKey),
                AES._decryptBytes(encryptedMessage.getNonce(), privateKey)
        );
    }

    public static UserToUserEncryptedMessage encrypt(UserToUserPlainMessage plainMessage, SecretKey privatekey) {

        return new UserToUserEncryptedMessage(
                AES._encryptString(plainMessage.getFromId(), privatekey),
                AES._encryptDate(plainMessage.getTimestamp(), privatekey)
        );
    }

    public static KDCToUserEncryptedMessage encrypt(KDCToUserPlainMessage plainMessage, SecretKey privateKey) {

        byte[] nonce = new byte[0];

        if(plainMessage.getNonce() != null) {
            nonce = plainMessage.getNonce();
        }

        return new KDCToUserEncryptedMessage(
                AES._encryptKey(plainMessage.getKey(), privateKey),
                AES._encryptDate(plainMessage.getLifetime(), privateKey),
                AES._encryptString(plainMessage.getId(), privateKey),
                nonce
        );
    }

    public static byte[] encrypt(String message, SecretKey privateKey) {
        return AES._encryptString(message, privateKey);
    }

    public static String decrypt(byte[] msg, SecretKey privateKey) {
        return AES._decryptString(msg, privateKey);
    }

}
