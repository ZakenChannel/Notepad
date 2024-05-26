package org.example.encryption;

import javax.crypto.KeyGenerator;
import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * The KeyManager class manages the generation and loading of the encryption key
 */
public class KeyManager {
    public static final String KEY_FILE_NAME = "secret.key";
    private static final String ALGORITHM = "AES";

    public static Key loadOrGenerateKey() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        File keyFile = new File(KEY_FILE_NAME);
        if (keyFile.exists()) {
            return loadKey();
        } else {
            KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
            keygen.init(256);
            Key key = keygen.generateKey();
            saveKey(key);
            return key;
        }
    }

    private static void saveKey(Key key) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(KEY_FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(key);
        }
    }

    private static Key loadKey() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(KEY_FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Key) ois.readObject();
        }
    }
}
