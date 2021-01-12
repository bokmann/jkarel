package org.loudouncodes.jkarel;


import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The BeeperStack is a renderable object that keeps track of its location on
 * the world and it's number of beepers.
 */
public class BeeperStack extends Item {

	private final int RADIUS = 12;

	private int numBeepers = 1;

	/**
	 * Constructs a BeeperStack at the specified location with the
	 * specified number of beepers.
	 */
	public BeeperStack(int x, int y, int beepers) {
		super(x, y);

		if (beepers < 1 && beepers != Arena.INFINITY) {
			Arena.logger.warning("Invalid amount of beepers: "
			                   + beepers + "...  Setting to 1...");
			beepers = 1;
		}

		numBeepers = beepers;
	}

	/**
	 * Returns the number of beepers in the BeeperStack.
	 */
	public int getBeepers() {
		return numBeepers;
	}

	/**
	 * Renders the beeper stack at the specified pixel locations using
	 * the specified Graphics object.
	 */
	public void render(Graphics g, Location c) {
		g.setColor(Color.black);
		g.fillOval(c.x - RADIUS, c.y - RADIUS, RADIUS * 2, RADIUS * 2);

		String text;
		if (numBeepers == Arena.INFINITY)
			text = "\u221E "; //The infinity sign
		else
			text = "" + numBeepers;

		Font f = Arena.getBeeperFont();

		FontMetrics fm = g.getFontMetrics(f);
		Rectangle2D bounds = fm.getStringBounds(text, g);

		g.setColor(Color.white);
		g.drawString(text, (int)(c.x - bounds.getWidth() / 2),
		             (int)(c.y + bounds.getHeight() / 2));
	}

}
