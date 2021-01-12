package org.loudouncodes.jkarel;

import org.loudouncodes.jkarel.xml.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The WorldBackend stores and preforms actions on the data structures storing
 * the information. This includes Beepers, Robots, and Walls. It also provides
 * the ability to load this information from xml files.
 */

public class WorldBackend {

	private static WorldBackend current = null;

	private Map<Location, BeeperStack> beepers;
	private List<Robot> robots;
	private List<Wall> walls;

	private int width = 10;
	private int height = 10;

	private Wall xAxisWall = null, yAxisWall = null;

	/**
	 * Creates a WorldBackend starting with the objects listed in the
	 * provided xml file.
	 * @param mapName the path to the xml file to load
	 */
	public WorldBackend(String mapName) {
		current = this;

		beepers = Collections.synchronizedMap(new HashMap < Location,
		                                      BeeperStack > ());
		robots = Collections.synchronizedList(new ArrayList<Robot>());
		walls = Collections.synchronizedList(new ArrayList<Wall>());

		walls.add(xAxisWall = new Wall(1, 0, width, Arena.HORIZONTAL));
		walls.add(yAxisWall = new Wall(0, 1, height, Arena.VERTICAL));

		parseMap(mapName);
	}

	/**
	 * Creates a WorldBackend starting with the default map.
	 */
	public WorldBackend() {
		this(null);
	}

	/**
	 * Adds a robot to the world.
	 */
	void addRobot(Robot r) {
		synchronized (robots) {
			robots.add(r);
		}
		Arena.step();
	}
	/**
	* Adds a robot to the world without having a time step. (Used when a map
	*creates a Robot when it is built.)
	*/

	void addRobotInternal(Robot r) {
		synchronized (robots) {
			robots.add(r);
		}
	}


	/**
	 * Removes a robot from the world.
	 */
	void removeRobot(Robot r) {
		synchronized (robots) {
			robots.remove(r);
		}
		Arena.step();
	}

	/**
	 * Adds the specified number of beepers to the stack of beepers at the
	 * specified location.
	 * @param x the x location of the taget location
	 * @param y the y location of the taget location
	 * @param num number of beepers to place at the location.
	 */
	public void putBeepers(int x, int y, int num) {
		Location c = new Location(x, y);
		if (num == Arena.INFINITY) {
			synchronized (beepers) {
				beepers.put(c, new BeeperStack(x, y, num));
			}
			return;
		}
		synchronized (beepers) {
			int oldBeepers = 0;

			BeeperStack b;
			if ((b = beepers.get(c)) != null)
				oldBeepers = b.getBeepers();

			if (oldBeepers == Arena.INFINITY)
				return;

			if (oldBeepers + num < 1)
				beepers.remove(c);
			else
				beepers.put(c, new BeeperStack(x, y, num + oldBeepers));
		}
	}

	/**
	 * Adds a wall to the world.
	 */
	public void addWall(Wall w) {
		synchronized (walls) {
			walls.add(w);
		}
	}

	/**
	 * Helper method to convert the attributes from the file which represent
	 * a beeper into a beeper object.
	 */
	public void addObject_beeper(Attributes a) {
		int x = Integer.parseInt(a.get("x"));
		int y = Integer.parseInt(a.get("y"));
		String num = a.get("num");

		if (num.equalsIgnoreCase("infinite"))
			putBeepers(x, y, Arena.INFINITY);
		else
			putBeepers(x, y, Integer.parseInt(num));
	}
	/**
	 * Helper method to convert the attributes from the file which represent
	 * a wall into a wall object.
	 */
	public void addObject_wall(Attributes a) {
		int x = Integer.parseInt(a.get("x"));
		int y = Integer.parseInt(a.get("y"));
		int length = Integer.parseInt(a.get("length"));
		int style = a.get("style").equalsIgnoreCase("horizontal") ?
		            Arena.HORIZONTAL : Arena.VERTICAL;

		addWall(new Wall(x, y, length, style));
	}
	/**
	 * Helper method to convert the attributes from the file which represent
	 * a robot into a robot object.
	 */
	public void addObject_robot(Attributes a) {
		int x = Integer.parseInt(a.get("x"));
		int y = Integer.parseInt(a.get("y"));
		int directionVal = Integer.parseInt(a.get("direction"));
    Direction direction = Direction.values()[directionVal];
		int beepers = Integer.parseInt(a.get("beepers"));

		new Robot(x, y, direction, beepers, true);
	}

