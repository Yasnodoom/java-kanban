package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final NodeList<Task> linkedHistory = new NodeList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (historyMap.containsKey(task.getId()))
            remove(task.getId());
        linkedHistory.addLast(task.clone());
        historyMap.put(task.getId(), linkedHistory.getLast());
    }

    @Override
    public void remove(int id) {
        linkedHistory.unlinkNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(linkedHistory.getNodes());
    }

    static class NodeList<T extends Task> {
        private Node<T> head;
        private Node<T> tail;

        public void addLast(T element) {
            final Node<T> last = tail;
            final Node<T> newLast = new Node<>(element, tail, null);
            tail = newLast;
            if (last == null) {
                head = newLast;
            } else {
                last.setNext(newLast);
            }
        }

        public Node<T> getLast() {
            final Node<T> last = tail;
            if (last == null)
                throw new NoSuchElementException();
            return tail;
        }

        public List<T> getNodes() {
            ArrayList<T> nodes = new ArrayList<>();
            if (head == null) {
                return nodes;
            }

            nodes.add(head.getElement());
            Node<T> current = head;

            while (current.getNext() != null) {
                current = current.getNext();
                nodes.add(current.getElement());
            }
            return nodes;
        }

        public void unlinkNode(Node<T> removeNode) {
            Node<T> prevNode = removeNode.getPrev();
            Node<T> nextNode = removeNode.getNext();

            removeNode.setElement(null);
            if (prevNode == null) {
                head = nextNode;
            } else {
                prevNode.setNext(nextNode);
                removeNode.setPrev(null);
            }
            if (nextNode == null) {
                tail = prevNode;
            } else {
                nextNode.setPrev(prevNode);
                removeNode.setNext(null);
            }
        }
    }
}


