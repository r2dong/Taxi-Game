import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.ArrayList;

/** 
  * a simulation of the cars and riders 
  * @author Rentian Dong
  */
public class Simulation implements ActionListener, ChangeListener{
	
	// fields to define simulation
	
	/** the gird that the simulation runs on */
	public Grid grid;
	
	private TimeTick tick;
	private Thread tickThread;
	private final int RIDERS_BEFORE_NEW_CAR;
	private final int SLIDER_MIN;
	private final int DEFAULT_PIXEL = 3;
	private int sliderMax;
	private int sliderVal;
	private int totalPickUp;
	private boolean gameStarted;
	
	// fields used to keep track of sim updates
	private int count;
	private boolean hasCountDown;
	private int lastTickUpdatePosition;
	private int lastObstacleUpdatePosition;
	private int numRoboCarsAdded;
	
	// Fields for storing initial state
	private int initialGridRow;
	private int initialGridCol;
	private ArrayList<String> initialController; // ArrayList will be initialized to 0 size so flags not necessary
	private ArrayList<Coord> initialRobot;
	private ArrayList<Coord> initialObstacle;
	private Coord initialRider;
	private Coord initialPlayer;
	private boolean hasInitialRider;
	private boolean hasInitialPlayer;
	
	// GUI fields
	private JFrame frame;
	private GraphicsGrid gGrid;
	private JButton newGameButton;
	private JButton pauseButton;
	private JLabel score;
	private JSlider slider;
	private GridBagConstraints c;
	
	/** 
	 * Construct a new simulation
 	 * @param rows the amoutn of rows of the grid
	 * @param cols the amoutn of columns of the grid
	 * @param pixels size of the grid elements
	 */
	public Simulation(int rows, int cols, int pixels) {
		this.totalPickUp = 0;
		this.grid = new Grid(rows, cols);
		this.numRoboCarsAdded = 0;
		this.RIDERS_BEFORE_NEW_CAR = 10;
		this.hasCountDown = false;
		this.gameStarted = false;
		hasInitialPlayer = false;
		hasInitialRider = false;
		this.SLIDER_MIN = 10;
		this.sliderMax = 100;
		this.sliderVal = 50;
		this.slider = new JSlider(SLIDER_MIN, sliderMax, sliderVal); // (min, max, initial value)
		this.tick = new TimeTick(sliderVal, this.grid, this);
		this.tickThread = new Thread(tick);
		this.slider.setFocusable(false);
		this.slider.addChangeListener(this);
		this.lastTickUpdatePosition = Integer.MIN_VALUE;
		this.lastObstacleUpdatePosition = Integer.MIN_VALUE;
		
		this.newGameButton = new JButton("New Game");
		this.newGameButton.setFocusable(false);
		this.newGameButton.addActionListener(this);
		
		this.pauseButton = new JButton("Pause");
		this.pauseButton.setFocusable(false);
		this.pauseButton.addActionListener(this);

		this.score = new JLabel("Riders Loaded: Player: 0, Robots: 0", SwingConstants.CENTER);
		this.score.setFont(new Font(score.getFont().getName(), Font.BOLD, 30));
		this.frame = new JFrame();
		this.frame.setMinimumSize(new Dimension(700, 700));
		this.gGrid = new GraphicsGrid(rows, cols, pixels);
		this.gGrid.setFocusable(true);
		
		// setup frame layout
		this.frame.setLayout(new GridBagLayout());
		
		// add the score pane
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 0;
		this.frame.add(score, c);
		
		// add graphics grid
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.ipady = 600;
		c.ipadx = 600;
		c.weightx = 0;
		c.gridwidth = 3;
		this.frame.add(gGrid, c);
		
		// add new game button
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.5;
		this.frame.add(newGameButton, c);
		
		// add pause button
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.5;
		this.frame.add(pauseButton, c);
		
		// add slider
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.5;
		this.frame.add(slider, c);
		
		this.frame.setVisible(true);
		
		// initialization of variables used to store the initial state
		this.initialGridRow = rows;
		this.initialGridCol = cols;
		this.initialController = new ArrayList<String>();
		this.initialRobot = new ArrayList<Coord>();
		this.initialObstacle = new ArrayList<Coord>();
	}
	
