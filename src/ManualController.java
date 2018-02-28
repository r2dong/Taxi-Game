/** 
 * @author Rentian Dong 
 */
public class ManualController extends CarController {

	public ManualController (CoordInfo oracle) {
		super(oracle);
	}
	
	public void setDefaultDirection() {	
		this.direction = ORIGIN;
	}
	
	public Coord roam (Coord current) {
		// the player car should not roam
		return direction;
	}
	
	public Coord drive (Coord current, Coord goal) {
		// the direction only affected by keyboard 
		return direction;
	}
	
	public void keyTyped(char key) {
		switch (key) {
			case 'h':
				if (this.direction.equals(NORTH))
					this.direction = WEST;
				else if (this.direction.equals(WEST))
					this.direction = SOUTH;
				else if (this.direction.equals(SOUTH))
					this.direction = EAST;
				else if (this.direction.equals(EAST))
					this.direction = NORTH;
				break;
			case 'l':
				if (this.direction.equals(NORTH))
					this.direction = EAST;
				else if (this.direction.equals(WEST))
					this.direction = NORTH;
				else if (this.direction.equals(SOUTH))
					this.direction = WEST;
				else if (this.direction.equals(EAST))
					this.direction = SOUTH;
					break;
				default:
					break;
		}
	}
}