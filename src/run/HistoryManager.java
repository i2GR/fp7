package run;

import tasks.AbstractTask;

import java.util.List;

/**
 * интерфейс управления историей просмотров
 * в истории просмотров отображабтся любые типы задач единым списком
 */
public interface HistoryManager {

    /**
     * внесение задачи как просмотренную
     *
     * @param task класс-родитель для всех типов задач
     * @return идентификатор добавляемой задачи
     */
    int add(AbstractTask task);

    /**
     * удаление задачи из истории просмотра
     *
     * @param id идентификатор задачи
     * @return удаляемая задача
     */
    AbstractTask remove(int id);

    /**
     * получение списка задач
     *
     * @return список задач
     */
    List<AbstractTask> getHistory();
}