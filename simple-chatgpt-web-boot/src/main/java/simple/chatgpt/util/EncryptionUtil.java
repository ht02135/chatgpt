package simple.chatgpt.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.ZeroSaltGenerator;

public class EncryptionUtil {

    private static final Logger logger = LogManager.getLogger(EncryptionUtil.class);

    private static final String PBE_ALGORITHM = "PBEWithMD5AndDES";
    private static final String MASTER_PASSWORD = "MySecretKey123";
    private static final String ENC_PREFIX = "ENC(";
    private static final String ENC_SUFFIX = ")";

    private static final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

    static {
        encryptor.setAlgorithm(PBE_ALGORITHM);
        encryptor.setPassword(MASTER_PASSWORD);
        // use deterministic encryption by adding a ZeroSaltGenerator
        encryptor.setSaltGenerator(new ZeroSaltGenerator()); // deterministic encryption
        logger.debug("EncryptionUtil initialized with algorithm={} and master password", PBE_ALGORITHM);
    }

    // Encrypts and wraps in ENC(...)
    public static String encrypt(String plainText) {
        logger.debug("encrypt plainText={}", plainText);
        if (plainText == null) return null;

        try {
            String encrypted = encryptor.encrypt(plainText);
            logger.debug("encrypt encrypted={}", encrypted);

            String wrapped = ENC_PREFIX + encrypted + ENC_SUFFIX;
            logger.debug("encrypt wrapped={}", wrapped);
            return wrapped;
        } catch (Exception e) {
            logger.error("Encryption failed for plainText={}", plainText, e);
            return null;
        }
    }

    // Decrypts string, removing ENC(...), quotes, and trimming
    public static String decrypt(String encryptedText) {
        logger.debug("decrypt encryptedText={}", encryptedText);
        if (encryptedText == null) return null;

        try {
            String toDecrypt = encryptedText.trim();

            // Remove surrounding quotes if any
            if ((toDecrypt.startsWith("\"") && toDecrypt.endsWith("\"")) ||
                (toDecrypt.startsWith("'") && toDecrypt.endsWith("'"))) {
                toDecrypt = toDecrypt.substring(1, toDecrypt.length() - 1).trim();
                logger.debug("decrypt removed quotes, toDecrypt={}", toDecrypt);
            }

            // Remove ENC(...) wrapper if present
            if (toDecrypt.startsWith(ENC_PREFIX) && toDecrypt.endsWith(ENC_SUFFIX)) {
                toDecrypt = toDecrypt.substring(ENC_PREFIX.length(), toDecrypt.length() - ENC_SUFFIX.length()).trim();
                logger.debug("decrypt removed ENC wrapper, toDecrypt={}", toDecrypt);
            }

            String decrypted = encryptor.decrypt(toDecrypt);
            logger.debug("decrypt decrypted={}", decrypted);
            return decrypted;
        } catch (Exception e) {
            logger.error("Decryption failed for encryptedText={}", encryptedText, e);
            return null;
        }
    }

    // Convenience method to check if a string is wrapped ENC(...)
    public static boolean isEncrypted(String text) {
        return text != null && text.startsWith(ENC_PREFIX) && text.endsWith(ENC_SUFFIX);
    }
}
