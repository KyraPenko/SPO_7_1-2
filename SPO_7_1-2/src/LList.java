public class LList<T> {
    LListNode<T> root;

    public void add(T item)
    {
        if(root == null)
        {
            root = new LListNode<T>(item);
        }
        else
        {
            LListNode<T> iter = root;
            while(iter.getNext() != null)
                iter = iter.getNext();
            iter.setNext(new LListNode<>(item));
        }
    }

    public T get(int number)
    {
        int c = 0;
        LListNode<T> iter = root;
        while(iter != null && c < number)
        {
            iter = iter.getNext();
            c++;
        }
        if(iter != null)
            return iter.getValue();
        return null;
    }

    @Override
    public String toString()
    {
        LListNode<T> iter = root;
        StringBuilder sb = new StringBuilder("[");
        while(iter != null)
        {
            sb.append(iter.getValue().toString() + " ");
            iter = iter.getNext();
        }
        sb.append("]");
        return sb.toString();
    }
}
