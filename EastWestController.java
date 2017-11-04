/** 
 * @author Rentian Dong 
 */
public class EastWestController extends CarController {
	
	// constructor
	public EastWestController(CoordInfo oracle) {
		super(oracle);
	}
	
   public void setDefaultDirection() {
		this.direction = EAST;
   }
  
   // return the direction when roaming 
   public Coord roam(Coord current) {
   	if (oracle.coordFree(current.add(direction))) {
			return direction;
		}
		else {
			if (direction.equals(EAST)) {
				direction = WEST;
				return direction;
			}
		   if (direction.equals(WEST)) {
				direction = EAST;
				return direction;
			}
		}
		// return a (0, 0) in case all failed
		return new Coord(0, 0);
   }
	
   // return the direction when driving 
   public Coord drive(Coord current, Coord goal) {
		Coord displacement = unit2(goal.diff(current));
		if (displacement.col != 0)
			return new Coord(0, displacement.col);
		else
			return new Coord(displacement.row, 0);
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