package simple.chatgpt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.ZeroSaltGenerator;
import org.junit.jupiter.api.Test;

/*
 hung: DONT REMOVE THIS COMMENT
 Test Jasypt encryption/decryption using constant for algorithm
 */
public class JasyptEncryptDecryptTest {

    private static final Logger logger = LogManager.getLogger(JasyptEncryptDecryptTest.class);

    // ================= Constants =================
    /*
    hung : dont remove it
    ///////////////////////////
    PBEWithMD5AndDES is an older password-based encryption (PBE) algorithm
    1>PBE = Password-Based Encryption
    1a>The key used to encrypt/decrypt is derived from a password, rather than 
    a fixed cryptographic key.
	1b>In Jasypt, you provide encryptor.setPassword("secret") — that’s your 
	PBE password.
	2>MD5
	MD5 is a hash function used to derive the encryption key from your password.
	MD5 itself is fast but weak, so modern recommendations often use SHA-256 
	or stronger key derivation functions.
	3>DES
	DES = Data Encryption Standard.
	It encrypts the data using the key derived from MD5.
	DES uses a 56-bit key, which is very weak by modern standards.
	4>PBEWithMD5AndDES is fine for learning/testing or hiding plaintext in XML, 
	but not recommended for serious production secrets, especially passwords.
	Use PBEWithHMACSHA512AndAES_256 (requires Java with unlimited strength crypto enabled).
    */
    private static final String PBE_ALGORITHM = "PBEWithMD5AndDES";
    private static final String MASTER_PASSWORD = "MySecretKey123";

    @Test
    public void testEncryptDecrypt() {
        logger.debug("testEncryptDecrypt START");

        String plainPassword = "ZAQ!zaq1";
        logger.debug("Plain password={}", plainPassword);

        // 1️⃣ Configure encryptor
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(PBE_ALGORITHM);
        encryptor.setPassword(MASTER_PASSWORD);
        // use deterministic encryption by adding a ZeroSaltGenerator
        encryptor.setSaltGenerator(new ZeroSaltGenerator()); // deterministic encryption

        // 2️⃣ Encrypt
        String encrypted = encryptor.encrypt(plainPassword);
        logger.debug("Encrypted password={}", encrypted);

        // 3️⃣ Decrypt
        String decrypted = encryptor.decrypt(encrypted);
        logger.debug("Decrypted password={}", decrypted);

        // 4️⃣ Assert
        assertEquals(plainPassword, decrypted);

        logger.debug("testEncryptDecrypt DONE");
    }
}