	/** 
	 * update the state of the simulation
	 */
	public void update() {
		score.setText("Riders Loaded: Player: " + this.grid.playerScore + " Robots: " + this.grid.robotScore);
		this.totalPickUp = this.grid.playerScore + this.grid.robotScore;
		this.gGrid.repaint();
		if (grid.hasPlayer)
			grid.riderLoaded(grid.player);
		// add new riders after one has been picked up
		this.updateRider();
		this.updateRobot();
		this.updateTick();
		this.updateObstacle();
	}
	
	private void updateRobot() {
		if (this.numRoboCarsAdded < Math.ceil((double)this.totalPickUp / 10)) {
			boolean robotAdded;
			//FIXME randomize the controller of the newly added cars
			Random ran1 = new Random();
			int controllerType = ran1.nextInt(3);
			CarController tempCtrl;
			switch (controllerType) {
				case 0:
					tempCtrl = new NorthSouthController(grid);
					break;
				case 1:
					tempCtrl = new EastWestController(grid);
					break;
				case 2:
					tempCtrl = new RandomController(grid);
					break;
				// use east as the defualt condition
				default:
					tempCtrl = new EastWestController(grid);
					break;
			}
			Random ran2 = new Random();
			SharedCar car = new SharedCar(tempCtrl, this.grid);
			do {
				car.setLocation(new Coord(ran2.nextInt(grid.dimension.row), ran2.nextInt(grid.dimension.col)));
				robotAdded = this.grid.add(car);
			} while (!robotAdded);
			this.gGrid.addGridObject(car);
			this.numRoboCarsAdded++;
		}
	}
	
	private void countDown() {
		if (hasCountDown) {
			count--;
		}
		else {
			Random ran = new Random();
			count = ran.nextInt(500);
			hasCountDown = true;
		}
	}
	
	private void updateRider() {
		boolean riderAdded;
		if (!this.grid.hasRider) {
			this.gGrid.removeGridObject(grid.rider);
			countDown();
			if (count == 0) {
				Rider r = new Rider();
				Random ran = new Random();
				do {
					Coord c = new Coord(ran.nextInt(grid.dimension.row), ran.nextInt(grid.dimension.col));
					r.setLocation(c);
					riderAdded = grid.add(r);
				} while (!riderAdded);
				gGrid.addGridObject(r);
				hasCountDown = false;
			}
		}
	}
	
	private void updateTick() {
		if (totalPickUp > 0 && totalPickUp % 10 == 0 && totalPickUp != lastTickUpdatePosition) {
			if (sliderMax >= SLIDER_MIN + 10)
				sliderMax = sliderMax - 10;
			if (sliderVal >= SLIDER_MIN + 10)
				sliderVal = sliderVal - 10;
			if (sliderMax < sliderVal)
				sliderVal = sliderMax;
			slider.setValue(sliderVal);
			slider.setMaximum(sliderMax);
			lastTickUpdatePosition = totalPickUp;
		}
	}
	
	private void updateObstacle() {
		boolean obstacleAdded = false;
		if (totalPickUp > 0 && totalPickUp % 10 == 0 && totalPickUp != lastObstacleUpdatePosition) {
			Obstacle ob = new Obstacle();
			Random ran = new Random();
			Coord c;
			do {
				c = new Coord(ran.nextInt(grid.dimension.row), ran.nextInt(grid.dimension.col));
				ob.setLocation(c);
				obstacleAdded = this.grid.add(ob);
			} while (!obstacleAdded);
			this.gGrid.addGridObject(ob);
			lastObstacleUpdatePosition = totalPickUp;
		}
	}
	
