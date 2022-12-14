package run.util;

import run.*;

import java.io.File;

/**
 * вспомогателльный класс  со статическими методами создания менеджеров задач
 *
 * @see InMemoryTaskManager :
 * менеджер задач с хранением задач только в оператвной памяти
 * менеджер задач с возможностью сохранения задач и истории задач в csv-файл
 * @see TaskSaver;
 */
public class Managers {

    /**
     * получение менеджера задач (хранение задач только в оператвной памяти)
     *
     * @return объект менеджера
     * @see InMemoryTaskManager
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    /**
     * получение менеджера задач (сохранение задач и истории задач в csv-файл)
     *
     * @param file объект CSV-файла восстановления
     * @return объект менеджера
     * @see FileBackedTasksManager
     * @see TaskLoader - вспомогательный класс с методами восстановления задач из csv-файла
     */
    public static FileBackedTasksManager loadFromFile(File file) {
        try {
            return new FileBackedTasksManager(file);
        } catch (ManagerLoadException mle) {
            System.out.println(mle.getMessage());
        }
        return null;
    }

    /**
     * метод создания стандартного менеджера истории
     *
     * @return объект менеджера истории InMemoryHistoryManager
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}