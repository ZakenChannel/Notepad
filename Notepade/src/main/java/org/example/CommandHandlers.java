package org.example;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * The class that executes user commands
 */
public class CommandHandlers {
    private final NoteManager noteManager;
    private final Scanner scanner;

    public CommandHandlers(NoteManager noteManager, Scanner scanner) {
        this.noteManager = noteManager;
        this.scanner = scanner;
    }

    public void handleWrite() {
        System.out.print("Введите ваши планы на сегодня: ");
        try {
            String note = scanner.nextLine();
            noteManager.saveNote(LocalDate.now(), note);
            System.out.println();
        } catch (NullPointerException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void handleRead() {
        StringBuilder data = noteManager.loadNotes();
        if (data.isEmpty()) {
            System.out.println("Записная книжка пуста!\n");
            return;
        }
        System.out.println(data);
    }

    public void handleDeleteNote() {
        try {
            StringBuilder data = noteManager.loadNotes();
            if (data.isEmpty()) {
                System.out.println("Записная книжка пуста!\n");
                return;
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

    public void handleFind() {
        System.out.println(noteManager.loadNotes());
        System.out.print("Введите дату, за которую хотите получить записи: ");
        System.out.println(noteManager.getNotesByData(scanner.nextLine()));
    }

    public void handleStatistics() {
        System.out.println(noteManager.loadNotes());
        System.out.println(noteManager.getStatistic());
        System.out.println();
    }

    public void handleInfo() {
        System.out.println("""
                Список команд:
                1) #write - добавить новую запись в книжку
                2) #read - получение существующих записей
                3) #find - получение существующих записей по дате
                4) #statistics - получение статистики использования записной книжки
                5) #delete-note - удаление записи по порядковому номеру
                6) #close - завершить выполнение программы
                """);
    }
}