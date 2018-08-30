package diddies;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Updater is a TimerTask, which means it, or more specifically run(), is called
 * ever time Space's Timer goes off. It is the job of the Updater to manage the
 * "physical" interaction between the Particles, such as apply their momentum or
 * simulate the effects of gravity.
 *
 * @author Ryan Kenney
 */
public class Updater extends TimerTask {

    // Attributes, by type
    // Gravity
    private final static double GRAVITATIONAL_CONSTANT = 5e-3;
    private boolean enableGravity = true;
    // Particle
    private final List<Particle> particles;
    private List<Particle> remove;
    private final static int MAX_SIZE = 10;
    private int currentSize = 0;
    // Etc
    private Space space;

    // Constructors
    /**
     * Creates a default Updater
     *
     * @param space The invoking Space reference
     */
    public Updater(Space space) {
	particles = new ArrayList<>();
	remove = new ArrayList<>();
	this.space = space;
    }

    // Methods
    /**
     * Adds <i>p</i> to the internal list of Particles. This ensures that the
     * Particle is drawn and "visible" to the others.
     *
     * @param p The Particle to add
     */
    public void addParticle(Particle p) {
	synchronized (particles) {
	    particles.add(p);
	    currentSize++;
	}
    }

    /**
     * Check if <i>p</i> is contained in any of the Particles.
     *
     * @param p The Point to check
     * @return True if <i>p</i> is contained in any Particle, false otherwise
     */
    public boolean containedInParticles(Point p) {
	synchronized (particles) {
	    for (Particle part : particles) {
		if (part.contains(p)) {
		    return true;
		}
	    }
	}

	return false;
    }

    public void drawAll(Graphics g) {
	synchronized (particles) {
	    for (Particle p : particles) {
		p.draw(g);
	    }
	}
    }

    /**
     * Check if the limit of Particles has been reached.
     *
     * @return True if no more Particles can be added, false otherwise
     */
    public boolean isFull() {
	return currentSize >= MAX_SIZE;
    }

    /**
     * The method that is called each Timer tick. This method is responsible for
     * applying momentum, calculating gravity, checking collisions, and managing
     * the list of Particles.
     */
    @Override
    public void run() {
	synchronized (particles) {
	    // Initial Momentum
	    for (Particle p : particles) {
		p.applyMomentum();
	    }

	    // Effects of gravity
	    // Some shortcuts
	    Velocity attraction1, attraction2; // gravity velocities
	    Velocity v1, v2; // original velocities
	    Particle p1, p2; // quick ref
	    Point2D centerMass1, centerMass2; // quick ref
	    double rawForce; // yield of gravity calculation
	    double angle;    // angle between p1 and p2

	    // remember (G*m1*m2)/d^2
	    if (enableGravity) {
		for (int i = 0; i < particles.size(); i++) {
		    // Establish the shortcuts
		    p1 = particles.get(i);
		    centerMass1 = p1.getCenterMass();
		    for (int j = i + 1; j < particles.size(); j++) {
			// Retrieve some values
			p2 = particles.get(j);
			centerMass2 = p2.getCenterMass();

			// Quick check for absorption
			// This is done here because the next calculation involves
			// dividing by the distance between p1 and p2, which could be 0
			if (centerMass1.equals(centerMass2)) {
			    if (p1.getMass() > p2.getMass()) {
				p1.absorb(p2);
				remove.add(particles.get(j));
			    } else {
				p2.absorb(p1);
				remove.add(particles.get(i));
			    }
			    System.out.println("Collision Sound Effect");
			    continue;
			}

			// Calculate the force due to gravity
			rawForce = (GRAVITATIONAL_CONSTANT
				* p1.getMass() * p2.getMass())
				/ (centerMass1.distanceSq(centerMass2));

			// Get the angle between p1 and p2
			angle = Math.atan2(centerMass2.getY() - centerMass1.getY(),
				centerMass2.getX() - centerMass1.getX());

			// Calculate the raw attraction
			attraction1 = new Velocity((rawForce * Math.cos(angle)) / p1.getMass(),
				(rawForce * Math.sin(angle)) / p1.getMass());
			attraction2 = new Velocity((rawForce * Math.cos(angle + Math.PI)) / p2.getMass(),
				(rawForce * Math.sin(angle + Math.PI)) / p2.getMass());

			// Apply the attraction
			p1.applyForce(attraction1);
			p2.applyForce(attraction2);
		    }
		}
	    }

	    // Check for collisions
	    for (int i = 0; i < particles.size(); i++) {
		// Individual collision with walls
		// get shortcuts
		p1 = particles.get(i);
		Velocity opposite = p1.getMomentum().getOpposite();
		Velocity change = Velocity.duplicate(p1.getMomentum());

		// Wall checking
		if (p1.getX() <= 0
			|| (p1.getX() + p1.getDiameter()) >= space.getWidth()) {
		    // Fix particles that get stuck
		    if (p1.getX() < 0) {
			p1.setLocation(1, p1.getY());
		    } else if ((p1.getX() + p1.getDiameter()) > space.getWidth()) {
			p1.setLocation((space.getWidth() - p1.getDiameter()) - 1, p1.getY());
		    }

		    change.setX(-change.getX());
		    p1.applyForce(opposite);
		    p1.applyForce(change);
		} else if ((p1.getY() <= 0)
			|| (p1.getY() + p1.getDiameter()) >= space.getHeight()) {
		    // Fix particles that get stuck
		    if (p1.getY() < 0) {
			p1.setLocation(p1.getX(), 1);
		    } else if ((p1.getY() + p1.getDiameter()) > space.getHeight()) {
			p1.setLocation(p1.getX(), (space.getHeight() - p1.getDiameter()) - 1);
		    }

		    change.setY(-change.getY());
		    p1.applyForce(opposite);
		    p1.applyForce(change);
		}

		// Deal with particle collision
		for (int j = i + 1; j < particles.size(); j++) {
		    // The current procedure is only partially accurate, and
		    // the deficiencies can be noticed by observation.
		    p2 = particles.get(j);
		    if (p1.intersects(p2)) {
			// Get the current momentum
			v1 = Velocity.duplicate(p1.getMomentum());
			v2 = Velocity.duplicate(p2.getMomentum());

			// Nullify and apply
			p1.applyForce(v1.getOpposite());
			p1.applyForce(v2, p2.getMass());

			p2.applyForce(v2.getOpposite());
			p2.applyForce(v1, p1.getMass());

		    }
		}
	    }

	    // Remove destroyed particles
	    particles.removeAll(remove);
	    currentSize -= remove.size();
	    remove.clear();

	    space.repaint();
	}
    }

    /**
     * If gravity is turned on, turn it off. If gravity is turned off, turn it
     * on.
     */
    public void toggleGravity() {
	enableGravity = !enableGravity;
    }

    /**
     * Check if gravity is turned on
     *
     * @return True if gravity is on, false otherwise
     */
    public boolean usingGravity() {
	return enableGravity;
    }
}
