package diddies;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * SimpleSpace is an application designed to experiment with the effects on gravity
 * and collisions of particles with varying masses.  The SimpleSpace class simply
 * acts as a container for the main application logic, which is in the Space class.
 * @author Ryan Kenney
 */
public class SimpleSpace extends JFrame {
    // Attributes
    private final static int WINDOW_HEIGHT = 600;
    private final static int WINDOW_WIDTH = 800;
    private Space activeSpace;

    // Constructors
    /**
     * Creates a SimpleSpace with default settings
     */
    public SimpleSpace() {
	initComponents();
    }

    // Methods
    /**
     * Set up the working environment and any containers or components
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
	// Set size/behaviour preferences
	setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
	setResizable(false);

	// Create the Space
	activeSpace = new Space();
	activeSpace.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
	this.addKeyListener(activeSpace);
	this.getContentPane().add(activeSpace);
    }

    /**
     * Creates the form
     *
     * @param args The command line arguments
     */
    public static void main(String args[]) {

	// Create and display the form
	java.awt.EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		new SimpleSpace().setVisible(true);
	    }
	});
    }
}
