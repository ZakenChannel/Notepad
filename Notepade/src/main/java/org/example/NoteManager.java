package org.example;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * The NoteManager class manages notes in a notebook. It provides methods to save, load, delete, and find notes, as well as to get statistics about the notes
 */
public class NoteManager {
    public static final String FILE_NAME = "storage.txt";
    private final NoteEncryptor encryptor;

    public NoteManager(NoteEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public void saveNote(LocalDate date, String text) {
        if (date != null && !text.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                String encryptedNote = encryptor.encrypt(date, text);
                writer.write(encryptedNote);
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
                String decryptedNote = encryptor.decrypt(line);
                stringBuilder.append(decryptedNote).append("\n");
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
            if (data.split(NoteEncryptor.SPLIT_DELIMITER)[0].equals(date.trim())) {
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

        for (String word : list) {
            for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
                if (word.charAt(letterIndex) == ' ') continue;
                count++;
            }
        }

        stringBuilder.append("Кол-во символов (без пробелов): ").append(count).append("\n");

        Map<String, Integer> dateCounts = new HashMap<>();

        for (String word : list) {
            String date = word.split(NoteEncryptor.SPLIT_DELIMITER)[0].trim();
            dateCounts.put(date, dateCounts.getOrDefault(date, 0) + 1);
            count += word.length();
        }

        String mostActiveDay = dateCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Нет записей");

        stringBuilder.append("Самый активный день: ").append(mostActiveDay).append("\n");

        return stringBuilder;
    }
}
