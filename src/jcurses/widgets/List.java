
package jcurses.widgets;

import java.util.Vector;

import jcurses.event.ItemEvent;
import jcurses.event.ItemListener;
import jcurses.event.ItemListenerManager;
import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;
import jcurses.themes.Theme;
import jcurses.util.Paging;
import jcurses.util.Rectangle;

/**
 * This class implements a list widget to select and 'invoke' one ore more items. Listeners can be registered to track selecting deselecting and 'invoking' of
 * items.
 */
public class List extends Widget implements IScrollable
{
  private static InputChar    __changeStatusChar = new InputChar(' ');
  private static InputChar    __callItemChar     = new InputChar('\n');
  private ItemListenerManager _listenerManager   = new ItemListenerManager();
  private ScrollbarPainter    _scrollbars        = null;
  private String              _title             = null;
  private Vector              _items             = new Vector();
  private Vector              _selected          = new Vector();
  private boolean             _multiple          = false;
  private boolean             _selectable        = true;
  private int                 _startIndex        = 0;
  private int                 _startPos          = 0;
  private int                 _trackedIndex      = 0;
  private int                 _visibleSize       = - 1;

  /**
   * The constructor
   * 
   * @param visibleSize number of visible items. If the entire number of items is more, the widget scrolls items 'by a window'. If -1 is given, than the visible
   *          size is defined dependent of the layout size, that is, the widget has no preferred y size.
   * @param multiple true, if more as one items can be selected a time, false, if only one item can be selected at a time, in this case selecting of an item
   *          causes deselecting of the previous selected item.
   */
  public List(int visibleSize, boolean multiple)
  {
    _visibleSize = visibleSize;
    _multiple = multiple;
    _scrollbars = new ScrollbarPainter(this);
  }

  /**
   * The constructor.
   * 
   * @param visibleSize number of visible items. If the entire number of items is more, the widget scrolls items 'by a window'. If -1 is given, than the visible
   *          size is defined dependent of the layout size, that is, the widget has no preferred y size.
   */
  public List(int visibleSize)
  {
    this(visibleSize, false);
  }

  /**
   * The constructor
   */
  public List()
  {
    this(- 1, false);
  }

  public Rectangle getBorderRectangle()
  {
    Rectangle rect = (Rectangle)getSize().clone();
    rect.setLocation(getAbsoluteX(), getAbsoluteY());
    return rect;
  }

  public float getHorizontalScrollbarLength()
  {
    if ( ( _items.size() == 0 ) || ( getMaxLength() <= getLineWidth() ) )

      // Keine Items - kein scrollbar
      return 0;

    if ( getMaxLength() > getLineWidth() )
      return ( (float)getLineWidth() ) / ( (float)getMaxLength() );

    return 0;
  }

  public float getHorizontalScrollbarOffset()
  {
    if ( ( _items.size() == 0 ) || ( getMaxLength() <= getLineWidth() ) )

      // Keine Items - kein scrollbar
      return 0;

    if ( getMaxLength() > getLineWidth() )
      return ( (float)_startPos ) / ( (float)getMaxLength() );

    return 0;
  }

  /**
   * Returns the String value of the item at the specified position
   * 
   * @param index specified position
   * 
   * @return item at the specified position
   */
  public String getItem(int index)
  {
    return (String)_items.elementAt(index);
  }

  public void setItem(int index, String aItem)
  {
    if ( ! aItem.equals(_items.get(index)) )
    {
      _items.set(index, aItem);
      reset();
      if ( isVisible() )
        redrawItem(index);
    }
  }

  /**
   * Returns the items in the list as a Vector
   * 
   * @return items contained in the list
   */
  public Vector getItems()
  {
    return (Vector)_items.clone();
  }

  /**
   * Returns the number of items in the list
   * 
   * @return number of items
   */
  public int getItemsCount()
  {
    return _items.size();
  }

  public CharColor getScrollbarColors()
  {
    return getColors(Theme.COLOR_WIDGET_SCROLLBAR);
  }

  /**
   * Sets, whether items can be selected at all
   * 
   * @param value true, if items can be selected, false otherwise ( in this case items can only be 'invoked')
   */
  public void setSelectable(boolean value)
  {
    _selectable = value;
  }

