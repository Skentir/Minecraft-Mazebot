import java.util.*;

public abstract class Solver { // Parent class for implementing algorithms
    protected Maze maze;
    protected String result;
    protected AbstractCollection<Node<Maze>> frontier;
    protected AbstractCollection<Space> closedSpaces;
    protected int nodesCounter;
    protected int pathLength;
    protected boolean manhattan;

    public abstract String solve();

    public abstract LinkedList<Node<Maze>> getNextSpaces(); // Function obtains the adjacent spaces that can be traversed

    public abstract String getResult(); // This returns the result of an algorithm

    public abstract AbstractCollection<Node<Maze>> getFrontier();

}
