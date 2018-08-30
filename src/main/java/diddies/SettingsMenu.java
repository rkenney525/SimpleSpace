package diddies;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The SettingsMenu class enables the user to more directly interact with
 * SimpleSpace. SettingsMenu does not directly modify anything in the Space
 * class, rather, it allows the user to change preferences that the Space class
 * can/will refer to.
 *
 * @author Ryan Kenney
 */
public class SettingsMenu extends JPanel implements ChangeListener, ActionListener {
    // Attributes, by type

    private Updater data;
    // Radius
    private int radius = 5;
    private JLabel radLabel;
    private final static String radString = "Radius: ";
    private JSlider radiusSlider;
    private static final int minRad = 0;
    private static final int maxRad = 20;
    private static final int defRad = 5;
    // Mass
    private int mass = 20;
    private JLabel massLabel;
    private final static String massString = "Mass: ";
    private JSlider massSlider;
    private final static int minMass = 0;
    private final static int maxMass = 100;
    private final static int defMass = 20;
    // Gravity
    private JLabel gravLabel;
    private final static String gravString = "Gravity: ";
    private final static String gravAction = "gravity";
    private JButton gravButton;

    // Constructors
    /**
     * Create a menu with a reference to the Space's Updater.
     *
     * @param data The Updater to use
     */
    public SettingsMenu(Updater data) {
	super();
	this.data = data;
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	initComponents();
    }

    // Methods
    /**
     * Called when some action was performed (hence the name). In the context of
     * SettingsMenu, it is called when something like the gravity button is
     * pushed.
     *
     * @param e The ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().equals(gravAction)) {
	    data.toggleGravity();
	    gravLabel.setText(gravString + ((data.usingGravity()) ? "ON" : "OFF"));
	}
    }

    /**
     * Get the mass to set
     *
     * @return The mass to set
     */
    public double getMass() {
	return mass;
    }

    /**
     * Get the radius to set
     *
     * @return The radius to set
     */
    public int getRadius() {
	return radius;
    }

    /**
     * Initialize the various menu components
     */
    private void initComponents() {
	// Radius
	radLabel = new JLabel(radString + radius);
	this.add(radLabel);
	radiusSlider = new JSlider(JSlider.HORIZONTAL, minRad, maxRad, defRad);
	radiusSlider.setMajorTickSpacing(5);
	radiusSlider.setMinorTickSpacing(1);
	radiusSlider.setPaintTicks(true);
	radiusSlider.setPaintLabels(true);
	radiusSlider.addChangeListener(this);
	radiusSlider.setFocusable(false);  // so Space doesnt have to compete for KeyEvents
	this.add(radiusSlider);

	// Mass
	massLabel = new JLabel(massString + mass);
	this.add(massLabel);
	massSlider = new JSlider(JSlider.HORIZONTAL, minMass, maxMass, defMass);
	massSlider.setMajorTickSpacing(20);
	massSlider.setMinorTickSpacing(1);
	massSlider.setPaintTicks(true);
	massSlider.setPaintLabels(true);
	massSlider.addChangeListener(this);
	massSlider.setFocusable(false);
	this.add(massSlider);

	// Gravity
	gravLabel = new JLabel(gravString + ((data.usingGravity()) ? "ON" : "OFF"));
	this.add(gravLabel);
	gravButton = new JButton("Toggle Gravity");
	gravButton.setActionCommand(gravAction);
	gravButton.addActionListener(this);
	gravButton.setFocusable(false);
	this.add(gravButton);
    }

    /**
     * Called when a slider had its value changed. This is used to set things
     * like the mass and radius values.
     *
     * @param e The ChangeEvent
     */
    @Override
    public void stateChanged(ChangeEvent e) {
	if (e.getSource() == radiusSlider) {
	    radius = (radiusSlider.getValue() == 0) ? 1 : radiusSlider.getValue();
	    radLabel.setText(radString + radius);
	} else if (e.getSource() == massSlider) {
	    mass = (massSlider.getValue() == 0) ? 1 : massSlider.getValue();
	    massLabel.setText(massString + mass);
	}
    }
}
