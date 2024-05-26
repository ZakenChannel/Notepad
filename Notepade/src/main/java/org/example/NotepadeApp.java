package org.example;

import org.example.encryption.KeyManager;
import org.example.encryption.NoteEncryptor;
import org.example.service.CommandHandlers;
import org.example.service.NoteManager;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class NotepadeApp {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Key key = KeyManager.loadOrGenerateKey();
        NoteEncryptor noteEncryptor = new NoteEncryptor(key);
        NoteManager noteManager = new NoteManager(noteEncryptor);
        CommandHandlers commandHandlers = new CommandHandlers(noteManager, scanner);
        System.out.println("Для получения ифнормации о командах, напишите #info");

        while (true) {
            System.out.print("Введите команду: ");
            String command = scanner.nextLine();
            switch (command.trim()) {
                case "#write" -> commandHandlers.handleWrite();
                case "#read" -> commandHandlers.handleRead();
                case "#delete-note" -> commandHandlers.handleDeleteNote();
                case "#find" -> commandHandlers.handleFind();
                case "#statistics" -> commandHandlers.handleStatistics();
                case "#info" -> commandHandlers.handleInfo();
                case "#close" -> {
                    return;
                }
                default -> System.out.println("Неверная команда, используйте #info для получения списка команд\n");
            }
        }
    }
}