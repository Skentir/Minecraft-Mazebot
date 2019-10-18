public class Node<T> {
    private T value;
    private Node<T> parent;

    //Bind content of c to the node
    public Node(T c) {
        this.value = c;
    }

    //Returns the content bound to the node
    public T getContent() {
        return value;
    }

    //Set content bound to the note
    public void setContent(T value) {
        this.value = value;
    }

    //Return father Node from current Node
    public Node<T> getParent() {
        return parent;
    }

    //Set parent of this Node
    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    //Returns true if Node has parent
    public boolean hasParent() {
        return this.parent != null;
    }

    //Returns link parent -> child in a String
    public String printParent() {
        return this.value.toString() + " <- " + this.parent.value.toString();
    }

    @Override
    public String toString() {
        return this.getContent().toString();
    }

}
