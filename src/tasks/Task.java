package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Обычная задача наследуется от класса AbstractTask
 * имеет Status
 */
public class Task extends AbstractTask {

    public Task(int taskId) {
        super(taskId);
        setStatus(Status.NEW);
        setType(TaskType.NORM);
    }

    public Task(int id, String name, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, description);
        setStatus(Status.NEW);
        setType(TaskType.NORM);
        setStartTime(startTime);
        setDuration(duration);
    }

    @Override
    public String toString() {
        return super.toString()
                + " [" + getStatus().toString() + ']';
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}