  /**
   * Sets, whether items can be selected at all
   * 
   * @return true, if items can be selected, false otherwise ( in this case items can only be 'invoked')
   */
  public boolean getSelectable()
  {
    return _selectable;
  }

  /**
   * DOCUMENT ME!
   * 
   * @param pos the position to test, whether selected
   * 
   * @return true, if the item at the specified position is selected, false otherwise
   */
  public boolean isSelected(int pos)
  {
    return ( (Boolean)_selected.elementAt(pos) ).booleanValue();
  }

  /**
   * DOCUMENT ME!
   * 
   * @return index of the selected item, if only one item is selected, <code>null</code> otherwise.
   */
  public int getSelectedIndex()
  {
    int[] results = getSelectedIndexes();
    int result = - 1;

    if ( results.length == 1 )
      result = results[0];

    return result;
  }

  /**
   * DOCUMENT ME!
   * 
   * @return indexes of all selected items, contained in the list
   */
  public int[] getSelectedIndexes()
  {
    int size = 0;

    for ( int i = 0; i < _items.size(); i++ )
    {
      boolean selected = isSelected(i);

      if ( selected )
        size++;
    }

    int[] result = new int[size];
    int currentIndex = 0;

    for ( int i = 0; i < _items.size(); i++ )
    {
      boolean selected = isSelected(i);

      if ( selected )
      {
        result[currentIndex] = i;
        currentIndex++;
      }
    }

    return result;
  }

  /**
   * Gets the String value of the currently selected item in the List. If multiple items are selected or no items are selected, null is returned.
   * 
   * @return the selected item, if exactly one item is selected, <code>null</code> otherwise.
   */
  public String getSelectedItem()
  {
    Vector results = getSelectedItems();
    String result = null;

    if ( results.size() == 1 )
      result = (String)results.elementAt(0);

    return result;
  }

  /**
   * Sets colors used painting selected items.
   * 
   * @param aColors colors used painting selected items
   * @deprecated Use setSelectedColors()
   */
  public void setSelectedItemColors(CharColor aColors)
  {
    setSelectedColors(aColors);
  }

  /**
   * @return colors used painting selected items.
   * @deprecated Use getSelectedColors()
   */
  public CharColor getSelectedItemColors()
  {
    return getSelectedColors();
  }

  /**
   * Gets a Vector of all the selected items for the list. If no items are selected, an empty Vector is returned.
   * 
   * @return all selected items, contained in the list
   */
  public Vector getSelectedItems()
  {
    Vector result = new Vector();

    for ( int i = 0; i < _items.size(); i++ )
    {
      boolean selected = isSelected(i);

      if ( selected )
        result.add(_items.elementAt(i));
    }

    return result;
  }

  /**
   * Sets the title of the list.
   * 
   * @param title the title of the list
   */
  public void setTitle(String title)
  {
    _title = title;
  }

  /**
   * Gets the title of the list.
   * 
   * @return list's title
   */
  public String getTitle()
  {
    return _title;
  }

  /**
   * Gets the index of the currently tracked item. This is where the 'cursor' line is when the user is navigating the list.
   * 
   * @return the index of the current tracked item.
   */
  public int getTrackedIndex()
  {
    return _trackedIndex;
  }

  /**
   * Sets the currently tracked item. This is where the 'cursor' line is when the user is navigating the list.
   * 
   * @param pos the index of the current tracked item.
   * 
   * @throws IllegalArgumentException if pos is out of range.
   */
  public void setTrackedItem(int pos)
  {
    if ( ( pos < 0 ) || ( pos >= getItemsCount() ) )
      throw new IllegalArgumentException("pos must be in the range: 0," + ( getItemsCount() - 1 ));

    int backupStartIndex = _startIndex;
    int backupTrackedIndex = _trackedIndex;

    if ( setTrack(pos) && isVisible() )
      redraw(( backupStartIndex == _startIndex ), _trackedIndex, backupTrackedIndex);
  }

  /**
   * This is currently the same as a call to getTrackedIndex(), however that is inconsistent with the rest of the API. It should return instead the String value
   * of the item. <br>
   * <br>
   * Currently it is simply being marked deprecated to avoiud causing people a lot of grief, however in a future release, it will be changed to return the
   * correct String value. In the meantime, getTrackedItemStr() has been introduced to provide the function of returning the String value.
   * 
   * @return the index of the current tracked item.
   * 
   * @deprecated
   */
  public int getTrackedItem()
  {
    return _trackedIndex;
  }

