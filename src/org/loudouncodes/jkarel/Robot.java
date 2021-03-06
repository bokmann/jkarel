package org.loudouncodes.jkarel;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.HashMap;

/**
 * A Robot is a basic movable object in the world. Students interct primarily
 * with Robot objects in order to solve the problems in the labs. Robots tend
 * to update their status whenever asked and then request a arena update with
 * Arena.step(). This class is intended to be subclassed to add simple
 * behaviors.
 */

public class Robot extends Item {

	private int beepers;
	private Direction direction;
  private HashMap<Direction,ImageIcon> icons;

	public Robot() {
		this(1, 1, Direction.EAST, 0);
	}

	public Robot(int x, int y, Direction dir, int beepers) {
		this(x, y, dir, beepers, false);
	}


	public Robot(int x, int y, Direction dir, int beeperCount, boolean internal) {
    super(x, y);
		direction = dir;
    
		if (beepers < 0 && beepers != BeeperStack.INFINITY) {
			Arena.logger.warning("Invalid amount of beepers: "
			                   + beepers + "...  Setting to 0...");
			beepers = 0;
		}
		beepers = beeperCount;

    icons = new HashMap<Direction,ImageIcon>();
    // in the near future, we will make this so one icon is needed, and the rest are
    // generated via rotation.
    // icons.put(Direction.NORTH, new ImageIcon(Robot.class.getResource("/icons/karel.png")));
    icons.put(Direction.NORTH, new ImageIcon(Robot.class.getResource("/icons/kareln.gif")));
    icons.put(Direction.EAST, new ImageIcon(Robot.class.getResource("/icons/karele.gif")));
    icons.put(Direction.SOUTH, new ImageIcon(Robot.class.getResource("/icons/karels.gif")));
    icons.put(Direction.WEST, new ImageIcon(Robot.class.getResource("/icons/karelw.gif")));

		if (ArenaModel.getCurrent() == null) {
			Arena.openDefaultWorld();
		}


		if (internal)
			ArenaModel.getCurrent().addRobotInternal(this);
		else
			ArenaModel.getCurrent().addRobot(this);

	}

	public Direction getDirection() {
		return direction;
	}

	public int getBeepers() {
		return beepers;
	}

	public synchronized void move() {
		if (Arena.isDead())
			return;

		boolean clear = frontIsClear();

		if (!clear) {
			Location c = getWallLocation(direction);
			Arena.die("Tried to walk " + direction + " through a wall at " + myLocation);
			return;
		}

		myLocation.move(direction);

		Arena.step();
	}

	public void turnLeft() {
		if (Arena.isDead())
			return;

		direction = direction.left();

		Arena.step();
	}

	private void turnRight() {
		if (Arena.isDead())
			return;

		direction = direction.right();

		Arena.step();
	}

	public void putBeeper() {
		if (Arena.isDead())
			return;

		if (beepers < 1 && beepers != BeeperStack.INFINITY) {
			Arena.die("Trying to put non-existent beepers!");
			return;
		}

		if (beepers != BeeperStack.INFINITY)
			beepers--;

		ArenaModel.getCurrent().putBeepers(myLocation, 1);

		Arena.step();
	}

	public void pickBeeper() {
		if (Arena.isDead())
			return;

		if (!ArenaModel.getCurrent().checkBeepers(myLocation)) {
			Arena.die("Trying to pick non-existent beepers!");
			return;
		}

		if (beepers != BeeperStack.INFINITY)
			beepers++;

		ArenaModel.getCurrent().putBeepers(myLocation, -1);

		Arena.step();
	}

	public boolean hasBeepers() {
		return beepers > 0 || beepers == BeeperStack.INFINITY;
	}

	public boolean frontIsClear() {
		return isClear(direction);
	}

	public boolean rightIsClear() {
		return isClear(direction.right());
	}

	public boolean leftIsClear() {
		return isClear(direction.left());
	}

	public boolean backIsClear() {
		return isClear(direction.right().right());
	}

	public boolean nextToABeeper() {
		return ArenaModel.getCurrent().checkBeepers(myLocation);
	}

	public boolean nextToARobot() {
		return ArenaModel.getCurrent().isNextToARobot(this, myLocation);
	}

	public boolean facingNorth() {
		return direction == Direction.NORTH;
	}

	public boolean facingSouth() {
		return direction == Direction.SOUTH;
	}

	public boolean facingEast() {
		return direction == Direction.EAST;
	}

	public boolean facingWest() {
		return direction == Direction.WEST;
	}

	private boolean isClear(Direction dir) {
		Location c = getWallLocation(direction);

		switch (dir) {
			case NORTH:
			case SOUTH:
				return !ArenaModel.getCurrent()
				       .checkWall(c.x, c.y, Arena.HORIZONTAL);
			case EAST:
			case WEST:
			default:
				return !ArenaModel.getCurrent()
				       .checkWall(c.x, c.y, Arena.VERTICAL);
		}
	}

	public void explode() {
		ArenaModel.getCurrent().removeRobot(this);
	}

	private Location getWallLocation(Direction dir) {
		int xc = myLocation.getX();
    int yc = myLocation.getY();

		switch (dir) {
			case NORTH: //Check in front, not behind
			case EAST: //Check in front, not behind
				break;
			case SOUTH: //Checking behind current location
				yc--;
				break;
			case WEST: //Checking behind current location
				xc--;
				break;
		}

		return new Location(xc, yc);
	}

	public void render(Graphics g, int x, int y) {
		ImageIcon i = icons.get(direction);
		g.drawImage(i.getImage(),
                x - i.getIconWidth() / 2,
		            y - i.getIconHeight() / 2, null);
	}

	public boolean nextToRobot(Robot other) {
		return (myLocation.equals(other.getLocation()));
	}
}
