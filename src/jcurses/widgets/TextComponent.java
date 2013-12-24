
package jcurses.widgets;

import jcurses.event.ValueChangedEvent;
import jcurses.event.ValueChangedListener;
import jcurses.event.ValueChangedListenerManager;

import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;

import jcurses.util.Paging;
import jcurses.util.Rectangle;

import java.util.ArrayList;

/**
 * The class is the superclass for text editing widgets
 */
public class TextComponent extends Widget
{
  //private static CharColor __textComponentDefaultColors = new CharColor(CharColor.MAGENTA, CharColor.BLACK);
  //private static CharColor __focusedTextComponentDefaultColors = new CharColor(CharColor.BLUE, CharColor.WHITE, CharColor.REVERSE);
  //private static CharColor __cursorDefaultColors = new CharColor(CharColor.BLACK, CharColor.WHITE);
  //private CharColor _cursorColors = getCursorDefaultColors();
  //private CharColor _focusedTextComponentColors = getFocusedTextComponentDefaultColors();
  ArrayList                           _lineLengths     = new ArrayList();
  ArrayList                           _lines           = new ArrayList();
  StringBuffer                        _text            = new StringBuffer("");

  //Listener
  private ValueChangedListenerManager _listenerManager = new ValueChangedListenerManager();

  /*
   * private static String escapeString(String text) { StringBuffer buf = new StringBuffer(); for (int i=0; i <text.length(); i++) { char c = text.charAt(i); if
   * (c == '\n') { buf.append("\\n"); } else if (c == '\r') { buf.append("\\r"); } else if (c == '\t') { buf.append("\\t"); } else { buf.append(c); } }
   * 
   * return buf.toString(); }
   */
  private boolean                     editable         = true;
  private int                         _cursPosX        = 0;
  private int                         _cursPosY        = 0;
  private int                         _firstChar       = 0;
  private int                         _firstLine       = 0;
  private int                         _preferredHeight = 0;
  private int                         _preferrdWidth   = 0;

  /**
   * A constructor accepting both the X and Y coordinates of the component and the String containig the inital contents of the component.
   * 
   * @param width the preferred width of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container
   *          and the text
   * @param height the preferred height of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container.
   * @param text the contents of the component
   */
  public TextComponent(int width, int height, String text)
  {
    _preferrdWidth = width;
    _preferredHeight = height;
    setText(text);
  }

  /**
   * The constructor
   * 
   * @param width the preferred width of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container
   *          and the text
   * @param height the preferred height of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container
   *          and the text.
   */
  public TextComponent(int width, int height)
  {
    this(width, height, "");
  }

  /**
   * Constructor with no arguments. No preferred width and height, empty.
   */
  public TextComponent()
  {
    this(- 1, - 1, "");
  }

  /**
   * Constructor taking the contents for the component as a parameter
   * 
   * @param text the contents of the component
   */
  public TextComponent(String text)
  {
    this(- 1, - 1, text);
  }

  public void setCursorColors(CharColor aColor)
  {
    setColors(aColor);
  }

  public CharColor getCursorColors()
  {
    return getColors();
  }

  public CharColor getCursorDefaultColors()
  {
    return getDefaultColors();
  }

  /**
   * The method sets the cursor position to given koordinates ( within the text, not widget )
   * 
   * @param x new x cursor coordinate within the text
   * @param y new y cursor coordinate within the text
   */
  public void setCursorLocation(int x, int y)
  {
    setCursorLocation(x, y, false);
  }

  /**
   * Sets the editable.
   * 
   * @param aEditable The editable to set
   */
  public void setEditable(boolean aEditable)
  {
    editable = aEditable;
  }

  /**
   * Returns the editable.
   * 
   * @return boolean
   */
  public boolean isEditable()
  {
    return editable;
  }

  /**
   * @deprecated Use setSelectedColors()
   */
  public void setTextComponentColors(CharColor aColor)
  {
    setSelectedColors(aColor);
  }

  /**
   * @deprecated Use getSelectedColors()
   */
  public CharColor getFocusedTextComponentColors()
  {
    return getSelectedColors();
  }

