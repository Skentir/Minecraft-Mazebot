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
  private final Font MINECRAFTIA = Font.loadFont(getClass().getResourceAsStream("assets/Minecraftia.ttf"),40);
  /*
  * To set a 15 by 15 tile map:
  * x_tiles = 600/40 = 15
  * y_tiles = 600/40 = 15
  */
  private static final int TILE_SIZE = 30;
  private static int MAP_WIDTH = 630;
  private static int MAP_HEIGHT = 630;
  private static int X_TILES = MAP_WIDTH/TILE_SIZE;
  private static int Y_TILES = MAP_HEIGHT/TILE_SIZE;
  private Tile[][] grid = new Tile[X_TILES][Y_TILES];
  private static Space[][] spaceGrid = new Space[X_TILES][Y_TILES];
  private static Space start;
  private static Space end;
  ImageView getStartedButton;
  ImageView beginFindingButton;

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
      getStartedButton = new ImageView(new Image("assets/mcbutton.png",150,80, true, false));
      getStartedButton.setId("get-started"); /* CHECK Controller to see what it does! :) */
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
                Tile tile; Space space;
                  if (x == 0 || (y == 0 || y == Y_TILES-1) || x == X_TILES-1)
                  {
                    tile = new Tile(x, y, true);
                    space = new Space(x,y,true);
                  }
                  else
                  {
                    tile = new Tile(x, y, false);
                    space = new Space(x,y, false);
                  }
                grid[x][y] = tile;
                new MazeGui().spaceGrid[x][y] = space;
                map.getChildren().add(tile);
            }
      }
      VBox mapInfo = new VBox(10);
      ImageView stepsImg = new ImageView(new Image("assets/steps.png", 200, 200, true, true));
      ImageView timeImg  = new ImageView(new Image("assets/time.png", 140, 140, true, true));
      ImageView exploredImg = new ImageView(new Image("assets/explored.png", 250, 250, true, true));
      beginFindingButton = new ImageView(new Image("assets/mcbutton.png",150,80, true, false));
      beginFindingButton.setId("start-maze");
      beginFindingButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
        new MazeController(this).beginSolving();
      });
      VBox.setMargin(beginFindingButton, new Insets(205, 0, 0, 30));
      Text stepsLabel = new Text("100");
      stepsLabel.setFont(MINECRAFTIA);
      stepsLabel.setFill(Color.PINK);
      VBox.setMargin(stepsLabel, new Insets(-15, 0, 0, 30));
      Text timeLabel = new Text("2.3 " + "s"); /* TODO: Extract Time */
      timeLabel.setFont(MINECRAFTIA);
      timeLabel.setFill(Color.LIGHTBLUE);
      VBox.setMargin(timeLabel, new Insets(-15, 0, 0, 30));
      Text exploredLabel = new Text("103679");
      exploredLabel.setFont(MINECRAFTIA);
      exploredLabel.setFill(Color.LIGHTGREEN);
      VBox.setMargin(exploredLabel, new Insets(-15, 0, 0, 30));
      mapInfo.getChildren().addAll(stepsImg, stepsLabel, timeImg, timeLabel, exploredImg, exploredLabel, beginFindingButton);
      BackgroundImage mazebg = new BackgroundImage(new Image("assets/dirtbg.png"),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
      mazePane.setMargin(map, new Insets(30, 0, 0, 35));
      mazePane.setMargin(mapInfo, new Insets(60, 0, 0, 25));
      mazePane.add(map, 0,0);
      mazePane.add(mapInfo, 1,0);
      mazePane.setBackground(new Background(mazebg));
      mazeScene = new Scene(mazePane, 1000, 700);
  }
  private static class Tile extends StackPane
  {
      private static final Image GRASS_PATH = new Image("assets/grass_path_top.png", TILE_SIZE, TILE_SIZE, true, true);
      private static final Image DIAMOND_ORE = new Image("assets/diamond_ore.png", TILE_SIZE, TILE_SIZE, true, true);
      private static final Image POWDER = new Image("assets/concretepowder_black.png", TILE_SIZE, TILE_SIZE, true, true);
      private static final Image BIRCH_PLANKS = new Image("assets/birch_planks.png", TILE_SIZE, TILE_SIZE, true, true);

      private int x,y;
      private boolean isWall;
      private Rectangle border = new Rectangle(TILE_SIZE-2, TILE_SIZE-2);
      private ImageView block;
      private int type;
      /*
        0 - space
        1 - wall
        2 - start
        3 - goal
      */
      public Tile(int x, int y, boolean isWall)
      {
        this.x = x;
        this.y = y;
        this.isWall = isWall;
        this.type = 0;
        if (isWall)
          block = new ImageView(BIRCH_PLANKS);
        else
          block = new ImageView(GRASS_PATH);
        border.setFill(Color.TRANSPARENT);
        getChildren().addAll(block, border);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
        setOnMouseClicked(e -> updateTile());
      }

      public void updateTile()
      {
        System.out.println("Changing tile image....");
        this.type++;
        this.type %= 4;
        switch(this.type)
        {
          case 0:
            block.setImage(GRASS_PATH);
            this.isWall = false;
            spaceGrid[x][y].setAttribute(" ");
            break;
          case 1:
            block.setImage(BIRCH_PLANKS);
            this.isWall = true;
            spaceGrid[x][y].setAttribute("*");
            break;
          case 2:
            block.setImage(POWDER);
            start = new MazeGui().spaceGrid[y][x];
            spaceGrid[x][y].setAttribute("s");
            this.isWall = false;
            break;
          case 3:
            block.setImage(DIAMOND_ORE);
            end = new MazeGui().spaceGrid[y][x];
            spaceGrid[x][y].setAttribute("o");
            this.isWall = false;
            /* TODO: Create function to save the coordinate of the goal */
            break;
      }
    }
  }

  private void updateSteve()
  {
    Image STEVE = new Image("assets/steve.gif", TILE_SIZE, TILE_SIZE, true, true);
    /* TODO: Update Start Tile with Steve*/
    
  }

  public Space getStartSpace()
  {
    return start;
  }
  public Space getEndSpace()
  {
    return end;
  }

  public Space[][] getGrid()
  {
    return spaceGrid;
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
      getStartedButton.setOnMouseClicked(handler);
      beginFindingButton.setOnMouseClicked(handler);
  }

  public static void main(String[] args)
  {
      launch(args);
  }
}