  /**
   * Gets the String value of the currently tracked item. <br>
   * <br>
   * This method is an iterim solution to avoid breaking existing code since really getTrackedItem() should serve this purpose.
   * 
   * @return the index of the current tracked item.
   */
  public String getTrackedItemStr()
  {
    return getItem(_trackedIndex);
  }

  public float getVerticalScrollbarLength()
  {
    if ( _items.size() == 0 )

      // Keine Items - kein scrollbar
      return 0;

    if ( _items.size() > getVisibleSize() )
      return ( (float)getVisibleSize() ) / ( (float)_items.size() );

    return 0;
  }

  public float getVerticalScrollbarOffset()
  {
    if ( _items.size() == 0 )

      // Keine Items - kein scrollbar
      return 0;

    if ( _items.size() > getVisibleSize() )
      return ( (float)_startIndex ) / ( (float)_items.size() );

    return 0;
  }

  /**
   * Adds an item to the list at the specified position
   * 
   * @param pos the position to insert the item
   * @param item item to add
   */
  public void add(int pos, String item)
  {
    _items.add(pos, item);
    _selected.add(pos, new Boolean(false));
    reset();
    if ( isVisible(pos) )
      refresh();
  }

  /**
   * Adds an item to the end of the list
   * 
   * @param item item to add
   */
  public void add(String item)
  {
    add(_items.size(), item);
  }

  /**
   * Adds a listener to the widget
   * 
   * @param listener listener to add
   */
  public void addListener(ItemListener listener)
  {
    _listenerManager.addListener(listener);
  }

  /**
   * Removes all items from the list
   */
  public void clear()
  {
    _items.clear();
    _selected.clear();
    reset();
    refresh();
  }

  /**
   * Deselects an item at the specified position
   * 
   * @param index position
   */
  public void deselect(int index)
  {
    select(index, false);

    if ( isVisible() )
      redrawItemBySelecting(index);

    dispatchEvent(index, false);
  }

  //Scrollbars
  public boolean hasHorizontalScrollbar()
  {
    return true;
  }

  public boolean hasVerticalScrollbar()
  {
    return true;
  }

  /**
   * Removes an item from the list at the specified position
   * 
   * @param pos position
   */
  public void remove(int pos)
  {
    _items.remove(pos);
    _selected.remove(pos);
    reset();
    if ( isVisible() )
      refresh();
  }

  /**
   * Removes the first occurence of <code>item</code> from the list.
   * 
   * @param item string, whose first occurence is to remove from the list.
   */
  public void remove(String item)
  {
    int index = _items.indexOf(item);

    if ( index != - 1 )
    {
      _items.remove(index);
      _selected.remove(index);
      if ( isVisible() )
        refresh();
    }
  }

  /**
   * Removes a listener from the widget
   * 
   * @param listener listener to remove
   */
  public void removeListener(ItemListener listener)
  {
    _listenerManager.removeListener(listener);
  }

  /**
   * Selects an item at the specified position
   * 
   * @param index position
   */
  public void select(int index)
  {
    select(index, true);

    if ( isVisible() )
      redrawItemBySelecting(index);

    dispatchEvent(index, true);
  }

  protected InputChar getChangeStatusChar()
  {
    return __changeStatusChar;
  }

  protected boolean isFocusable()
  {
    return true;
  }

  /**
   * The method returns the display representation of the string und is called by the widget before it paints an item. The idea is to make it possible in
   * derived classes to paint other strings as managed in the widget. Here returns always the same string as <code>item</code>
   * 
   * @param item string to give display representation
   * 
   * @return display representation of the string
   */
  protected String getItemRepresentation(String item)
  {
    return item;
  }

  protected Rectangle getPreferredSize()
  {
    return new Rectangle(- 1, ( _visibleSize < 0 ) ? ( - 1 ) : ( _visibleSize + 2 ));
  }

  /**
   * This method tests, if the item at the specified position can be selected and invoked at all. The sense is, to give derived classes the posssibility to
   * implement 'separators'. Here returns always <code>true</code>.
   * 
   * @param i the position to test
   * 
   * @return true if the item at the specified position can be selected and invoked, false otherwise
   */
  protected boolean isSelectable(int i)
  {
    return true;
  }

