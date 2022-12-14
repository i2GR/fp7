package test;

import org.junit.jupiter.api.Test;
import run.util.TaskSaver;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskSaverTest {
    LocalDateTime startTime = LocalDateTime.of(2000, 1, 1, 0, 0);

    Task task1 = new Task(1, "task", "description 1",
                        Duration.ofMinutes(20), startTime);
    SubTask sub2 = new SubTask(2, 3, "subtask", "description 2",
                        Duration.ofMinutes(10), startTime.plus(Duration.ofMinutes(20)));
    EpicTask epic3 = new EpicTask(3, "epic", "description 3", sub2);

    @Test
    void taskToString() {
        String expected = "1,NORM,task,NEW,description 1,2000-01-01T00:00,PT20M\n";

        assertEquals(expected, TaskSaver.taskToString(task1));
    }

    @Test
    void subToString() {
        String expected = "2,SUBT,subtask,NEW,description 2,2000-01-01T00:20,PT10M,3\n";

        assertEquals(expected, TaskSaver.taskToString(sub2));
    }

    @Test
    void epicTaskToString() {
        String expected = "3,EPIC,epic,NEW,description 3,2000-01-01T00:20,PT10M\n";

        assertEquals(expected, TaskSaver.epicTaskToString(epic3, "NEW"));
    }

    @Test
    void historyToString() {
        List<AbstractTask> history;

        history = List.of(sub2);
        String emptyHistory = TaskSaver.historyToString(new ArrayList<>());
        String historyOneTask = TaskSaver.historyToString(history);

        assertEquals("\n", emptyHistory);
        assertEquals("\n2", historyOneTask);
    }
}