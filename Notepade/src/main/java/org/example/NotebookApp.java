package org.example;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Scanner;

public class NotebookApp {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        NoteManager noteManager = new NoteManager();
        System.out.println("Для получения ифнормации о командах, напишите #info");

        while (true) {
            System.out.print("Введите команду: ");
            String command = scanner.nextLine();
            switch (command.trim()) {
                case "#write" -> {
                    System.out.print("Введите ваши планы на сегодня: ");
                    try {
                        String note = scanner.nextLine();
                        noteManager.saveNote(LocalDate.now(), note);
                        System.out.println();
                    } catch (NullPointerException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                case "#read" -> {
                    StringBuilder data = noteManager.loadNotes();
                    if (data.isEmpty()) {
                        System.out.println("Записная книжка пуста!\n");
                        continue;
                    }

                    System.out.println(data);
                }
                case "#delete-note" -> {
                    try {
                        StringBuilder data = noteManager.loadNotes();

                        if (data.isEmpty()) {
                            System.out.println("Записная книжка пуста!\n");
                            continue;
                        }

                        System.out.print(data);
                        System.out.print("Введите порядковый номер записи, которую хотите удалить: ");
                        int index = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println();
                        noteManager.deleteNoteByIndex(index);
                    } catch (IndexOutOfBoundsException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                case "#find" -> {
                    System.out.println(noteManager.loadNotes());
                    System.out.print("Введите дату, за которую хотите получить записи: ");
                    System.out.println(noteManager.getNotesByData(scanner.nextLine()));
                }
                case "#statistics" -> {
                    System.out.println(noteManager.loadNotes());
                    System.out.println(noteManager.getStatistic());
                    System.out.println();
                }
                case "#info" -> System.out.println("""
                        Список комманд:
                        1) #write - добавить новую запись в книжку
                        2) #read - получение существующих записей
                        3) #find - получение существующих засписей по дате
                        4) #statistics - получение статистики использования записной книжки5) #delete-note - удаление записи по порядковому номеру
                        6) #close - завершить выполнение программы
                        """);
                case "#close" -> {
                    return;
                }
                default -> System.out.println("Неверная комманда, используйте #info для получения списк комманд\n");
            }
        }
    }
}