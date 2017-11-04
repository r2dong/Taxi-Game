class DummyController extends CarController {
	
	// Constructor
	public DummyController(CoordInfo oracle) {
		super(oracle);
      this.direction = NORTH;
	}
	
	// set default direction to North
   public void setDefaultDirection() {
		this.direction = SOUTH;
	}
  
   // return the direction when roaming 
   public Coord roam(Coord current) {
   	return WEST;
   }

   // return the direction when driving 
   public Coord drive(Coord current, Coord goal) {
   	return NORTH;
   }
}
