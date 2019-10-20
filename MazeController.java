import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;

public class MazeController implements EventHandler<ActionEvent>
{
  private MazeGui gui;
  private ArrayList<Node> path;

  public MazeController(MazeGui gui)
  {
    this.gui = gui;
    gui.addActionListener(this);
    path = new ArrayList<>();
  }
  
}
