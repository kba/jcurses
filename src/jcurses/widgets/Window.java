
package jcurses.widgets;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import jcurses.event.WindowEvent;
import jcurses.event.WindowListener;
import jcurses.event.WindowListenerManager;
import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;
import jcurses.themes.Theme;
import jcurses.themes.WindowThemeOverride;
import jcurses.util.Rectangle;

/**
 * Under jCurses, unlike some GUI libraries, a Window is not a widget, but contains a panel (the root panel), that contains all widgets. A window can, but
 * doesn't must, have a border and a title. All windows under jcurses are managed in a stack, the topmost visible window window on the stack gets all input
 * chars for handling, this is so called focus window. If a window is created, it goes automatically to the top of the stack and lives there until another
 * window is created or explicitly brought to the top.
 */
public class Window {
	private static InputChar __defaultClosingChar = new InputChar(InputChar.KEY_ESC);
	private static InputChar __defaultFocusChangeChar = new InputChar(InputChar.KEY_TAB);
	private static InputChar __upChar = new InputChar(InputChar.KEY_UP);
	private static InputChar __downChar = new InputChar(InputChar.KEY_DOWN);
	private static InputChar __leftChar = new InputChar(InputChar.KEY_LEFT);
	private static InputChar __rightChar = new InputChar(InputChar.KEY_RIGHT);
	private InputChar _closingChar = getDefaultClosingChar();
	private InputChar _focusChangeChar = getDefaultFocusChangeChar();
	private Hashtable _shortCutsTable = new Hashtable();
	private Vector _shortCutsList = new Vector();

	/**
	 *  Self-documenting
	 */
	public final static int DIR_LEFT = 0;
	/**
	 *  Self-documenting
	 */
	public final static int DIR_RIGHT = 1;
	/**
	 *  Self-documenting
	 */
	public final static int DIR_UP = 2;
	/**
	 *  Self-documenting
	 */
	public final static int DIR_DOWN = 3;

	/*
	 *  can this can be private?
	 */
	boolean _closed = false;
	/*
	 *  can this can be private?
	 */
	Theme _theme = new WindowThemeOverride();

	private Panel _root = null;
	private Rectangle _rect = null;
	private String _title = null;
	private Vector _focusableChildren = null;

	//Listener-Zeugs
	private WindowListenerManager _listenerManager = new WindowListenerManager();
	private boolean _border = false;
	private boolean _hasShadow = true;
	private boolean _visible = false;
	private int _currentIndex = -1;

	/**
	 * The constructor
	 *
	 * @param  x       the the top left corner's x coordinate
	 * @param  y       the top left corner's y coordinate
	 * @param  width   window's width
	 * @param  height  window's height
	 * @param  border  true, if the window has a border, false in other case
	 * @param  title   DOCUMENT ME!
	 */
	public Window(int x, int y, int width, int height, boolean border, String title) {
		_border = border;
		_title = title;
		_rect = new Rectangle(width, height);
		_rect.setLocation(x, y);

		int x1 = border ? (x + 1) : x;
		int y1 = border ? (y + 1) : y;
		int w = border ? (width - 2) : width;
		int h = border ? (height - 2) : height;

		_root = new Panel(w, h);
		_root.setLocation(x1, y1);
		_root.setWindow(this);

		WindowManager.createWindow(this);
	}

	/**
	 *  Gets the theme attribute of the Window class
	 *
	 * @return    The theme value
	 */
	public static Theme getTheme() {
		return WindowManager.getTheme();
	}

	/**
	 *  Sets the theme attribute of the Window class
	 *
	 * @param  aTheme  The new theme value
	 */
	public static void setTheme(Theme aTheme) {
		WindowManager.setTheme(aTheme);
	}

	/**
	 * The constructor. A window created with this constructor is centered on the screen.
	 *
	 * @param  width   window's width
	 * @param  height  window's height
	 * @param  border  true, if the window has a border, false in other case
	 * @param  title   title which appears in the title position on the window
	 */
	public Window(int width, int height, boolean border, String title) {
		this((Toolkit.getScreenWidth() - width) / 2, (Toolkit.getScreenHeight() - height) / 2, width, height, border, title);
	}

	/**
	 *  Sets the borderColors attribute of the Window object
	 *
	 * @param  aColors  The new borderColors value
	 */
	public void setBorderColors(CharColor aColors) {
		_theme.setColor(Theme.COLOR_WINDOW_BORDER, aColors);
		repaint();
	}

