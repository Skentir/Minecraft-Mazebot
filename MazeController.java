import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import java.util.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;

public class MazeController implements EventHandler<ActionEvent>, Explorer
{
  private MazeGui gui;
  private ArrayList<Node> path;
  private int dimension;
  private Maze maze;
  private char[][] map;
  private Space start;
  private Space end;

  public MazeController(MazeGui gui)
  {
    this.gui = gui;
    gui.addActionListener(this);
    path = new ArrayList<>();
  }
  @Override
  public void handle(ActionEvent event)
  {
    EventTarget target = event.getTarget();
    if (target instanceof javafx.scene.Node)
    {
      javafx.scene.Node node = (javafx.scene.Node)target;
    //  Alert alert;
      Optional<ButtonType> result;
      switch(node.getId())
      {
        case "get-started":
          gui.setScene(MazeGui.MAZE);
          break;
         /* Start A* Algorithm */
        case "start-maze":
          gui.setScene(MazeGui.MAIN_MENU);
          break;
      }
    }
  }

  public void beginSolving()
  {
    start = gui.getStartSpace();
    end = gui.getEndSpace();
    System.out.printf ("%d %d, %d %d\n", start.getX(), start.getY(), end.getX(), end.getY());
    Maze m1 = new Maze(21,start, end);

    Space[][] grid = gui.getGrid();

    for (int i = 0; i < 21; i++)
      for (int j = 0; j < 21; j++)
      {
        if (grid[i][j].isWall())
          m1.setMazeWall(i,j);
        System.out.printf ("%d %d %b\n", i, j, grid[i][j].isWall());
      }
    // m1.setMazeWall(2,0);
    // m1.setMazeWall(3,0);
    // m1.setMazeWall(1,2);
    // m1.setMazeWall(2,2);
    // m1.setMazeWall(3,2);
    // m1.setMazeWall(4,2);
    // m1.setMazeWall(4,3);
    // m1.setMazeWall(1,4);
    // m1.setMazeWall(2,2);

    new Thread(() -> { AStar a1= new AStar(this, m1, true); a1.solve(); }).start();

    //System.out.println(a1.solve());
  }

  // called when the A* algorithm explores a new tile
  public void onExplore(Solver state) {
    // this is where you update the tiles and gui
    AbstractCollection<Space> explored = state.getExplored();
    for (Space space : explored)
      gui.updateTile(space.getX(), space.getY(), 0);
  }

  @Override
  public void onPathFound(Node solution) {
    // display the path
    Node current = solution.getParent();

    while (current.hasParent()) {
      Maze content = current.getContent();
      Space space = content.getCurrentSpace();

      try {
        Thread.sleep(20);
      } catch (Exception ex) {

      }
      gui.updateTile(space.getX(), space.getY(), 1);

      current = current.getParent();
    }
  }
}
