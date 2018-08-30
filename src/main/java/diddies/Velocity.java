package diddies;

/**
 * Effectively encapsulates a velocity.  It should be noted however that the class
 * by itself is not a true velocity, as it depends on the containing class to
 * implement its use.
 * 
 * A true velocity is a change in position per unit time.  The velocity class contains
 * an X and Y vector, with no reference in time.  The intended use is that, whatever
 * time step you use, you would apply this velocity to the containing object's
 * position.  It is essential that this velocity be the velocity applied at the
 * smallest time increment (ie a call to update(), run(), handleTick(), etc), as the
 * maximum values for either X or Y are 1, meaning a Velocity will never push
 * the containing object more than 1 pixel, in any direction, in one iteration.
 * This provides a certain degree of certainty when handling collisions.  In short,
 * no object can "skip over" any pixel in its path.
 * 
 * Here's an example of intended use
 * 
 * public class Man {
 *   Velocity v;
 *   double x, y; // position, though some class (Point2D) may be preferred
 *   ...
 *   public void update() {
 *     this.x += v.getX()
 *     this.y += v.getY()
 *     ...
 *   }
 * }
 * @author Ryan Kenney
 */
public class Velocity {

    // Attributes
    private double x;
    private double y;

    // Constructors
    /**
     * Creates a Velocity with x and y components
     * @param x The X component
     * @param y The Y component
     */
    public Velocity(double x, double y) {
        this.setValues(x, y);
    }

    // Methods
    /**
     * Adds v's X and Y components to the current value's X and Y components.
     * This abides by the upper limit of 1 (and lower limit of -1) rule
     * @param v The Velocity to apply
     */
    public void applyVelocity(Velocity v) {
        this.setValues(this.getX() + v.getX(),
                this.getY() + v.getY());
    }
    
    /**
     * Creates a Velocity with identical properties to v
     * @param v The Velocity to copy
     * @return A Velocity identical to v
     */
    public static Velocity duplicate(Velocity v) {
        return new Velocity(v.x, v.y);
    }
    
    /**
     * Returns a Velocity with the same magnitude but in opposite direction
     * @return The Velocity that goes in the opposite direction
     */
    public Velocity getOpposite() {
        return new Velocity(-1 * getX(), -1 * getY());
    }

    /**
     * Get the X component
     * @return the X component
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get the Y component
     * @return the Y component
     */
    public double getY() {
        return y;
    }
    
    /**
     * Directly modifies the X and Y components of this Velocity. This abides by 
     * the upper limit of 1 (and lower limit of -1) rule
     * @param x The X component to set
     * @param y The Y component to set
     */
    public final void setValues(double x, double y) {
        this.setX((x > 1) ? 1 : x);
        this.setX((x < -1) ? -1 : x);
        this.setY((y > 1) ? 1 : y);
        this.setY((y < -1) ? -1 : y);
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }
}
