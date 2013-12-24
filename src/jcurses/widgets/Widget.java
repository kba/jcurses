/**
 * Dies ist die Root-Klasse f?r alle Widgets.
 */

package jcurses.widgets;

import java.util.Vector;

import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.themes.Theme;
import jcurses.themes.WindowThemeOverride;
import jcurses.util.Rectangle;

/**
 * This class is superclass for all jcurses widgets. For implementing a ne widget you must derive it.
 * 
 * An jcurses widget is already used within a window. Its task ist to help it's <code>WidgetContainer</code> to layout itself, giving needed informations, to
 * paint itself and to handle input. Handling input is needed only, if the widget has is an input widget ( examples are text input widget, list widget) and has
 * currently focus, that is is selected by user to handle input. This selectig ocurrs by typing a special key (currenty 'tab') to switch between input widgets.
 * 
 * All widgets are ordered in a hierarchy. An widget is already has a container, if it isn't the root pane of a window.
 */
public abstract class Widget
{
  WidgetContainer   _parent  = null;
  Window            _window  = null;
  private Theme     _theme   = new WindowThemeOverride();
  private Rectangle _rect    = new Rectangle();
  private boolean   _focus   = false;

  /**
   * Methoden, die Sichtbarkeit regeln. Ein widget ist dann sichtbar wenn er UND sein Parent sichtbar sind
   */
  private boolean   _visible = true;

  /**
   * Set colors of the widget
   * 
   * @param aColors new colors
   *  
   */
  public void setColors(CharColor aColors)
  {
    _theme.setColor(Theme.COLOR_WIDGET_TEXT, aColors);
  }

  public void setColors(String aKey, CharColor aColors)
  {
    _theme.setColor(aKey, aColors);
  }

  /**
   * @return colors of the widget
   */
  public CharColor getColors()
  {
    return _theme.getColor(Theme.COLOR_WIDGET_TEXT);
  }

  public CharColor getColors(String aKey)
  {
    return _theme.getColor(aKey);
  }

  public CharColor getDefaultColors(String aKey)
  {
    return WindowManager.getTheme().getColor(aKey);
  }

  public CharColor getBorderDefaultColors()
  {
    return getDefaultColors(Theme.COLOR_WIDGET_BORDER);
  }

  public CharColor getDefaultSelectedColors()
  {
    return getDefaultColors(Theme.COLOR_WIDGET_SELECTED);
  }

  /**
   * @return button's colors, if it is focused
   */
  public CharColor getSelectedColors()
  {
    return _theme.getColor(Theme.COLOR_WIDGET_SELECTED);
  }

  /**
   * Sets button's colors in focused state
   * 
   * @param aColor button's colors, if it is focused
   */
  public void setSelectedColors(CharColor aColor)
  {
    _theme.setColor(Theme.COLOR_WIDGET_SELECTED, aColor);
  }

  /**
   * Sets colors used painting the title
   * 
   * @param aColors colors used painting the title
   */
  public void setTitleColors(CharColor aColors)
  {
    setColors(Theme.COLOR_WIDGET_TITLE, aColors);
  }

  /**
   * Gets the colors used for painting the title.
   * 
   * @return colors used painting the title
   */
  public CharColor getTitleColors()
  {
    return getColors(Theme.COLOR_WIDGET_TITLE);
  }

  public CharColor getActionColors()
  {
    return getColors(Theme.COLOR_WIDGET_ACTION);
  }

  public void setActionColors(CharColor aColor)
  {
    setColors(Theme.COLOR_WIDGET_ACTION, aColor);
  }

  public CharColor getBorderColors()
  {
    return getColors(Theme.COLOR_WIDGET_BORDER);
  }

  public void setBorderColors(CharColor aBorderColors)
  {
    setColors(Theme.COLOR_WIDGET_BORDER, aBorderColors);
  }

