package test;

import org.junit.Before;
import org.junit.Test;
import run.util.ManagerLoadException;
import run.util.TaskLoader;
import tasks.AbstractTask;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

/**
 * тесты класса TaskLoader
 *
 * @see TaskLoader
 */
public class TaskLoaderTest {
    List<String> lines = new ArrayList<>();
    List<AbstractTask> expHistory = new ArrayList<>();
    List<AbstractTask> actHistory = new ArrayList<>();
    HashMap<Integer, Task> expTasks = new HashMap<>();
    HashMap<Integer, Task> actTasks = new HashMap<>();
    HashMap<Integer, SubTask> expSubs = new HashMap<>();
    HashMap<Integer, SubTask> actSubs = new HashMap<>();
    HashMap<Integer, EpicTask> expEpics = new HashMap<>();
    HashMap<Integer, EpicTask> actEpics = new HashMap<>();
    TaskLoader taskLoader;
    int expMaxId;

    /**
     * сохранение строк с данными о задачах в список строк
     * создание задач из вспомогательного класса создания задач для тестов
     *
     */
    @Before
    public void setUp() {
        lines.add("id,type,name,status,description,start,duration,epic");
        lines.add("2,SUBT,name 2,NEW,descr 2,2000-01-01T00:00,PT05M,3");
        lines.add("1,NORM,name 1,NEW,descr 1,2000-01-01T00:20,PT20M");
        lines.add("3,EPIC,name 3,NEW,descr 3,2000-01-01T00:40,PT20M");
        lines.add("4,SUBT,name 4,NEW,descr 4,2000-01-01T00:05,PT15M,3");
        lines.add("");
        lines.add("1,2,3");
        taskLoader = new TaskLoader(lines);
        expTasks.put(1, new Task(1, "name 1", "descr 1",
                        Duration.ofMinutes(20),
                        LocalDateTime.of(2000, 1, 1, 0, 20, 0)));
        expSubs.put(2, new SubTask(2, 3, "name 2", "descr 2",
                        Duration.ofMinutes(5),
                        LocalDateTime.of(2000, 1, 1, 0, 0, 0)));
        expSubs.put(4, new SubTask(4, 3, "name 4", "descr 4",
                Duration.ofMinutes(15),
                LocalDateTime.of(2000, 1, 1, 0, 5, 0)));
        expEpics.put(3, new EpicTask(3, "name 3", "descr 3",
                        new SubTask(4, 3, "name 4", "descr 4",
                        Duration.ofMinutes(15),
                        LocalDateTime.of(2000, 1, 1, 0, 5, 0)),
                        new SubTask(2, 3, "name 2", "descr 2",
                        Duration.ofMinutes(5),
                        LocalDateTime.of(2000, 1, 1, 0, 0, 0))));
        expHistory.add(expTasks.get(1));
        expHistory.add(expSubs.get(2));
        expHistory.add(expEpics.get(3));
        expMaxId = 4;
    }

    /**
     * тест метода addAbstractAndRestoreHistory
     *
     * @see TaskLoader#addItemAndRestoreHistory(List)
     * проверка соответствия истории ,полученнои из строки и истории полученной рабочими методами
     * проверка размера
     * проверка порядока идентифкаиторов
     */
    @Test
    public void addAbstractAndRestoreHistory() {
        try {
            actHistory = taskLoader.addItemAndRestoreHistory(lines);
        } catch (ManagerLoadException mle) {
            fail(mle.getMessage());
        }

        boolean condition = true;
        int index = -1;
        for (int i = 0; i < actHistory.size(); i++) {
            System.out.println(actHistory.get(i) + "<>" + expHistory.get(i));
            if (!actHistory.get(i).equals(expHistory.get(i))) {
                index = i;
                condition = false;
                break;
            }
        }

        assertEquals("History Size mismatch", actHistory.size(), expHistory.size());
        assertTrue("Failed @index: " + index, condition);
    }