  /**
   * @deprecated Use getDefaultSelectedColors()
   */
  public CharColor getFocusedTextComponentDefaultColors()
  {
    return getDefaultSelectedColors();
  }

  /**
   * Sets the conteined text
   * 
   * @param text text to set
   */
  public void setText(String text)
  {
    setText(text, true);
  }

  /**
   * Sets the contained text
   * 
   * @param text text to set
   * @param dispatchEvent if true, the widget is repainted
   */
  public void setText(String text, boolean dispatchEvent)
  {
    text = ( text == null ) ? "" : text;
    _text = new StringBuffer(text);
    updateText(dispatchEvent);
    reset();
    repaint();
  }

  /**
   * @return contained text
   */
  public String getText()
  {
    return _text.toString();
  }

  public void addListener(ValueChangedListener listener)
  {
    _listenerManager.addListener(listener);
  }

  public void removeListener(ValueChangedListener listener)
  {
    _listenerManager.removeListener(listener);
  }

  protected boolean isFocusable()
  {
    return true;
  }

  /**
   * DOCUMENT ME!
   * 
   * @return preferred height
   */
  protected int getPreferredHeight()
  {
    return _preferredHeight;
  }

  /**
   * Aus Widget
   * 
   * @return DOCUMENT ME!
   */
  protected Rectangle getPreferredSize()
  {
    return new Rectangle(_preferrdWidth, _preferredHeight);
  }

  protected int getTextHeight()
  {
    return _lines.size();
  }

  /**
   * The method returns the rectangle, within that the text is painted. Is overrided by derived classes for example to implement a border.
   * 
   * @return DOCUMENT ME!
   */
  protected Rectangle getTextRectangle()
  {
    Rectangle result = getSize();
    result.setLocation(getAbsoluteX(), getAbsoluteY());
    result.setHeight(result.getHeight());
    return result;
  }

  protected int getTextWidth()
  {
    int result = 0;

    for ( int i = 0; i < _lineLengths.size(); i++ )
    {
      int value = ( (Integer)_lineLengths.get(i) ).intValue();

      if ( value > result )
        result = value;
    }

    return result;
  }

  /**
   * Gets the X coordinate of first character int the component.
   * 
   * @return the X coordinate of first character int the component.
   */
  protected int getTextX()
  {
    return _firstChar;
  }

  /**
   * Gets the Y coordinate of first character int the component.
   * 
   * @return the Y coordinate of first character int the component.
   */
  protected int getTextY()
  {
    return _firstLine;
  }

  /**
   * Gets the preferred width of the component
   * 
   * @return preferred width
   */
  protected int getPreferredWidth()
  {
    return _preferrdWidth;
  }

  protected void doPaint()
  {
    drawBox();
    drawText();

    if ( hasFocus() )
      drawCursor();
  }

  protected void doRepaint()
  {
    doPaint();
  }

  /**
   * This method draws text-dependent additional things such as scrollbars. As default it makes nothing, can be overriden in inherited classes ( for example
   * text area)
   */
  protected void drawAdditionalThings()
  {
  //nothing
  }

  /**
   * Aus Widget
   */
  protected void focus()
  {
    changeColors();
    drawCursor();
  }