  protected boolean setTrack(int pos)
  {
    return findNextSelectableItem(pos, 1, false, 0);
  }

  protected void doPaint()
  {
    Rectangle rect = (Rectangle)getSize().clone();
    rect.setLocation(getAbsoluteX(), getAbsoluteY());
    Toolkit.drawBorder(rect, getBorderColors());
    drawTitle();
    _scrollbars.paint();
    drawRectangle();
    drawItems();
  }

  protected void doRepaint()
  {
    doPaint();
  }

  protected void focus()
  {
    redrawSelectedItems();
  }

  protected boolean handleInput(InputChar ch)
  {
    int backupStartIndex = _startIndex;
    int backupTrackedIndex = _trackedIndex;

    // Keine Items - keine Eingabe
    if ( _items.size() == 0 )
      return false;

    if ( ch.getCode() == InputChar.KEY_RIGHT )
    {
      if ( incrementStartPos() )
        refresh();

      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_LEFT )
    {
      if ( decrementStartPos() )
        refresh();

      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_UP )
    {
      if ( decrementTrack() )
        redraw(( backupStartIndex == _startIndex ), _trackedIndex, backupTrackedIndex);

      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_DOWN )
    {
      if ( incrementTrack() )
        redraw(( backupStartIndex == _startIndex ), _trackedIndex, backupTrackedIndex);

      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_HOME )
    {
      if ( setTrack(0) )
        redraw(( backupStartIndex == _startIndex ), _trackedIndex, backupTrackedIndex);

      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_END )
    {
      if ( setTrack(getItemsCount() - 1) )
        redraw(( backupStartIndex == _startIndex ), _trackedIndex, backupTrackedIndex);

      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_NPAGE )
    {
      if ( incrementPage() )
        redraw(( backupStartIndex == _startIndex ), _trackedIndex, backupTrackedIndex);

      return true;
    }
    else if ( ch.getCode() == InputChar.KEY_PPAGE )
    {
      if ( decrementPage() )
        redraw(( backupStartIndex == _startIndex ), _trackedIndex, backupTrackedIndex);

      return true;
    }
    else if ( ch.equals(__changeStatusChar) && getSelectable() )
    {
      if ( isSelected(_trackedIndex) )
        deselect(_trackedIndex);
      else
        select(_trackedIndex);

      return true;
    }
    else if ( ch.equals(__callItemChar) )
    {
      callItem(_trackedIndex);
      return true;
    }

    return false;
  }

  protected void unfocus()
  {
    redrawSelectedItems();
  }

  int getCurrentPageOffset()
  {
    return getPaging().getPageOffset(_trackedIndex);
  }

  int getPageEndIndex(int pageNumber)
  {
    return getPaging().getPageEndIndex(pageNumber);
  }

  int getPageStartIndex(int pageNumber)
  {
    return getPaging().getPageStartIndex(pageNumber);
  }

  private int getCurrentPageNumber()
  {
    return getPageNumber(_trackedIndex);
  }

  private int getMaxLength()
  {
    int result = 0;

    for ( int i = 0; i < _items.size(); i++ )
    {
      String item = (String)_items.elementAt(i);

      if ( item.length() > result )
        result = item.length();
    }

    return result;
  }

  private int getMaxStartPos()
  {
    Rectangle rect = (Rectangle)getSize().clone();
    int width = rect.getWidth() - 2;
    int result = getMaxLength() - width;
    result = ( result < 0 ) ? 0 : result;

    return result;
  }

  /*
   * private int getMaximumStartIndex() { return (_items.size() < getVisibleSize()) ? 0 : (_items.size() - getVisibleSize()); }
   */
  private int getPageNumber(int index)
  {
    return getPaging().getPageNumber(index);
  }

  private int getPageSize()
  {
    return getPaging().getPageSize();
  }

  private Paging getPaging()
  {
    return new Paging(getVisibleSize(), getItemsCount());
  }

  private boolean isVisible(int index)
  {
    if ( _items.size() == 0 )
      return false;

    return ( ( index >= _startIndex ) && ( index < ( _startIndex + getVisibleSize() ) ) );
  }

  private int getVisibleSize()
  {
    return getSize().getHeight() - 2;
  }

  private int getLineWidth()
  {
    return getWidth() - 2;
  }

