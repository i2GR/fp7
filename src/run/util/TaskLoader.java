package run.util;

import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * вспомогательный класс с методами восстановления задач из csv-файла
 * восстановление:
 * - объектов задачи трех типов
 *
 * @see Task
 * @see SubTask
 * @see EpicTask
 * - помещение их в структуры хранения задач каждого типа (HashMap)
 * - история задач с преобразованием из сведений об идентификаторах задач в в кастомный
 * LinkedHashSet<AbstractTask>
 * @see run.HistoryManager
 * - значение максимального id задачи
 * <p>
 * восстановление данных требует их предварительного хранения
 * поэтому для восстановления необходимо создавать экземпляр класса
 */
public class TaskLoader {

    private final File file;

    /**
     * последний id сохраненной задачи для восстановления в менеджере
     */
    private int maxId;
    private final List<AbstractTask> history;

    /**
     * общая HashMap для сохранения экземпляров задач всех типов
     */
    private final HashMap<Integer, AbstractTask> idMapAbstract = new HashMap<>();


    /**
     * конструктор
     * @param file файл сохранения задач и истории
     */
    public TaskLoader(File file) {
        this.file = file;
        // параллельно заполняется idMapAbstract
        this.history = addItemAndRestoreHistory(TaskLoader.getRawData(file));
    }

    /**
     * конструктор для тестирования
     * @param lines лист со строками с данными о задачах и истории
     */
    public TaskLoader(List<String> lines) {
        this.file = null;
        this.history = addItemAndRestoreHistory(lines);
    }

