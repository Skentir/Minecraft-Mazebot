import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
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
  private static final int TILE_SIZE = 30;
  private int MAP_WIDTH = 630;
  private int MAP_HEIGHT = 630;
  private int X_TILES = MAP_WIDTH/TILE_SIZE;
  private int Y_TILES = MAP_HEIGHT/TILE_SIZE;
  private Tile[][] grid = new Tile[X_TILES][Y_TILES];

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
      /*  Start Screen */
      Label welcomeLabel = new Label();
      ImageView title = new ImageView(new Image("assets/titlescreen.png"));
      ImageView getStartedButton = new ImageView(new Image("assets/mcbutton.png",150,80, true, false));
      getStartedButton.setId("get-started");
      getStartedButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
        this.setScene(MAZE);
      });
      BorderPane mainPane = new BorderPane();
      VBox mainBox = new VBox();
      mainBox.setAlignment(Pos.CENTER);
      mainBox.getChildren().addAll(title, getStartedButton);
      BackgroundImage titlebg = new BackgroundImage(new Image("assets/titlebg.jpg", true),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          new BackgroundSize(1.0, 1.0, true, true, false, false));

      mainPane.setBackground(new Background(titlebg));
      mainPane.setStyle("-fx-font-size: 2em;");
      mainBox.setMargin(getStartedButton, new Insets(15, 0, 0, 0));
      mainPane.setCenter(mainBox);
      startScene = new Scene(mainPane, 1000, 700);

      /* Maze Grid */
      GridPane mazePane = new GridPane();
      Pane map = new Pane();
      map.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
      for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = new Tile(x, y, Math.random() < 0.2);
                grid[x][y] = tile;
                map.getChildren().add(tile);
            }
      }
      VBox mapInfo = new VBox(10);
      ImageView stepsImg = new ImageView(new Image("assets/steps.png"));
      VBox.setMargin(stepsImg, new Insets(50, 0, 0, 0));
      ImageView timeImg  = new ImageView(new Image("assets/time.png"));
      ImageView exploredImg = new ImageView(new Image("assets/explored.png"));
      Label stepsLabel = new Label("Steps:");
      VBox.setMargin(stepsLabel, new Insets(0, 0, 0, 10));
      Label timeLabel = new Label("Time:");
      VBox.setMargin(timeLabel, new Insets(0, 0, 0, 10));
      Label exploredLabel = new Label("Explored Nodes:");
      VBox.setMargin(exploredLabel, new Insets(0, 0, 0, 10));
      mapInfo.getChildren().addAll(stepsImg, stepsLabel, timeImg, timeLabel, exploredImg, exploredLabel);

      BackgroundImage mazebg = new BackgroundImage(new Image("assets/dirtbg.png"),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
      mazePane.setMargin(map, new Insets(30, 0, 0, 35));
      mazePane.add(map, 0,0);
      mazePane.add(mapInfo, 1,0);
      mazePane.setBackground(new Background(mazebg));
      mazeScene = new Scene(mazePane, 1000, 700);
  }

  private class Tile extends StackPane
  {
      private int x,y;
      private boolean isWall;
      private Rectangle border = new Rectangle(TILE_SIZE-2, TILE_SIZE-2);
      private ImageView block = new ImageView(new Image("assets/grass_path_top.png", TILE_SIZE, TILE_SIZE, true, true));

      public Tile(int x, int y, boolean isWall)
      {
        this.x = x;
        this.y = y;
        this.isWall = isWall;

        border.setStroke(Color.BLACK);
        border.setFill(Color.TRANSPARENT);
        getChildren().addAll(block, border);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
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