  private void callItem(int index)
  {
    ItemEvent event = new ItemEvent(this, index, _items.elementAt(index), ItemEvent.CALLED);
    _listenerManager.handleEvent(event);
  }

  private boolean decrementPage()
  {
    int nextPos = 0;

    if ( getCurrentPageNumber() > 0 )
      nextPos = getPaging().getIndexByPageOffset(getCurrentPageNumber() - 1, getCurrentPageOffset());
    else
      nextPos = 0;

    return findNextSelectableItem(nextPos, - 1, false, 0);
  }

  private boolean decrementStartPos()
  {
    if ( _startPos > 0 )
    {
      _startPos--;
      return true;
    }

    return false;
  }

  private boolean decrementTrack()
  {
    boolean found = false;

    if ( _trackedIndex > 0 )
      found = findNextSelectableItem(_trackedIndex - 1, - 1, true, - 1);

    return found;
  }

  private void dispatchEvent(int index, boolean value)
  {
    ItemEvent event = new ItemEvent(this, index, _items.elementAt(index), value ? ItemEvent.SELECTED : ItemEvent.DESELECTED);
    _listenerManager.handleEvent(event);
  }

  private void drawFirstRowSelected()
  {
    if ( hasFocus() )
      Toolkit.drawRectangle(getAbsoluteX() + 1, getAbsoluteY() + 1, getSize().getWidth() - 2, 1, getSelectedColors());
  }

  private void drawItems()
  {
    Rectangle rect = (Rectangle)getSize().clone();
    rect.setLocation(getAbsoluteX(), getAbsoluteY());

    for ( int i = 0; i < getVisibleSize(); i++ )
    {
      int index = _startIndex + i;

      if ( index < _items.size() )
        drawItem(index, rect);
      else
        eraseItem(rect, i);
    }

    if ( _items.size() == 0 )
      drawFirstRowSelected();
  }

  private void eraseItem(Rectangle aRect, int aIndex)
  {
    Toolkit.drawRectangle(new Rectangle(aRect.getX() + 1, aRect.getY() + aIndex + 1, aRect.getWidth() - 2, 1), getColors());
  }

  private void drawRectangle()
  {
    Rectangle rect = (Rectangle)getSize().clone();
    rect.setWidth(rect.getWidth() - 2);
    rect.setHeight(rect.getHeight() - 2);
    rect.setLocation(getAbsoluteX() + 1, getAbsoluteY() + 1);
    Toolkit.drawRectangle(rect, getColors());
  }

  private void drawTitle()
  {
    if ( _title != null )
    {
      int mLength = Math.min(getSize().getWidth() - 2, _title.length());
      int mX = getAbsoluteX() + ( ( getSize().getWidth() - mLength ) / 2 );
      Toolkit.printString(_title, mX, getAbsoluteY(), mLength, 1, getTitleColors());
    }
  }

  private boolean findNextSelectableItem(int pos, int searchDirection, boolean onlySearchDirection, int stepping)
  {
    if ( getItemsCount() == 0 )
      return false;

    // we use a single virtual page if not displayed
    int page = 0;
    int start = 0;
    int end = _items.size();

    if ( isVisible() )
    {
      page = getPageNumber(pos);
      start = getPageStartIndex(page);
      end = getPageEndIndex(page);
    }

    boolean found = false;

    if ( isSelectable(pos) )
      found = true;
    else
    {
      int searchPos = pos;

      while ( ( searchPos <= end ) && ( searchPos >= start ) && ( ! found ) )
      {
        searchPos += searchDirection;
        found = isSelectable(searchPos);
      }

      if ( ! found && ! onlySearchDirection )
      {
        searchPos = pos;

        while ( ( searchPos <= end ) && ( searchPos >= start ) && ( ! found ) )
        {
          searchPos -= searchDirection;
          found = isSelectable(searchPos);
        }
      }

      pos = searchPos;
    }

    if ( found )
    {
      if ( stepping == 0 )
        _startIndex = start;
      else if ( stepping == - 1 )
      {
        if ( ! isVisible(pos) )
          _startIndex = pos;
      }
      else
      {
        if ( ! isVisible(pos) )
          _startIndex = Math.max(0, pos - getVisibleSize() + 1);
      }

      _trackedIndex = pos;
    }

    return found;
  }

