import java.util.ArrayList;
import java.util.Random;

/** 
 * the grid that determines how GridObjects interact
 * @author Rentian Dong  
 */
public class Grid implements GridInfo, CoordInfo {
	
	/** ArrayList of all the robots on the grid */
	public ArrayList<SharedCar> carList;
	
	/** ArrayList of all the obstacles on the grid */
	public ArrayList<Obstacle> obstacleList;
	
	/** the rider on the grid */
	public Rider rider;
	
	/** the player on the grid */
	public SharedCar player;
	
	/** Score of the player (number of riders picked up by player) */
	public int playerScore;
	
	/** Score of the robot (number of riders picked up any robot) */
	public int robotScore;
	
	/** true if the grid has a rider, false otherwise */
	public boolean hasRider;
	
	/** true if the grid has a player, false otherwise */
	public boolean hasPlayer;
	
	/** a Coord object used to store the size of the grid */
	public Coord dimension;
	
	/** 
	 * Construct a new grid using the given dimensions
	 * @param row amount of rows in the grid
	 * @param col amount of columns in the grid
	 */
	public Grid(int row, int col) {
		this.dimension = new Coord(row, col);
		this.carList = new ArrayList<SharedCar>();
		this.obstacleList = new ArrayList<Obstacle>();
		this.playerScore = 0;
		this.robotScore = 0;
		this.hasRider = false;
		this.hasPlayer = false;
		// this.wantRider = true;
	}
	
	/** 
	 * Clears all GridObjects of the grid
	 */
	public void clearAll() {
		this.carList.clear();
		this.obstacleList.clear();
		this.rider = null;
		this.player = null;
		this.playerScore = 0;
		this.robotScore = 0;
		this.hasRider = false;
		this.hasPlayer = false;
	}
	
	/** 
	 * "draws" the grid and everything on it with console output 
	 * @return a String representation of the grid
	 *
	 * <p>
	 * "r" means rider
	 * "C" is a car
	 * "#" is an obstacle
	 */
	public String toString() {
		String toReturn = "";
		//print upper boundary
		for (int j = 0; j < dimension.col + 2; j++) {
			toReturn += "=";
		}
		toReturn += "\n";
		
		// body of grid
		for (int i = 0; i < dimension.row; i++) {
			toReturn += "|";
			for (int j = 0; j < dimension.col; j++) {
				Coord tempCoord = new Coord(i, j);
				// find the rider
				if (tempCoord.equals(rider.location) && hasRider) {
					toReturn += rider.symbol;
					continue;
				}
				if (coordFree(tempCoord)) {
					toReturn += " ";
				} else {
					toReturn += search(new Coord(i, j)).symbol;
				}
			}
			toReturn += "|\n";
		}
		// lower boundary
		for (int j = 0; j < dimension.col + 2; j++) {
			toReturn += "=";
		}
		toReturn += "\n";
		
		return toReturn;
	}
	
	/** 
	 * add a SharedCar to the Grid
	 * @param car the car to be added
	 * @return true if successfully added, false otherwise
	 */
	public boolean add(SharedCar car) {
		if (car == null) {
			return false;
		} else if (coordFree(car.location)) {
			carList.add(car);
			return true;
		} else {
			return false;
		}
	}
	
	/** 
	 * adds an obstacle to the Grid
	 * @param ob the obstacle to be added
	 * @return true if successfully added, false otherwise
	 */
	public boolean add(Obstacle ob) {
		if (ob == null) {
			return false;
		} else if (coordFree(ob.location)) {
			if (hasRider) {
				if (rider.location.equals(ob.location)) {
					return false;
				}
			}
			obstacleList.add(ob);
			return true;
		} else {
			return false;
		}
	}
	
	/** 
	 * adds a rider to the Grid
	 * @param r the Rider to be added
	 * @return true if successfully added, false otherwise
	 */
	public boolean add(Rider r) {
		if (r == null || hasRider) {
			return false;
		} else if (coordFree(r.location)) {
			rider = r;
			hasRider = true;
			for (int i = 0; i < carList.size(); i++)
				carList.get(i).newRider(r.location);
			return true;
		} else {
			return false;
		}
	}
	
	/** 
	 * adds a player to the Grid
	 * @param car the player to be added
	 * @return true if successfully added, false otherwise
	 */
	public boolean addPlayer(SharedCar car) {
		if (hasPlayer || car == null) // check if there is already a player
			return false;
		else {
			this.player = car;
			this.hasPlayer = true;
			return true;
		}
	}
	
	/** 
	 * determine if a car could move to a Coord in the grid
	 * @param car the car to be moved
	 * @param loc the location moving to
	 * @return true if can move to the Coord, false otherwise
	 */
	public boolean claim(SharedCar car, Coord loc) {
		return coordFree(loc);
	}
	
	/** 
	 * Determine if a Coordinate is free
 	 * @param loc location to query
 	 * @return  true if loc is in bounds and available
 	 *          else false.  
 	 *          return false if loc is null
	*/
	public boolean coordFree(Coord loc) {
		// check boundary conditions
		if (loc == null || loc.row < 0 || loc.col < 0 
			 || dimension.row - 1 < loc.row
			 || dimension.col - 1 < loc.col) {
				 return false;
		}
		
		// check if occupied by any GridObject in the List
		for (int i = 0; i < carList.size(); i++) {
			if (carList.get(i).location.equals(loc))
				return false;
		}
		for (int i = 0; i < obstacleList.size(); i++) {
			if (obstacleList.get(i).location.equals(loc))
				return false;
		}
		if (hasPlayer) {
			if (player.location.equals(loc)) {
				return false;
			}
		}
		return true;
	}
	
	/** 
	 * updates the status of the grid
	 */
	public void update() {
		drive();
	}
	
	/** 
	 * moves all the cars of the grid one step forward
	 */
	public void drive() {
		for (int i = 0; i < carList.size(); i++) {
			carList.get(i).drive();
			riderLoaded(carList.get(i));
		}
		if (this.hasPlayer)
			player.drive();
	}

	/** 
	 * Determine if rider is loaded by a car
 	 * @param car the car that might have loaded the rider
 	 * @return  true if successfully loaded, false otherwise
	 */
	public boolean riderLoaded(SharedCar car) {
		boolean flag = false;
		// check if the car is on the rider
		if (!hasRider)
			return false;
		if (car.location.equals(rider.location) && hasRider) {
			flag = true;
			rider.pickUp(car);
			for (int i = 0; i < carList.size(); i++)
				carList.get(i).roam();
			// change score
			if (hasPlayer) {
				if (car.location.equals(player.location)) {
					playerScore++;
				} else {
					robotScore++;
				}
			} else {
				robotScore++;
			}
			hasRider = false;
		}
		return flag;
	}

	//looks for a grid object in the storred arrays that has the particular Coord
	private GridObject search(Coord loc) {
		for (int i = 0; i < carList.size(); i++) {
			if (carList.get(i).location.equals(loc))
				return carList.get(i);
		}
		for (int i = 0; i < obstacleList.size(); i++) {
			if (obstacleList.get(i).location.equals(loc))
				return obstacleList.get(i);
		}
		if (rider != null && hasRider) {
			if (rider.location.equals(loc))
				return rider;
		}
		Rider nullRider = new Rider();
		nullRider.setLocation(new Coord(Integer.MAX_VALUE, Integer.MAX_VALUE));
		return nullRider;
	}
}