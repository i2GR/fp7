package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * наследуется от класса Task
 * имеет дополнительное поле идентификатор эпик-задача, к которой принаджелиж экз. подзадачи
 * Имеет Status
 */
public class SubTask extends Task {
    private final int overId;

    public SubTask(int thisId, int overId) {
        super(thisId);
        this.overId = overId;

        setType(TaskType.SUBT);
    }

    public SubTask(int id, int overId, String name, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, description, duration, startTime);
        this.overId = overId;
        setType(TaskType.SUBT);
    }

    public int getOverId() {
        return overId;
    }

    @Override
    public String toString() {
        return super.toString()
                + " [" + overId + ']';
    }
}