package org.example;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NoteManager {
    public static final String FILE_NAME = "storage.txt";
    public static final String KEY_FILE_NAME = "secret.key";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static Key key;

    public NoteManager() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        File keyFile = new File(KEY_FILE_NAME);
        if (keyFile.exists()) {
            key = loadKey();
        } else {
            KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
            keygen.init(256);
            key = keygen.generateKey();
            saveKey(key);
        }
    }

    public void saveNote(LocalDate date, String text) {
        if (date != null && !text.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                SecureRandom random = new SecureRandom();
                byte[] rnd = new byte[16];
                random.nextBytes(rnd);
                IvParameterSpec ivSpec = new IvParameterSpec(rnd);

                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
                byte[] encDate = cipher.doFinal(formatDate(date).getBytes());
                byte[] encText = cipher.doFinal(text.getBytes());

                writer.write(DatatypeConverter.printHexBinary(rnd) + " - " + DatatypeConverter.printHexBinary(encDate) + " - " + DatatypeConverter.printHexBinary(encText));
                writer.newLine();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            return;
        }

        throw new NullPointerException("Текст пустой, попробуйте еще раз");
    }

    public StringBuilder loadNotes() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ", 3);
                if (parts.length == 3) {
                    String ivHex = parts[0];
                    String encDateHex = parts[1];
                    String encTextHex = parts[2];

                    byte[] iv = DatatypeConverter.parseHexBinary(ivHex);
                    IvParameterSpec ivSpec = new IvParameterSpec(iv);

                    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
                    byte[] decDate = cipher.doFinal(DatatypeConverter.parseHexBinary(encDateHex));
                    byte[] decText = cipher.doFinal(DatatypeConverter.parseHexBinary(encTextHex));

                    String decryptedDate = new String(decDate);
                    String decryptedText = new String(decText);

                    stringBuilder.append(decryptedDate).append(" - ").append(decryptedText).append("\n");
                }
            }

            return stringBuilder;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void deleteNoteByIndex(int index) {
        List<String> notes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                notes.add(line);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        int adjustedIndex = index - 1;

        if (adjustedIndex >= 0 && adjustedIndex < notes.size()) {
            notes.remove(adjustedIndex);
        } else {
            throw new IndexOutOfBoundsException("Неверный индекс: " + index);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String note : notes) {
                writer.write(note);
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public StringBuilder getNotesByData(String date) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] list = loadNotes().toString().split("\n");

        for (String data : list) {
            if (data.split(" - ")[0].equals(date.trim())) {
                stringBuilder.append(data).append("\n");
            }
        }

        return stringBuilder;
    }

    public StringBuilder getStatistic() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> list = Arrays.asList(loadNotes().toString().split("\n"));
        int count = 0;
        stringBuilder.append("Кол-во записей: ").append(list.size()).append("\n");

        for (String s : list) {
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == ' ') continue;
                count++;
            }
        }

        stringBuilder.append("Кол-во символов (без пробелов): ").append(count).append("\n");

        Map<String, Integer> dateCounts = new HashMap<>();

        for (String s : list) {
            String date = s.split(" - ")[0].trim();
            dateCounts.put(date, dateCounts.getOrDefault(date, 0) + 1);
            count += s.length();
        }

        String mostActiveDay = dateCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Нет записей");

        stringBuilder.append("Самый активный день: ").append(mostActiveDay).append("\n");

        return stringBuilder;
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    private void saveKey(Key key) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(NoteManager.KEY_FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(key);
        }
    }

    private Key loadKey() throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(NoteManager.KEY_FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Key) ois.readObject();
        }
    }
}