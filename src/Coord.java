import static java.lang.Math.*;

/** 
 * defines a single position analogous to a coordinate
 * @author Rentian Dong
 */
public class Coord implements Comparable<Coord> {
	
	/** An integer representing the column where the coordinate lies */
	public int col;
	/** An integer representing the row where the coordinate lies */
	public int row;
	
	/** Default Constructor assigns the coordinate at (0, 0) */
	public Coord() {
		this.col = 0;
		this.row = 0;
	}
	
	/**
	* Duplicates the coordinates of another Coord object
	* @param c the Coord object to be copied
	*/
	public Coord(Coord c) {
		this.col = c.col;
		this.row = c.row;
	}
	
	/**
	 * Sets the coordinate at the speicifed point
	 * @param r row number of the coordinate
	 * @param c column number of the coordinate
	 */
	public Coord(int r, int c) {
		this.row = r;
		this.col = c;
	}
	
	/** Computes the Manhattan distance between two coordinates
	 * @param b the other Coord object to compute distance with
	 * @return null if b is null, or another object containing 
	 *         the Manhattan distance if everything is valid 
	 */
	public Coord dist(Coord b) {
		if (b == null) 
			return null;
		return new Coord(abs(this.row - b.row), abs(this.col - b.col));
	}
	
	/** 
	 * Computes the signed distance between the two points
	 * @param b the other Coord object to compute distance with
	 * @return null if b is null, if everything valid then a new 
	 * 	 	  Coord object containing the differences
	 */
	public Coord diff(Coord b) {
		if (b == null)
			return null;
		return new Coord(this.row - b.row, this.col - b.col);
	}
	
	/** 
	 * Computes the sum of the square of the two vertical distances 
	 * between coordinates
	 * @param b the other Coord to compute with
	 * @return null if b is null, else the sum of the squared distances
	 */
	public int dist2(Coord b) {
		if(b == null)
			return Integer.MAX_VALUE;
		Coord c = this.dist(b);
		return round((float)(pow(this.dist(b).col, 2) + pow(this.dist(b).row, 2)));
	}
	
	/**
	 * Computes the sign of this Coord object
	 * @return a new Coord object with sign information
		        stored in the fields, -1 if coordinate less
				  than 0, 1 otherwise.
	 */
	public Coord unit() {
		return new Coord(sign(row), sign(col));
	}
	
	/** 
	 * Computes the sum of two Coord objects as the sum
	 * of row and col, the sign will be kept.
	 * @param b the other Coord object to compute sum with
	 * @return null if b is null, or a new Coord object containing
		        the sum of each coordinate
	 */ 
	public Coord add(Coord b) {
		if (b == null)
			return null;
		return new Coord(this.row + b.row, this.col + b.col);
	}
	
	/**
	 * Compares the squared difference to the origin
	 * @param b the other Coord object to compare the distance with
	 * @return Integer.MIN_VALUE if b is null, the difference between
				  squared distance to the origin of the two points otherwise
				  negative if this is closer to the origin.
	 */
	public int compareTo(Coord b) {
		if (b == null)
			return Integer.MIN_VALUE;
		Coord origin = new Coord();
		return this.dist2(origin) - b.dist2(origin);
	}
	
	/**
	 * Compares if two Coord objects are equal
	 * @param other the other object to compare with
	 * @return false if other is not Coord or at least 
				  one of the coordinates disagree, true otherwise
	 */
	@Override 
	public boolean equals(Object other) {
		if (other instanceof Coord) {
			Coord tempCoord = (Coord)other;
			if (this.row == tempCoord.row && this.col == tempCoord.col)
				return true;
		}
		return false;
	}
	
	/** returns a String representation of the coordinates of this Coord object */
	public String toString() {
		return "row = " + this.row + ", column = " + this.col;
	}
	
	// private method used to get sign of an integer, returns -1 
	// if less than 0, 1 otherwise
	private int sign(int i) {
		if (i < 0)
			return -1;
		else
			return 1;
	}
}