    /**
     * метод получения массива (String[]) строк из файла
     *
     * @param file csv-файл восстановления
     * @return массив строк, каждый элемент соответствует строке в файле (разделитель строк: '\n')
     * @throws ManagerLoadException - ошибки чтения или загрузки из файла
     * @see TaskSaver для лписания формата сохранения
     */
    public static List<String> getRawData(File file) throws ManagerLoadException {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
            if (lines == null || lines.size() == 0) {
                throw new ManagerLoadException("Ошибка загрузки back-файла. Файл пуст");
            }
        } catch (IllegalArgumentException iae) {
            throw new ManagerLoadException("Ошибка загрузки back-файла. Необходима проверка имени файла");
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения содержимого back-файла.");
        }
        return lines;
    }

    /**
     * Метод-оболочка восстановления задач из массива строк, полученных в методе
     * {@link TaskLoader#getRawData(File file)}
     * предварительно сохраняет задачи в общей HashMap<Integer, AbstractTask> (см. далее)
     *
     * @param lines         - массив строк содержимого файла
     * @return история задач в виде списка List<AbstractTask> (объекты задач)
     * полчаемая как результат выполнения основного метода восстановления задач из массива строк
     * {@link TaskLoader#iterateLines(List, int)}
     * @throws ManagerLoadException - перед обработкой проверяется "первая строка" файла (первый элемент lines)
     *                              как предварительная проверка формата данных
     */
    public List<AbstractTask> addItemAndRestoreHistory (List<String> lines) throws ManagerLoadException {
        final int FIRST_ROW_INDEX = 0;
        if (!lines.get(FIRST_ROW_INDEX).equals(TaskSaver.HEADER)) {
            throw new ManagerLoadException("Неверный заголовок back-файла");
        }
        return iterateLines(lines, FIRST_ROW_INDEX + 1);
    }

    public List<AbstractTask> getHistory() {
        return history;
    }

    public File getFile() {
        return file;
    }

    /**
     * восстановление HashMap хранения <b>обычных задач</b>
     *
     * @return HashMap хранения обычных задач для передавчи в конструктор менеджера задач
     * @see TaskLoader#addItemAndRestoreHistory
     * )
     * @see run.FileBackedTasksManager
     * для преобразования значений структуры HashMap<Integer, AbstractTask> в нужный тип используется вспомогательный
     * метод
     * @see TaskLoader#castToType(Map, TaskType)
     */
    public HashMap<Integer, Task> getTasks() {
        HashMap<Integer, Task> idMapTask = new HashMap<>();
        castToType(idMapTask, TaskType.NORM);
        return idMapTask;
    }

    /**
     * восстановление HashMap хранения <b>подзадач</b>
     *
     * @return HashMap хранения позадач для передавчи в конструктор менеджера задач
     * @see TaskLoader#addItemAndRestoreHistory
     * )
     * @see run.FileBackedTasksManager
     * для преобразования значений структуры HashMap<Integer, AbstractTask> в нужный тип используется вспомогательный
     * метод
     * @see TaskLoader#castToType(Map, TaskType)
     */
    public HashMap<Integer, SubTask> getSubs() {
        HashMap<Integer, SubTask> idMapSub = new HashMap<>();
        castToType(idMapSub, TaskType.SUBT);
        return idMapSub;
    }

    /**
     * восстановление HashMap хранения <b>Эпик-задач</b>
     *
     * @return - HashMap хранения эпиков для передавчи в конструктор менеджера задач
     * @see TaskLoader#addItemAndRestoreHistory
     * )
     * @see run.FileBackedTasksManager
     * для преобразования значений структуры HashMap<Integer, AbstractTask> в нужный тип используется вспомогательный
     * метод
     * @see TaskLoader#castToType(Map, TaskType)
     */
    public HashMap<Integer, EpicTask> getEpics() {
        HashMap<Integer, EpicTask> idMapEpic = new HashMap<>();
        castToType(idMapEpic, TaskType.EPIC);
        return idMapEpic;
    }

    /**
     * согласование восстановленных <b>Эпик-задач</b> и соответствующих восстановленных <b>подзадач</b>
     * перед передачей в конструктор менеджера задач
     *
     * @param idMapSubs  HashMap хранения эпиков
     * @param idMapEpics HashMap хранения позадач
     * @see run.FileBackedTasksManager
     */
    public void attachSubs(Map<Integer, SubTask> idMapSubs, Map<Integer, EpicTask> idMapEpics) {
        for (SubTask sub : idMapSubs.values()) {
            idMapEpics.get(sub.getOverId()).addSubTask(sub);
        }

    }

    /**
     * Общий метод восстановления задач из массива строк, полученных в методе
     * {@link TaskLoader#getRawData(File file)}
     *
     * @param lines         массив строк содержимого файла
     * @param index         индекс обработываемого элемента массива lines
     *                      чтобы не было "магического числа"
     * @return история задач в виде списка List<AbstractTask> (объекты задач)
     * <p>
     * Общий алгоритм:
     * - метод рекурсивно проходит по массиву строк, полученных из файла сохранения
     * - если переданный массив строк пустой возвращается пустой список
     * - если элемент массива с текущим индексом - пустая строка, метод прошелся по всем сохраненным
     * задачам.
     * Ожидается, что следующий элемент содержит историю. вызывается метод преобразования этой строки в
     * лист с целочисленными значениями
     * @see TaskLoader#fromString(String)
     * - обычный цикл выполнения метода подразуемевает сохранение строки как экземпляр AbstractTask
     * в HashMap
     */
    private List<AbstractTask> iterateLines(List<String> lines, int index) {
        if (lines.isEmpty() || lines.size() == 1) {
            return new ArrayList<>();
        }
        if (index == lines.size()) {
            return new ArrayList<>();
        }
        if (lines.get(index).equals("")) {
            try {
                String historyLine = lines.get(index + 1);
                List<Integer> ids = historyFromString(historyLine);
                return restoreHistory(ids);
            } catch (IndexOutOfBoundsException e) {
                return new ArrayList<>();
            }
        } else {
            String rawData = lines.get(index);
            try {
                AbstractTask abs = fromString(rawData);
                int id = abs.getId();
                idMapAbstract.put(id, abs);
                maxId = updateMaxId(id);
            } catch (ManagerLoadException mle) {
                System.out.println(mle.getMessage());
                // пропустить строку
            }
            return iterateLines(lines, index + 1);
        }
    }

    /**
     * метод преобразования тектовых данных в объект класса AbstractTask
     * для предварительного сохранения
     * этап 1: преобразование строки в массив строк по количесству сохраненных параметров и приведение параметров к
     * необходимому типу данных
     * этап 2: создание объекта задач из сохраненных параметров
     *
     * @param value строка для обработки
     * @return объект список задачи
     * @throws ManagerLoadException - в случае невозможности преобразования данных из строки к заданному типу
     *                              - в случае возможного неправильного формата данных (количество полей к обработке)
     * @see TaskLoader#fromParameters(int, TaskType, String, Status, String, int)
     */
    private AbstractTask fromString(String value) throws ManagerLoadException {
        String[] savedData = value.split(",");
        if (!(savedData.length == 7 || savedData.length == 8)) {
            throw new ManagerLoadException("Ошибка формата сохраненной задачи:[" + value + ']');
        }
        try {
            int id = Integer.parseInt(savedData[0]);
            TaskType type = TaskType.valueOf(savedData[1].toUpperCase().trim());
            String name = savedData[2];
            Status status = Status.valueOf(savedData[3].toUpperCase().trim());
            String description = savedData[4];
            int epicId = (savedData.length == 8) ? Integer.parseInt(savedData[7]) : -1;
            AbstractTask abs = fromParameters(id, type, name, status, description, epicId);
            if (abs == null) {
                throw new ManagerLoadException("Ошибка восстановления задачи из параметров: " + Arrays.toString(savedData));
            }
            return abs;
        } catch (NumberFormatException nfe) {
            throw new ManagerLoadException("Ошибка восстановления id:" + savedData[0]);
        } catch (IllegalArgumentException iae) {
            throw new ManagerLoadException("Ошибка восстановления id или статуса");
        }
    }

    /**
     * создание задачи как объекта заданного класса (Task, SubTask, EpicTask) bиз параметров
     *
     * @param id          - идентификатор
     * @param type        - тип задачи (Task, SubTask, EpicTask)
     * @param name        - название задачи
     * @param status      - сохраненный статус задачи
     * @param description - описание задачи
     * @param epicId      - в случае SubTask - идентификатор Эпик-задачи, к которой относится подзадача
     * @return - объект задачи как переменная типа AbstractTask (приведение в последующих методах)
     */
    private AbstractTask fromParameters(int id, TaskType type, String name,
                                        Status status, String description, int epicId) {
        switch (type) {
            case NORM:
                AbstractTask task = setNameAndDescr(new Task(id), name, description);
                ((Task) task).setStatus(status);
                return task;
            case SUBT:
                AbstractTask subTask = setNameAndDescr(new SubTask(id, epicId), name, description);
                ((SubTask) subTask).setStatus(status);
                return subTask;
            case EPIC:
                return setNameAndDescr(new EpicTask(id), name, description);
        }
        return null;
    }

    /**
     * метод (предусмотрен ТЗ) непосредственной обработки строки файла, сожержащей историю задач
     *
     * @param row строка с данными
     * @return список идентификаторов задач в том же порядке, в котором они сохранены в файле
     */
    private List<Integer> historyFromString(String row) {
        List<Integer> ids = new ArrayList<>();
        String[] stringIds = row.split(",");
        if (stringIds.length == 0) {
            return ids;
        }
        for (String id : stringIds) {
            try {
                ids.add(Integer.parseInt(id));
            } catch (NumberFormatException nfe) {
                // пропустить добавление элемента
                // ?? лучше загрузить частично, чем ничего не восстановить?
            }
        }
        return ids;
    }

    /**
     * метод создания списка задач в истории по списку id в истории
     * списко задач нужен для непосредственной передачи в конструктор менеджера
     * для использования в нем непереопределенного метода AddToHistory(AbstractTask)
     * (с целью НЕ созадавть доп. методы в классах менеджера)
     *
     * @param ids           восстановленный список идентификуаторов задач
     * @return список задач
     * @see run.FileBackedTasksManager
     */

    private List<AbstractTask> restoreHistory(List<Integer> ids) {
        List<AbstractTask> history = new ArrayList<>();
        for (Integer id : ids) {
            history.add(idMapAbstract.get(id));
        }
        return history;
    }

    /**
     * вспомогательный метод преобразования объектов задач к нужному типу
     *
     * @param map    экземпляр с интерфейсом Map, в который импортируются данные
     * @param type   тип объекта
     * @param <T>    класс в экз. которого преобразуется экземпляр задачи (Task, SubTask, EpicTask)
     */

    private <T extends AbstractTask> void castToType
    (Map<Integer, T> map, TaskType type) {
        for (AbstractTask item : idMapAbstract.values()) {
            if (item.getTaskType() == type) {
                map.put(item.getId(), (T) item);
            }
        }
    }

    /**
     * обновление максимального значения идентификатора среди сохраненных задач
     *
     * @param value идентификатор текущей задачи
     * @return обновленное максимальное значение идентификатора
     */
    private int updateMaxId(int value) {
        return Math.max(value, maxId);
    }

    /**
     * вспомогательный метод добавление в задачу любого типа названия и описания
     *
     * @param item        объект задачи (один из типов Task, SubTask, AbstractTask)
     * @param name        название задачи
     * @param description описание задачи
     * @param <T>         кдасс задачи
     * @return обновленный объект задачи с сохраненным названием/описанием
     */
    private <T extends AbstractTask> T setNameAndDescr(T item, String name, String description) {
        item.setNameForTest(name);
        item.setDescriptionForTest(description);
        return item;
    }

    public int getMaxId() {
        return maxId;
    }
}
