import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;

public class Player 
{
  private int x,y;
  public void setPosition(double x, double y)
  {
    if (x > 0 || x < map.length)
      this.x = x;
    this.y = y;
  }
}
