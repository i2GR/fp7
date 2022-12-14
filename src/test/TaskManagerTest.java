package test;

import run.TaskManager;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<M extends TaskManager> {
    /**
     * Задачи для тестов
     * имя переменной: [тип задачи][идентификатор]
     */
    public SubTask sub1, sub2, sub5, sub8, sub9;

    /**
     * Epic3 c подзадачми sub1, sub2, sub5
     * epic7 без подзадач
     */
    public EpicTask epic3, epic7;
    public Task task4, task6;


    void makeItems() {
        sub1 = new SubTask(1, 3, "name 1", "descr 1",
                Duration.ofMinutes(20),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        sub2 = new SubTask(2, 3, "name 2", "descr 2",
                Duration.ofMinutes(15),
                LocalDateTime.of(2000, 1, 1, 0, 20, 0));
        sub5 = new SubTask(5, 3, "name 5", "descr 5",
                Duration.ofMinutes(20),
                LocalDateTime.of(2000, 1, 1, 1, 25, 0));
        epic3 = new EpicTask(3, "name 3", "descr 3", sub5, sub2, sub1);

        task4 = new Task(4, "name 4", "descr 4",
                Duration.ofMinutes(15),
                LocalDateTime.of(2000, 1, 1, 0, 40, 0));

        task6 = new Task(6, "name 6", "descr 6",
                Duration.ofMinutes(30),
                LocalDateTime.of(2000, 1, 1, 0, 55, 0));

        epic7 = new EpicTask(7, "name 7", "descr 7");

        sub8 = new SubTask(8, 3, "name 8", "descr 8",
                Duration.ofMinutes(5),
                LocalDateTime.of(2001, Month.DECEMBER, 1, 0, 0, 0));
        sub9 = new SubTask(9, 3, "name 9", "descr 9",
                Duration.ofMinutes(5),
                LocalDateTime.of(2002, Month.DECEMBER, 1, 0, 0, 0));
    }
    public M manager;

    /**
     * проверка изменения (счетчика) идентификаторов задач
     */
    void assertNewTaskId() {
        int initialId = manager.getNewTaskId();
        int updatedId = manager.getNewTaskId();
        int expected = initialId + 1;
        assertEquals(expected, updatedId);
    }

    /**
     * проверка получения списка всех задач
     * проверка количества, состава
     * @param expectedArray ожидаемый массив задач
     */
    void assertGetAllTasks(Task[] expectedArray) {
        Task[] actualArray = manager.getAllTasks().toArray(new Task[]{});
        assertEquals(expectedArray.length, actualArray.length, "Количество полученных задач не равно заданному");
        assertArrayEquals(expectedArray, actualArray, "Массивы задач не совпадают");
        System.out.println(Arrays.toString(expectedArray));
        System.out.println(Arrays.toString(expectedArray));
    }

    /**
     * проверка получения списка всех эпиков
     * проверка количества, состава
     * @param expectedArray ожидаемый массив эпиков
     */
    void assertGetAllEpics(EpicTask[] expectedArray) {
        EpicTask[] actualArray = manager.getAllEpics().toArray(new EpicTask[]{});
        assertEquals(expectedArray.length, actualArray.length, "Количество полученных эпиков не равно заданному");
        assertArrayEquals(expectedArray, actualArray, "Массивы задач не совпадают");
        System.out.println(expectedArray.length);
        System.out.println(Arrays.toString(expectedArray));
    }

    /**
     * проверка получения списка всех подзадач
     * проверка количества, состава
     * @param expectedArray ожидаемый массив подзадач
     */
    void assertGetAllSubs(SubTask[] expectedArray) {
        SubTask[] actualArray = manager.getAllSubs().toArray(new SubTask[]{});
        assertEquals(expectedArray.length, actualArray.length);
        assertArrayEquals(expectedArray, actualArray);
        System.out.println(expectedArray.length);
        System.out.println(Arrays.toString(expectedArray));
    }

    /**
     * проверка получения задачи по идентификатору
     *
     * @param expected ожидаемая задача
     * @param id запрашиваемый идентификатор
     */
    void assertGetTaskById(Task expected, int id) {
        Task actual = manager.getTaskById(id);
        assertEquals(expected, actual, "Полученная задача не совпадает с заданной");
    }

    /**
     * проверка получения эпик-задачи по идентификатору
     *
     * @param expected ожидаемый эпик
     * @param id запрашиваемый идентификатор
     */
    void assertGetEpicById(EpicTask expected, int id) {
        EpicTask actual = manager.getEpicById(id);
        assertEquals(expected, actual, "Полученный эпик не совпадает с заданным");
    }

    /**
     * проверка получения подзадачи по идентификатору
     *
     * @param expected ожидаемая подзада
     * @param id запрашиваемый идентификатор
     */
    void assertGetSubById(SubTask expected, int id) {
        SubTask actual = manager.getSubById(id);
        assertEquals(expected, actual, "Полученная подзадача не совпадает с заданной");
    }

    /**
     * проверка добавления задачи
     *
     * @param expected ожидаемая (добавляемая) задача
     * @param id запрашиваемый идентификатор для проверки задачи, попавшей в хранилище
     */
    void assertAddNewTask(Task expected, int id) {
        manager.addNewTask(expected);
        Task actual = manager.getTaskById(id);
        assertEquals(expected, actual, "Задачи не совпадают");
    }

    /**
     * проверка добавления эпика
     *
     * @param expected ожидаемая (добавляемая) эпик-задача
     * @param id запрашиваемый идентификатор для проверки эпика, попавшего в хранилище
     */
    void assertAddNewEpic(EpicTask expected, int id) {
        manager.addNewEpic(expected);
        EpicTask actual = manager.getEpicById(id);
        assertEquals(expected, actual, "Задачи не совпадают");
    }

    /**
     * проверка добавления подзадачи
     *
     * @param expected ожидаемая (добавляемая) подзадача
     * @param id запрашиваемый идентификатор для проверки подзадачи, попавшей в хранилище
     */
    void assertAddNewSub(SubTask expected, int id) {
        manager.addNewSub(expected);
        SubTask actual = manager.getSubById(id);
        assertEquals(expected, actual, "Задачи не совпадают");
    }

    /**
     * проверка обновления задачи
     *
     * @param actual обновленная задача
     * @param id запрашиваемый идентификатор для проверки задачи в хранилище
     */
    void assertUpdateTask(Task actual, int id) {
        Task expected = manager.getTaskById(id);
        int returnedId = manager.updateTask(actual);
        assertNotEquals(-1, returnedId, "Задача не найдена.");
        assertNotEquals(expected, manager.getTaskById(id), "получена задача без обновления");
    }

    /**
     * проверка обновления задачи, не находящейся в хранилище
     *
     * @param actual обновленная задача
     */
    void assertUpdateNotExistentTask(Task actual) {
        assertEquals(-1, manager.updateTask(actual), "неправильная работа при обновлении");
    }

    /**
     * проверка обновления эпик-задачи
     *
     * @param actual обновленный эпик
     * @param id запрашиваемый идентификатор для проверки эпика в хранилище
     */
    void assertUpdateEpic(EpicTask actual, int id) {
        EpicTask expected = manager.getEpicById(id);
        int returnedId = manager.updateEpic(actual);
        assertNotEquals(-1, returnedId, "Задача не найдена.");
        assertNotEquals(expected, manager.getEpicById(id), "получена задача без обновления");
        System.out.println(expected.toString());
        System.out.println(actual.toString());
    }

    /**
     * проверка обновления эпика, не находящегося в хранилище
     *
     * @param actual обновленная задача
     */
    void assertUpdateNotExistentEpic(EpicTask actual) {
        assertEquals(-1, manager.updateEpic(actual), "неправильная работа при обновлении");
    }

    /**
     * проверка обновления подзадачи
     *
     * @param actual обновленная подзадача
     * @param id запрашиваемый идентификатор для проверки подзадачи в хранилище
     */
    void assertUpdateSub(SubTask actual, int id) {
        SubTask expected = manager.getSubById(id);
        int returnedId = manager.updateSub(actual);
        assertNotEquals(-1, returnedId, "Задача не найдена.");
        assertNotEquals(expected, manager.getSubById(id), "получена задача без обновления");
        System.out.println(expected.toString());
        System.out.println(actual.toString());
    }
    /**
     * проверка обновления подзачаи, не находящейся в хранилище
     *
     * @param actual обновленная задача
     */
    void assertUpdateNotExistentSubTask(SubTask actual) {
        assertEquals(-1, manager.updateSub(actual), "неправильная работа при обновлении");
    }

    /**
     * проверка удаления всех задач из менеджера
     * @param tasks массив задач для проверки
     */
    void assertDeleteAllTasks(Task... tasks) {
        assertNotEquals(0,tasks.length, "в тест передан массив без задач");
        Arrays.stream(tasks).forEach(t -> manager.addNewTask(t));
        assertFalse(manager.getAllTasks().isEmpty(), "В менеджер не добавлены задачи");
        assertEquals(manager.getAllTasks().size(), tasks.length, "В менеджер добавлено неверное кол-во задач");
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Менеджер содержит задачи после удаления");
    }

    /**
     * проверка удаления всех эпиков из менеджера
     * @param epics массив эпиков для проверки
     */
    void assertDeleteAllEpics(EpicTask... epics) {
        assertNotEquals(0, epics.length, "в тест передан массив без эпиков");
        Arrays.stream(epics).forEach(e -> manager.addNewEpic(e));
        assertFalse(manager.getAllEpics().isEmpty(), "В менеджер не добавлены эпики");
        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty(), "Менеджер содержит эпик-задачи после удаления");
    }

    /**
     * проверка удаления всех подзадач из менеджера
     * @param subs массив подзадач для проверки
     */
    void assertDeleteAllSubs(SubTask... subs) {
        assertNotEquals(0, subs.length, "в тест передан массив без подзадач");
        Arrays.stream(subs).forEach(s -> manager.addNewSub(s));
        assertFalse(manager.getAllSubs().isEmpty(), "В менеджер не добавлены подзадачи");
        manager.deleteAllSubs();
        assertTrue(manager.getAllSubs().isEmpty(), "Менеджер содержит подзадачи после удаления");
    }

    /**
     * проверка удаления задач по идентификаторы
     * @param task задача для проверки (после удаления задача не должна содержаться в менеджере)
     * @param id идентификатор задачи для проверки
     */
    void assertDeleteTaskById(Task task, int id) {
        manager.addNewTask(task);
        assertEquals(task.getId(), id, "в тест передана задача с неверным id");
        assertEquals(task, manager.getTaskById(id), "ожидаемая и полученная задачи не совпадают");
        manager.deleteTaskById(id);
        assertFalse(manager.getAllTasks().contains(task), "в менеджере осталась проверяемая задача");
    }

    /**
     * проверка удаления эпика по идентификаторы
     * @param epic эпик для проверки (после удаления эпик не должен содержаться в менеджере)
     * @param id идентификатор эпика для проверки
     */
    void assertDeleteEpicById(EpicTask epic, int id) {
        manager.addNewEpic(epic);
        assertEquals(epic.getId(), id, "в тест передана эпик-задача с неверным id");
        assertEquals(epic, manager.getEpicById(id), "ожидаемая и полученная эпик-задачи не совпадают");
        manager.deleteEpicById(id);
        assertFalse(manager.getAllEpics().contains(epic), "в менеджере осталась проверяемая эпик-задача");
    }

    /**
     * проверка удаления подзадач по идентификаторы
     * @param sub подзадача для проверки (после удаления задача не должна содержаться в менеджере)
     * @param id идентификатор подзадачи для проверки
     */
    void assertDeleteSubById(SubTask sub, int id) {
        manager.addNewSub(sub);
        assertEquals(sub.getId(), id, "в тест передана под-задача с неверным id");
        assertEquals(sub, manager.getSubById(id), "ожидаемая и полученная под-задачи не совпадают");
        manager.deleteSubById(id);
        assertFalse(manager.getAllSubs().contains(sub), "в менеджере осталась проверяемая эпик-задача");
    }


    /**
     * проверка получения подзадач эпика
     * @param epic проверяемый эпик
     * @param epicId идентификатор эпика
     * @param subs подзадачи, добавленные в эпик
     */
    void assertGetSubsForEpicId(EpicTask epic, int epicId, int[] expected, SubTask[] subs) {
        assertEquals(epic.getId(), epicId, "переданный epicId и идентификатор эпика не совпадают");
        Arrays.stream(subs).forEach(manager::addNewSub);
        epic.addSubTask(subs);
        manager.addNewEpic(epic);
        List<SubTask> receivedSubs = manager.getSubsForEpicId(epicId);
        int[] actual = new int[receivedSubs.size()];
        int i = 0;
        for(SubTask sub : receivedSubs) {
            actual[i++] = sub.getId();
        }
        assertArrayEquals(expected, actual, "полученный список id подзадач не совпадает с заданным");
    }

    /**
     * проверка получения статуса эпика
     * @param epic проверяемый эпик
     * @param expected ожидаемый статус
     * @param subs подзадачи эпика
     * @param stats соответствующие статусы подзадач, которые нужно установить для них
     */
    void assertGetEpicStatus(EpicTask epic, Status expected, SubTask[] subs, Status[] stats) {
        Arrays.stream(subs).forEach(sub -> {epic.addSubTask(sub); manager.addNewSub(sub);});
        manager.addNewEpic(epic);
        assertEquals(subs.length, stats.length, "в тест переданы разное кол-во подзадач/статусов");
        if (subs.length > 0){
            for (int i = 0; i < subs.length; i++) {
                subs[i].setStatus(stats[i]);
                assertEquals(manager.getSubById(subs[i].getId()).getStatus(),
                        stats[i],
                        "Статус подзадачи не обновлен");
            }
        }
        Status actual = manager.getEpicStatus(epic.getId());
        assertEquals(expected, actual, "Рассчитан неверный статус");
    }

    void assertClearSubsListOfEpic(EpicTask epic, int epicId, int expectedSubsAfterAdd, SubTask[] subs) {
        assertEquals(epic.getId(), epicId, "переданный epicId и идентификатор эпика не совпадают");
        Arrays.stream(subs).forEach(epic::addSubTask);
        manager.addNewEpic(epic);
        assertEquals(expectedSubsAfterAdd, manager.getEpicById(epicId).getSubs().size(),
                     "в эпик записано неверное количество подзадач для эпика");
        manager.clearSubsListOfEpic(epicId);
        assertTrue(manager.getEpicById(epicId).getSubs().isEmpty(), "в списке подзадач эпика остались задачи");
    }

    void assertGetPrioritizedTasks(Task[] tasks, SubTask[] subs, int[] expected) {
        Arrays.stream(tasks).forEach(t -> manager.addNewTask(t));
        Arrays.stream(subs).forEach(s -> manager.addNewSub(s));
        int[] actual = manager.getPrioritizedTasks().stream().mapToInt(e -> e.getId()).toArray();
        assertArrayEquals(expected, actual);
    }
}