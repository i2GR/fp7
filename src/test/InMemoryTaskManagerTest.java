package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.InMemoryTaskManager;
import run.util.Managers;
import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void eachSetUp() {
        manager = (InMemoryTaskManager) Managers.getDefault();
        makeItems();
    }

    @Test
    void testGetNewTaskId() {
        assertNewTaskId();
    }

    @Test
    void testGetAllTasksNotEmpty() {
        manager.addNewTask(task4);
        manager.addNewTask(task6);
        Task[] expected = {task4, task6};
        assertGetAllTasks(expected);
    }

    @Test
    void testGetAllTasksWhenEmpty() {
        Task[] expected = {};
        assertGetAllTasks(expected);
    }


    @Test
    void testGetAllEpics() {
        manager.addNewSub(sub1);
        manager.addNewSub(sub2);
        manager.addNewSub(sub5);
        manager.addNewEpic(epic7);
        manager.addNewEpic(epic3);
        EpicTask[] expected = {epic3, epic7};
        assertGetAllEpics(expected);
    }

    @Test
    void testGetAllEpicsEmpty() {
        EpicTask[] expected = {};
        assertGetAllEpics(expected);
    }

    @Test
    void testGetAllSubsNotEmpty() {
        manager.addNewSub(sub1);
        manager.addNewSub(sub2);
        manager.addNewSub(sub5);
        SubTask[] expected = {sub1, sub2, sub5};
        assertGetAllSubs(expected);
    }

    @Test
    void testGetAllSubsEmpty() {
        SubTask[] expected = {};
        assertGetAllSubs(expected);
    }

    @Test
    void testGetTaskById() {
        manager.addNewTask(task4);
        manager.addNewTask(task6);
        assertGetTaskById(task4, 4);
        assertGetTaskById(task6, 6);
    }

    @Test
    void testGetNotExistentTaskById() {
        assertGetTaskById(null, 8);
    }

    @Test
    void testGetEpicById() {
        manager.addNewEpic(epic7);
        manager.addNewEpic(epic3);
        assertGetEpicById(epic7, 7);
        assertGetEpicById(epic3, 3);
    }

    @Test
    void testGetNotExistentEpicById() {
        assertGetEpicById(null, 8);
    }

    @Test
    void testGetSubById() {
        manager.addNewSub(sub1);
        manager.addNewSub(sub2);
        manager.addNewSub(sub5);
        assertGetSubById(sub1, 1);
        assertGetSubById(sub2, 2);
        assertGetSubById(sub5, 5);
    }

    @Test
    void testGetNotExistentSubById() {
        assertGetSubById(null, 8);
    }

    @Test
    void testAddNewTask() {
        assertAddNewTask(task4, 4);
        assertAddNewTask(task6, 6);
        assertAddNewTask(null, 8);
    }

    @Test
    void testAddNewEpic() {
        assertAddNewEpic(epic7, 7);
        assertAddNewEpic(null, 8);
    }

    @Test
    void testAddNewSub() {
        assertAddNewSub(sub1, 1);
        assertAddNewSub(sub5, 5);
        assertAddNewSub(null, 8);
    }

    @Test
    void testUpdateTask() {
        manager.addNewTask(task4);
        Task newTask4 = new Task(4, "name new4", "descr new4",
                Duration.of(15, ChronoUnit.MINUTES),
                LocalDateTime.of(2022, Month.DECEMBER, 3, 17, 0, 0));

        assertUpdateTask(newTask4, 4);
        assertUpdateNotExistentTask(new Task(99));
        assertUpdateNotExistentTask(new Task(-1));
    }

    @Test
    void testUpdateEpic() {
        manager.addNewEpic(epic7);
        EpicTask newEpic7 = new EpicTask(7, "name new7", "descr new7");

        assertUpdateEpic(newEpic7, 7);
        assertUpdateNotExistentEpic(new EpicTask(99));
        assertUpdateNotExistentEpic(new EpicTask(-1));
    }

    @Test
    void testUpdateSub() {
        manager.addNewSub(sub1);
        SubTask newSub1 = new SubTask(1, 9, "name new1", "descr new1",
                              Duration.of(25, ChronoUnit.MINUTES),
                              LocalDateTime.of(2022, Month.DECEMBER, 3, 19, 42, 0));

        assertUpdateSub(newSub1, 1);
        assertUpdateNotExistentSubTask(new SubTask(99, -1));
        assertUpdateNotExistentSubTask(new SubTask(-1, -1));
    }


    @Test
    void testDeleteAllTasks() {
        assertDeleteAllTasks(task4, task6);
    }

    @Test
    void testDeleteAllEpicsWithSubs() {
        manager.addNewSub(sub1);
        manager.addNewSub(sub2);

        assertDeleteAllEpics(epic3, epic7);
    }

    @Test
    void testDeleteAllSubsAssociated() {
        manager.addNewEpic(epic3);

        assertDeleteAllSubs(sub1, sub2, sub5);
        assertFalse(epic3.getSubs().contains(1));
        assertFalse(epic3.getSubs().contains(2));
    }

    @Test
    void testDeleteAllSubsNotAssociated() {
    assertDeleteAllSubs(sub9, sub8);
    }

    @Test
    void testDeleteTaskById() {
        assertDeleteTaskById(task4, 4);
    }

    @Test
    void testDeleteEpicByIdNoSubs() {
        assertDeleteEpicById(epic7, 7);
    }

    @Test
    void testDeleteEpicByIdWithSubs() {
        manager.addNewSub(sub1);
        manager.addNewSub(sub2);
        manager.addNewSub(sub5);

        assertDeleteEpicById(epic3, 3);
        assertFalse(manager.getAllSubs().contains(sub1));
        assertFalse(manager.getAllSubs().contains(sub2));
        assertFalse(manager.getAllSubs().contains(sub5));
    }

    @Test
    void testDeleteSubByIdNotAssociated() {
        assertDeleteSubById(sub9,9);
    }

    @Test
    void testDeleteSubByIdAssociated() {
        manager.addNewEpic(epic3);

        assertDeleteSubById(sub1,1);
        assertFalse(manager.getEpicById(3).getSubs().contains(1));
        assertTrue(manager.getEpicById(3).getSubs().contains(2));
        assertTrue(manager.getEpicById(3).getSubs().contains(5));
        assertDeleteSubById(sub2,2);
        assertFalse(manager.getEpicById(3).getSubs().contains(2));
        assertTrue(manager.getEpicById(3).getSubs().contains(5));
        assertDeleteSubById(sub5,5);
        assertFalse(manager.getEpicById(3).getSubs().contains(5));
    }

    @Test
    void testGetSubsForEpicId() {
        assertGetSubsForEpicId(epic3, 3, new int[]{1, 2, 5} ,new SubTask[]{sub1, sub2, sub5});

        epic3.addSubTask(sub8);

        assertGetSubsForEpicId(epic3, 3, new int[]{1, 2, 5, 8}, new SubTask[]{sub8});
    }

    @Test
    void testGetEpicStatusNoSubs() {
        assertGetEpicStatus(epic7, Status.NEW, new SubTask[]{}, new Status[]{});
    }

    @Test
    void testGetEpicStatusAllSubsNew() {
        assertGetEpicStatus(epic3, Status.NEW,
                            new SubTask[]{sub1, sub2, sub5}, new Status[]{Status.NEW, Status.NEW, Status.NEW});
        assertGetEpicStatus(epic3, Status.NEW,
                new SubTask[]{sub5}, new Status[]{Status.NEW});
    }

    @Test
    void testGetEpicStatusInProgressWithSubStatusDone() {
        assertGetEpicStatus(epic3, Status.IN_PROGRESS,
                new SubTask[]{sub1, sub2, sub5}, new Status[]{Status.DONE, Status.NEW, Status.NEW});
        assertGetEpicStatus(epic3, Status.IN_PROGRESS,
                new SubTask[]{sub1, sub2, sub5}, new Status[]{Status.NEW, Status.DONE, Status.NEW});
        assertGetEpicStatus(epic3, Status.IN_PROGRESS,
                new SubTask[]{sub1, sub2, sub5}, new Status[]{Status.DONE, Status.DONE, Status.NEW});
        assertGetEpicStatus(epic3, Status.IN_PROGRESS,
                new SubTask[]{sub1}, new Status[]{Status.IN_PROGRESS});
    }

    @Test
    void testGetEpicStatusInProgressWithSubsVariousStatuses() {
        assertGetEpicStatus(epic3, Status.IN_PROGRESS,
                new SubTask[]{sub1, sub2, sub5}, new Status[]{Status.DONE, Status.NEW, Status.IN_PROGRESS});
        assertGetEpicStatus(epic3, Status.IN_PROGRESS,
                new SubTask[]{sub1, sub2, sub5}, new Status[]{Status.NEW, Status.IN_PROGRESS, Status.DONE});
    }

    @Test
    void testGetEpicStatusInProgressWithAllSubsDone() {
        assertGetEpicStatus(epic3, Status.DONE,
                new SubTask[]{sub1, sub2, sub5}, new Status[]{Status.DONE, Status.DONE, Status.DONE});
        assertGetEpicStatus(epic3, Status.DONE,
                new SubTask[]{sub1, sub2}, new Status[]{Status.DONE, Status.DONE});
        assertGetEpicStatus(epic3, Status.DONE,
                new SubTask[]{sub1}, new Status[]{Status.DONE});
    }

    @Test
    void testClearSubsListOfEpic() {
        assertClearSubsListOfEpic(epic7, 7, 0, new SubTask[]{});
        assertClearSubsListOfEpic(epic3, 3, 4, new SubTask[]{sub8});
        assertClearSubsListOfEpic(epic3, 3, 3, new SubTask[]{sub5, sub2, sub1});
    }

    @Test
    void testGetPrioritizedTasks() {
        task4.setStartTime(LocalDateTime.of(2000, 1, 1, 0,0,0));
        task6.setStartTime(LocalDateTime.of(1999, 1, 1, 0,0,0));
        sub1.setStartTime(LocalDateTime.of(2001, 1, 1, 0,0,0));
        sub2.setStartTime(LocalDateTime.of(2010, 1, 1, 0,0,0));
        sub5.setStartTime(LocalDateTime.of(2002, 1, 1, 0,0,0));

        assertGetPrioritizedTasks(new Task[]{task4, task6},
                                  new SubTask[]{sub5, sub1, sub2}, new int[]{6,4,1,5,2});
    }

    /**
     * тест метода testValidateTimeFrame()
     * для работы теста нуэно применить модификатор public к методу
     * временные параметры задаются вручную
    */
    @Test
    void testValidateTimeFrame() {
        task4.setStartTime(LocalDateTime.of(2000, 1, 1, 0,10,0));
        task4.setDuration(Duration.ofMinutes(10));
        task6.setStartTime(LocalDateTime.of(2000, 1, 1, 0,0,0));
        task6.setDuration(Duration.ofMinutes(10));

        manager.addNewTask(task4);

        boolean b = manager.validateTimeFrame(task6);
        System.out.println(b);
    }
}
