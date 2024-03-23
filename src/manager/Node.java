package manager;

import java.util.Objects;

public class Node<T> {
    private T element;
    private Node<T> prev;
    private Node<T> next;

    public Node(T element, Node<T> prev, Node<T> next) {
        this.element = element;
        this.prev = prev;
        this.next = next;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node<?> node)) return false;

        if (!Objects.equals(element, node.element)) return false;
        if (!Objects.equals(prev, node.prev)) return false;
        return Objects.equals(next, node.next);
    }

    @Override
    public int hashCode() {
        int result = element != null ? element.hashCode() : 0;
        result = 31 * result + (prev != null ? prev.hashCode() : 0);
        result = 31 * result + (next != null ? next.hashCode() : 0);
        return result;
    }
}