  protected boolean handleInput(InputChar ch)
  {
    int bCursorPosX = _cursPosX;
    int bCursorPosY = _cursPosY;
    int bFirstChar = _firstChar;
    int bFirstLine = _firstLine;
    char bChar = getCharacterAtCursorPosition();

    if ( ch.getCode() == InputChar.KEY_RIGHT )
    {
      setCursorLocation(_cursPosX + 1, _cursPosY);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_LEFT )
    {
      setCursorLocation(_cursPosX - 1, _cursPosY);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_UP )
    {
      setCursorLocation(_cursPosX, _cursPosY - 1);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_DOWN )
    {
      setCursorLocation(_cursPosX, _cursPosY + 1);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_HOME )
    {
      setCursorLocation(_cursPosX, 0);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_END )
    {
      setCursorLocation(_cursPosX, getTextHeight() - 1);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_NPAGE )
    {
      int newYPos = 0;

      if ( getCurrentPageNumber() < ( getPageSize() - 1 ) )
        newYPos = getPaging().getIndexByPageOffset(getCurrentPageNumber() + 1, getCurrentPageOffset());
      else
        newYPos = getTextHeight() - 1;

      setCursorLocation(_cursPosX, newYPos, true);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_PPAGE )
    {
      int newYPos = 0;

      if ( getCurrentPageNumber() > 0 )
        newYPos = getPaging().getIndexByPageOffset(getCurrentPageNumber() - 1, getCurrentPageOffset());
      else
        newYPos = 0;

      setCursorLocation(_cursPosX, newYPos, true);
      redrawAfterCursorMove(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine, bChar);
    }
    else if ( ch.getCode() == InputChar.KEY_BACKSPACE )
    {
      deleteCharBeforeCursorLocation();

      if ( _cursPosX == 0 )
      {
        if ( _cursPosY > 0 )
        {
          int y = _cursPosY - 1;

          if ( y < 0 )
            y = 0;

          int x = ( (Integer)_lineLengths.get(y) ).intValue();
          setCursorLocation(x, y);
          redrawAfterTextChange(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine);
        }
      }
      else
      {
        setCursorLocation(_cursPosX - 1, _cursPosY);
        redrawLine(_cursPosY);
      }

      return true;
    }
    else if ( ! ch.isSpecialCode() )
    {
      char c = ch.getCharacter();
      insertCharAtCursorLocation(c);

      if ( c == '\n' )
      {
        setCursorLocation(0, _cursPosY + 1);
        redrawAfterTextChange(bCursorPosX, bCursorPosY, bFirstChar, bFirstLine);
      }
      else
      {
        setCursorLocation(_cursPosX + 1, _cursPosY);
        redrawLine(_cursPosY);
      }

      return true;
    }

    return false;
  }

  /**
   * This method refreshes text-dependent additional after a text change such as scrollbars. As default it makes nothing, can be overriden in inherited classes (
   * for example text area)
   */
  protected void refreshAdditionalThings()
  {
  //nothing
  }

  /**
   * This method replaces a line of text to printing througth another text line. This can be overrided in derived classes for example for painting all chars as
   * capitals, or for password input. Here the same line is returned.
   * 
   * @param line a text line to replace, contains no line breaks
   * 
   * @return decoded line
   */
  protected String replaceTextLineForPrinting(String line)
  {
    return line;
  }

  protected void unfocus()
  {
    changeColors();
    redrawOldChar(_cursPosX, _cursPosY, getCharacterAtCursorPosition());
  }

  int getCurrentPageOffset()
  {
    return getPaging().getPageOffset(_cursPosY);
  }

  int getPageEndIndex(int pageNumber)
  {
    return getPaging().getPageEndIndex(pageNumber);
  }

  int getPageStartIndex(int pageNumber)
  {
    return getPaging().getPageStartIndex(pageNumber);
  }

  private char getCharacterAtCursorPosition()
  {
    char result = 0;

    if ( _text.length() > 0 )
    {
      String line = getLine(_cursPosY);

      if ( _cursPosX < line.length() )
        result = line.charAt(_cursPosX);
      else
        result = 0;
    }

    return result;
  }

  private int getCurrentPageNumber()
  {
    return getPageNumber(_cursPosY);
  }

  private boolean isCursorChanged(int x, int y)
  {
    return ! ( ( _cursPosX == x ) && ( _cursPosY == y ) );
  }

