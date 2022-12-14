package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import run.FileBackedTasksManager;
import run.util.Managers;
import run.util.TaskSaver;
import tasks.AbstractTask;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;


class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    @Override
    @BeforeEach
    void eachSetUp() {
        manager = new FileBackedTasksManager("data/testSaveFile.csv");
        super.makeItems();
    }

    @Override
    @Test
    void testGetAllTasksNotEmpty() {
        super.testGetAllTasksNotEmpty();
    }

    @Override
    @Test
    void testGetAllTasksWhenEmpty() {
        super.testGetAllTasksWhenEmpty();
    }

    @Override
    @Test
    void testGetAllEpics() {
        super.testGetAllEpics();
    }

    @Override
    @Test
    void testGetAllEpicsEmpty() {
        super.testGetAllEpicsEmpty();
    }

    @Override
    @Test
    void testGetAllSubsNotEmpty() {
        super.testGetAllSubsNotEmpty();
    }

    @Override
    @Test
    void testGetAllSubsEmpty() {
        super.testGetAllSubsNotEmpty();
    }

    @Override
    @Test
    void testGetTaskById() {
        super.testGetTaskById();
    }

    @Override
    @Test
    void testGetNotExistentTaskById() {
        super.testGetNotExistentTaskById();
    }

    @Override
    @Test
    void testGetEpicById() {
        super.testGetEpicById();
    }

    @Override
    @Test
    void testGetNotExistentEpicById() {
        super.testGetNotExistentEpicById();
    }

    @Override
    @Test
    void testGetSubById() {
        super.testGetSubById();
    }

    @Override
    @Test
    void testGetNotExistentSubById() {
        super.testGetNotExistentSubById();
    }

    @Override
    @Test
    void testAddNewTask() {
        super.testAddNewTask();
    }

    @Override
    @Test
    void testAddNewEpic() {
        super.testAddNewEpic();
    }

    @Override
    @Test
    void testAddNewSub() {
        super.testAddNewEpic();
    }

    @Override
    @Test
    void testUpdateTask() {
        super.testUpdateTask();
    }

    @Override
    @Test
    void testUpdateEpic() {
        super.testUpdateEpic();
    }

    @Override
    @Test
    void testUpdateSub() {
        super.testUpdateSub();
    }


    @Override
    @Test
    void testDeleteAllTasks() {
        super.testDeleteAllTasks();
    }

    @Override
    @Test
    void testDeleteAllEpicsWithSubs() {
        super.testDeleteAllEpicsWithSubs();
    }

    @Override
    @Test
    void testDeleteAllSubsAssociated() {
        super.testDeleteAllSubsAssociated();
    }

    @Override
    @Test
    void testDeleteAllSubsNotAssociated() {
        // добавление подзадач в менеджер, т.к.
        // при любой операции ввода новых данных в FileBacked-менеджере проводится запись
        // при записи определяется статус эпика от под-задач в менеджере
        // поэтому необходимо записать подзадачи в менеджер
        manager.addNewSub(sub1);
        manager.addNewSub(sub2);
        manager.addNewSub(sub5);
        super.testDeleteAllSubsAssociated();
    }

    @Override
    @Test
    void testDeleteTaskById() {
        super.testDeleteTaskById();
    }

    @Override
    @Test
    void testDeleteEpicByIdNoSubs() {
        super.testDeleteEpicByIdNoSubs();
    }

    @Override
    @Test
    void testDeleteEpicByIdWithSubs() {
        super.testDeleteEpicByIdWithSubs();
    }

    @Override
    @Test
    void testDeleteSubByIdNotAssociated() {
        super.testDeleteEpicByIdWithSubs();
    }

    @Override
    @Test
    void testDeleteSubByIdAssociated() {
        super.testDeleteSubByIdAssociated();
    }

    @Override
    @Test
    void testGetSubsForEpicId() {
        super.testGetSubsForEpicId();
    }

    @Override
    @Test
    void testGetEpicStatusNoSubs() {
        super.testGetEpicStatusNoSubs();
    }

    @Override
    @Test
    void testGetEpicStatusAllSubsNew() {
        super.testGetEpicStatusNoSubs();
    }

    @Override
    @Test
    void testGetEpicStatusInProgressWithSubStatusDone() {
        super.testGetEpicStatusInProgressWithSubStatusDone();
    }

    @Override
    @Test
    void testGetEpicStatusInProgressWithSubsVariousStatuses() {
        super.testGetEpicStatusInProgressWithSubsVariousStatuses();
    }

    @Override
    @Test
    void testGetEpicStatusInProgressWithAllSubsDone() {
        super.testGetEpicStatusInProgressWithAllSubsDone();
    }

    @Override
    @Test
    void testClearSubsListOfEpic() {
        super.testClearSubsListOfEpic();
    }

    @Test
    void testLoadEmptyManager() {
        manager = Managers.loadFromFile(makeEmptySaveFile());

        // Тестирование загрузки из пустого файла
        Assertions.assertTrue(manager.getAllTasks().isEmpty(), "Непустой состав задач");
        Assertions.assertTrue(manager.getAllEpics().isEmpty(), "Непустой состав подзадач");
        Assertions.assertTrue(manager.getAllSubs().isEmpty(), "Непустой состав эпиков");
        Assertions.assertTrue(manager.getHistory().isEmpty(), "непустая история");
    }

    /**
     * тестирование истории с пустым файлом сохранения
     */
    @Test
    void testEmptyHistory() {
        File file = makeEmptySaveFile();
        manager = Managers.loadFromFile(file);
        addItemsToManager();
        manager = Managers.loadFromFile(file);

        List<AbstractTask> emptyHistory = manager.getHistory();

        Assertions.assertNotNull(emptyHistory, "история не инициализирована (null)");
        Assertions.assertEquals(0, emptyHistory.size());

    }

    @Test
    void testRecordedHistory() {
        File file = makeEmptySaveFile();
        manager = Managers.loadFromFile(file);
        addItemsToManager();
        for (int id : new int[]{4, 6, 6, 1, 2}) {
            manager.getTaskById(id);
        }
        for (int id : new int[]{1, 8, 5, 2, 1, 9}) {
            manager.getSubById(id);
        }
        for (int id : new int[]{3, 8, 5, 3, 7, 3}) {
            manager.getEpicById(id);
        }

        int[] expected = {3, 7, 9, 1, 2, 5, 8, 6, 4};
        List<AbstractTask> list = manager.getHistory();
        int[] actual = list.stream().mapToInt(a -> a.getId()).toArray();

        Assertions.assertArrayEquals(expected, actual, "полученная история: " + Arrays.toString(actual));
    }

    @Test
    void testSaveAndRestoreEpicWOSubs() {
        File file = makeEmptySaveFile();
        manager = Managers.loadFromFile(file);
        manager = Managers.loadFromFile(makeEmptySaveFile());
        manager.addNewEpic(epic7);
        manager = Managers.loadFromFile(file);

        Assertions.assertNotNull(manager.getEpicById(7));
        Assertions.assertEquals(epic7, manager.getEpicById(7), "полученный эпик" + manager.getEpicById(7).toString());
    }
    @Override
    @Test
    void testGetPrioritizedTasks() {
        super.testGetPrioritizedTasks();
    }

    /**
     * вспомогательный метод создания пустого сейва
     * @return объект файла
     */
    private File makeEmptySaveFile() {
        File file = new File("data/testSaveLoad.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(TaskSaver.HEADER+ '\n');
        } catch (IOException ioe) {
            System.out.println("Ошибка записи в файл");
        }
        return file;
    }

    /**
     * вспомогательный метод записи задач в менеджер
     * при записи обновляется сейв-файл
     */
    private void addItemsToManager() {
        for(SubTask s : new SubTask[]{sub1, sub2, sub5, sub8, sub9}) {
            manager.addNewSub(s);
        }
        for(EpicTask e : new EpicTask[]{epic3, epic7}) {
            manager.addNewEpic(e);
        }
        for (Task t : new Task[]{task4, task6}) {
            manager.addNewTask(t);
        }
    }

}