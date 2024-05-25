package org.example;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The NoteEncryptor class handles the encryption and decryption of notes
 */
public class NoteEncryptor {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String SPLIT_DELIMITER = " - ";
    private static final int IV_INDEX = 0;
    private static final int ENCRYPTED_DATE_INDEX = 1;
    private static final int ENCRYPTED_TEXT_INDEX = 2;
    private final Key key;

    public NoteEncryptor(Key key) {
        this.key = key;
    }

    public String encrypt(LocalDate date, String text) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] rnd = new byte[16];
        random.nextBytes(rnd);
        IvParameterSpec ivSpec = new IvParameterSpec(rnd);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encDate = cipher.doFinal(formatDate(date).getBytes());
        byte[] encText = cipher.doFinal(text.getBytes());

        return DatatypeConverter.printHexBinary(rnd) + SPLIT_DELIMITER +
                DatatypeConverter.printHexBinary(encDate) + SPLIT_DELIMITER +
                DatatypeConverter.printHexBinary(encText);
    }

    public String decrypt(String encryptedNote) throws Exception {
        String[] parts = encryptedNote.split(SPLIT_DELIMITER, 3);
        if (parts.length == 3) {
            String ivHex = parts[IV_INDEX];
            String encDateHex = parts[ENCRYPTED_DATE_INDEX];
            String encTextHex = parts[ENCRYPTED_TEXT_INDEX];

            byte[] iv = DatatypeConverter.parseHexBinary(ivHex);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] decDate = cipher.doFinal(DatatypeConverter.parseHexBinary(encDateHex));
            byte[] decText = cipher.doFinal(DatatypeConverter.parseHexBinary(encTextHex));

            String decryptedDate = new String(decDate);
            String decryptedText = new String(decText);

            return decryptedDate + SPLIT_DELIMITER + decryptedText;
        }
        throw new RuntimeException("Неверный формат записи");
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
