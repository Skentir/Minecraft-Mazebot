import java.util.*;

public class AStar extends Solver{
    public AStar(Maze maze, Boolean manhattan) {
        this.maze = maze;
        this.result = "";
        this.manhattan = manhattan;
        this.maze.getStart().assignMaze(this.maze);
        this.frontier = new PriorityQueue<Node<Maze>>(new Comparator<Node<Maze>>() {
            @Override
            public int compare(Node<Maze> s1, Node<Maze> s2) {
                Double sf1 = s1.getContent().getCurrentSpace().getF();
                Double sf2 = s2.getContent().getCurrentSpace().getF();
                Double sh1 = s1.getContent().getCurrentSpace().getH();
                Double sh2 = s2.getContent().getCurrentSpace().getH();

                if(sf1 > sf2)
                    return 1;
                else if(sf1 == sf2) {
                    if(sh1 > sh2)
                        return 1;
                    else if(sh1 == sh2)
                        return 0;
                    else
                        return -1;
                }
                else
                    return -1;
            }
        });
        this.closedSpaces = new PriorityQueue<Space>(new Comparator<Space>() {
            @Override
            public int compare(Space s1, Space s2) {
                double sf1 = s1.getF();
                double sf2 = s2.getF();
                double sh1 = s1.getH();
                double sh2 = s2.getH();

                if(sf1 > sf2)
                    return 1;
                else if(sf1 == sf2) {
                    if(sh1 > sh2 )
                        return 1;
                    else if(sh1 == sh2)
                        return 0;
                    else
                        return -1;
                }
                else
                    return -1;
            }
        });

    }

    public String solve() {
        this.maze.initMaze(); //Re-init maze

        Boolean endfound = false;
        this.nodesCounter = 0;
        this.pathLength = 0;

        //Compute F value of Starting square
        if(manhattan) {
            this.maze.getStart().calcManhattanH();
        }
        else {
            this.maze.getStart().calcEuclidH();
        }

        this.maze.getStart().calcF();

        //Init data structures
        this.frontier.clear(); //Clear frontier Queue
        ((PriorityQueue<Node<Maze>>) this.frontier).offer(new Node<Maze>(this.maze)); //Adding the first node (Start node) (G is at 0, Start to Start = 0)
        this.closedSpaces.clear(); //Clear closedSquares

        //Measure run time
        long startTime = System.currentTimeMillis();

        while(!endfound)
        {
            if(this.frontier.isEmpty())
                break;
            else
            {
                Node<Maze> current = ((PriorityQueue<Node<Maze>>) this.frontier).remove();
                this.maze = (Maze) current.getContent();
                Space currState = this.maze.getCurrentSpace();

                if(currState.getX() == this.maze.getEnd().getX() && currState.getY() == this.maze.getEnd().getY())
                {
                    Node<Maze> temp = new Node<Maze>(this.maze);
                    temp.setParent(current);
                    ((PriorityQueue<Node<Maze>>) this.frontier).offer(temp);
                    endfound = true;
                }

                else
                {
                    LinkedList<Node<Maze>> nexts = this.getNextSpaces();
                    if(!this.closedSpaces.contains(currState))
                    {
                        this.closedSpaces.add(currState);
                        currState.setAttribute("*");
                    }

                    Iterator<Node<Maze>> x = nexts.iterator();

                    while(x.hasNext())
                    {
                        Node<Maze> neighbor = x.next();

                        if(this.closedSpaces.contains(neighbor.getContent().getCurrentSpace()))
                            continue;
                        else
                        {
                            if(!this.frontier.contains(neighbor))
                            {
                                neighbor.setParent(current);
                                ((PriorityQueue<Node<Maze>>) this.frontier).offer(neighbor);
                                this.nodesCounter++;
                            }
                        }
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();

        long time = endTime - startTime;

        if(this.manhattan)
            this.result = "    ___                    __  ___            __          __  __            \r\n" +
                    "   /   | __/|_            /  |/  /___ _____  / /_  ____ _/ /_/ /_____ _____ \r\n" +
                    "  / /| ||    /  ______   / /|_/ / __ `/ __ \\/ __ \\/ __ `/ __/ __/ __ `/ __ \\\r\n" +
                    " / ___ /_ __|  /_____/  / /  / / /_/ / / / / / / / /_/ / /_/ /_/ /_/ / / / /\r\n" +
                    "/_/  |_||/             /_/  /_/\\__,_/_/ /_/_/ /_/\\__,_/\\__/\\__/\\__,_/_/ /_/ \n";
        else
            this.result = "    ___                    ______           ___     __\r\n" +
                    "   /   | __/|_            / ____/_  _______/ (_)___/ /\r\n" +
                    "  / /| ||    /  ______   / __/ / / / / ___/ / / __  / \r\n" +
                    " / ___ /_ __|  /_____/  / /___/ /_/ / /__/ / / /_/ /  \r\n" +
                    "/_/  |_||/             /_____/\\__,_/\\___/_/_/\\__,_/   \n";

        if(endfound)
        {
            this.maze.resetGrid();
            Node<Maze> revertedTree = ((PriorityQueue<Node<Maze>>) this.frontier).remove();

            revertedTree = revertedTree.getParent();
            this.result += "Path: " + this.maze.getEnd().toString() + "(End) <- ";
            this.pathLength++;

            while(revertedTree.hasParent())
            {
                Maze temp = revertedTree.getContent();
                Space state = temp.getCurrentSpace();

                if(!state.equals(this.maze.getEnd()))
                {
                    this.result += state.toString() + " <- ";
                    this.maze.getGrid()[state.getY()][state.getX()].setAttribute("*");
                    this.pathLength++;
                }
                revertedTree = revertedTree.getParent();
            }

            this.result += this.maze.getStart().toString() + "(Start) \n" + "Path length: " + this.pathLength + "\nNumber of nodes created: " + this.nodesCounter + "\nExecution time: " + time/1000d + " seconds\n";
            this.result += this.maze.printMaze();
        }
        else
        {
            this.result += "Failed : Unable to go further and/or end is unreachable.";
        }

        return this.result;
    }

    public LinkedList<Node<Maze>> getNextSpaces() {
        LinkedList<Node<Maze>> res = new LinkedList<Node<Maze>>();

        LinkedList<Maze> nexts = this.maze.getCurrentSpace().getNexts();

        int gCurrent = this.maze.getCurrentSpace().getG();

        for(int i = 0; i < nexts.size(); i++) {
            Space temp = nexts.get(i).getCurrentSpace();
            if(!this.closedSpaces.contains(temp))
                nexts.get(i).getCurrentSpace().calcManhattanH();
            else
                nexts.get(i).getCurrentSpace().calcEuclidH();

            nexts.get(i).getCurrentSpace().incG(gCurrent);
            nexts.get(i).getCurrentSpace().calcF();

            Node<Maze> tempNode = new Node<Maze>(nexts.get(i));
            res.add(tempNode);

        }
        return res;
    }

    public String getResult() {
        if(this.result == "")
            return "No resolution computed, use the solve method first";
        else
            return this.result;
    }

    public AbstractCollection<Node<Maze>> getFrontier() {
        return this.frontier;
    }
}
