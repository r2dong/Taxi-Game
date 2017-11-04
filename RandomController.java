import java.util.Random;

/** 
 * @author Rentian Dong
 */
public class RandomController extends CarController {
	
	Random ran = new Random();

	// constructor
	public RandomController(CoordInfo oracle) {
		super(oracle);
		this.setDefaultDirection();
	}
	
   public void setDefaultDirection() {
		this.direction = randomRoam();
   }
  
   // return the direction when roaming 
   public Coord roam(Coord current) {
   	return randomRoam();
   }
	
   // return the direction when driving 
   public Coord drive(Coord current, Coord goal) {
		Coord displacement = unit2(goal.diff(current));
		Coord distance = goal.diff(current);
		if (Math.abs(distance.row) > Math.abs(distance.col)) {
			return new Coord(displacement.row, 0);
		} else {
			return new Coord(0, displacement.col);
		}
   }
	
	private Coord randomRoam() {
		int dir = ran.nextInt(4);
		switch (dir) {
			case 0:
				return new Coord(1, 0);
			case 1:
				return new Coord(-1, 0);
			case 2:
				return new Coord(0, 1);
			case 3:
				return new Coord(0, -1);
			default:
				return direction;
		}
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