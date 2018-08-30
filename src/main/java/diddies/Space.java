package diddies;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.Timer;
import javax.swing.JPanel;

/**
 * The Space class handles the inner logic and organization of the Particles and
 * all necessary subcomponents. It uses a timer to control the movement of
 * Particles and listens to both MouseEvents and KeyboardEvents for control.
 *
 * @author Ryan Kenney
 */
public class Space extends JPanel implements MouseListener, KeyListener {
    // Attributes

    private Timer time;
    private static final int SIM_SPEED = 1;
    private static final int AIM_DIAMETER = 100;
    private Updater data;
    private Point start;
    private SettingsMenu menu;

    // Constructors
    /**
     * Create a Space object with default settings
     */
    public Space() {
	super();
	initComponents();
    }

    // Methods
    /**
     * Initializes the Timer and menu
     */
    private void initComponents() {
	super.addMouseListener(this);
	this.setLayout(new BorderLayout());

	data = new Updater(this);

	time = new Timer(true);
	time.scheduleAtFixedRate(data, 0, SIM_SPEED);

	menu = new SettingsMenu(data);
	menu.setPreferredSize(new Dimension(200, getHeight()));
	menu.setVisible(false);
	this.add(menu, BorderLayout.LINE_END);

	// Colorless background - RGB values are meaningless here
	setBackground(new Color(0, 0, 0, 0));
    }

    /**
     * Unused
     *
     * @param e The KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Unused
     *
     * @param e The KeyEvent
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Listens for the ESC key and toggles the menu when it is typed
     *
     * @param e The KeyEvent
     */
    @Override
    public void keyTyped(KeyEvent e) {
	if (e.getKeyChar() == (char) KeyEvent.VK_ESCAPE) {
	    menu.setVisible(!menu.isVisible());
	}
    }

    /**
     * Unused
     *
     * @param e The MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Unused
     *
     * @param e The MouseEvent
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Unused
     *
     * @param e The MouseEvent
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Creates the starting point for the new Particle. This can not be on a
     * menu or on another Particle.
     *
     * @param e The MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON1
		&& !overlapsComponent(e.getPoint())
		&& !data.isFull()) {
	    start = e.getPoint();
	}
    }

    /**
     * Creates a Particle with a Velocity in the direction of the mouse
     * pointer's position with a magnitude that is a factor of a the distance
     * between the start point and the current point. The start point can not be
     * inside of a Particle.
     *
     * @param e The MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON1
		&& start != null
		&& !overlapsComponent(start)) {
	    Point2D end = new Point2D.Double(e.getX(), e.getY());
	    Particle p = new Particle(new Point2D.Double(start.getX() - menu.getRadius(), start.getY() - menu.getRadius()),
		    menu.getMass(), menu.getRadius());
	    Velocity init = new Velocity((end.getX() - start.getX()) / (getWidth() + getHeight()),
		    (end.getY() - start.getY()) / (getWidth() + getHeight()));
	    p.applyForce(init);
	    data.addParticle(p);
	}

	start = null;
    }

    /**
     * Check if <i>p</i> is contained within the menu or any of the Particles.
     *
     * @param p The Point to check
     * @return True if the Point is contained within a Particle or menu, false
     * otherwise
     */
    private boolean overlapsComponent(Point p) {
	// Check and see if the point is inside a component
	if (data.containedInParticles(p)
		|| (menu.isVisible() && menu.getBounds().contains(p))) {
	    return true;
	}

	// It wasnt
	return false;
    }

    /**
     * Responsible for drawing the background, aiming graphics, particles, and
     * menus.
     *
     * @param g The Graphics object to use
     */
    @Override
    public void paint(Graphics g) {// Init
	Graphics2D g2 = (Graphics2D) g;
	g2.setColor(Color.RED);

	// Draw background
	g2.setColor(Color.BLACK);
	g2.fillRect(0, 0, getWidth(), getHeight());

	// Draw aiming graphics, if applicable
	g2.setColor(Color.RED);
	if (start != null) {
	    g2.drawOval((int) start.getX() - (AIM_DIAMETER / 2), (int) start.getY() - (AIM_DIAMETER / 2),
		    AIM_DIAMETER, AIM_DIAMETER);
	    Point p = getMousePosition();
	    if (p != null) {
		g2.drawLine((int) start.getX(), (int) start.getY(),
			(int) p.getX(), (int) p.getY());
	    }
	}

	// Draw particles
	data.drawAll(g);

	// Draw Menus
	super.paint(g);
    }
}
