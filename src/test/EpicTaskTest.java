package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.AbstractTask;
import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    EpicTask epicNoSubs, epicWithSubs;
    SubTask sub3, sub4, sub5, sub6, sub7;
    LocalDateTime moment;

    @BeforeEach
    void testMakeNewTasks(){
        moment = LocalDateTime.of(2000, 1, 1, 0, 0);
        sub3 = new SubTask(3, 2, "name3", "descr3", Duration.ofMinutes(10), moment);
        sub4 = new SubTask(4, 2, "name4", "descr4",
                Duration.ofMinutes(15), moment.plusMinutes(10));
        sub5 = new SubTask(5, 2, "name5", "descr5",
                Duration.ofMinutes(5), moment.plusMinutes(25));
        sub6 = new SubTask(6, 2, "name6", "descr6",
                Duration.ofMinutes(10), moment.minusMinutes(20));
        sub7 = new SubTask(7, 2, "name7", "descr7", Duration.ofMinutes(10), moment);
        epicNoSubs = new EpicTask(1,"name1", "descr1");
        epicWithSubs = new EpicTask(2, "name2", "descr2", sub3, sub4, sub5);
    }

    /**
     * тест добавления дубликатов подзадач
     * дубликаты не должны быть добавлены
     */
    @Test
    void testAddSubTasksInitiallyDuplicated() {
        Integer[] actual;

        epicWithSubs.addSubTask(sub3, sub4, sub5);
        actual = epicWithSubs.getSubs().toArray(new Integer[]{});

        assertEquals(0, epicNoSubs.getSubs().size());
        assertArrayEquals(new Integer[]{3,4,5}, actual,
                "получен неправильный перечень задач");
    }

    /**
     * тест добавления подзадач, не принадлежащих эпику
     * подзадачи не должны быть добавлены
     */
    @Test
    void testAddSubTaskIfSubNotBelongEpic() {
        Integer[] actual;

        epicNoSubs.addSubTask(sub6, sub7);
        actual = epicNoSubs.getSubs().toArray(new Integer[]{});

        assertEquals(0, epicNoSubs.getSubs().size(), " в списке есть задачи:" + Arrays.toString(actual));
        assertArrayEquals(new Integer[]{}, actual,
                "нарушен порядок сортировки идентификаторов задач");
    }

    /**
     * тест добавления подзадачи дважды
     * подзадача должна быть добавлен один раз
     */
    @Test
    void testAddSubTaskWithDuplicates() {
        Integer[] actual;

        epicWithSubs.addSubTask(sub6, sub6);
        actual = epicWithSubs.getSubs().toArray(new Integer[]{});

        assertArrayEquals(new Integer[]{3, 4, 5, 6}, actual,
                "получен неправильный перечень задач. " + Arrays.toString(actual));
    }

    /**
     * тест получения времени старта и длительности задачи
     * время старта по первой задаче (sub3)
     */
    @Test
    void testTimeParamsFromFirstSub() {
        Duration expectedDuration = sub3.getDuration().plus(sub4.getDuration()).plus(sub5.getDuration());

        assertEquals(sub3.getStartTime(), epicWithSubs.getStartTime());
        assertEquals(expectedDuration, epicWithSubs.getDuration());
    }

    /**
     * тест получения времени старта и длительности задачи при добавении новой задачи
     * время старта по новой задаче
     * новая первая подзадача в списке (sub6)
     */
    @Test
    void testTimeParamsFromNewSubAdded() {
        Duration expectedDuration;

        epicWithSubs.addSubTask(sub6);
        expectedDuration = sub3.getDuration().plus(sub4.getDuration()).plus(sub5.getDuration()).plus(sub6.getDuration());

        assertEquals(expectedDuration, epicWithSubs.getDuration());
        assertEquals(sub6.getStartTime(), epicWithSubs.getStartTime());

    }

    /**
     * тест удаления задач
     */
    @Test
    public void testRemoveSubTask() {
        Integer[] expectedSubs = new Integer[]{4,5};
        epicWithSubs.removeSubTask(sub3);
        assertArrayEquals(expectedSubs, epicWithSubs.getSubs().toArray(new Integer[]{}));
    }

    /**
     * тест получения времени старта и длительности задачи
     * времея старта по первой задаче
     */
    @Test
    void testTimeParamsFromSubs() {
        LocalDateTime expectedTime = moment;
        Duration expectedDuration = sub3.getDuration().plus(sub4.getDuration()).plus(sub5.getDuration());
        assertEquals(expectedTime, epicWithSubs.getStartTime());
        assertEquals(expectedDuration, epicWithSubs.getDuration());
    }

    /**
     * тест получения времени старта и длительности задачи при удалении первой задачи
     * время старта по ставшей первой задаче (sub4)
     */
    @Test
    void testTimeParamsWhenRemoveFirstSub() {
        epicWithSubs.removeSubTask(sub3);
        Duration expectedDuration = sub4.getDuration().plus(sub5.getDuration());

        assertEquals(sub4.getStartTime(), epicWithSubs.getStartTime());
        assertEquals(expectedDuration, epicWithSubs.getDuration());
    }

    /**
     * тест получения времени старта и длительности задачи при удалении последней задачи
     * длительность по ставшей последней задаче
     */
    @Test
    void testTimeParamsWhenRemoveLastSub() {
        epicWithSubs.removeSubTask(sub5);
        Duration expectedDuration = sub3.getDuration().plus(sub4.getDuration());

        assertEquals(sub3.getStartTime(), epicWithSubs.getStartTime());
        assertEquals(expectedDuration, epicWithSubs.getDuration());
    }

    /**
     * тест получения времени старта и длительности задачи при удалении последней задачи
     * длительность по НОВОЙ последней задаче
     */
    @Test
    void testTimeParamsWhenRemoveInnerSub() {
        epicWithSubs.removeSubTask(sub4);
        Duration expectedDuration = sub3.getDuration().plus(sub5.getDuration());

        assertEquals(sub3.getStartTime(), epicWithSubs.getStartTime());
        assertEquals(expectedDuration, epicWithSubs.getDuration());
    }

    /**
     * тест получения времени старта и длительности задачи при удалении всех задач
     */
    @Test
    void testTimeParamsWhenRemoveAllSub() {
        epicWithSubs.removeSubTask(sub3);
        epicWithSubs.removeSubTask(sub4);
        epicWithSubs.removeSubTask(sub5);

        assertEquals(AbstractTask.DEFAULT_TIME, epicWithSubs.getStartTime());
        assertEquals(Duration.ZERO, epicWithSubs.getDuration());
    }

    /**
     * тест удаления всех подзадач
     */
    @Test
    public void testClearSubTasksTest() {
        epicWithSubs.clearSubTasks();

        assertEquals(AbstractTask.DEFAULT_TIME, epicWithSubs.getStartTime());
        assertEquals(Duration.ZERO, epicWithSubs.getDuration());
    }

    /**
     * тест получения статуса заглушки
     */
    @Test
    void testSetInitialStatusNA() {
        assertEquals(Status.N_A, epicNoSubs.getStatus(), "статус эпика без подзадач не равен N_A");
        assertEquals(Status.N_A, epicWithSubs.getStatus(), "статус эпика c подзадачами не равен N_A");
    }

    /**
     * тест получения статуса заглушки
     */
    @Test
    void testSetStatus() {
        //Status.N_A статус "заглушка"
        epicNoSubs.setStatus(Status.NEW);
        epicWithSubs.setStatus(Status.DONE);

        assertEquals(Status.N_A, epicNoSubs.getStatus(),
                "статус эпика без подзадач после вызова метода не равен N_A");
        assertEquals(Status.N_A, epicWithSubs.getStatus(),
                "статус эпика без подзадач после вызова метода не равен N_A");
    }

    /**
     * тест получения длительности-заглушки
     */
    @Test
    void testGetDuration() {
        epicNoSubs.setDuration(Duration.ofDays(8));

        assertEquals(Duration.ZERO, epicNoSubs.getDuration(),
                "длительность эпика не равна Duration.ZERO ");
    }

    /**
     * тест получения времени старта-заглушки
     */
    @Test
    void testGetStartTime() {
        epicNoSubs.setStartTime(LocalDateTime.of(1,1,1,0,0));

        assertEquals(AbstractTask.DEFAULT_TIME, epicNoSubs.getStartTime(),
                "старт эпика не равен Instant.EPOCH");
    }

    /**
     * тест метода ToString()
     */
    @Test
    void testToString() {
        assertEquals("001 EPIC [NoSubs]", epicNoSubs.toString(), epicNoSubs.toString());
        assertEquals("002 EPIC [3, 4, 5]", epicWithSubs.toString(), epicNoSubs.toString());

    }

    /**
     * тест геттера getSubs()
     */
    @Test
    void testGetSubs() {
        epicWithSubs.clearSubTasks();
        epicWithSubs.addSubTask(sub5, sub3, sub4);

        assertArrayEquals(new Integer[]{3, 4, 5}, epicWithSubs.getSubs().toArray(new Integer[]{}));
    }
}