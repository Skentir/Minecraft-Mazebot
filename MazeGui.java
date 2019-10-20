import javafx.scene.text.*;
import javafx.scene.Scene;
import javafx.scene.Stage;
import javafx.application.Application;

public class MazeGui extends Application
{
  Stage stage;
  Solver solver;
  private Scene startScene, mazeScene;
  private Node[][] mazeScene;

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
      currentStage.setTitle("Minecraft Maze Bot");
      currentStage.setResizable(false);
      currentStage.show();
  }
  private void createScenes()
  {
      Label welcomeLabel = new Label();
      /*Insert path to title screen logo*/
      //ImageView title = new ImageView(new Image("assets/insertFileNameHere.png"));
      Button getStartedButton = new Button();
      /* Assign CSS script to make button pretty */
      getStartedButton.setStyle("-fx-font-weight: bold; -fx-background-color: #8fe1a2; -fx-text-fill: darkslateblue;");
      getStartedButton.setText("Get Started");
      /* Assign id to connect to the controller */
      getStartedButton.setId("get-started");

      BorderPane mainPane = new BorderPane();
      VBox mainBox = new VBox();
      mainBox.setAlignment(Pos.CENTER);
      mainBox.getChildren().addAll(title, getStartedButton);
      mainPane.setStyle("-fx-background-color: #662d91;-fx-font-size: 2em;");
      mainBox.setMargin(getStartedButton, new Insets(15, 0, 0, 0));
      mainPane.setCenter(mainBox);
      startScene = new Scene(mainPane, 700, 500);


  }
}
