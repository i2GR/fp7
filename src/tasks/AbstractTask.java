package tasks;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * от абстрактного класса AbstractTask наследуются
 * Task - односложные задачи
 * {@link SubTask}
 * EpicTask - эпические:) задачи {@link EpicTask}
 * конструктор классов  задач предусматривет использование метода extendThis(int id) для инициализации/объявления спепцифических для классов полей;
 */
public abstract class AbstractTask {
    private final int id;
    private String name;
    private String description;
    private TaskType taskType;
    Status status;
    public static final LocalDateTime DEFAULT_TIME = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));
    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime = DEFAULT_TIME;

    public AbstractTask(int taskId) {
        id = taskId;
        status = Status.NEW;
    }

    public AbstractTask(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() { return startTime.plus(duration);}

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setNameForTest(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescriptionForTest(String description) {
        this.description = description;
    }

    public void setType(TaskType type) {
        this.taskType = type;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return String.format("%1$03d ", id) + this.taskType.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask that = (AbstractTask) o;
        return id == that.id && name.equals(that.name) && description.equals(that.description) && taskType == that.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskType);
    }
}