  private boolean incrementPage()
  {
    int nextPos = 0;

    if ( getCurrentPageNumber() < ( getPageSize() - 1 ) )
      nextPos = getPaging().getIndexByPageOffset(getCurrentPageNumber() + 1, getCurrentPageOffset());
    else
      nextPos = getItemsCount() - 1;

    //System.err.println("Next Page ["+nextPos+"]");

    return findNextSelectableItem(nextPos, 1, false, 0);
  }

  private boolean incrementStartPos()
  {
    if ( _startPos < getMaxStartPos() )
    {
      _startPos++;
      return true;
    }

    return false;
  }

  private boolean incrementTrack()
  {
    boolean found = false;

    if ( _trackedIndex < ( getItemsCount() - 1 ) )
      found = findNextSelectableItem(_trackedIndex + 1, 1, true, 1);

    return found;
  }

  private void drawItem(int index, Rectangle rect)
  {
    // is it visible?
    if ( index < _startIndex || index > _startIndex + getVisibleSize() )
      return;

    int x = rect.getX() + 1;
    int y = ( rect.getY() + 1 + index ) - _startIndex;
    int width = rect.getWidth() - 2;
    boolean toSelect = ( ( ( index == _trackedIndex ) && hasFocus() ) || ( isSelected(index) ) );

    CharColor colors = toSelect ? getSelectedColors() : getColors();

    String item = getItemRepresentation((String)_items.elementAt(index));

    if ( item.length() < ( _startPos + 1 ) )
      item = "";
    else
    {
      if ( _startPos != 0 )
        item = item.substring(_startPos, item.length());
    }

    if ( ( item.length() < width ) && ( toSelect ) )
    {
      StringBuffer itemBuffer = new StringBuffer();
      itemBuffer.append(item);

      for ( int i = 0; i < ( width - item.length() ); i++ )
        itemBuffer.append(' ');

      item = itemBuffer.toString();
    }

    Toolkit.printString(item, x, y, width, 1, colors);
    if ( item.length() < width )
      Toolkit.drawRectangle(x + item.length(), y, width - item.length(), 1, colors);
  }

  private void redraw(boolean flag, int trackedIndex, int backupTrackedIndex)
  {
    if ( flag )
    {
      //System.err.println("Redraw ["+trackedIndex+" / "+backupTrackedIndex+"]");
      redrawItem(trackedIndex, getRectangle());
      redrawItem(backupTrackedIndex, getRectangle());
    }
    else
    {
      //System.err.println("Repaint");
      //paint();
      drawItems();
    }
  }

  private void redrawItem(int index)
  {
    redrawItem(index, getRectangle());
  }

  private void redrawItem(int index, Rectangle rect)
  {
    drawItem(index, rect);
    //int x = rect.getX() + 1;
    //int y = ( rect.getY() + 1 + index ) - _startIndex;
    //int width = rect.getWidth() - 2;
    //Rectangle itemRect = new Rectangle(x, y, width, 1);
    //printItem(index, itemRect);
    //boolean toSelect = ( ( ( index == _trackedIndex ) && hasFocus() ) || ( isSelected(index) ) );
    //CharColor colors = toSelect ? getSelectedColors() : getColors();
    //Toolkit.changeColors(itemRect, colors);
  }

  private void redrawItemBySelecting(int index)
  {
    if ( ! ( ( index == _trackedIndex ) && hasFocus() ) )
      redrawItem(index, getRectangle());
  }

  private void redrawSelectedItems()
  {
    for ( int i = 0; i < getVisibleSize(); i++ )
    {
      int index = _startIndex + i;

      if ( index < _items.size() )
      {
        boolean toSelect = ( ( index == _trackedIndex ) || ( isSelected(index) ) );

        if ( toSelect )
          redrawItem(index, getRectangle());
      }
    }
  }

  private void refresh()
  {
    _scrollbars.refresh();
    drawRectangle();
    drawItems();
  }

  private void reset()
  {
    _startIndex = 0;
    _trackedIndex = 0;
    _startPos = 0;
  }

  private void select(int index, boolean value)
  {
    if ( ! ( isSelected(index) == value ) )
    {
      int selected = getSelectedIndex();
      _selected.set(index, new Boolean(value));

      if ( ( ! _multiple ) && value )
      {
        if ( selected != - 1 )
          deselect(selected);
      }
    }
  }
}