	/**
	 *  Gets the borderColors attribute of the Window object
	 *
	 * @return    The borderColors value
	 */
	public CharColor getBorderColors() {
		return _theme.getColor(Theme.COLOR_WINDOW_BORDER);
	}

	/**
	 * Has the window been closed?
	 *
	 * @return    true if the window is already closed, false otherwise.
	 */
	public boolean isClosed() {
		return _closed;
	}

	/**
	 * The method defines a new window's closing character. Default is escape.
	 *
	 * {@code null} means no closing character
	 *
	 * @param  character  new window's closing character - {@code null} means no closing character
	 */
	public void setClosingChar(InputChar character) {
		_closingChar = character;
	}

	/**
	 * The method returns the character which, when encountered in the default input handler
	 * causes JCurses to close this window.
	 *
	 * @return    window's closing character - {@code null} means no closing character
	 */
	public InputChar getClosingChar() {
		return _closingChar;
	}

	/**
	 *  Gets the defaultBorderColors attribute of the Window object
	 *
	 * @return    The defaultBorderColors value
	 */
	public CharColor getDefaultBorderColors() {
		return getTheme().getColor(Theme.COLOR_WINDOW_BORDER);
	}

	/**
	 *  Gets the defaultTitleColors attribute of the Window object
	 *
	 * @return    The defaultTitleColors value
	 */
	public CharColor getDefaultTitleColors() {
		return getTheme().getColor(Theme.COLOR_WINDOW_TITLE);
	}

	/**
	 * The method defined the charater used to navigate (change the focus) between widgets within the window. Default is 'tab'
	 *
	 * @param  character  new window's focus changing charater
	 */
	public void setFocusChangeChar(InputChar character) {
		_focusChangeChar = character;
	}

	/**
	 * The method returns the character used to navigate (change the focus) between widgets within the window. Default is 'tab'
	 *
	 * @return    window's focus changing charater
	 */
	public InputChar getFocusChangeChar() {
		return _focusChangeChar;
	}

	/**
	 * Sets the root panel of the window. This is the top most widget container in the window's widget hierarchy. It occupies the entire window out of the border
	 * (if exists ).
	 *
	 * @param  root  a Panel suitable to be a root panel.
	 */
	public void setRootPanel(Panel root) {
		_root = root;
		_root.setWindow(this);
		repaint();
	}

	/**
	 *  Window manager closes all windows.
	 */
	public static void closeAllWindows() {
		WindowManager.closeAll();
	}

	/**
	 * Returns the  root panel of the window. This is the top most widget container in the window's widget hierarchy. It occupies the entire window out of the border
	 * (if exists ).
	 *
	 * @return    the root panel of the window
	 */
	public Panel getRootPanel() {
		//Ein kommentar
		return _root;
	}

	/**
	 * The method defines whether the window is to paint with a shadow.
	 *
	 * @param  value  true if a shadow is to paint, false otherwise
	 */
	public void setShadow(boolean value) {
		_hasShadow = value;
		repaint();
	}

	/**
	 *  Indicates whether the window is to paint with a shadow.
	 *
	 * @return    true if a shadow is to paint, false otherwise
	 */
	public boolean hasShadow() {
		return _hasShadow;
	}

	/**
	 *  Sets the titleColors attribute of the Window object
	 *
	 * @param  aColors  The new titleColors value
	 */
	public void setTitleColors(CharColor aColors) {
		_theme.setColor(Theme.COLOR_WINDOW_TITLE, aColors);
		repaint();
	}

	/**
	 *  Gets the titleColors attribute of the Window object
	 *
	 * @return    The titleColors value
	 */
	public CharColor getTitleColors() {
		return _theme.getColor(Theme.COLOR_WINDOW_TITLE);
	}

	/**
	 * The method changes the window's visibility status
	 *
	 * @param  aVisible  true, if the window becomes visible, false in other case
	 */
	public void setVisible(boolean aVisible) {
		if (aVisible != isVisible()) {
			if (aVisible) {
				show();
			} else {
				hide();
			}
		}
	}

	/**
	 * The method returns the window's visibility status
	 *
	 * @return    true, if the window becomes visible, false in other case
	 */
	public boolean isVisible() {
		return _visible;
	}

	/**
	 * The method adds a listener to the window
	 *
	 * @param  listener  the listener to add
	 */
	public void addListener(WindowListener listener) {
		_listenerManager.addListener(listener);
	}