  /**
   * The method switches focus to this widget, if it is focusable at all.
   */
  public void getFocus()
  {
    if ( getWindow() != null )
      getWindow().changeFocus(this);
  }

  /**
   * @return widget's size
   */
  public Rectangle getSize()
  {
    return new Rectangle(getWidth(), getHeight());
  }

  /**
   * The method manages visibility
   * 
   * @param visible true, if the widget is to make visible, false otherwise.
   */
  public void setVisible(boolean visible)
  {
    _visible = visible;
  }

  /**
   * The method returns true, if the visibility flag of the widget is true. This doesn't mean that the widget ist currently visible, because the parent whole
   * window can be unvisible, use the method <code>isVisible</code> to query the visisbility
   * 
   * @return true, if the visibility flag is set, false otherwise
   */
  public boolean getVisible()
  {
    return _visible;
  }

  /**
   * return true, if the widget is currently visible, false otherwise.
   *  
   */
  public boolean isVisible()
  {
    Widget parent = getParent();

    if ( ( parent != null ) && ( ! ( parent.isVisible() ) ) )
      return false;

    Window w = getWindow();
    boolean result = ( ( _visible ) && ( w != null ) && ( w.isVisible() ) );
    return result;
  }

  /**
   * @return x coordinate within the container
   */
  public int getX()
  {
    return _rect.getX();
  }

  public int getWidth()
  {
    return _rect.getWidth();
  }

  public int getHeight()
  {
    return _rect.getHeight();
  }

  /**
   * @return y coordinate within the container
   */
  public int getY()
  {
    return _rect.getY();
  }

  /**
   * @return true, if the widget has currenty focus,that is handles input, in othe case false
   */
  public boolean hasFocus()
  {
    return _focus && ( _parent == null || _parent.hasFocus() );
  }

  /**
   * @return x coordinate on the screen
   */
  protected int getAbsoluteX()
  {
    int mX = getX();

    if ( getParent() != null )
      mX += getParent().getAbsoluteX(); // + getParent().getClientArea().getX();

    return mX;
  }

  /**
   * @return y coordinate on the screen
   */
  protected int getAbsoluteY()
  {
    int mY = getY();
    if ( getParent() != null )
      mY += getParent().getAbsoluteY();
    //mY += getParent().getClientArea().getY();
    return mY;
    /*
     * 
     * int result = _rect.getY();
     * 
     * for (WidgetContainer mWidget = getParent(); mWidget != null; mWidget = getParent()) result += mWidget.getChildsRectangle().getX();
     * 
     * result = _rect.getY() + getParent().getAbsoluteY();
     * 
     * if ( getParent().getChildsRectangle() != null ) result = result + getParent().getChildsRectangle().getY();
     * 
     * return result;
     */
  }

  /**
   * Sets widget's container. Is called by framework, schouldn't be called writing applications
   * 
   * @param parent new container
   */
  protected void setParent(WidgetContainer parent)
  {
    _parent = parent;
  }

  /**
   * @return widget's container
   */
  protected WidgetContainer getParent()
  {
    return _parent;
  }

  /**
   * This method gives the widget container the infomation about the preferred size of this widget. Must be implemented by derived classes.
   */
  protected abstract Rectangle getPreferredSize();

