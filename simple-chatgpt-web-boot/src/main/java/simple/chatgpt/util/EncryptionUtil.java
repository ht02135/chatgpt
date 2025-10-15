package simple.chatgpt.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

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
        logger.debug("EncryptionUtil initialized with algorithm={} and master password", PBE_ALGORITHM);
    }

    // Encrypts and wraps in ENC(...)
    public static String encrypt(String plainText) {
    	logger.debug("encrypt plainText={}", plainText);
    	
        if (plainText == null) return null;
        
        String encrypted = encryptor.encrypt(plainText);
        logger.debug("encrypt encrypted={}", encrypted);
        
        String wrapped = ENC_PREFIX + encrypted + ENC_SUFFIX;
        logger.debug("encrypt wrapped={}", wrapped);
        
        return wrapped;
    }

    // Decrypts string, removing ENC(...) if present
    public static String decrypt(String encryptedText) {
    	logger.debug("decrypt encryptedText={}", encryptedText);
    	
        if (encryptedText == null) return null;
        
        String toDecrypt = encryptedText;
        if (encryptedText.startsWith(ENC_PREFIX) && encryptedText.endsWith(ENC_SUFFIX)) {
            toDecrypt = encryptedText.substring(ENC_PREFIX.length(), encryptedText.length() - ENC_SUFFIX.length());
            logger.debug("decrypt toDecrypt={}", toDecrypt);
        }
        String decrypted = encryptor.decrypt(toDecrypt);
        logger.debug("decrypt decrypted={}", decrypted);
        return decrypted;
    }

    // Convenience method to check if a string is wrapped ENC(...)
    public static boolean isEncrypted(String text) {
        return text != null && text.startsWith(ENC_PREFIX) && text.endsWith(ENC_SUFFIX);
    }
}