	/**
	 * The method closed the window, that is removes it from window stack and
	 * eventually from the rendered display, if the window was visible.
	 */
	public void close() {
		hide();
		_closed = true;
		WindowManager.removeWindow(this);
	}

	/**
	 * The method hides the window
	 */
	public void hide() {
		_visible = false;
		WindowManager.doWindowVisibilityChange(this);
	}

	/**
	 * The method moves the window to the top of the stack
	 */
	public void moveToTheTop() {
		WindowManager.moveToTop(this);
	}

	/**
	 * The method computes new window's layout. The method must already be called, if anything on the window building is changed, for example, an widget is
	 * removed or isn't more focusable ( because not visible or other ).
	 */
	public void pack() {
		cutIfNeeded();
		configureRootPanel();
		_root.pack();
		loadFocusableChilds();
		loadShortcuts();
	}

	/**
	 * The method remove a listener from the window
	 *
	 * @param  listener  the listener to remove
	 */
	public void removeListener(WindowListener listener) {
		_listenerManager.removeListener(listener);
	}

	/**
	 * The method shows the window
	 */
	public void show() {
		if (!isVisible()) {
			WindowManager.createWindow(this);
			pack();
			_visible = true;
			WindowManager.doWindowVisibilityChange(this);
		}
	}

	/**
	 * The method tries to close the window, after the user has typed 'escape' or an other closing character. The procedure is as following: If the window has
	 * listeners, than an event is sent to the listeners. The window can be closed bei listeners. Didn't listeners close the window, in leaves open. Has the
	 * window no listeners, than the method closes it.
	 *
	 * @return    true if close was successful
	 */
	public boolean tryToClose() {
		if (_listenerManager.countListeners() > 0) {
			_listenerManager.handleEvent(new WindowEvent(this, WindowEvent.CLOSING));

			return isClosed();
		}

		close();

		return true;
	}

	/**
	 * Returns the rectangle occupied by the window.
	 *
	 * @return    the rectangle occupied by the window
	 */
	protected Rectangle getRectangle() {
		return _rect;
	}

	/**
	 * Accounts for shadow is any
	 *
	 * @return    the rectangle occupied by the window
	 */
	protected Rectangle getClipRectangle() {
		if (hasShadow()) {
			return new Rectangle(_rect.getX(), _rect.getY(), _rect.getWidth() + 1, _rect.getHeight() + 1);
		}

		return _rect;
	}

	/**
	 * The method is called, if the window gets focus.
	 */
	protected void activate() {
		_listenerManager.handleEvent(new WindowEvent(this, WindowEvent.ACTIVATED));
	}

	/**
	 *  Changes the focus between widgets and propagates the change notification.
	 *  Internal only, should not be called by application code.
	 *
	 * @param  aWidgetIndex  Index of widget to receive focus.
	 */
	protected void changeFocus(int aWidgetIndex) {
		if (aWidgetIndex != _currentIndex) {
			if (isFocusableIndex(_currentIndex)) {
				((Widget) _focusableChildren.get(_currentIndex)).setFocus(false);
			}

			_currentIndex = aWidgetIndex;

			if (isFocusableIndex(aWidgetIndex)) {
				((Widget) _focusableChildren.get(aWidgetIndex)).setFocus(true);
			}
		}
	}

	/**
	 *  Changes the focus between widgets and propagates the change notification.
	 *  Internal only, should not be called by application code. 
	 *
	 * @param  aWidget  the Widge itself to which the focus is to be changed.
	 */
	protected void changeFocus(Widget aWidget) {
		changeFocus(_focusableChildren.indexOf(aWidget));
	}

	/**
	 *  Changes the focus between widgets and propagates the change notification
	 *  merely by incrementing the index, wrapping to zero (0) if necessary.
	 *  Internal only, should not be called by application code. 
	 */
	protected void changeFocus() {
		//changeFocus(Math.min(Math.max(0, _currentIndex + 1), _focusableChildren.size() - 1));
		if (_currentIndex >= _focusableChildren.size() - 1 || _currentIndex < -1) {
			changeFocus(0);
		} else {
			changeFocus(_currentIndex + 1);
		}
	}