  private void setCursorLocation(int x, int y, boolean pageAlignment)
  {
    if ( y < 0 )
      _cursPosY = 0;
    else if ( y >= _lines.size() )
      _cursPosY = _lines.size() - 1;
    else
      _cursPosY = y;

    int length = ( (Integer)_lineLengths.get(_cursPosY) ).intValue();

    if ( x < 0 )
      _cursPosX = 0;
    else if ( x >= length )
      _cursPosX = length;
    else
      _cursPosX = x;

    _cursPosY = ( _cursPosY < 0 ) ? 0 : _cursPosY;

    // first Position
    if ( _firstChar > _cursPosX )
      _firstChar = _cursPosX;
    else if ( _firstChar < ( _cursPosX - getTextRectangle().getWidth() + 1 ) )
      _firstChar = ( _cursPosX - getTextRectangle().getWidth() + 1 );

    if ( pageAlignment )
      _firstLine = getPageStartIndex(getPageNumber(_cursPosY));
    else
    {
      if ( _firstLine > _cursPosY )
        _firstLine = _cursPosY;
      else if ( _firstLine < ( _cursPosY - getTextRectangle().getHeight() + 1 ) )
        _firstLine = ( _cursPosY - getTextRectangle().getHeight() + 1 );
    }
  }

  private int getFirstCharNumber()
  {
    /*
     * int result = _cursPosX - getTextRectangle().getWidth()+1; result = (result < 0)?0:result;
     */
    return _firstChar;
  }

  private int getFirstLineNumber()
  {
    /*
     * int result = _cursPosY - getTextRectangle().getHeight()+1; result = (result < 0)?0:result;
     */
    return _firstLine;
  }

  private String getLine(int index)
  {
    if ( index > _lines.size() )
      return null;

    int pos = ( (Integer)_lines.get(index) ).intValue();
    int length = ( (Integer)_lineLengths.get(index) ).intValue();

    return _text.substring(pos, pos + length);
  }

  private Rectangle getLineRectangle(int firstLine)
  {
    Rectangle rect = getTextRectangle();
    int y = ( ( firstLine - _firstLine ) >= 0 ) ? ( firstLine - _firstLine ) : 0;
    rect.setLocation(rect.getX(), rect.getY() + y);
    rect.setHeight(1);
    return rect;
  }

  private int getPageNumber(int index)
  {
    return getPaging().getPageNumber(index);
  }

  private int getPageSize()
  {
    return getPaging().getPageSize();
  }

  //Paging
  private Paging getPaging()
  {
    return new Paging(getVisibleHeight(), getTextHeight());
  }

  private boolean isTextChanged(int x, int y)
  {
    return ! ( ( _firstChar == x ) && ( _firstLine == y ) );
  }

  private Rectangle getTextRectangle(int firstLine)
  {
    Rectangle rect = getTextRectangle();
    int y = ( ( firstLine - _firstLine ) >= 0 ) ? ( firstLine - _firstLine ) : 0;
    rect.setLocation(rect.getX(), rect.getY() + y);
    rect.setHeight(rect.getHeight() - y);
    return rect;
  }

  private int getVisibleHeight()
  {
    return getTextRectangle().getHeight();
  }

  private void changeColors()
  {
    //Rectangle rect = getTextRectangle();
    //CharColor colors = hasFocus() ? getSelectedColors() : getColors();
    //Toolkit.changeColors(rect, colors);
    doRepaint();
  }

  private void deleteCharBeforeCursorLocation()
  {
    if ( ! isEditable() )
      return;

    int pos = ( (Integer)_lines.get(_cursPosY) ).intValue() + _cursPosX;

    if ( pos > 0 )
      _text.deleteCharAt(pos - 1);

    updateText();
  }

  private void drawBox(Rectangle rect)
  {
    CharColor colors = hasFocus() ? getSelectedColors() : getColors();
    Toolkit.drawRectangle(rect, colors);
  }

  private void drawBox()
  {
    drawBox(getTextRectangle());
  }

  private void drawChar(int x, int y, CharColor colors, char c)
  {
    int x1 = x - _firstChar;
    int y1 = y - _firstLine;
    String toPrint = ( c == 0 ) ? " " : replaceTextLineForPrinting("" + c);
    Toolkit.printString(toPrint, getTextRectangle().getX() + x1, getTextRectangle().getY() + y1, colors);
  }

  private void drawCursor()
  {
    char c = getCharacterAtCursorPosition();
    drawChar(_cursPosX, _cursPosY, getCursorColors(), c);
  }

