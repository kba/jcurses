
package jcurses.widgets;

import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;
import jcurses.util.Rectangle;

/**
 * This class implements a input text field to edie a one line text.
 *  
 */
public class TextField extends TextComponent
{
  private String _delimiter = null;

  /**
   * The constructor
   * 
   * @param width the preferred width, if -1, there is no preferred size.
   * @param text initial text, if null, the field is empty
   */
  public TextField(int width, String text)
  {
    super(width, 1, text);
  }

  /**
   * The constructor
   * 
   * @param width the preferred width, if -1, there is no preferred size.
   */
  public TextField(int width)
  {
    super(width, 1);
  }

  public TextField()
  {
    this(- 1);
  }

  public void setDelimiter(char c)
  {
    char[] chars = new char[1];
    chars[0] = c;
    _delimiter = new String(chars);
  }

  /**
   * @deprecated Use setBorderColors()
   */
  public void setDelimiterColors(CharColor aColor)
  {
    setBorderColors(aColor);
  }

  /**
   * @deprecated Use getBorderColors()
   */
  public CharColor getDelimiterColors()
  {
    return getBorderColors();
  }

  /**
   * @deprecated Use getBorderDefaultColors()
   */
  public CharColor getDelimiterDefaultColors()
  {
    return getBorderDefaultColors();
  }

  public void setText(String text)
  {
    if ( text == null )
      super.setText(text);
    else
    {
      if ( text.indexOf("\n") != - 1 )
        super.setText(text.substring(0, text.indexOf("\n")));
      else
        super.setText(text);
    }
  }

  protected static String getDefaultDelimiter()
  {
    return "|";
  }

  protected Rectangle getTextRectangle()
  {
    Rectangle result = (Rectangle)getSize().clone();
    result.setLocation(getAbsoluteX() + 1, getAbsoluteY());
    result.setWidth(result.getWidth() - 2);

    return result;
  }

  protected void doPaint()
  {
    super.doPaint();

    // Begrenzer malen
    Toolkit.printString(getDelimiterString(), getAbsoluteX(), getAbsoluteY(), getBorderColors());
    Toolkit.printString(getDelimiterString(), ( getAbsoluteX() + getSize().getWidth() ) - 1, getAbsoluteY(), getBorderColors());
  }

  /**
   * Input-Behandlundg
   */
  protected boolean handleInput(InputChar ch)
  {
    boolean filter = ( ch.getCode() == InputChar.KEY_UP ) || ( ch.getCode() == InputChar.KEY_DOWN ) || ( ch.getCode() == InputChar.KEY_NPAGE )
        || ( ch.getCode() == InputChar.KEY_PPAGE ) || ( ch.getCode() == InputChar.KEY_END ) || ( ch.getCode() == InputChar.KEY_HOME )
        || ( ( ! ch.isSpecialCode() ) && ( ch.getCharacter() == '\n' ) ) || ( ( ! ch.isSpecialCode() ) && ( ch.getCharacter() == '\r' ) );

    if ( ! filter )
      return super.handleInput(ch);

    return false;
  }

  private String getDelimiterString()
  {
    if ( _delimiter == null )
      return new String("|");

    return _delimiter;
  }
}