/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/Cryptography.java-arc   1.0   Aug 10 2007 18:01:22   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/Cryptography.java-arc  $
 * 
 *    Rev 1.0   Aug 10 2007 18:01:22   makuier
 * Initial revision.
 * 
 *    Rev 1.0   Aug 06 2007 17:59:24   makuier
 * Initial revision.
 * 
 *    Rev 1.0   Apr 09 2003 09:34:32   goethalo
 * Initial revision.
*/
package net.arcor.bks.common;

/**
 * Class containing utilities for cryptology.
 * @author goethalo
 */
public final class Cryptography {

    /**
     * Decrypts a text encrypted by the CCB system.
     * @param encryptionKey    the encryption key
     * @param encryptedText    the encrypted text
     * @param doubleEncrypted  indicates whether the text is double-encrypted.
     * @return String          the decrypted text.
     * @throws FIFException if the text could not be decrypted.
     */
    public static String ccbDecrypt(String encryptionKey,
                                    String encryptedText,
                                    boolean doubleEncrypted)
            throws Exception {
        if (doubleEncrypted) {
            return (new CCBEncryptor(
                        new CCBEncryptor(
                            CCBEncryptor.getCcbDoubleEncryptKey()).decrypt(
                                encryptionKey))).decrypt(encryptedText);
        } else {
            return (new CCBEncryptor(encryptionKey).decrypt(encryptedText));
        }
    }

    /**
     * Encrypts a text using the CCB system encryption method.
     * @param encryptionKey    the encryption key to use
     * @param text             the text to encrypt
     * @return String          the enrypted text.
     * @throws FIFException if the text could not be encrypted.
     */
    public static String ccbEncrypt(String encryptionKey,
                                     String text)
            throws Exception {
        return (new CCBEncryptor(encryptionKey).encrypt(text));
    }

    /**
     * Encrypts akey using the CCB double-encryption method.
     * @param encryptionKey
     * @return String
     * @throws FIFException
     */
    public static String ccbDoubleEncryptKey(String encryptionKey)
            throws Exception {
        return (new CCBEncryptor(
                    CCBEncryptor.getCcbDoubleEncryptKey()).encrypt(
                        encryptionKey));
    }
}
