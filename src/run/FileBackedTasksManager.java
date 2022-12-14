package run;

import run.util.ManagerLoadException;
import run.util.Managers;
import run.util.TaskLoader;
import run.util.TaskSaver;
import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * класс менеджера, который после каждой операции автоматически сохраняет все задачи и их состояние в специальный файл.
 * реализует интерфейс TaskManager
 *
 * @see TaskManager
 * имеет такую же система классов и интерфейсов, как и InMemoryHistoryManager.
 * является наследником InMemoryHistoryManager
 * @see InMemoryHistoryManager
 * отличается реализацией сохранения данных в файл
 * @see TaskSaver
 */
public class FileBackedTasksManager extends InMemoryTaskManager {

     private final File saveFile;

    /**
     * Конструктор пустого менеджера, принимающий имя файла для сохранения
     * @param fileName мя файла для сохранения
     * @throws ManagerLoadException ошибка загрузки из файла (см. док-цию пои исключению)
     *
     */
    public FileBackedTasksManager(String fileName) {
        super(Managers.getDefaultHistory());
        saveFile = new File(fileName);
        save();
    }

    /**
     * Конструктор, принимающий зааранее обработанные данные из файла сохранения
     *
     * @param file экз. файл предварительно сохраненными данными о задачах
     * @implNote список сохраняется в файл в прямом порядке.
     * для восстановления используется метод интерфейса addToHistory()
     * (специальный метод восстановления не создавался в классе родителе
     * поэтому перед восстановлением history реверсируется
     * @see Managers#loadFromFile(File)
     * @see Task
     * @see SubTask
     * @see EpicTask
     */
    public FileBackedTasksManager(File file) throws ManagerLoadException {
        super(Managers.getDefaultHistory());
        TaskLoader taskLoader = new TaskLoader(file);
        ((Map<Integer, Task>) super.getTaskMap(TaskType.NORM)).putAll(taskLoader.getTasks());
        ((Map<Integer, SubTask>) super.getTaskMap(TaskType.SUBT)).putAll(taskLoader.getSubs());
        ((Map<Integer, EpicTask>) super.getTaskMap(TaskType.EPIC)).putAll(taskLoader.getEpics());
        taskLoader.attachSubs((Map<Integer, SubTask>) super.getTaskMap(TaskType.SUBT),
                              (Map<Integer, EpicTask>) super.getTaskMap(TaskType.EPIC));
        this.saveFile = taskLoader.getFile();
        setIdForNewTask(taskLoader.getMaxId());
        List<AbstractTask> loadedHistory  = taskLoader.getHistory();
        Collections.reverse(loadedHistory);
        loadedHistory.forEach(t -> addToHistory(t));
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(EpicTask epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSub(SubTask sub) {
        int id = super.addNewSub(sub);
        save();
        return id;
    }

    @Override
    public int updateTask(Task task) {
        int id = super.updateTask(task);
        save();
        return id;
    }

    @Override
    public int updateEpic(EpicTask epic) {
        int id = super.updateEpic(epic);
        save();
        return id;
    }

    @Override
    public int updateSub(SubTask sub) {
        int id = super.updateSub(sub);
        save();
        return id;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubs() {
        super.deleteAllSubs();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubById(int id) {
        super.deleteSubById(id);
        save();
    }

    @Override
    public void clearSubsListOfEpic(int id) {
        super.clearSubsListOfEpic(id);
        save();
    }

    @Override
    public int addToHistory(AbstractTask task) {
        int id = super.addToHistory(task);
        save();
        return id;
    }

     /**
     * метод сохранения задач в файл
     * данные из каждой структуры HashMap преобразуются в строковое представление
     *
     * @see TaskSaver#taskToString(Task) - преобразование задач и подзадач
     * @see TaskSaver#epicTaskToString(EpicTask, String) - преобразование эпик-задач
     * @see TaskSaver#historyToString(List)
     * Описание формата сохранения:
     * @see TaskSaver
     */
    private void save() {
        List<Task> tasksAndSubs = new ArrayList<>();
        // создание заголовка CSV
        StringBuilder save = new StringBuilder(TaskSaver.HEADER + '\n');
        List<EpicTask> epics = getAllEpics();
        // сохранение данных о текущих статусах эпиков (необязательно)
        HashMap<Integer, Status> epicIdMapStatus = getEpicsStatuses(epics);
        tasksAndSubs.addAll(getAllTasks());
        tasksAndSubs.addAll(getAllSubs());
        // сохранение задач/подзадач и эпиков может быть в любом порядке
        // т.к. статус Эпик-задачи определеяется статусом входящих подзадач,
        // эпик-задачи можно сохранять со статусом Status.N_A
        // статус эпик-задачи может быть восттановлен по статусу под-задач при восстановлении из файла
        for (Task t : tasksAndSubs) {
            save.append(TaskSaver.taskToString(t));
        }
        for (EpicTask e : epics) {
            Status status = epicIdMapStatus.get(e.getId());
            save.append(TaskSaver.epicTaskToString(e, status.toString()));
        }
        // история должна быть преобразована в последнюю очередь для следования формату
        save.append(TaskSaver.historyToString(getHistory()));
        saveToFile(saveFile, save.toString());
    }

    /**
     * метод записи сохраненных данных в файл
     *
     * @param file файл для записи
     * @param data преобразованные строковые данные
     */
    private void saveToFile(File file, String data) {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(data);
        } catch (IOException ioe) {
            System.out.println("Ошибка записи в файл");
        }
    }

    /**
     * вспомогательный метод получения списка статусов эпик-задач для сохранения в файл
     * (необязательно)
     *
     * @param epics структура HashMap с эпик-задачами
     * @return структура HashMap идентификатор эпик-задачи <> статус эпик-задачи
     */
    private HashMap<Integer, Status> getEpicsStatuses(List<EpicTask> epics) {
        HashMap<Integer, Status> epicIdMapStatus = new HashMap<>();
        for (EpicTask e : epics) {
            epicIdMapStatus.put(e.getId(), getEpicStatus(e.getId()));
        }
        return epicIdMapStatus;
    }
}
