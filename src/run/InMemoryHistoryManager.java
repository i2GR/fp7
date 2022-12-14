package run;

import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * класс реализации методов HistoryManager
 * для управления историей просмотра в оперативной памяти при работе InMemoryTaskManager
 *
 * @see HistoryManager
 * @see InMemoryTaskManager
 */
public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final HashMap<Integer, Node> historyMap = new HashMap<>();

    @Override
    public int add(AbstractTask task) {
        int id = task.getId();
        if (historyMap.containsKey(id)) {
            remove(id);
        }
        return linkLast(task);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return getTasks();
    }

    /**
     * добавление информации о просмотре задачи в конец списка
     *
     * @param task задача как экз. AbstractTask
     * @return идентификатор задачи
     */
    private int linkLast(AbstractTask task) {
        int id = task.getId();
        Node l = last;
        Node newNode = new Node(l, task, null);
        historyMap.put(id, newNode);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        return id;
    }

    /**
     * метод реализации получения списка задач из истории задач
     * итерирует по задачам, начиная с последней, занесенной в историю
     *
     * @return сформированный список задач (List)
     */
    private List<AbstractTask> getTasks() {
        List<AbstractTask> taskList = new ArrayList<>();
        Node node = last;
        while (node != null) {
            taskList.add(node.item);
            node = node.prev;
        }
        return taskList;
    }

    @Override
    public AbstractTask remove(int id) {
        return removeNode(historyMap.remove(id));
    }

    /**
     * метод реализации удаления задачи из истории
     * по аналогии с методом remove() класса LinkedList
     *
     * @param node - узел связанного списка
     * @return удаляемая задача как переменная AbstractTask или null
     */
    private AbstractTask removeNode(Node node) {
        if (node != null) {
            Node prevNode = node.prev;
            Node nextNode = node.next;
            if (prevNode != null) {
                prevNode.next = nextNode;
            } else {
                first = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            } else {
                last = prevNode;
            }
            node.prev = null;
            node.next = null;
        return node.item;
        }
        return null;
    }

    /**
     * внутренний класс (Nested Static Class) узла связанного списка
     * по аналогии с узлом класса LinkedList
     */
    private static class Node {
        AbstractTask item;
        Node next;
        Node prev;

        Node(Node prev, AbstractTask item, Node next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }

        @Override
        public String toString() {
            String p = (prev != null) ? String.format("%03d", prev.item.getId()) : "bgn";
            String n = (next != null) ? String.format("%03d", next.item.getId()) : "end";
            return '[' + p + "]->[" + String.format("%03d", item.getId()) + "]->[" + n + ']';
        }
    }
}