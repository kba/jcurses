
package jcurses.widgets;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;
import jcurses.util.Rectangle;
import jcurses.util.TextUtils;

/**
 * This class implements a label widget
 */
public class Label extends Widget {
	private String _label = null;

	/**
	 * The constructor
	 *
	 *
	 * @param  aLabel   label's text param colors label's colors
	 * @param  aColors  Description of the Parameter
	 */
	public Label(String aLabel, CharColor aColors) {
		this(aLabel);
		setColors(aColors);
	}

	/**
	 *  Gets the text of the Label
	 *
	 * @return    The text value
	 */
	public String getText() {
		return _label;
	}

	/**
	 *  Sets the text  of the Label
	 *
	 * @param  aText  The new text value
	 */
	public void setText(String aText) {
		_label = aText;
		if (_label == null) {
			_label = "";
		}
	}

	/**
	 * The constructor which makes a Lable of a String
	 *
	 *
	 * @param  aLabel  label's text
	 */
	public Label(String aLabel) {
		setText(aLabel);
	}

	/**
	 *  Calculates the preferred size of the Label.
	 *
	 * @return    The preferred size
	 */
	protected Rectangle getPreferredSize() {
		String[] mLines = TextUtils.wrapLines(_label, Integer.MAX_VALUE);

		int mWide = 0;
		for (int mIdx = 0; mIdx < mLines.length; mIdx++) {
			mWide = Math.max(mWide, mLines[mIdx].length());
		}

		return new Rectangle(mWide, mLines.length);
	}

	/**
	 *  The interface method that draws the label in its rectangle in its colors.
	 */
	protected void doPaint() {
		Toolkit.printString(_label, getRectangle(), getColors());
	}
}

