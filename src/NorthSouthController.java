/** 
 * @author Rentian Dong 
 */
public class NorthSouthController extends CarController {
	
	// constructor
	public NorthSouthController(CoordInfo oracle) {
		super(oracle);
		this.setDefaultDirection();
	}
	
   public void setDefaultDirection() {
		this.direction = NORTH;
   }
  
   // return the direction when roaming 
   public Coord roam(Coord current) {
   	if (oracle.coordFree(current.add(direction))) {
			return direction;
		}
		else {
			if (direction.equals(NORTH)) {
				direction = SOUTH;
				return direction;
			}
		   if (direction.equals(SOUTH)) {
				direction = NORTH;
				return direction;
			}
		}
		return new Coord(0, 0);
   }
	
   // return the direction when driving 
   public Coord drive(Coord current, Coord goal) {
		Coord displacement = unit2(goal.diff(current));
		if (displacement.row != 0)
			return new Coord(displacement.row, 0);
		else
			return new Coord(0, displacement.col);
   }
	
	
	// private methods here differ by the public unit() in Coord
	// as they return 0 if coordinate is the same, should be easier
	// to use in drive
	private Coord unit2(Coord c) {
		return new Coord(sign2(c.row), sign2(c.col));
	}
	
	private int sign2(int i) {
		if (i > 0) {return 1;}
		else if (i < 0) {return -1;}
		else {return 0;}
	}
}