  private void drawLine(int index)
  {
    Rectangle rect = getTextRectangle();
    CharColor colors = hasFocus() ? getSelectedColors() : getColors();
    int firstLine = getFirstLineNumber();
    int firstChar = getFirstCharNumber();
    int pos = ( (Integer)_lines.get(index) ).intValue();
    int length = ( (Integer)_lineLengths.get(index) ).intValue();

    if ( ( firstChar < length ) && ( ( index - firstLine ) < rect.getHeight() ) )
    {
      int length2 = length - firstChar;
      int length3 = ( length2 > rect.getWidth() ) ? rect.getWidth() : length2;
      Toolkit.printString(replaceTextLineForPrinting(_text.substring(pos + firstChar, pos + firstChar + length3)), rect.getX(), ( rect.getY() + index )
          - firstLine, colors);
    }
  }

  private void drawText(int index)
  {
    int firstLine = getFirstLineNumber();
    int begin = ( firstLine > index ) ? firstLine : index;

    for ( int i = begin; i < _lines.size(); i++ )
      drawLine(i);
  }

  private void drawText()
  {
    drawText(0);
  }

  private void insertCharAtCursorLocation(char c)
  {
    if ( ! isEditable() )
      return;

    int pos = ( (Integer)_lines.get(_cursPosY) ).intValue() + _cursPosX;
    _text.insert(pos, c);
    updateText();
  }

  private void redrawAfterCursorMove(int bCursorPosX, int bCursorPosY, int bFirstChar, int bFirstLine, char bChar)
  {
    if ( isTextChanged(bFirstChar, bFirstLine) )
      paint();
    else if ( isCursorChanged(bCursorPosX, bCursorPosY) )
    {
      redrawOldChar(bCursorPosX, bCursorPosY, bChar);
      drawCursor();
    }
  }

  private void redrawAfterTextChange(int bCursorPosX, int bCursorPosY, int bFirstChar, int bFirstLine)
  {
    if ( isTextChanged(bFirstChar, bFirstLine) )
      paint();
    else if ( isCursorChanged(bCursorPosX, bCursorPosY) )
    {
      int y = Math.min(_cursPosY, bCursorPosY);
      drawBox(getTextRectangle(y));
      drawText(y);
      drawCursor();
      refreshAdditionalThings();
    }
  }

  private void redrawLine(int index)
  {
    drawBox(getLineRectangle(index));
    drawLine(index);
    drawCursor();
    refreshAdditionalThings();
  }

  private void redrawOldChar(int bCursorPosX, int bCursorPosY, char bChar)
  {
    CharColor colors = hasFocus() ? getSelectedColors() : getColors();
    drawChar(bCursorPosX, bCursorPosY, colors, bChar);
  }

  private void reset()
  {
    _cursPosX = 0;
    _cursPosY = 0;
    _firstChar = 0;
    _firstLine = 0;
  }

  private void updateText()
  {
    updateText(true);
  }

  private void updateText(boolean dispatchEvent)
  {
    //neu darstellen
    _lines.clear();
    _lineLengths.clear();

    String text = _text.toString();

    int pos = 0;

    while ( text.indexOf("\n", pos) != - 1 )
    {
      _lines.add(new Integer(pos));
      _lineLengths.add(new Integer(text.indexOf("\n", pos) - pos));
      pos = text.indexOf("\n", pos) + 1;
    }

    if ( pos < text.length() )
    {
      _lines.add(new Integer(pos));
      _lineLengths.add(new Integer(_text.length() - pos));
    }

    if ( _lines.size() == 0 )
    {
      _lines.add(new Integer(0));
      _lineLengths.add(new Integer(0));
    }
    else
    {
      if ( text.endsWith("\n") )
      {
        _lines.add(new Integer(text.length()));
        _lineLengths.add(new Integer(0));
      }
    }

    //event abschicken
    if ( dispatchEvent )
      _listenerManager.handleEvent(new ValueChangedEvent(this));
  }
}