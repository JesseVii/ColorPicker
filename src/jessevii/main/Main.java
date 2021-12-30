package jessevii.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class Main extends JFrame {
	public boolean hasFocus;
	
	public static void main(String[] args) {
		//Calls the consturctor and creates the JFrame
		new Main();
	}
	
	public Main() {
		//Adds focus listener
        Toolkit.getDefaultToolkit().addAWTEventListener(new Listener(), AWTEvent.FOCUS_EVENT_MASK);

		//Sets some JFrame settings
		this.setTitle("Color Picker");
		this.setSize(225, 109);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.add(new Panel());
		this.setVisible(true);

		//Timer that updates the JFrame and JPanel if hasFocus is true
		new Timer(10, e -> {
			if (hasFocus) {
				this.repaint();
			}
		}).start();
	}

	//JPanel for drawing the stuff with paintComponent method
	public class Panel extends JPanel {
		public Panel() {
			this.setBackground(new Color(13, 13, 13));
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;

			PointerInfo info = MouseInfo.getPointerInfo();
			Color colorUnderMouse = getColorUnderMouse(info);

			//Draw info about the color and mouse coordinates
			drawString(g2d, addColors("RGB: " + colorUnderMouse.getRed() + ", " + colorUnderMouse.getGreen() + ", " + colorUnderMouse.getBlue()), 5, 15, 15);
			drawString(g2d, addColors("HEX: #" + Integer.toHexString(colorUnderMouse.getRGB()).substring(2)), 5, (15 + 1) * 2, 15);
			drawString(g2d, addColors("MouseX: " + info.getLocation().x), 5, (15 + 1) * 3, 15);
			drawString(g2d, addColors("MouseY: " + info.getLocation().y), 5, (15 + 1) * 4, 15);

			//Draw a rectangle showing the current color
			g2d.setColor(Color.WHITE);
			g2d.drawRect(150, 10, 50, 50);
			g2d.setColor(colorUnderMouse);
			g2d.fillRect(151, 11, 49, 49);
		}
	}

	/**
	 * Focus listener for setting the hasFocus variable
	 * When focus is lost the user has clicked outside of the JFrame
	 * And it will stop updating the color until they click the JFrame again to get focus
	 */
    public class Listener implements AWTEventListener {
        public void eventDispatched(AWTEvent event) {
        	if (event.getID() == FocusEvent.FOCUS_LOST) {
				hasFocus = false;
        	} else if (event.getID() == FocusEvent.FOCUS_GAINED) {
				hasFocus = true;
			}
        }
    }

	/**
	 * Adds color to the text
	 * First color then after ":" second color
	 */
	public String addColors(String text) {
		String[] split = text.split(":");
		return "-:255, 255, 255:-" + split[0] + ":-:226, 184, 227:-" + split[1];
	}

	/**
	 * Draws a string to the screen using the passed graphics.
	 * You can pass an rgb color with the string like this -:0, 255, 0:- and it will use the color with the upcoming text until u pass another color.
	 * If you use multiple colors then you must include the first color too
	 */
	public void drawString(Graphics2D g, String text, int x, int y, int size) {
		int widthPlus = 0;
		String startRegex = "-:";
		String[] texts = {text};

		//Set antialiasing
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Arial", Font.BOLD, size));

		//Split the text into list for each given color
		if (text.contains(startRegex)) {
			ArrayList<String> list = new ArrayList<>();
			for (String s : text.split(startRegex)) {
				if (!s.isEmpty()) {
					list.add(startRegex + s);
				}
			}

			String temp[] = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				temp[i] = list.get(i);
			}

			texts = temp;
		}

		//Loop through the splitted color text things and add the width of the text to widthPlus so next piece will be rendered in the correct location
		for (String s : texts) {
			//Set color
			Color color = Color.WHITE;
			if (s.contains(startRegex)) {
				String[] split = s.replace(startRegex, "").substring(0, s.indexOf(":-") - 2).replace(" ", "").split(",");
				color = new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			}

			//Draw text and add its width to widthPlus
			String parsedText = s;
			if (parsedText.contains(startRegex)) {
				parsedText = parsedText.substring(parsedText.indexOf(":-") + 2);
			}

			parsedText = parsedText.replace(" &&&", "");
			g.setColor(color);
			g.drawString(parsedText, x + widthPlus, y);
			widthPlus += g.getFontMetrics().stringWidth(parsedText);
		}
	}

	/**
	 * Gets the color that is currently under the mouse
	 */
	public Color getColorUnderMouse(PointerInfo info) {
		try {
			return new Robot().getPixelColor(info.getLocation().x, info.getLocation().y);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
