public class DummyGrid implements GridInfo {
	
	// claimed always returns true
	public boolean claim(SharedCar car, Coord loc) {
		return true;
	}
	
	// always returns false
	public boolean riderLoaded(SharedCar car) {
		return false;
	}
}