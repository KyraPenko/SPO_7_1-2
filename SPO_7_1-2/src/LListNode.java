public class LListNode<T> {
    private T value;
    LListNode<T> next;

    public LListNode() {
    }

    public LListNode(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public LListNode<T> getNext() {
        return next;
    }

    public void setNext(LListNode<T> next) {
        this.next = next;
    }
}