	/**
	 *  Changes the focus between widgets and propagates the change notification
	 *  based on a sense of direction in the relation of all widgets.
	 *  Internal only, should not be called by application code. 
	 *
	 * @param  aDirection  One of the directions DIR_LEFT DIR_RIGHT DIR_UP DIR_DOWN 
	 */
	protected void moveFocus(int aDirection) {
		if (_focusableChildren.isEmpty()) {
			return;
		}

		Widget mCurrent = getCurrentWidget();

		if (mCurrent == null) {
			mCurrent = (Widget) _focusableChildren.get(0);
		}

		Widget mResult = mCurrent;

		for (Iterator mIt = _focusableChildren.iterator(); mIt.hasNext(); ) {
			Widget mCandidate = (Widget) mIt.next();

			if (mCandidate == mCurrent) {
				continue;
			}

			Rectangle mCurRect = mCurrent.getRectangle();
			Rectangle mCandRect = mCandidate.getRectangle();
			Rectangle mResRect = mResult.getRectangle();

			int vDelta = mCurRect.verticalDistanceFrom(mCandRect) - mCurRect.verticalDistanceFrom(mResRect);
			int hDelta = mCurRect.horizontalDistanceFrom(mCandRect) - mCurRect.horizontalDistanceFrom(mResRect);

			switch (aDirection) {
							case DIR_LEFT:

								if (mCandRect.isLeftOf(mCurRect)) {
									if ((mResult == mCurrent) || (vDelta < 0) || ((vDelta == 0) && (hDelta < 0))) {
										mResult = mCandidate;
									}
								}

								break;
							case DIR_RIGHT:

								if (mCandRect.isRightOf(mCurRect)) {
									if ((mResult == mCurrent) || (vDelta < 0) || ((vDelta == 0) && (hDelta < 0))) {
										mResult = mCandidate;
									}
								}

								break;
							case DIR_UP:

								if (mCandRect.isAbove(mCurRect)) {
									if ((mResult == mCurrent) || (hDelta < 0) || ((hDelta == 0) && (vDelta < 0))) {
										mResult = mCandidate;
									}
								}

								break;
							case DIR_DOWN:

								if (mCandRect.isBelow(mCurRect)) {
									if ((mResult == mCurrent) || (hDelta < 0) || ((hDelta == 0) && (vDelta < 0))) {
										mResult = mCandidate;
									}
								}

								break;
			}
		}

		changeFocus(mResult);
	}

	/**
	 * The method is called, if the window is to be closed.
	 */
	protected void closed() {
		_closed = true;
		_listenerManager.handleEvent(new WindowEvent(this, WindowEvent.CLOSED));
	}

	/**
	 * The method is called, if the window loses focus.
	 */
	protected void deactivate() {
		_listenerManager.handleEvent(new WindowEvent(this, WindowEvent.DEACTIVATED));
	}

	/**
	 * The method is called by the library to handle an input character, if the window has the focus.
	 *
	 * @param  inp the object instance representing the input char
	 */
	protected void handleInput(InputChar inp) {
		Widget cur = getCurrentWidget();

		//System.err.println("Window.handleInput( 0x"+Integer.toHexString(inp.getCode())+" )");

		if ((cur != null) && cur.handleInput(inp)) {
			return;
		} else if (inp.equals(getClosingChar())) {
			tryToClose();
		} else if (inp.equals(getFocusChangeChar())) {
			changeFocus();
		} else if (inp.equals(__upChar)) {
			moveFocus(DIR_UP);
		} else if (inp.equals(__downChar)) {
			moveFocus(DIR_DOWN);
		} else if (inp.equals(__leftChar)) {
			moveFocus(DIR_LEFT);
		} else if (inp.equals(__rightChar)) {
			moveFocus(DIR_RIGHT);
		} else if (isShortCut(inp)) {
			getWidgetByShortCut(inp).handleInput(inp);
		} else {
			onChar(inp);
		}
	}

	/**
	 * The method is called by <code>handleInput</code>, if no widget has handled the input. Derived classes can override the method to define additional
	 * shortcuts.
	 *
	 * @param  inp the object instance representing the input char
	 */
	protected void onChar(InputChar inp) {
		//default nothing
	}

	/**
	 * The method paint's the window
	 */
	protected void paint() {
		drawThingsIfNeeded();
		_root.paint();
	}

	/**
	 * Currently the method makes the same as repaint, in next versions the method will repaint only the part of the window, that was hided.
	 */
	protected void repaint() {
		if (isVisible()) {
			drawThingsIfNeeded();
			_root.repaint();
		}
	}

