package diddies;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * The graphical and logical encapsulation of a particle. Particle stores a
 * position and velocity, as well as a diameter. Diameter is used to calculate
 * the density of the particle (mass per area), which is represented visually by
 * differing colors. The higher the density, the higher the frequency of light
 * (bluer).
 *
 * @author Ryan Kenney
 */
public class Particle {
    // Attributes

    private int diameter;
    private Point2D position;
    private Velocity velocity;
    private double mass;
    private Color color;

    // Constructors
    /**
     * Create a Particle at <i>position</i> with a mass of <i>mass</i> and a
     * radius of <i>radius</i>.
     *
     * @param position The position of the particle. This is the point where the
     * particle is drawn, not the center of the particle's mass.
     * @param mass The mass of the particle, in AMUs
     * @param radius The radius of the particle, in pixels
     */
    public Particle(Point2D position, double mass, int radius) {
	this.position = position;
	this.mass = mass;
	velocity = new Velocity(0, 0);
	color = getColor(mass, radius);
	diameter = radius * 2;
    }

    // Methods
    /**
     * Absorb <i>p</p> into the Particle. The particle takes all of the mass and
     * 1/4 of the diameter from <i>p</p>.
     *
     * @param p The Particle to absorb
     */
    public void absorb(Particle p) {
	this.mass += p.mass;
	this.diameter += Math.ceil(p.diameter / 4);
	this.color = getColor(mass, diameter / 2);
    }

    /**
     * Adds <i>v</i>'s Velocity vector to the current Velocity.
     *
     * @param v The Velocity to apply
     */
    public void applyForce(Velocity v) {
	velocity.applyVelocity(v);
    }

    /**
     * This function is used to handle elastic collisions in which the mass of
     * the other Particle is used in the calculation. A better design, hopefully
     * to be implemented, would be to handle all forces the same and use only
     * the Particle's mass. The force vector would have already been modified by
     * the giving Particle's mass prior to the receiving Particle handling it.
     *
     * @param force The Velocity of the other Particle
     * @param sourceMass The mass of the other Particle
     */
    public void applyForce(Velocity force, double sourceMass) {
	// In a one-dimensional elastic collision, the force after a collision:
	// v = ((v*(m-other.m))+(2*other.m*other.v))/(m+other.m)
	double newX = ((velocity.getX() * (mass - sourceMass)) + (2 * sourceMass * force.getX())) / (mass + sourceMass);
	double newY = ((velocity.getY() * (mass - sourceMass)) + (2 * sourceMass * force.getY())) / (mass + sourceMass);
	velocity.applyVelocity(new Velocity(newX, newY));
    }

    /**
     * Modifies the Particle's position by its Velocity. Since the Velocity
     * vector represents how far the Particle should move in one unit of time,
     * it makes sense to call this method each unit of time.
     */
    public void applyMomentum() {
	position.setLocation(position.getX() + velocity.getX(),
		position.getY() + velocity.getY());
    }

    /**
     * Check if <i>p</i> is a point inside the Particle
     *
     * @param p The Point to check
     * @return True if <i>p</i> is contained in the Particle, false otherwise
     */
    public boolean contains(Point p) {
	Area area = new Area(new Ellipse2D.Double(position.getX(), position.getY(), diameter, diameter));
	return area.contains(p);
    }

    /**
     * The instructions for drawing the Particle. The Particle tells the
     * Graphics object where to draw, what size, and what color.
     *
     * @param g The Graphics object to use
     */
    public void draw(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;

	g2.setColor(color);
	g2.fillOval((int) position.getX(), (int) position.getY(), diameter, diameter);
    }

    /**
     * Get the center point of the Particle, which is simply the position vector
     * with the radius added to both X and Y components.
     *
     * @return The center of the PArticle
     */
    public Point2D getCenterMass() {
	double radius = diameter / 2;
	return new Point2D.Double(position.getX() + radius, position.getY() + radius);
    }

    /**
     * Returns a Color based on the density. Higher density = Higher light
     * frequency (blue).
     *
     * @param mass The mass of the Particle
     * @param radius The radius of the Particle
     * @return The Color of the Particle by density
     */
    private static Color getColor(double mass, double radius) {
	// density = mass per unit volume
	double density = mass / (Math.PI * Math.pow(radius, 2));
	if (density <= .03) {
	    return Color.RED;
	} else if (density <= .1) {
	    return Color.ORANGE;
	} else if (density <= .5) {
	    return Color.YELLOW;
	} else if (density <= 1) {
	    return Color.WHITE;
	} else {
	    return Color.BLUE;
	}
    }

    /**
     * Get the diameter of the Particle
     *
     * @return The Particle's diameter
     */
    public int getDiameter() {
	return diameter;
    }

    /**
     * Get the mass of the Particle
     *
     * @return The Particle's mass
     */
    public double getMass() {
	return mass;
    }

    /**
     * Get the momentum (Velocity) of the Particle
     *
     * @return The Particle's momentum
     */
    public Velocity getMomentum() {
	return velocity;
    }

    /**
     * Get the X component of the Particle's position
     *
     * @return The X component of the Particle's position
     */
    public double getX() {
	return position.getX();
    }

    /**
     * Get the Y component of the Particle's position
     *
     * @return The Y component of the Particle's position
     */
    public double getY() {
	return position.getY();
    }

    /**
     * Check if the calling Particle overlaps with <i>p</i>
     *
     * @param p The Particle to compare
     * @return True if the Particle's overlap, false otherwise
     */
    public boolean intersects(Particle p) {
	Area area1 = new Area(new Ellipse2D.Double(position.getX(), position.getY(), diameter, diameter));
	Area area2 = new Area(new Ellipse2D.Double(p.position.getX(), p.position.getY(), p.diameter, p.diameter));
	area1.intersect(area2);
	return !area1.isEmpty();
    }

    /**
     * Change the Particle's position to be (<i>x</i>, <i>y</i>)
     *
     * @param x The X component to set
     * @param y The Y component to sey
     */
    public void setLocation(double x, double y) {
	position.setLocation(x, y);
    }
}