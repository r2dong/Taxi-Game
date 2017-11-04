import javax.swing.*;
import java.awt.*;

public class DummyGraphics {
	public static void main(String[] args) {
		Grid a = new Grid(10, 10);
		GraphicsGrid g = new GraphicsGrid(10, 10, 5);
		SharedCar[] cars = new SharedCar[5];
		Obstacle[] obstacles = new Obstacle[5];
		EastWestController[] controllers = new EastWestController[5];
		for (int i = 0; i < 5; i++) {
			controllers[i] = new EastWestController(a);
			cars[i] = new SharedCar(controllers[i], a);
			cars[i].setLocation(new Coord(i, 9 - i));
			a.add(cars[i]);
			g.addGridObject(cars[i]);
		}
		for (int i = 0; i < 5; i++) {
			obstacles[i] = new Obstacle();
			obstacles[i].setLocation(new Coord(3, 8 - i));
			//a.add(obstacles[i]);
			//g.addGridObject(obstacles[i]);
		}
		Rider r = new Rider();
		r.setLocation(new Coord(1, 5));
		a.add(r);
		g.addGridObject(r);
		for (int i = 0; i < 5; i++) {
			cars[i].newRider(r.location);
		}
		JFrame f = new JFrame();
		f.add(g);
		f.pack();
		f.setVisible(true);
		System.out.println(a.toString());
		for (int i = 0; i < 4; i++) {
			a.drive();
			System.out.println(a.toString());
		}
	}
}