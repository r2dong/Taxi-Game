import java.awt.Color;

/** 
 * Other cars cannot move past obstacles 
 * @author Rentian Dong
 */
public class Obstacle extends GridObject {
	
	/**
	 * Construct a new Obstacle at the origin
	 */
	public Obstacle() {
		color = Color.BLACK;
		location = new Coord(0,0);
		this.setSymbol("#");
   }
	
}