	/**
	 * Helper method to parse the attributes from the file which dictate the
	 * size of the world.
	 */
	public void loadProperties_defaultSize(Attributes a) {
		int w = Integer.parseInt(a.get("width"));
		int h = Integer.parseInt(a.get("height"));

		Arena.setSize(w, h);
	}

	/**
	 * Starts off the loading of the specified map.
	 * @param mapName the path to the map file
	 */
	void parseMap(String mapName) {
		Element e = new XMLParser().parse(getInputStreamForMap(mapName));
		WorldParser.initiateMap(e);
	}

	/**
	 * Helper method to open the specified file.
	 * @param fileName the path to the map file
	 */
	private InputStream getInputStreamForMap(String fileName) {
		FileInputStream f = null;

		try {
			if (fileName == null)
				throw new FileNotFoundException();

			f = new FileInputStream(new File(fileName));
		}
		catch (FileNotFoundException e) {
			if (fileName != null)
				Arena.logger.warning("Map " + fileName + " not found, using default map file...");

			try {
				InputStream is = getClass().getResourceAsStream(Arena.DEFAULT_MAP);

				if (is == null)
					throw new FileNotFoundException();

				return is;
			}
			catch (Exception g) {
				Arena.logger.severe("Default map file not found!  Aborting...");
				System.exit(1);
			}
		}

		return f;
	}

	/**
	 * Returns a Map of BeeperStacks with Location keys
	 */
	Map<Location, BeeperStack> getBeepers() {
		return beepers;
	}
	/**
	 * Returns a list of the Walls on the map
	 */
	List<Wall> getWalls() {
		return walls;
	}
	/**
	 * Returns a list of the Robots on the map
	 */
	List<Robot> getRobots() {
		return robots;
	}

	/**
	 * Checks to see if a wall exists at the specified location and direction.
	 * Used when trying to determine collisions.
	 * @param x x-location of potential wall
	 * @param y y-location of potential wall
	 * @param style orientation of potential wall
	 */
	boolean checkWall(int x, int y, int style) {
		synchronized (walls) {
			switch (style) {
				case Arena.HORIZONTAL:
					for (Wall w : walls)
						if (w.getStyle() == style)
							if (w.getY() == y &&
							                x >= w.getX() &&
							                x < w.getX() + w.getLength())
								return true;

					break;
				case Arena.VERTICAL:
				default:
					for (Wall w : walls)
						if (w.getStyle() == style)
							if (w.getX() == x &&
							                y >= w.getY() &&
							                y < w.getY() + w.getLength())
								return true;

					break;
			}
		}
		return false;
	}

	/**
	 * Checks to see if any beepers exist at the specified location.
	 * @param x x-location of the location to check
	 * @param y y-location of the location to check
	 */
	boolean checkBeepers(int x, int y) {
		return beepers.get(new Location(x, y)) != null;
	}

	/**
	 * Checks to see if there is a Robot besides the specified robot at a
	 * given location.
	 * @param r Robot to exclude from the search
	 * @param x x-location of the location to search
	 * @param y y-location of the location to search
	 */
	boolean isNextToARobot(Robot r, int x, int y) {
		synchronized (robots) {
			for (Robot robot : robots)
				if (robot != r && robot.getX() == x && robot.getY() == y)
					return true;

			return false;
		}
	}

	/**
	 * Resizes the world, resizing the bottom and left walls if needed.
	 * @param width how wide the world should be
	 * @param height how high the world should be
	 */
	void setSize(int width, int height) {
		if (this.width != width) {
			this.width = width;
			walls.remove(xAxisWall);
			walls.add(xAxisWall = new Wall(1, 0, width, Arena.HORIZONTAL));
		}

		if (this.height != height) {
			this.height = height;
			walls.remove(yAxisWall);
			walls.add(yAxisWall = new Wall(0, 1, height, Arena.VERTICAL));
		}
	}

	/**
	 * Returns a location representing the size of the world.
	 */
	public Location getSize() {
		return new Location(width, height);
	}

	/**
	 * Closes the world, making any new calls create a new world.
	 */
	void close() {
		current = null;
	}

	/**
	 * Returns the currently running instance of WorldBackend.
	 */
	public static WorldBackend getCurrent() {
		return current;
	}
}