	/**
	 *  Resize to specified size 
	 *
	 * @param  width   d'oh
	 * @param  height  d'oh
	 */
	protected void resize(int width, int height) {
		_rect.setWidth(width);
		_rect.setHeight(height);
	}

	/**
	 *  Gets the currentWidget attribute of the Window object
	 *
	 * @return    The currentWidget value
	 */
	private Widget getCurrentWidget() {
		if (isFocusableIndex(_currentIndex)) {
			return ((Widget) _focusableChildren.elementAt(_currentIndex));
		}

		return null;
	}

	/**
	 *  Gets the defaultClosingChar attribute of the Window object
	 *
	 * @return    The defaultClosingChar value
	 */
	private InputChar getDefaultClosingChar() {
		return __defaultClosingChar;
	}

	/**
	 *  Gets the defaultFocusChangeChar attribute of the Window object
	 *
	 * @return    The defaultFocusChangeChar value
	 */
	private InputChar getDefaultFocusChangeChar() {
		return __defaultFocusChangeChar;
	}

	/**
	 *  Gets the focusableIndex attribute of the Window object
	 *
	 * @param  aIdx  Description of the Parameter
	 * @return       The focusableIndex value
	 */
	private boolean isFocusableIndex(int aIdx) {
		return (aIdx >= 0) && (aIdx < _focusableChildren.size());
	}

	/**
	 * Input handler to identify shortcuts <br>
	 * There are four important cases:
	 * <ol>
	 * <li>Window close key was entered</li>
	 * <li>Shift focus to next widget key was entered</li>
	 * <li>Some other defined shortcut key was entered</li>
	 * <li>Handling input from a child that has the focus</li>
	 * </ol>
	 * Behandlung der Eingabe. <br>
	 * Vier m?gliche F?lle: <br>
	 * 1. Fenster schliessen. <br>
	 * 2. Zum n?chsten Widget springen. <br>
	 * 3. Shortcut bearbeiten. <br>
	 * 3. Eingabe vom aktuell Fokus habenden Kind bearbeiten lassen.
	 *
	 * @param  inp  object instance representing the input char
	 * @return      true if this char is to be handled as a shortcut
	 */
	private boolean isShortCut(InputChar inp) {
		return (_shortCutsList.indexOf(inp) != -1);
	}

	/**
	 *  Finds a widget from its associated shortcut char
	 *
	 * @param  inp  object instance representing the input char.
	 * @return      The widget indexed in the shortcuts by the input char
	 */
	private Widget getWidgetByShortCut(InputChar inp) {
		return (Widget) _shortCutsTable.get(inp);
	}

	/**
	 *  Create (if necessary) a root panel and do some rectangle math on
	 * the root panel so it fits
	 */
	private void configureRootPanel() {
		if (_root == null) {
			_root = new Panel();
		}

		int x = _rect.getX();
		int y = _rect.getY();
		int width = _rect.getWidth();
		int height = _rect.getHeight();

		if (_border) {
			x++;
			y++;
			width -= 2;
			height -= 2;
		}

		_root.setSize(new Rectangle(width, height));
		_root.setX(x);
		_root.setY(y);
	}

	/**
	 *  Clip a rectangle
	 */
	private void cutIfNeeded() {
		int maxWidth = Toolkit.getScreenWidth() - _rect.getX() - (_hasShadow ? 1 : 0);
		if (_rect.getWidth() > maxWidth) {
			_rect.setWidth(maxWidth);
		}

		int maxHeight = Toolkit.getScreenHeight() - _rect.getY() - (_hasShadow ? 1 : 0);
		if (_rect.getHeight() > maxHeight) {
			_rect.setHeight(maxHeight);
		}
	}

	/**
	 * Draw features like border and title if needed
	 */
	private void drawThingsIfNeeded() {
		if (_border) {
			Toolkit.drawBorder(_rect, getBorderColors());
		}

		paintTitle();
	}

	/**
	 *  Load and show in order (i.e., changing focus) each of the window's focusable children.
	 */
	private void loadFocusableChilds() {
		_focusableChildren = _root.getListOfFocusables();
		if (!isFocusableIndex(_currentIndex)) {
			changeFocus();
		}
	}

