
package jcurses.widgets;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;
import jcurses.themes.Theme;
import jcurses.util.Rectangle;

/**
 * This class implements a text area to edit a text with meny lines
 */
public class TextArea extends TextComponent implements IScrollable
{
  private ScrollbarPainter _scrollbars              = null;

  /**
   * The constructor
   * 
   * @param width the preferred width of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container
   *          and the text
   * @param height the preferred height of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container.
   * @param text the initial text, if <code>null</code> the component is empty
   */
  public TextArea(int width, int height, String text)
  {
    super(width, height, text);
    _scrollbars = new ScrollbarPainter(this);
  }

  /**
   * The constructor
   * 
   * @param width the preferred width of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container
   *          and the text
   * @param height the preferred height of the component. If -1 is stated, there is no preferred width and the component is layouted dependend on the container.
   */
  public TextArea(int width, int height)
  {
    this(width, height, null);
  }

  /**
   * Constructor without arguments
   */
  public TextArea()
  {
    this(- 1, - 1);
  }

  public Rectangle getBorderRectangle()
  {
    Rectangle rect = (Rectangle)getSize().clone();
    rect.setLocation(getAbsoluteX(), getAbsoluteY());
    return rect;
  }

  public float getHorizontalScrollbarLength()
  {
    if ( ! ( ( getTextWidth() > 0 ) && ( getTextWidth() > getVisibleTextWidth() ) ) )
      return 0;

    return ( (float)getVisibleTextWidth() ) / ( (float)getTextWidth() );
  }

  public float getHorizontalScrollbarOffset()
  {
    if ( ! ( ( getTextWidth() > 0 ) && ( getTextWidth() > getVisibleTextWidth() ) ) )
      return 0;

    return ( (float)getTextX() ) / ( (float)getTextWidth() );
  }

  public void setScrollbarColors(CharColor aColors)
  {
    setColors(Theme.COLOR_WIDGET_SCROLLBAR, aColors);
  }

  public CharColor getScrollbarColors()
  {
    return getColors(Theme.COLOR_WIDGET_SCROLLBAR);
  }

  public CharColor getScrollbarDefaultColors()
  {
    return getDefaultColors(Theme.COLOR_WIDGET_SCROLLBAR); 
  }

  public float getVerticalScrollbarLength()
  {
    if ( ! ( ( getTextHeight() > 0 ) && ( getTextHeight() > getVisibleTextHeight() ) ) )
      return 0;

    return ( (float)getVisibleTextHeight() ) / ( (float)getTextHeight() );
  }

  public float getVerticalScrollbarOffset()
  {
    if ( ! ( ( getTextHeight() > 0 ) && ( getTextHeight() > getVisibleTextHeight() ) ) )
      return 0;

    return ( (float)getTextY() ) / ( (float)getTextHeight() );
  }

  public boolean hasHorizontalScrollbar()
  {
    return true;
  }

  public boolean hasVerticalScrollbar()
  {
    return true;
  }

  protected Rectangle getTextRectangle()
  {
    Rectangle result = (Rectangle)getSize().clone();
    result.setLocation(getAbsoluteX() + 1, getAbsoluteY() + 1);
    result.setWidth(result.getWidth() - 2);
    result.setHeight(result.getHeight() - 2);

    return result;
  }

  protected void doPaint()
  {
    super.doPaint();
    Toolkit.drawBorder(getBorderRectangle(), getBorderColors());
    drawAdditionalThings();
  }

  protected void drawAdditionalThings()
  {
    _scrollbars.paint();
  }

  protected void refreshAdditionalThings()
  {
    _scrollbars.refresh();
  }

  private int getVisibleTextHeight()
  {
    return getSize().getHeight() - 2;
  }

  //Scrollbars

  /*
   * private void drawVerticalScrollbar() { Rectangle rect = (Rectangle)getSize().clone(); rect.setLocation(getAbsoluteX(), getAbsoluteY());
   * 
   * int visibleTextWidth = rect.getWidth()-2; int visibleTextHeight = rect.getHeight()-2;
   * 
   * if ((getTextHeight()>0) &&(getTextHeight() > visibleTextHeight)) { float firstPart = ((float)getTextY())/((float)getTextHeight()); float lastPart =
   * ((float)(getTextHeight()-visibleTextHeight-getTextY()))/((float)getTextHeight());
   * ScrollbarUtils.drawScrollBar(rect.getY()+1,rect.getY()+rect.getHeight()-2, rect.getX()+rect.getWidth()-1, firstPart, lastPart, ScrollbarUtils.VERTICAL); }
   * 
   * 
   *  }
   * 
   * 
   * private void drawHorizontalScrollbar() { Rectangle rect = (Rectangle)getSize().clone(); rect.setLocation(getAbsoluteX(), getAbsoluteY());
   * 
   * int visibleTextWidth = rect.getWidth()-2; int visibleTextHeight = rect.getHeight()-2;
   * 
   * if ((getTextWidth()>0) &&(getTextWidth() > visibleTextWidth)) { float firstPart = ((float)getTextX())/((float)getTextWidth()); float lastPart =
   * ((float)(getTextWidth()-visibleTextWidth-getTextX()))/((float)getTextWidth()); ScrollbarUtils.drawScrollBar(rect.getX()+1,rect.getX()+rect.getWidth()-2,
   * rect.getY()+rect.getHeight()-1, firstPart, lastPart, ScrollbarUtils.HORIZONTAL); }
   * 
   * 
   *  }
   */
  private int getVisibleTextWidth()
  {
    return getSize().getWidth() - 2;
  }
}