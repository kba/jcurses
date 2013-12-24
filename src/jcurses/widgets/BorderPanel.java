
package jcurses.widgets;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;
import jcurses.util.Rectangle;

/**
 * This class implements a panel with a border.
 *
 */

public class BorderPanel extends Panel {

	private CharColor _colors = getDefaultBorderColors();
	private static CharColor __defaultBorderColors = new CharColor(CharColor.WHITE, CharColor.BLACK);

	/**
	 *Constructor for the BorderPanel with default w / h
	 */
	public BorderPanel() {
		super();
	}

	/**
	 *Constructor for the BorderPanel with width and height provided
	 *
	 * @param  width  width
	 * @param  height height
	 */
	public BorderPanel(int width, int height) {
		super(width, height);
	}

	@Override
	protected void paintSelf() {
		Toolkit.drawBorder(getRectangle(), getBorderColors());
		super.paintSelf();
	}

	@Override
	protected void repaintSelf() {
		Toolkit.drawBorder(getRectangle(), getBorderColors());
		super.repaintSelf();
	}

	/**
	 *  Gets the borderColors attribute of the BorderPanel object
	 *
	 * @return    The borderColors value
	 */
	public CharColor getBorderColors() {
		return _colors;
	}

	/**
	 *  Sets the borderColors attribute of the BorderPanel object
	 *
	 * @param  colors  The new borderColors value
	 */
	public void setBorderColors(CharColor colors) {
		_colors = colors;
	}

	/**
	 *  Gets the defaultBorderColors attribute of the BorderPanel object
	 *
	 * @return    The defaultBorderColors value
	 */
	protected CharColor getDefaultBorderColors() {
		return __defaultBorderColors;
	}

	/**
	 *  Gets the clientArea attribute of the BorderPanel object
	 *
	 * @return    The clientArea value
	 */
	protected Rectangle getClientArea() {
		Rectangle rect = (Rectangle) getSize().clone();
		rect.setLocation(1, 1);
		rect.setWidth(rect.getWidth() - 2);
		rect.setHeight(rect.getHeight() - 2);

		return rect;
	}

}
