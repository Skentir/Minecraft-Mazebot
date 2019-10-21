import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;

public class MazeController implements EventHandler<ActionEvent>
{
  private MazeGui gui;
  private ArrayList<Node> path;
  private int dimension;
  private Maze maze;
  private char[][] map;

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
    if (target instanceof Node)
    {
      Node node = (Node)target;
    //  Alert alert;
      Optional<ButtonType> result;
      switch(node.getId())
      { case "get-started":
          gui.setScene(MazeGui.MAIN_MENU);
          break;
         /* Start A* Algorithm */
        case "start-maze":
          gui.setScene(MazeGui.MAZE);
          break;
      }
    }
  }


  private void buildMap()
  {
    for (int i = 0; i< map.length; ++i)
    {
      for (int j=0; j< map.length; ++j)
      {

        /* Makes all the sides walls */
        if (j == 0 || (i == 0 || i == map.length-1) || (j == map.length-1))
          map[i][j] = 'W';


        /* Makes sure the outer area isn't covered */
        else if ((j == 1 && (i != 0 && i != map.length-1)) ||
            ((i == 1 || i == map.length-2) && j != map.length-1) ||
            (j == map.length-2 && (i != 0 && i != map.length-1))) {
          map[i][j] = 'S';
        }

        else { /* Makes the rest of the spots open */
          map[i][j] = 'S';
          /* Integer pos = i * map.length + j;
          openSpots.add(pos); */
        }
      }
    }
  }

}
