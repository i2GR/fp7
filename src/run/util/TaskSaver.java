package run.util;

import tasks.*;

import java.util.List;

/**
 * класс статических методов преобразования задач из менеджера и истории задач в текстовое представление
 * (вызывается из менеджера задач)
 *
 * @implNote для правильного сохранения задач в файл методы класса в менеджере задач могут быть вызываны в любой
 * последовательности:
 * @implNote Однако метод сохранении истории должен быть вызван последним
 * @see run.FileBackedTasksManager
 * Формат сохранения
 * - тип файла CSV, разделители - запятые, перевод строки: '\n'
 * @see TaskSaver#SEPARATOR
 * @see TaskSaver#ENDLINE
 * - файл имеет заголовок (легенду):
 * id,type,name,status,description,epic
 * <p>
 * 'id' - идентификатор
 * 'type' - тип задачи (Task, SubTask, EpicTask)
 * 'name' - название задачи
 * 'status' - сохраненный статус задачи
 * 'description' - описание задачи
 * 'epicId' - в случае SubTask - идентификатор Эпик-задачи, к которой относится подзадача
 * @see TaskSaver#HEADER
 * <p>
 * последующие строки после заголовка: текстовое представление задачи по указанному выше формату
 * поля задач сохранены через разделители-запятые
 * после последней сохраненной задачи сохраняется пустая строка
 * последняя строка в файле: список через разделители идентификаторов задач ,находящихся в истории в прямом порядке
 */
public class TaskSaver {
    public static final String HEADER = "id,type,name,status,description,start,duration,epic";
    private static final char SEPARATOR = ',';
    private static final char ENDLINE = '\n';

    /**
     * метод преобразования задачи в строку
     * подходит для преобразования задач тип Task и SubTask
     * (при преобразовании они отличаются всего одним параметром - идентификатором эпика для подзадачи
     *
     * @param task экземпляр задачи(/подзадачи)
     * @return строковое представление в заданном формате
     * @see Task
     * @see SubTask
     */
    public static String taskToString(Task task) {
        StringBuilder save = new StringBuilder();
        save.append(task.getId()).append(SEPARATOR);
        save.append(task.getTaskType().toString()).append(SEPARATOR);
        save.append(task.getName()).append(SEPARATOR);
        save.append(task.getStatus().toString()).append(SEPARATOR);
        save.append(task.getDescription()).append(SEPARATOR);
        save.append(task.getStartTime()).append(SEPARATOR);
        save.append(task.getDuration());
        if (task.getTaskType() == TaskType.SUBT) {
            save.append(SEPARATOR).append(((SubTask) task).getOverId());
        }
        return save.append(ENDLINE).toString();
    }

    /**
     * метод преобразования Эпик-задачи в строку
     *
     * @param epic   экземпляр эпика
     * @param status статус эпика, определенный непосредственно перед сохранением
     * @return строковое представление в заданном формате
     * @implNote технически сохранять статус-эпика не нужно, т.к. его статус определяется статусом входящих подзадач
     * но сохранение статуса регламентировано форматом
     * @see EpicTask
     */
    public static String epicTaskToString(EpicTask epic, String status) {
        StringBuilder save = new StringBuilder();
        save.append(epic.getId()).append(SEPARATOR);
        save.append(epic.getTaskType().toString()).append(SEPARATOR);
        save.append(epic.getName()).append(SEPARATOR);
        save.append(status).append(SEPARATOR);
        save.append(epic.getDescription()).append(SEPARATOR);
        save.append(epic.getStartTime()).append(SEPARATOR);
        save.append(epic.getDuration()).append(ENDLINE);
        return save.toString();
    }

    /**
     * метод преобразования списка задач в строку со списком идентификаторов задач в истории
     *
     * @param history список задач из метода менеджера задач
     * @return строковое представление списка идентификаторов
     * @see run.TaskManager#getHistory()
     */
    public static String historyToString(List<AbstractTask> history) {
        StringBuilder save = new StringBuilder("\n");
        if (history.isEmpty()) {
            return save.toString();
        }
        for (AbstractTask element : history) {
             save.append(element.getId()).append(SEPARATOR);
        }
        return save.deleteCharAt(save.length() - 1).toString();
    }
}
