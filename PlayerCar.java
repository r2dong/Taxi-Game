/**
 * @author Rentian Dong
 */
public class PlayerCar extends SharedCar {
	// made specifically for a public setdir method
	
	public PlayerCar(CarController ctrl, GridInfo grid) {
		super(ctrl, grid);
	}
	
	public publicSetDirection(Coord direction) {
		this.setDir(direction);
	}
