package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import run.InMemoryHistoryManager;
import tasks.AbstractTask;
import tasks.EpicTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * тест публичных методов InMemoryHistoryManager (для финального проекта спринта 5)
 * тест метода getHistory - совместно с add(AbstractTask)
 * {@link run.InMemoryHistoryManager#getHistory()}
 * {@link run.InMemoryHistoryManager#add(AbstractTask)}
 *
 * тест метода remove(int)
 * {@link run.InMemoryHistoryManager#remove(int)}
 *
 * в классе InMemoryHistoryManager поля и класс для хранения списка задач приватные,
 * поэтому отдельно публичный метод add(AbstractTask) с текущим уровнем знаний проверить сложно
 */
public class InMemoryHistoryManagerTest {
    int taskNumber = 1 << 3; // не меньше 8
    InMemoryHistoryManager history = new InMemoryHistoryManager();
    AbstractTask[] initialArr = new AbstractTask[taskNumber];
    AbstractTask[] reversedArr = new AbstractTask[taskNumber];

    /**
     * для тестов используется массив задач для сравнения
      */
    @Before
    public void setup() {
        System.out.println("Тест с кол-вом задач: " + taskNumber);
        // создание исходного массива  с задачами #initialArr размера [taskNumber]
        // создание массива #reversedArr с ожидаемым выводом на основе исходного массива c обратным порядком задач
        // (последняя задача в истории задач должна быть на первой позиции)
        for (int i = 0; i < taskNumber ; i++) {
            // 50/50 Normal Task/EpicTask
            AbstractTask task = (i < taskNumber/2) ? new Task(i) : new EpicTask(i);
            task.setNameForTest("name " + i);
            task.setDescriptionForTest("descr " + i);
            initialArr[i] = task;
            reversedArr[taskNumber -1 - i] = task;
        }
        System.out.println("Посл-ть задач: " + Arrays.toString(initialArr));
    }

    /**
     * тест getHistory()
     * {@link run.InMemoryHistoryManager#getHistory()}
     * задачи добавляются в историю заданым методом add(task)
     * список (List) задач преобразуется в массив
     */
    @Test
    public void getHistory() {
        // добавление задач методом add(AbstractTask) в историю из массива initialArr
        for (int i = 0; i < taskNumber ; i++) {
            history.add(initialArr[i]);
        }

        //преобразование в массив списка, получаемого тестируемым методом getHistory()
        List<AbstractTask> l = history.getHistory();

        AbstractTask[] actSequence = l.toArray(new AbstractTask[]{});
        System.out.println("История задач: " + Arrays.toString(actSequence));
        // проверка утверждения
        Assert.assertArrayEquals(reversedArr, actSequence);

    }

    /**
     * тест удаления задачи из "середины" истории
     * {@link run.InMemoryHistoryManager#remove(int)}
     */
    @Test
    public void removeInners() {
        // создание массива задач с ожидаемым результатом на основе массива c обратным порядком задач
        List<AbstractTask> listRef = new ArrayList<>(List.of(reversedArr));
        // исключение задач с заданным идентификатором
        listRef.remove(initialArr[2]);
        listRef.remove(initialArr[3]);
        listRef.remove(initialArr[6]);
        AbstractTask[] ArrRef = listRef.toArray(new AbstractTask[]{});
        System.out.println("Посл-ть задач: " + Arrays.toString(ArrRef));
        // добавление задач методом add(AbstractTask) в историю из массива initialArr
        for (int i = 0; i < taskNumber ; i++) {
            history.add(initialArr[i]);
        }

        // исключение задач тестируемым методом
        history.remove(2);
        history.remove(3);
        history.remove(6);

        List<AbstractTask> listAct = history.getHistory();
        AbstractTask[] actSequence = listAct.toArray(new AbstractTask[]{});
        // проверка утверждения
        Assert.assertArrayEquals(ArrRef, actSequence);
    }

    /**
     * тест удаления первой и последней задачи из истории
     * {@link run.InMemoryHistoryManager#remove(int)}
     */
    @Test
    public void removeFirstAndLast() {
        // создание массива задач с ожидаемым результатом на основе массива c обратным порядком задач
        List<AbstractTask> listRef = new ArrayList<>(List.of(reversedArr));
        // исключение задач с заданным идентификатором
        listRef.remove(initialArr[0]);
        listRef.remove(initialArr[taskNumber - 1]);
        AbstractTask[] ArrRef = listRef.toArray(new AbstractTask[]{});
        System.out.println("Посл-ть задач: " + Arrays.toString(ArrRef));
        // добавление задач методом add(AbstractTask) в историю из массива initialArr
        for (int i = 0; i < taskNumber ; i++) {
            history.add(initialArr[i]);
        }

        // исключение задач тестируемым методом
        history.remove(0);
        history.remove(7);

        List<AbstractTask> listAct = history.getHistory();
        AbstractTask[] actSequence = listAct.toArray(new AbstractTask[]{});
        // проверка утверждения
        Assert.assertArrayEquals(ArrRef, actSequence);
    }

    /**
     * тест удаления задачи, не представленной в истории
     * {@link run.InMemoryHistoryManager#remove(int)}
     */
    @Test
    public void removeAbsent() {
        // добавление задач методом add(AbstractTask) в историю из массива initialArr
        for (int i = 0; i < taskNumber ; i++) {
            history.add(initialArr[i]);
        }
        // исключение задачи не находящейся в истории тестируемым методом
        history.remove(12);

        List<AbstractTask> listAct = history.getHistory();
        AbstractTask[] actSequence = listAct.toArray(new AbstractTask[]{});
        Assert.assertArrayEquals(reversedArr, actSequence);
    }
}