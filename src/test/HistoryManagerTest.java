package test;

import org.junit.jupiter.api.Test;
import run.HistoryManager;
import run.util.Managers;
import tasks.AbstractTask;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    Task task1 = new Task(1);
    Task task2 = new Task(2);
    Task task3 = new Task(3);
    EpicTask epic4 = new EpicTask(4);
    SubTask sub5 = new SubTask(5, 4);
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void getHistoryEmpty() {
        List<AbstractTask> emptyHistory = historyManager.getHistory();

        assertTrue(emptyHistory.isEmpty());
    }

    @Test
    void getHistoryAfterRemove() {
        historyManager.add(task1);
        historyManager.remove(1);

        List<AbstractTask> emptyHistory = historyManager.getHistory();

        assertTrue(emptyHistory.isEmpty());
    }
    @Test
    void getHistoryWithDuplicatesAdded() {
        historyManager.add(task1);
        historyManager.add(epic4);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        int[] expected = {3, 2, 1, 4};

        int[] actual = historyManager.getHistory().stream().mapToInt(e -> e.getId()).toArray();

        assertEquals(4, historyManager.getHistory().size());
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    @Test
    void removeFromTail() {
        addItemsToHistory();
        int[] expected = {3, 2, 5, 4};

        historyManager.remove(1);

        int[] actual = historyManager.getHistory().stream().mapToInt(e -> e.getId()).toArray();
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    @Test
    void removeFromMiddle() {
        addItemsToHistory();
        int[] expected = {3, 5, 4, 1};

        historyManager.remove(2);

        int[] actual = historyManager.getHistory().stream().mapToInt(e -> e.getId()).toArray();
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    @Test
    void removeFromBeginning() {
        addItemsToHistory();
        int[] expected = {2, 5, 4, 1};

        historyManager.remove(3);

        int[] actual = historyManager.getHistory().stream().mapToInt(e -> e.getId()).toArray();
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    /**
     * вспомогательный метод добавлени задач в историю для тестов
     */
    private void addItemsToHistory() {
        historyManager.add(task1);
        historyManager.add(epic4);
        historyManager.add(sub5);
        historyManager.add(task2);
        historyManager.add(task3);
    }

}