	/**
	 *  Load the shortcut table
	 */
	private void loadShortcuts() {
		_shortCutsList.clear();
		_shortCutsTable.clear();

		Vector list = _root.getListOfWidgetsWithShortCuts();

		for (int i = 0; i < list.size(); i++) {
			Widget widget = (Widget) list.elementAt(i);
			Vector shortCuts = widget.getShortCutsList();
			_shortCutsList.addAll(shortCuts);

			for (int j = 0; j < shortCuts.size(); j++) {
				_shortCutsTable.put(shortCuts.elementAt(j), widget);
			}
		}
	}

	/**
	 *  Paint the title
	 */
	private void paintTitle() {
		if (_title != null) {
			Toolkit.printString(_title, _rect.getX() + ((_rect.getWidth() - _title.length()) / 2), _rect.getY(), getTitleColors());
		}
	}

	/**
	 * Get the Window title
	 * @return    Returns the title.
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Set the Window title
	 * @param  aTitle  The title to set.
	 */
	public void setTitle(String aTitle) {
		_title = aTitle;
		repaint();
	}
}

/**
 *  A class of utility comparators for Widget positions in Windows.
 *
 */
class WindowWidgetComparator implements Comparator {

	/**
	 *  Description of the Method
	 *
	 * @param  obj1  Description of the Parameter
	 * @param  obj2  Description of the Parameter
	 * @return       Description of the Return Value
	 */
	public int compare(Object obj1, Object obj2) {
		if (obj1 instanceof Widget && obj2 instanceof Widget) {
			Widget widget1 = (Widget) obj1;
			Widget widget2 = (Widget) obj2;

			if (isBelow(widget1, widget2)) {
				return 1;
			}

			if (isAbove(widget1, widget2) || isLeft(widget1, widget2)) {
				return -1;
			}

			if (isRight(widget1, widget2)) {
				return 1;
			}

			return 0;
		}

		return obj2.hashCode() - obj1.hashCode();
	}

	/**
	 *  Tests if one widget is above another
	 *
	 * @param  aWidget     Widget A
	 * @param  aWidgetRef  Widget B
	 * @return             true IFF A .ABOVE. B
	 */
	static boolean isAbove(Widget aWidget, Widget aWidgetRef) {
		return aWidget.getRectangle().isAbove(aWidgetRef.getRectangle());
	}

	/**
	 *  Tests if one widget is above another
	 *
	 * @param  aWidget     Widget A
	 * @param  aWidgetRef  Widget B
	 * @return             true IFF A .BELOW. B
	 */
	static boolean isBelow(Widget aWidget, Widget aWidgetRef) {
		return aWidget.getRectangle().isBelow(aWidgetRef.getRectangle());
	}

	/**
	 *  Gets the distance between two widgets
	 *
	 * @param  aWidget     Widget A
	 * @param  aWidgetRef  Widget B
	 * @return             The distance between A and B
	 
	 */
	static int getDistance(Widget aWidget, Widget aWidgetRef) {
		return aWidget.getRectangle().distanceFrom(aWidgetRef.getRectangle());
	}

	/**
	 *  Gets the horizontal distance between two widgets.
	 *
	 * @param  aWidget     Widget A
	 * @param  aWidgetRef  Widget B
	 * @return             The horizontal distance between A and B
	 */
	static int getHorizontalDistance(Widget aWidget, Widget aWidgetRef) {
		return aWidget.getRectangle().horizontalDistanceFrom(aWidgetRef.getRectangle());
	}

	/**
	 * True if Widget A is left of Widget B
	 *
	 * @param  aWidget     Widget A
	 * @param  aWidgetRef  Widget B
	 * @return             True if Widget A is left of Widget B
	 */
	static boolean isLeft(Widget aWidget, Widget aWidgetRef) {
		return aWidget.getRectangle().isLeftOf(aWidgetRef.getRectangle());
	}

	/**
	 * True if Widget A is right of Widget B
	 *
	 * @param  aWidget     Widget A
	 * @param  aWidgetRef  Widget B
	 * @return             True if Widget A is right of Widget B
	 */
	static boolean isRight(Widget aWidget, Widget aWidgetRef) {
		return aWidget.getRectangle().isRightOf(aWidgetRef.getRectangle());
	}

	/**
	 *  Gets the vertical distance between two widgets.
	 *
	 * @param  aWidget     Widget A
	 * @param  aWidgetRef  Widget B
	 * @return             The vertical distance between A and B
	 */
	static int getVerticalDistance(Widget aWidget, Widget aWidgetRef) {
		return aWidget.getRectangle().verticalDistanceFrom(aWidgetRef.getRectangle());
	}
}