	/** 
	 * actions carried out by the buttons of the GUI
 	 * @param event the event that triggers the action
	 */
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
			case "New Game":
				if (!gameStarted) {
					this.tickThread.start();
					gameStarted = true;
				} else {
					this.restartGame();
				}	
				break;
			case "Pause":
				this.tick.changeState();
				this.pauseButton.setText("Resume");
				this.pauseButton.repaint();
				break;
			case "Resume":
				this.tick.changeState();
				this.pauseButton.setText("Pause");
				this.pauseButton.repaint();
			default:
				break;
		}
	}
	
	/** 
	 * defines the action of the slider of the GUI
 	 * @param event the event that triggers the action
	 */
	public void stateChanged(ChangeEvent event) {
		this.tick.setTicks(slider.getValue());
		this.sliderVal = slider.getValue();
	}
	
	/** 
	 * start the game or restart the game if the game has already started
	 */
	public void run() {
		if (gameStarted == false) {
			this.tick.run();
			gameStarted = true;
		} else {
			this.restartGame();
		}
	}
	
	/** 
	 * initialize the game with a given GridSetup object
 	 * @param setup the GridSetup object used to setup this simulation
	 */
	public void initializeGame(GridSetup setup) {
		// add current player first
		if (setup.getPlayer() != null) {
			ManualController pCtrl = new ManualController(this.grid);
			SharedCar player = new SharedCar(pCtrl, this.grid);
			player.setColor(Color.RED);
			player.setLocation(setup.getPlayer());
			if (grid.addPlayer(player)) {
				gGrid.addGridObject(player);
				PlayerKey key = new PlayerKey(pCtrl, player);
				gGrid.addKeyListener(key);
				
				// store initial player position
				initialPlayer = new Coord(player.location.row, player.location.col);
				hasInitialPlayer = true;
			}
		}
		
		// add obstacles
		Obstacle tempOb;
		for (int i = 0; i < setup.getObstacles().length; i++) {
			tempOb = new Obstacle();
			tempOb.setLocation(setup.getObstacles()[i]);
			if (grid.add(tempOb)) {
				initialObstacle.add(tempOb.location);
				gGrid.addGridObject(tempOb);
			}
		}
		
		// add rider
		if (setup.getRider() != null) {
			Rider rider = new Rider();
			rider.setLocation(setup.getRider());
			if (grid.add(rider)) {
				gGrid.addGridObject(rider);
				// store initial rider
				initialRider = setup.getRider();
				hasInitialRider = true;
			}
		}
		
		// add robot cars
		SharedCar tempCar;
		CarController tempController;
		for (int i = 0; i < setup.getRobocars().length; i++) {
			switch (setup.getControllers()[i]) {
				case "east":
					tempController = new EastWestController(grid);
					break;
				case "north":
					tempController = new NorthSouthController(grid);
					break;
				case "random":
					tempController = new RandomController(grid);
					break;
				default:
					tempController = new EastWestController(grid);
					break;
			}
			tempCar = new SharedCar(tempController, this.grid);
			tempCar.setLocation(setup.getRobocars()[i]);
			if (grid.add(tempCar)) {
				gGrid.addGridObject(tempCar);
				if (grid.hasRider)
					tempCar.newRider(grid.rider.location);
			
				// add initial controllers
				initialController.add(setup.getControllers()[i]);
				// add initial car positions
				initialRobot.add(tempCar.location);
			}
		}
	}

	private void restartGame() {
		
		// reset variable used to track updates
		count = Integer.MIN_VALUE;
		hasCountDown = false;
		lastTickUpdatePosition = Integer.MIN_VALUE;
		lastObstacleUpdatePosition = Integer.MIN_VALUE;
		numRoboCarsAdded = 0;
		
		// remove everything from grid and gGrid
		grid.clearAll();
		gGrid.clearObjects();
		
		// add obstacles
		Obstacle tempOb;
		for (int i = 0; i < initialObstacle.size(); i++) {
			tempOb = new Obstacle();
			tempOb.setLocation(initialObstacle.get(i));
			this.grid.add(tempOb);
			this.gGrid.addGridObject(tempOb);
		}
		
		// add initialcars
		SharedCar tempCar;
		CarController tempCtrl;
		// size of initial controller and initial robots should be the same
		for (int i = 0; i < initialRobot.size(); i++) {
			//FIXME add code for other controllers
			switch (initialController.get(i)) {
				case "north":
					tempCtrl = new NorthSouthController(grid);
					break;
				case "east":
					tempCtrl = new EastWestController(grid);
					break;
				case "random":
					tempCtrl = new RandomController(grid);
					break;
				// use east as the defualt condition
				default:
					tempCtrl = new EastWestController(grid);
					break;
			}
			tempCar = new SharedCar(tempCtrl, grid);
			tempCar.setLocation(initialRobot.get(i));
			grid.add(tempCar);
			gGrid.addGridObject(tempCar);
		}
		
		// add initial player
		if (hasInitialPlayer) {
			ManualController tempPlayerCtrl = new ManualController(grid);
			SharedCar tempPlayer = new SharedCar(tempPlayerCtrl, grid);
			tempPlayer.setColor(Color.RED);
			tempPlayer.setLocation(initialPlayer);
			grid.addPlayer(tempPlayer);
			gGrid.addGridObject(tempPlayer);
			PlayerKey key = new PlayerKey(tempPlayerCtrl, tempPlayer);
			gGrid.addKeyListener(key);
		}
		
		//addinitial rider
		Rider tempRider = new Rider();
		if (hasInitialRider) {
			tempRider.setLocation(initialRider);
			grid.add(tempRider);
			gGrid.addGridObject(tempRider);
		}
	}

	// GridSetup used as command line arguments here
	public static void main(String[] args) {
		final int DEFAULT_PIXEL = 3;
		Simulation sim;
		switch (args.length) {
			case 1:
			// check boundary condition, falls to default if not met
				GridSetup setup = new GridSetup(args[0]);
				if (setup.getDimension().row >= 1 && setup.getDimension().col >= 1) {
					sim = new Simulation(setup.getDimension().row, setup.getDimension().col, DEFAULT_PIXEL);
					sim.initializeGame(setup);
					break;
				}
				args = new String[2];
				args[0] = "10";
				args[1] = "10";
			case 2:
				sim = new Simulation(Integer.parseInt(args[0]), Integer.parseInt(args[1]), DEFAULT_PIXEL);
				ManualController pCtrl = new ManualController(sim.grid);
				SharedCar player = new SharedCar(pCtrl, sim.grid);
				PlayerKey key = new PlayerKey(pCtrl, player);
				sim.gGrid.addKeyListener(key);
				sim.grid.addPlayer(player);
				Coord c = new Coord(Integer.parseInt(args[0]) / 2, Integer.parseInt(args[1]) / 2);
				player.setLocation(c);
				player.setColor(Color.RED);
				sim.gGrid.addGridObject(player);
				
				// store initialPlayer
				sim.initialPlayer = new Coord(player.location.row, player.location.col);
				sim.hasInitialPlayer = true;
				
				// default case has only initial player
				break;
			default:
				break;
		}
	}
}

// class used the drive the player's car
class PlayerKey implements KeyListener {
	ManualController ctrl;
	SharedCar car;
	
	/** class used the define the acitons of the user controlled car */
	public PlayerKey(ManualController ctrl, SharedCar car) {
		this.ctrl = ctrl;
		this.car = car;
	}
	
	/** 
	 * actions that should be performed if a certain key is typed 
 	 * @param event the event that triggers that action
	 */
	public void keyTyped(KeyEvent event) {
		if (event.getKeyChar() != ' ')
			ctrl.keyTyped(event.getKeyChar());
		else {
			car.drive();
		}
	}
	
	/** 
	 * not used 
	 */
	public void keyPressed(KeyEvent event) {
		
	}
	
	/** 
	 * not used 
	 */
	public void keyReleased(KeyEvent event) {
		// do nothing
	}
}