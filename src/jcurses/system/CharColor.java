
package jcurses.system;

/**
 * Instances of this class are used by painting to set color attributes of painted chars. Both black-white mode and color mode attributes can ( and must be) be
 * declared. For the color mode, colors of the background an the foreground can be declared, for the background mode can be declared, whether painted chars are
 * output normal, reverse or in string font (bold).
 * <p>
 * possible values for colors: <br>
 * <code>BLACK</code><br>
 * <code>BLUE</code><br>
 * <code>CYAN</code><br>
 * <code>GREEN</code><br>
 * <code>NAGENTA</code><br>
 * <code>RED</code><br>
 * <code>YELLOW</code><br>
 * <code>WHITE</code>
 * <p>
 * possible values for black-white mode attributes (these attributes are also available for some color displays): <br>
 * <code>BOLD</code><br>
 * <code>NORMAL</code><br>
 * <code>REVERSE</code>
 *
 */
public class CharColor {
	//color constants
	/**
	 *  The color
	 */
	public final static short BLACK = 0;
	/**
	 *  The color
	 */
	public final static short RED = 1;
	/**
	 *  The color
	 */
	public final static short GREEN = 2;
	/**
	 *  The color
	 */
	public final static short YELLOW = 3;
	/**
	 *  The color
	 */
	public final static short BLUE = 4;
	/**
	 *  The color
	 */
	public final static short MAGENTA = 5;
	/**
	 *  The color
	 */
	public final static short CYAN = 6;
	/**
	 *  The color
	 */
	public final static short WHITE = 7;

	/**
	 *  The black-white mode constant
	 */
	public final static short NORMAL = 0;
	/**
	 *  The black-white mode constant
	 */
	public final static short REVERSE = 1;
	/**
	 *  The black-white mode constant
	 */
	public final static short BOLD = 2;

	private short _background;
	private short _blackWhiteAttribute = 0;
	private short _colorAttribute = 0;
	private short _foreground;

	/**
	 * The constructor
	 *
	 * @param  background           background color
	 * @param  foreground           foreground color
	 * @param  blackWhiteAttribute  mode attribute
	 * @param  colorAttribute       mode attribute
	 */
	public CharColor(short background, short foreground, short blackWhiteAttribute, short colorAttribute) {
		verifyColor(background);
		verifyColor(foreground);
		verifyAttribute(colorAttribute);
		verifyAttribute(blackWhiteAttribute);
		_background = background;
		_foreground = foreground;
		_blackWhiteAttribute = blackWhiteAttribute;
		_colorAttribute = colorAttribute;
		//initChtype();
	}

	/**
	 * The constructor
	 *
	 * @param  background           background color
	 * @param  foreground           foreground color
	 * @param  blackWhiteAttribute  mode attribute color mode attribute will be set to <code>NORMAL</code>
	 */
	public CharColor(short background, short foreground, short blackWhiteAttribute) {
		this(background, foreground, blackWhiteAttribute, NORMAL);
	}

	/**
	 * The constructor, sets both the black-white mode attribute and the color mode attribute to <code>NORMAL</code>
	 *
	 * @param  background  background color
	 * @param  foreground  foreground color
	 */
	public CharColor(short background, short foreground) {
		this(background, foreground, NORMAL);
	}

	/**
	 * The method sets the background color
	 *
	 * @param  background  value to be set
	 */
	public void setBackground(short background) {
		verifyColor(background);
		_background = background;
		//initChtype();
	}

	/**
	 * Accessor pattern
	 *
	 * @return    the background color
	 */
	public short getBackground() {
		return _background;
	}

	/**
	 * Sets the black-white mode attribute
	 *
	 * @param  blackWhiteAttribute  new black-white mode attribute
	 */
	public void setBlackWhiteAttribute(short blackWhiteAttribute) {
		_blackWhiteAttribute = blackWhiteAttribute;
	}

	/**
	 * Accessor pattern
	 *
	 * @return    the black-white mode attribute
	 */
	public short getBlackWhiteAttribute() {
		return _blackWhiteAttribute;
	}

	/**
	 * Sets the color mode attribute
	 *
	 * @param  colorAttribute  new color mode attribute
	 */
	public void setColorAttribute(short colorAttribute) {
		_colorAttribute = colorAttribute;
	}

	/**
	 * Accessor pattern
	 *
	 * @return    the color mode attribute
	 */
	public short getColorAttribute() {
		return _colorAttribute;
	}

	/**
	 * The method sets the foreground color
	 *
	 * @param  foreground  value to be set
	 */
	public void setForeground(short foreground) {
		verifyColor(foreground);
		_foreground = foreground;
		//initChtype();
	}

	/**
	 * The method gets the foreground color
	 *
	 * @return    the foreground color
	 */
	public short getForeground() {
		return _foreground;
	}

	/**
	 *  Represent the character colors as a string
	 *
	 * @return    the character colors as a string
	 */
	public String toString() {
		if (Toolkit.hasColors()) {
			return "[background=" + getColorName(_background) + ", foreground=" + getColorName(_foreground) + "]";
		}

		return "[modi=" + getModusName(_blackWhiteAttribute) + "]";
	}

	/**
	 *  Gets the colorName attribute of the CharColor object
	 *
	 * @param  index  Description of the Parameter
	 * @return        The colorName value
	 */
	private String getColorName(short index) {
		switch (index) {
						case BLACK:
							return "BLACK";
						case WHITE:
							return "WHITE";
						case GREEN:
							return "GREEN";
						case YELLOW:
							return "YELLOW";
						case MAGENTA:
							return "MAGENTA";
						case CYAN:
							return "CYAN";
						case BLUE:
							return "BLUE";
						case RED:
							return "RED";
						default:
							return "UNKNOWN COLOR";
		}
	}

	/**
	 *  Gets the modusName attribute of the CharColor object
	 *
	 * @param  index  Description of the Parameter
	 * @return        The modusName value
	 */
	private String getModusName(short index) {
		switch (index) {
						case NORMAL:
							return "NORMAL";
						case REVERSE:
							return "REVERSE";
						case BOLD:
							return "BOLD";
						default:
							return "UNKNOWN MODUS";
		}
	}

	/**
	 *  Gets the pairNo attribute of the CharColor object
	 *
	 * @return    The pairNo value
	 */
	short getPairNo() {
		if (Toolkit.hasColors()) {
			return Toolkit.getColorPairNo(this);
		}

		return -1;
	}

	/**
	 *  Gets the attribute attribute of the CharColor object
	 *
	 * @return    The attribute value
	 */
	long getAttribute() {
		if (Toolkit.hasColors()) {
			return Toolkit.mapAttribute(getColorAttribute());
		}

		return Toolkit.mapAttribute(getBlackWhiteAttribute());
	}

	/**
	 * Verify the color attribute as being one we support
	 *
	 * @param  attribute                  the color attribute
	 * @throws  IllegalArgumentException  on unknown color attribute
	 */
	private void verifyAttribute(short attribute) {
		if ((attribute != NORMAL) && (attribute != REVERSE) && (attribute != BOLD)) {
			throw new IllegalArgumentException("Unknown color attribute:" + attribute);
		}
	}

	/**
	 * Verify the color attribute as being one we support
	 *
	 * @param  color                      the color
	 * @throws  IllegalArgumentException  on unknown color
	 */
	private void verifyColor(short color) {
		if ((color != BLACK) && (color != RED) && (color != GREEN) && (color != YELLOW) && (color != BLUE) && (color != MAGENTA) && (color != CYAN)
				 && (color != WHITE)) {
			throw new IllegalArgumentException("Unknown color:" + color);
		}
	}
}
