
package jcurses.widgets;

import jcurses.event.ValueChangedEvent;
import jcurses.event.ValueChangedListener;
import jcurses.event.ValueChangedListenerManager;
import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;
import jcurses.util.Rectangle;

/**
 * This class implements a checkbox widget. This checkboxes state is modified by typing a special char (default 'space'). You can register listeners by this
 * widget to track state changes.
 */
public class CheckBox extends Widget
{
  private static InputChar            __changeStatusChar = new InputChar(' ');
  private boolean                     _checked           = false;
  private ValueChangedListenerManager _listenerManager   = new ValueChangedListenerManager();

  /**
   * @deprecated Use getDefaultSelectedColors()
   */
  public CharColor getFocusedCheckboxDefaultColors()
  {
    return getDefaultSelectedColors();
  }

  /**
   * @deprecated Use getSelectedColors()
   * @return checkboxes colors, if it is focused
   */
  public CharColor getFocusedCheckboxColors()
  {
    return getSelectedColors();
  }

  /**
   * Sets colors of the checkbox in focused state.
   * 
   * @deprecated Use setSelectedColors()
   * @param aColor checkboxes colors, if it is focused
   */
  public void setFocusedCheckboxColors(CharColor aColor)
  {
    setSelectedColors(aColor);
  }

  /**
   * Adds listener to the checkbox to track states changes
   * 
   * @param listener listener to add
   */
  public void addListener(ValueChangedListener listener)
  {
    _listenerManager.addListener(listener);
  }

  /**
   * Removes listener from the checkbox
   * 
   * @param listener to remove
   */
  public void removeListener(ValueChangedListener listener)
  {
    _listenerManager.removeListener(listener);
  }

  /**
   * The constructor.
   * 
   * @param checked true, if the checkbox is checked at first time, false otherwise
   */
  public CheckBox(boolean checked)
  {
    _checked = checked;
  }

  /**
   * The constructor creates an unchecked checkbox
   */
  public CheckBox()
  {
    this(false);

  }

  /**
   * return true, if the checkbox is checked , false otherwise
   */
  public boolean getValue()
  {
    return _checked;
  }

  /**
   * Sets checkboxes value
   * 
   * @param value if the checkbox becomes checked , false otherwise
   */
  public void setValue(boolean value)
  {
    boolean oldValue = _checked;
    _checked = value;
    if ( oldValue != _checked )
    {
      _listenerManager.handleEvent(new ValueChangedEvent(this));
    }
    paint();
  }

  protected Rectangle getPreferredSize()
  {
    return new Rectangle(3, 1);
  }

  protected void doPaint()
  {
    Rectangle rect = (Rectangle)getSize().clone();
    rect.setLocation(getAbsoluteX(), getAbsoluteY());
    String text = "[" + ( ( _checked ) ? "X" : " " ) + "]";
    CharColor colors = hasFocus() ? getFocusedCheckboxColors() : getColors();
    Toolkit.printString(text, rect, colors);
  }

  protected boolean isFocusable()
  {
    return true;
  }

  protected void doRepaint()
  {
    doPaint();
  }

  protected boolean handleInput(InputChar ch)
  {
    if ( ch.equals(__changeStatusChar) )
    {
      setValue(( _checked ) ? false : true);
      paint();
      return true;
    }

    return false;
  }

  protected void focus()
  {
    changeColors();
  }

  protected void unfocus()
  {
    changeColors();
  }

  private void changeColors()
  {
    //CharColor colors = hasFocus() ? getFocusedCheckboxColors() : getColors();
    //Toolkit.changeColors(getRectangle(), colors);
    doRepaint();
  }

}