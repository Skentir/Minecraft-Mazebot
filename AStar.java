import java.util.*;

/*

Further considerations
- if f(n) is equal
- manhattan or euclidean distance
* */

public class AStar extends Solver {

    public AStar(Explorer explorer, Maze maze, Boolean manhattan) {
        this.explorer = explorer;
        this.maze = maze;
        this.result = "";
        this.manhattan = manhattan;
        this.maze.getStart().assignMaze(this.maze);
        this.fringe = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node s1, Node s2) {
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
        this.explored = new HashSet<>();

    }

    public String solve() {
        this.maze.initMaze(); //Re-init maze

        Boolean endfound = false; // Initially false until end state is reached
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
        this.fringe.clear(); //Clear fringe Queue
        ((PriorityQueue<Node>)this.fringe).offer(new Node(this.maze)); //Adding the first node (Start node) (G is at 0, Start to Start = 0)
        this.explored.clear(); //Clear explored

        //Measure run time
        long startTime = System.currentTimeMillis();

        while(!endfound)
        {
            if(this.fringe.isEmpty())
                break;
            else
            {
                Node current = ((PriorityQueue<Node>) this.fringe).remove(); //Remove current most optimal path
                this.maze = (Maze) current.getContent();
                Space currState = this.maze.getCurrentSpace();

                if(currState.getX() == this.maze.getEnd().getX() && currState.getY() == this.maze.getEnd().getY()) // Goal state is reached
                {
                    Node temp = new Node(this.maze);
                    temp.setParent(current);
                    ((PriorityQueue<Node>) this.fringe).offer(temp);
                    endfound = true;
                }

                else
                {
                    ArrayList<Node> nexts = this.getNextSpaces(); // Do not get spaces with walls
//                    this.explored.add(currState);
                    if(!this.explored.contains(currState.getPoint())) {
                        this.explored.add(currState.getPoint());
                        currState.setAttribute("*");
                    }

                    ArrayList<Node> x = new ArrayList<Node>();
                    for(int i = 0; i < nexts.size(); i++)
                        x.add(nexts.get(i));

                    for(int i= 0 ; i < x.size() ; i++) {
                        Node neighbor = x.get(i);

                        if(!this.explored.contains(neighbor.getContent().getCurrentSpace().getPoint())) { //Do not re-explore paths already explored
                      //      if(!this.fringe.contains(neighbor)) {  // Do not add paths already in fringe (possibility of being explored)
                                neighbor.setParent(current);
                                ((PriorityQueue<Node>) this.fringe).offer(neighbor);
                                this.nodesCounter++;
                    //        }
                        }
                    }

                    // notify the gui and show explored tiles
                    try {
                        Thread.sleep(20);
                    } catch (Exception ex) {

                    }

                    explorer.onExplore(this);
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
            // this.maze.resetGrid();
            Node revertedTree = ((PriorityQueue<Node>) this.fringe).remove();
            explorer.onPathFound(revertedTree);

            revertedTree = revertedTree.getParent();
            this.result += "Path: " + this.maze.getEnd().toString() + "(End) <- ";
            this.pathLength++;

            while(revertedTree.hasParent()) {
                Maze temp = revertedTree.getContent();
                Space state = temp.getCurrentSpace();

                if(!state.equals(this.maze.getEnd()))
                {
                    this.result += state.toString() + " <- ";
                    this.maze.getGrid()[state.getY()][state.getX()].setAttribute("*");
                    pathLength++;
                }
                revertedTree = revertedTree.getParent();
            }

            pathLength--; // Since first node has cost 0

            this.result += this.maze.getStart().toString() + "(Start) \n" + "Path length: " + this.pathLength + "\nNumber of nodes created: " + this.nodesCounter + "\nExecution time: " + time/1000d + " seconds\n";
            this.result += this.maze.printMaze();
        }
        else
        {
            this.result += "Failed : Unable to go further and/or end is unreachable.";
        }

        return this.result;
    }

    public ArrayList<Node> getNextSpaces() {
        ArrayList<Node> res = new ArrayList<Node>();

        ArrayList<Maze> nexts = this.maze.getCurrentSpace().getNexts();

        int gCurrent = this.maze.getCurrentSpace().getG();

        for(int i = 0; i < nexts.size(); i++) {
            Space temp = nexts.get(i).getCurrentSpace();
            if(!this.explored.contains(temp.getPoint()))
				if (manhattan)
					nexts.get(i).getCurrentSpace().calcManhattanH();
				else
					nexts.get(i).getCurrentSpace().calcEuclidH();

            nexts.get(i).getCurrentSpace().incG(gCurrent);
            nexts.get(i).getCurrentSpace().calcF();

            Node tempNode = new Node(nexts.get(i));
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

    public AbstractCollection<Node> getFringe() {
        return this.fringe;
    }
    public AbstractCollection<Point> getExplored() { return this.explored; }
}
