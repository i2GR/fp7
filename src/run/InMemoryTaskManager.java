package run;

import tasks.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Менеджер работы с задачами в памяти
 */
public class InMemoryTaskManager implements TaskManager {
    /**
     * счетчик для присвоения уникального идентификатора задач
     *
     * @see TaskManager#getNewTaskId()
     */
    private int idForNewTask;
    /**
     * Мапы для хранения задач по типам
     */
    private final Map<Integer, EpicTask> epics = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>
            (new Comparator<>() {
                @Override
                public int compare(Task t1, Task t2) {
                    if (t1.getStartTime().equals(t2.getStartTime()))
                        return 0;
                    return t1.getStartTime().isBefore(t2.getStartTime()) ? -1 : 1;
                }
            });
    /**
     * объект для реализации истории просмотра в памяти
     */
    private final HistoryManager history;

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
    }

    public void setIdForNewTask(int initialValue) {
        idForNewTask = initialValue;
    }
    /**
     * метод получения значения нового идентификатора задач
     * (обновляет значение)
     *
     * @return новое значения счетчика-идентификатора создаваемой задачи
     */
    public int getNewTaskId() {
        return idForNewTask++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubs() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        return getTaskCommon(id, tasks, true);
    }

    @Override
    public EpicTask getEpicById(int id) {
        return getTaskCommon(id, epics, true);
    }

    @Override
    public SubTask getSubById(int id) {
        return getTaskCommon(id, subtasks, true);
    }

    @Override
    public int addNewTask(Task task) {
        return recordTaskCommon(task, tasks);
    }

    @Override
    public int addNewEpic(EpicTask epic) {
        return recordTaskCommon(epic, epics);
    }

    @Override
    public int addNewSub(SubTask sub) {
        return recordTaskCommon(sub, subtasks);
    }

    @Override
    public int updateTask(Task task) {
        return updateTaskCommon(task, tasks);
    }

    @Override
    public int updateEpic(EpicTask epic) {
        return updateTaskCommon(epic, epics);
    }

    @Override
    public int updateSub(SubTask sub) {
        return updateTaskCommon(sub, subtasks);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            history.remove(id);
        }
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> SubsIdsToRemove = new ArrayList<>();
        epics.values().forEach(e -> SubsIdsToRemove.addAll(e.getSubs()));
        SubsIdsToRemove.forEach(this::deleteSubById);
        for (Integer id : epics.keySet()) {
            history.remove(id);
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubs() {
        for (Integer id : subtasks.keySet()) {
            // изменено  после проверки тестом
            EpicTask epic = getTaskCommon(subtasks.get(id).getOverId(), epics, false);
            if (epic != null) {
                //fp7
                epic.clearSubTasks();
            }
            history.remove(id);
        }
        subtasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        deleteTaskByIdCommon(id, tasks);
    }

    @Override
    public void deleteEpicById(int id) {
        deleteSubsOfEpic(id);
        deleteTaskByIdCommon(id, epics);
    }

    @Override
    public void deleteSubById(int id) {
        SubTask sub = getTaskCommon(id, subtasks, false);
        if (sub == null) {
            return;
        }
        EpicTask epic = getTaskCommon(sub.getOverId(), epics, false);
        if (epic != null) {
            epic.removeSubTask(sub);
        }
        deleteTaskByIdCommon(id, subtasks);
    }

    @Override
    public List<SubTask> getSubsForEpicId(int id) {
        EpicTask epic = getTaskCommon(id, epics, false);
        List<SubTask> subs = new ArrayList<>();
        if (epic != null) {
            ArrayList<Integer> subsList = (epic.getSubs());
            subsList.forEach(i -> subs.add(getTaskCommon(i, subtasks, false)));
        }
        return subs;
    }

    @Override
    public Status getTaskStatus(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            return task.getStatus();
        }
        return null;
    }

    @Override
    public Status getEpicStatus(int id) {
        if (epics.get(id) != null) {
            List<SubTask> subsList = getSubsForEpicId(id);
            if (subsList.isEmpty()) {
                return Status.NEW;
            } else {
                // гипотеза 1 при проверке: все подзадачи со статусо NEW
                boolean allSubsDone = true;
                // гипотеза 2 при проверке: все подзадачи со статусо DONE
                boolean allSubsNew = true;
                for (SubTask subTask : subsList) {
                    if (subTask != null) {
                        switch (subTask.getStatus()) {
                            case NEW:
                                // Если одна подзадача имеет статус NEW -> гипотеза 2 (все задачи "новые") не верна
                                allSubsDone = false;
                                break;
                            case IN_PROGRESS:
                                // Если одна подзадача имеет статус IN_PROGRESS -> для всего эпика статус IN_PROGRESS
                                return Status.IN_PROGRESS;
                            case DONE:
                                // Если одна подзадача имеет статус DONE -> гипотеза 1 (все задачи "завершены") не верна
                                allSubsNew = false;
                                break;
                        }
                    }
                }
                // если поддтвердилась гипотеза 1 -> статус эпика NEW
                if (allSubsDone) {
                    return Status.DONE;
                }
                // если поддтвердилась гипотеза 2 -> статус эпика DONE
                if (allSubsNew) {
                    return Status.NEW;
                }
            }
            // В общем случае статус эпика IN_PROGRESS
            return Status.IN_PROGRESS;
        }
        return Status.N_A;
    }

    @Override
    public Status getSubtaskStatus(int id) {
        SubTask task = subtasks.get(id);
        if (task != null) {
            return task.getStatus();
        }
        return null;
    }

    @Override
    public void clearSubsListOfEpic(int id) {
        EpicTask epic = epics.get(id);
        if (epic != null) {
            // fp5
            for (Integer subId : epic.getSubs()) {
                deleteTaskByIdCommon(subId, subtasks);
            }
            //fp7
            epic.clearSubTasks();
        }
    }

    @Override
    public LocalDateTime getTaskEndTime(int id) {
        Task task = getTaskCommon(id, tasks, false);
        if (task != null) {
            return task.getEndTime();
        }
        return null;
    }
    @Override
    public LocalDateTime getSubEndTime(int id) {
        SubTask sub = getTaskCommon(id, subtasks, false);
        if (sub != null) {
            return sub.getEndTime();
        }
        return null;
    }

    @Override
    public LocalDateTime getEpicEndTime(int id) {
        EpicTask epic = getTaskCommon(id, epics, false);
        if (epic != null) {
            return epic.getEndTime();
        }
        return null;
    }

    @Override
    public int addToHistory(AbstractTask task) {
        return history.add(task);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return history.getHistory();
    }


    /**
     * метод для реализации теста для присвоения имени и описания задач
     * нужен, чтобы операция не обозначалась в истории (чтобы не были задействованы методы:
     * {@link #getTaskById},
     * {@link #getEpicById},
     * {@link #getSubById},
     * а также private метод {@link #getTaskCommon} )
     *
     */
    public Map<Integer, ? extends AbstractTask> getTaskMap(TaskType taskType) {
        switch (taskType) {
            case NORM:
                return tasks;
            case EPIC:
                return epics;
            case SUBT:
                return subtasks;
        }
        return epics;
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    /**
     * метод записи задачи в Мапу
     * используется для избежания дублирования кода в методах:
     * {@link #addNewTask(Task)}
     * {@link #addNewEpic(EpicTask)}
     * {@link #addNewSub(SubTask)}
     *
     * @param item объект задачи заданного типа (Task, EpicTask, SubTask)
     * @param map  соответствующий объект Map (HashMap), в котором должна храниться задача
     * @param <T>  тип-параметр для получения нужной структуры данных Map (HashMap) па заправшиваемому типу задач
     * @return идентификатор добавленной задачи/эпика/подзадачи
     */
    private <T extends AbstractTask> int recordTaskCommon(T item, Map<Integer, T> map) {
        int id = -1;
        if (validateTimeFrame(item)) {
            try {
                id = item.getId();
            } catch (NullPointerException npe) {
                return -1;
            }
            if (map.containsKey(id)) {
                return -1;
            }
            map.put(id, item);
            try {
                prioritizedTasks.add((Task) item);
            } catch (ClassCastException cce) {
                // эпик
            }
            return id;
        }
        return id;
    }

    /**
     * метод обновления задачи в Мапе
     * используется для избежания дублирования кода в методах:
     * {@link #updateTask(Task)}
     * {@link #updateEpic(EpicTask)}
     * {@link #updateSub(SubTask)}
     *
     * @param item объект задачи заданного типа (Task, EpicTask, SubTask)
     * @param map  соответствующий объект Map (HashMap), в котором должна храниться задача
     * @param <T>  тип-параметр для получения нужной структуры данных Map (HashMap) па заправшиваемому типу задач
     * @return идентификатор добавленной задачи/эпика/подзадачи
     */
    private <T extends AbstractTask> int updateTaskCommon(T item, Map<Integer, T> map) {
        int id = -1;
        if (validateTimeFrame(item)) {
            try {
                id = item.getId();
            } catch (NullPointerException npe) {
                return -1;
            }
            if (map.containsKey(id)) {
                map.put(id, item);
                try {
                    prioritizedTasks.add((Task) item);
                } catch (ClassCastException cce) {
                    // эпик
                }
                return id;
            }
            return -1;
        }
        return id;
    }

    /**
     * метод получения задачи из Мапы
     * используется для избежания дублирования кода в методах:
     * {@link #getTaskById(int)}
     * {@link #getEpicById(int)}
     * {@link #getSubById(int)}
     *
     * @param id           идентификатор задачи, которую нужно получить
     * @param map          соответствующий объект Map (HashMap), в котором должна храниться задача (см. @see)
     * @param addToHistory параметр для определения, нужно ли помещать задачу в историю
     *                     задачи помещаются в историю только при вызовах методов:
     *                     {@link #getTaskById(int)}
     *                     {@link #getEpicById(int)}
     *                     {@link #getSubById(int)}
     *                     Данный метод вызываетя также в методе получения списка задач для "эпика":
     *                     {@link #getSubsForEpicId(int)}
     *                     при вызове этого методов задачи в историю не заносятся
     * @param <T>          тип-параметр для получения нужной структуры данных Map (HashMap) па заправшиваемому типу задач
     * @return задача нужного типа (Task, Epic, SubTask)
     * @see tasks
     * @see #epics
     * @see #subtasks
     */
    private <T extends AbstractTask> T getTaskCommon(int id, Map<Integer, T> map, boolean addToHistory) {
        T item = map.get(id);
        if (item != null && addToHistory) {
            addToHistory(item);
        }
        return item;
    }

    /**
     * общий метод удаления задач по идентификатору
     * используется для избежания дублирования кода в методах:
     * {@link #deleteTaskById(int)}
     * {@link #deleteEpicById(int)}
     * {@link #deleteSubById(int)}
     *
     * @param id  идентификатор задачи
     * @param map соответствующий объект Map (HashMap), в котором должна храниться задача
     * @param <T> тип-параметр для получения нужной структуры данных Map (HashMap) па заправшиваемому типу задач
     */
    private <T extends AbstractTask> void deleteTaskByIdCommon(int id, Map<Integer, T> map) {
        history.remove(id);
        try{
            prioritizedTasks.remove((Task) map.get(id));
        } catch (ClassCastException cce) {
            // эпик
        }
        map.remove(id);
    }

    /**
     * метод удаления подзадач из менеджера для заданного по id эпика
     *
     * @param id идентификатор эпика, подзадачи которого нужно удалять
     */
    private void deleteSubsOfEpic(int id) {
        for (Integer subId : epics.get(id).getSubs()) {
            deleteTaskByIdCommon(subId, subtasks);
        }
        epics.get(id).clearSubTasks();
    }

    /**
     * метод проверки пересечения временных промежутков задач/подзадач
     * критерий проверки: разное время старта задач
     * и окончание одной задачи не позже начала другой задачи
     * @param newItem добавляемая (обновляемая) задача
     * @return true/false: задача может быть добавлена или нет
     */
    public boolean validateTimeFrame(AbstractTask newItem) {
        if (newItem == null)
            return false;
        if (newItem.getTaskType() == TaskType.EPIC)
            return true;
        if (prioritizedTasks.isEmpty())
            return true;
        for(Task treeItem : prioritizedTasks) {
            if (treeItem.getStartTime().isBefore(newItem.getStartTime())) {
                if (treeItem.getEndTime().isBefore(newItem.getStartTime())
                    || treeItem.getEndTime().equals(newItem.getStartTime())) {
                    return true;
                }
            }
            if (newItem.getStartTime().isBefore(treeItem.getStartTime())) {
                if (newItem.getEndTime().isBefore(treeItem.getStartTime())
                    || newItem.getEndTime().equals(treeItem.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }
}