  /**
   * Returns the rectangle on the screen, that contains this widget
   * 
   * @return the rectangle on the screen, that contains this widget
   */
  protected Rectangle getRectangle()
  {
    return new Rectangle(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
  }

  protected void setSize(int aWide, int aHigh)
  {
    setWidth(aWide);
    setHeight(aHigh);
  }

  /**
   * Sets the size of the widget.
   * 
   * @param size new size
   */
  protected void setSize(Rectangle size)
  {
    setSize(size.getWidth(), size.getHeight());
  }

  /**
   * /** Sets widget's window. Is called by framework, schouldn't be called writing applications
   * 
   * @param window widget's window
   */
  protected void setWindow(Window window)
  {
    _window = window;
  }

  /**
   * @return widget's window
   */
  public Window getWindow()
  {
    if ( getParent() == null )
      return _window;

    return getParent().getWindow();
  }

  /**
   * Sets the x coordinate within the container. Is called by framework, schouldn't be called writing applications
   * 
   * @param aX x coordinate within the container
   */
  protected void setX(int aX)
  {
    _rect.setX(aX);
  }

  /**
   * Sets the y coordinate within the container. Is called by framework, schouldn't be called writing applications
   * 
   * @param aY y coordinate within the container
   */
  protected void setY(int aY)
  {
    _rect.setY(aY);
  }

  /**
   * This method paints the widget. Will be called by <code>paint()<code>,
   * only if the widget is visible. Must be implemented be derived classes.
   */
  protected abstract void doPaint();

  /**
   * This method repaints the widget. Will be called by <code>paint()<code>,
   * only if the widget is visible. Calls doPaint() by default. Override in a subclass if repaint() can be more efficient that paint().
   */
  protected void doRepaint()
  {
    doPaint();
  }

  /**
   * @return default colors for this widget. What this mentiones in a concret case, is dependent on the derived class.
   */
  public CharColor getDefaultColors()
  {
    return _theme.getColor(Theme.COLOR_DEFAULT);
  }

  /**
   * The method declares, whether the widget can handle input ( get focus ), that is, whether this is an input widget.
   * 
   * @return true, if the widget can handle input, in other case false
   */
  protected boolean isFocusable()
  {
    return false;
  }

  /**
   * This method returns a list of short cut chars, that the widget want to handle. If a char from the list is typed by user, it will be handled always my this
   * widget not bei the widget currenty having focus, except the having focus widget handles ALL chars and tells this throuth the method
   * <code>handleAllPrintableChars</code>. To enable shortcuts for a new widget, you must override this method.
   */
  protected Vector getShortCutsList()
  {
    return null;
  }

  /**
   * The method is called by <code>setFocus</code> to tell the widget that it has recieved focus. This method should be overridden by subclassesto react
   * getting focus, for example to repaint widget getting focus.
   */
  protected void focus()
  {
  // this is for subclasses to implement, but need to be concrete here
  }

  /**
   * The method is called by framework to let the widget handle an input char. Schould be overrided be derived classes, if these can handle input.
   * 
   * @return true, if the widget has handled the char, false in other case
   */
  protected boolean handleInput(InputChar inputChar)
  {
    return false;
  }

  /**
   * The method is called by the framework to paint the widget
   */
  protected void paint()
  {
    //System.err.println("("+this+").paint() - enter @ " + System.currentTimeMillis());
    
    if ( isVisible() )
      doPaint();
    //System.err.println("("+this+").paint() - exit @ " + System.currentTimeMillis());
  }

  /**
   * The method is called by the framework to repaint the widget
   */
  protected void repaint()
  {
    if ( isVisible() )
      doRepaint();
  }

  /**
   * The method is called by <code>setFocus</code> to tell widget that it has lost focus. This method should be overridden by subclasses to react losing
   * focus, for example to repaint widget losing focus.
   */
  protected void unfocus()
  {
  // this is for subclasses to implement, but need to be concrete here
  }

  /**
   * The method is called by framework if focus is switched,that is, either the widget has get or lost focus.
   * 
   * @param aFocus true, if the widget has get focus, in other case false
   */
  void setFocus(boolean aFocus)
  {
    _focus = aFocus;
    if ( _parent != null )
      _parent.setFocus(_focus);

    if ( aFocus )
      focus();
    else
      unfocus();
  }

  protected void setHeight(int aHeight)
  {
    _rect.setHeight(aHeight);
  }

  protected void setLocation(int aX, int aY)
  {
    _rect.setLocation(aX, aY);
  }

  protected void setWidth(int aWidth)
  {
    _rect.setWidth(aWidth);
  }
}