
package jcurses.widgets;

import jcurses.system.InputChar;

import jcurses.util.Rectangle;

/**
 *   A visual list that handles user selection events
 */
public class MenuList extends List {
	private final static String SEPARATOR = "\u0000\u0000\u0000\u0000";
	private final static String SEPARATOR_STRING = "";
	private String _separatorString = SEPARATOR_STRING;

	/**
	 * Sets the text to use by painting separators
	 *
	 *
	 * @param  value  separator string
	 *
	 */
	public void setSeparatorString(String value) {
		_separatorString = value;
	}

	/**
	 * Returns the text used by painting separators
	 *
	 *
	 * @return    separator string
	 *
	 */
	public String getSeparatorString() {
		return _separatorString;
	}

	/**
	 * Adds a separator at the specified position
	 *
	 *
	 * @param  index  position to add a separator
	 *
	 */
	public void addSeparator(int index) {
		add(index, SEPARATOR);
	}

	/**
	 * Adds a separator at the end of the list
	 */
	public void addSeparator() {
		addSeparator(getItemsCount());
	}

	/**
	 *  Gets the itemRepresentation attribute of the MenuList object
	 *
	 * @param  item  Description of the Parameter
	 * @return       The itemRepresentation value
	 */
	protected String getItemRepresentation(String item) {
		if (item == SEPARATOR) {
			return getSeparatorString();
		}

		return item;
	}

	/**
	 *  Gets the preferredSize attribute of the MenuList object
	 *
	 * @return    The preferredSize value
	 */
	protected Rectangle getPreferredSize() {
		return new Rectangle(getMaxItemLength() + 2, getItemsCount() + 2);
	}

	/**
	 *  Gets the selectable attribute of the MenuList object
	 *
	 * @param  index  Description of the Parameter
	 * @return        The selectable value
	 */
	protected boolean isSelectable(int index) {
		return (!(getItem(index) == SEPARATOR));
	}

	/**
	 *  Description of the Method
	 *
	 * @param  ch  Description of the Parameter
	 * @return     Description of the Return Value
	 */
	protected boolean handleInput(InputChar ch) {
		if (!ch.equals(getChangeStatusChar())) {
			return super.handleInput(ch);
		}

		return false;
	}

	/**
	 *  Gets the maxItemLength attribute of the MenuList object
	 *
	 * @return    The maxItemLength value
	 */
	private int getMaxItemLength() {
		int result = 0;

		for (int i = 0; i < getItemsCount(); i++) {
			int length = getItemRepresentation((getItem(i))).length();
			result = (length > result) ? length : result;
		}

		return result;
	}
}
