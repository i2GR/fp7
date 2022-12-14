package tasks;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Наследуется напрямую от ItemToDo, т.к. отличается от обычной задачи @see {@link SubTask}:
 * не хранит статус;
 * хранит список подзадач.
 */
public class EpicTask extends AbstractTask {
    private final TreeSet<Integer> subsIds = new TreeSet<>();

    public EpicTask(int taskId) {
        super(taskId);
        setType(TaskType.EPIC);
    }

    public EpicTask(int id, String name, String description) {
        super(id, name, description);
        setType(TaskType.EPIC);
        setStatus(Status.N_A);
    }

    public EpicTask(int id, String name, String description, SubTask... subs) {
        super(id, name, description);
        setType(TaskType.EPIC);
        addSubTask(subs);
        setStatus(Status.N_A); //статус "заглушка"
    }

    public void addSubTask(SubTask... subs) {
        for(SubTask sub : subs) {
            if (sub.getOverId() == getId() && subsIds.add(sub.getId())) {
                plusDuration(sub.getDuration());
                updateStartTime(sub.getStartTime(), sub.getDuration(), true);
            }
        }
    }

    public void removeSubTask(SubTask... subs) {
        for(SubTask sub : subs) {
            if (sub.getOverId() == getId()) {
                subsIds.remove(sub.getId());
                minusDuration(sub.getDuration());
                updateStartTime(sub.getStartTime(), sub.getDuration(), false);
            }
        }
    }

    public void clearSubTasks() {
        subsIds.clear();
        setDuration(Duration.ZERO);
        setStartTime(EpicTask.DEFAULT_TIME);
    }

    @Override
    public void setStatus(Status status) {
       this.status = Status.N_A; //статус "заглушка"
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        if (!subsIds.isEmpty()) {
            super.setStartTime(startTime);
        }
    }

    @Override
    public void setDuration(Duration duration) {
        if (!subsIds.isEmpty()) {
            super.setDuration(duration);
        } else {
            super.setDuration(Duration.ZERO);
        }
    }
    @Override
    public LocalDateTime getStartTime() {
        if (!subsIds.isEmpty()) {
            return super.getStartTime();
        }
        return EpicTask.DEFAULT_TIME;
    }

    @Override
    public String toString() {
        return super.toString()
                + " "
                + (subsIds.isEmpty() ? "[NoSubs]" : Arrays.toString(subsIds.toArray(new Integer[]{})));
    }

    public ArrayList<Integer> getSubs() {
        return new ArrayList<>(subsIds);
    }

    /**
     * добавление длительности при добавлении подзадачи
     * @param subDuration длительность подзадачи
    */
    private void plusDuration(Duration subDuration) {
        setDuration(this.getDuration().plus(subDuration));
    }

    /**
     * уменьшение длительности при удалении подзадачи
     * @param subDuration длительность подзадачи
     */
    private void minusDuration(Duration subDuration) {
        setDuration(this.getDuration().minus(subDuration));
    }

    /**
     * обновление старта при добавлении/удалении подзадачи
     *
     * @param subStartTime старт подзадачи
     * @param subDuration длительность подзадачи
     * @param addSub добавление подзадачи к эпику/удаление подзадачи из эпика
     */
    private void updateStartTime(LocalDateTime subStartTime, Duration subDuration, boolean addSub) {
        if (addSub) {
            // подзадача была только что добавлена
            if (subsIds.size() == 1) {
                setStartTime(subStartTime);
                return;
            }
            if (getStartTime().isAfter(subStartTime)) {
                setStartTime(subStartTime);
            }
        } else {
            if (subsIds.isEmpty()) {
                setStartTime(EpicTask.DEFAULT_TIME);
                return;
            }
            if (getStartTime().isEqual(subStartTime)) {
                setStartTime(getStartTime().plus(subDuration));
            }
        }
    }
}