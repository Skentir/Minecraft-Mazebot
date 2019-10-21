import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import java.util.Optional;

public class MazeGui extends Application
{
  Stage stage;
  Solver solver;
  private Scene startScene, mazeScene;
  public static final String MAIN_MENU = "MENU";
  public static final String MAZE = "MAZE";
  /*
  * To set a 15 by 15 tile map:
  * x_tiles = 600/40 = 15
  * y_tiles = 600/40 = 15
  */
  private static final int TILE_SIZE = 40;
  private int MAP_WIDTH = 600;
  private int MAP_HEIGHT = 600;
  private int X_TILES = MAP_WIDTH/TILE_SIZE;
  private int Y_TILES = MAP_HEIGHT/TILE_SIZE;
  private char[][] map;

  private ImageView botView;    /* AI bot that will traverse */
  private ImageView wallView;   /* wall nodes */
  private ImageView spaceView;  /* unexplored node*/
  private ImageView pathView;   /* explored node */

  public MazeGui()
  {
  }

  @Override
  public void start(Stage currentStage)
  {
      stage = currentStage;
      createScenes();
      MazeController controller = new MazeController(this);
      currentStage.setScene(startScene);
      currentStage.setMaximized(true);
      currentStage.setTitle("Minecraft Maze Bot");
      currentStage.setResizable(false);
      currentStage.show();
  }
  private void createScenes()
  {
      Label welcomeLabel = new Label();
      /*Insert path to title screen logo*/
      ImageView title = new ImageView(new Image("assets/titlescreen.png"));
  //    Button getStartedButton = new Button();
      /* Assign CSS script to make button pretty */
  //    getStartedButton.setStyle("-fx-font-weight: bold; -fx-background-color: #8fe1a2; -fx-text-fill: black;");
  //    getStartedButton.setText("Get Started");
      /* Assign id to connect to the controller */
  //    getStartedButton.setId("get-started"); *
      ImageView getStartedButton = new ImageView(new Image("assets/mcbutton.png",150,40, false, false));
      getStartedButton.setId("get-started");
      getStartedButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
        this.setScene(MAZE);
      });
      BorderPane mainPane = new BorderPane();
      VBox mainBox = new VBox();
      mainBox.setAlignment(Pos.CENTER);
      mainBox.getChildren().addAll(title, getStartedButton);
      BackgroundImage titlebg = new BackgroundImage(new Image("assets/titlebg.jpg"),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
      mainPane.setBackground(new Background(titlebg));
      mainPane.setStyle("-fx-font-size: 2em;");
      mainBox.setMargin(getStartedButton, new Insets(15, 0, 0, 0));
      mainPane.setCenter(mainBox);
      startScene = new Scene(mainPane, 700, 500);


      GridPane mazePane = new GridPane();
      BackgroundImage mazebg = new BackgroundImage(new Image("assets/dirtbg.png"),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
      mazePane.setBackground(new Background(mazebg));
      mazeScene = new Scene(mazePane, 700, 500);
  }

  private class Tile extends StackPane
  {
      private int x,y;
      private boolean isWall;
      private Rectangle border = new Rectangle(TILE_SIZE-2, TILE_SIZE-2);
      private ImageView block;

      public Tile(int x, int y, boolean isWall)
      {
        this.x = x;
        this.y = y;
        this.isWall = isWall;
        border.setStroke(black);
      }
  }

  public void setScene(String scene)
  {
    if (scene == MAIN_MENU)
      stage.setScene(startScene);
    else if (scene == MAZE)
      stage.setScene(mazeScene);
  }

  private void attachHandlerToPane(Pane pane, EventHandler<ActionEvent> handler)
  {
    for (Node node : pane.getChildren())
    {
        if (node instanceof Button)
        {
            Button button = (Button)node;
            button.setOnAction(handler);
        }
        else if (node instanceof Pane)
            attachHandlerToPane((Pane)node, handler);
      }
  }

  private void attachHandlerToScene(Scene scene, EventHandler<ActionEvent> handler)
  {
    Parent root = scene.getRoot();
    if (root instanceof Pane)
    {
        attachHandlerToPane((Pane)root, handler);
    }
  }

  public void addActionListener(EventHandler<ActionEvent> handler)
  {
    attachHandlerToScene(startScene, handler);
    attachHandlerToScene(mazeScene, handler);
  }

  public void addMouseListener(EventHandler<MouseEvent> handler)
  {
  //    itemsList.setOnMouseClicked(handler);
  }

  public static void main(String[] args)
    {
        launch(args);
    }


}