    /**
     * тест метода restoreTasks
     *
     * @see TaskLoader#getTasks()
     * проверка успешности создания структуры HashMap
     * поэлементная проверка содержимого по идентификатору
     */
    @Test
    public void getTasks() {
        try {
            actHistory = taskLoader.addItemAndRestoreHistory(lines);
        } catch (ManagerLoadException mle) {
            fail(mle.getMessage());
        }

        actTasks = taskLoader.getTasks();

        // condition =-1 если не найдены несовпадающие задачи
        int condition = checkCondition(expTasks, actTasks);
        assertEquals("Task amount mismatch", actTasks.size(), expTasks.size());
        assertEquals("Failed @index: " + condition, -1, condition);
    }

    /**
     * тест метода restoreSubs
     *
     * @see TaskLoader#getSubs()
     * проверка успешности создания структуры HashMap
     * поэлементная проверка содержимого по идентификатору
     */
    @Test
    public void getSubs() {
        try {
            actHistory = taskLoader.addItemAndRestoreHistory(lines);
        } catch (ManagerLoadException mle) {
            fail(mle.getMessage());
        }

        actSubs = taskLoader.getSubs();

        // condition =-1 если не найдены несовпадающие задачи
        int condition = checkCondition(expSubs, actSubs);
        assertEquals("Task amount mismatch", actSubs.size(), expSubs.size());
        assertEquals("Failed @index: " + condition, -1, condition);
    }

    /**
     * тест метода restoreEpics
     *
     * @see TaskLoader#getEpics()
     * проверка успешности создания структуры HashMap
     * поэлементная проверка содержимого по идентификатору
     */
    @Test
    public void getEpics() {
        try {
            actHistory = taskLoader.addItemAndRestoreHistory(lines);
        } catch (ManagerLoadException mle) {
            fail(mle.getMessage());
        }

        actEpics = taskLoader.getEpics();
        actSubs = taskLoader.getSubs();
        taskLoader.attachSubs(actSubs, actEpics);

        // condition =-1 если не найдены несовпадающие задачи
        int condition = checkCondition(expEpics, actEpics);

        assertEquals("Task amount mismatch", actEpics.size(), expEpics.size());
        assertEquals("Failed @index: " + condition, -1, condition);
    }

    /**
     * тест метода attachSubs
     *
     * @implNote идентификатор ЭПИК-ЗАДАЧИ: 3
     * @see TaskLoader#attachSubs(Map, Map)
     * проверка успешности создания структуры HashMap
     * поэлементная проверка содержимого по идентификатору Эпик-задач
     */
    @Test
    public void attachSubs() {
        try {
            actHistory = taskLoader.addItemAndRestoreHistory(lines);
        } catch (ManagerLoadException mle) {
            fail(mle.getMessage());
        }
        actSubs = taskLoader.getSubs();
        actEpics = taskLoader.getEpics();

        taskLoader.attachSubs(actSubs, actEpics);

        Integer[] act = actEpics.get(3).getSubs().toArray(new Integer[]{});
        Integer[] exp = expEpics.get(3).getSubs().toArray(new Integer[]{});
        System.out.println(Arrays.toString(act));
        System.out.println(Arrays.toString(exp));

        assertArrayEquals(exp, act);
    }

    /**
     * тест метода getMaxId
     *
     * @see TaskLoader#getMaxId()
     * проверка равенства ожидаемого и полученного значения
     */
    @Test
    public void getMaxId() {
        assertEquals(expMaxId, taskLoader.getMaxId());
    }

    /**
     * вспомогательный метод поиска первого расхождения в восстановленных мапах задач
     * @param expMap ожидаемый HashMap элементов (задач/подзадач/эпиков)
     * @param actMap полученный из сейва HashMap элементов
     * @return идентификатор элемента, для котороге не совпало значение в expMap и actMap
     * @implNote если все соответствующие задачи совпадают - возвращается -1
     * @param <T> класс задачи/подзадачи/эпика
     */
    private <T extends AbstractTask> int checkCondition(HashMap<Integer,T> expMap, HashMap<Integer,T> actMap) {
        for (Integer id : actMap.keySet()) {
            System.out.println(actMap.get(id) + "<>" + expMap.get(id));
            if (!actMap.get(id).equals(expMap.get(id))) {
                return id;
            }
        }
        return -1;
    }
}