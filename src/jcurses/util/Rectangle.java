
package jcurses.util;

/**
 * This is a class to represent an screen rectangle. To implement this class was needed, because <code>java.awt.rectangle</code> works with double's, this is
 * by a text based terminal senseless.
 */
public class Rectangle
{
  int _height = 0;
  int _width  = 0;
  int _x      = 0;
  int _y      = 0;

  /**
   * The constructor
   * 
   * @param x the x coordinate of the top left corner
   * @param y the y coordinate of the top left corner
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public Rectangle(int x, int y, int width, int height)
  {
    _x = x;
    _y = y;
    _width = width;
    _height = height;
  }

  public Rectangle()
  {
  // need zero arg constructor
  }

  /**
   * The constructor, that defines only the size but no location
   * 
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public Rectangle(int width, int height)
  {
    _width = width;
    _height = height;
  }

  /**
   * Returns true if this rectangle is ABOVE the specified rectange
   * 
   * @param aRect The reference rectange for the comparison
   * 
   * @return true if this rectange is above, or false if it is not.
   */
  public boolean isAbove(Rectangle aRect)
  {
    return getBottom() < aRect.getTop();
  }

  /**
   * Returns true if the this rectangle is BELOW the specified rectange
   * 
   * @param aRect The reference rectange for the comparison
   * 
   * @return true if this rectange is below, or false if it is not.
   */
  public boolean isBelow(Rectangle aRect)
  {
    return getTop() > aRect.getBottom();
  }

  public int getBottom()
  {
    return ( getTop() + getHeight() ) - 1;
  }

  /**
   * Returns true or false indicating if the Rectangle is Empty. Empty is defined in this case as covering 0 units of area in the arbitrary coordinate system
   * used for specifying location and size. If the rectange occupies at least one unit of area, is is considered not empty, and false is returned.
   * 
   * @return <code>true</code> if the rectangle is empty in other case <code>false</code>
   */
  public boolean isEmpty()
  {
    return ( _width <= 0 ) || ( _height <= 0 );
  }

  /**
   * Sets the height of the rectangle
   * 
   * @param height the height of the rectangle to set
   */
  public void setHeight(int height)
  {
    _height = height;
  }

  /**
   * Gets the height of the rectangle
   * 
   * @return the height of the rectangle
   */
  public int getHeight()
  {
    return _height;
  }

  public int getLeft()
  {
    return getX();
  }

  /**
   * Returns true if the this rectangle is to the LEFT the specified rectange
   * 
   * @param aRect The reference rectange for the comparison
   * 
   * @return true if this rectange is to the left, or false if it is not.
   */
  public boolean isLeftOf(Rectangle aRect)
  {
    return getRight() < aRect.getLeft();
  }

  /**
   * Sets the location of the rectangle
   * 
   * @param x new x coordinate
   * @param y new y coordinate
   */
  public void setLocation(int x, int y)
  {
    setX(x);
    setY(y);
  }

  public void setSize(int aWide, int aHigh)
  {
    setWidth(aWide);
    setHeight(aHigh);
  }

  public void move(int aX, int aY)
  {
    setX(getX() + aX);
    setY(getY() + aY);
  }

  public int getRight()
  {
    return ( getLeft() + getWidth() ) - 1;
  }

  /**
   * Returns true if the this rectangle is to the RIGHT the specified rectange
   * 
   * @param aRect The reference rectange for the comparison
   * 
   * @return true if this rectange is to the right, or false if it is not.
   */
  public boolean isRightOf(Rectangle aRect)
  {
    return getLeft() > aRect.getRight();
  }

  public int getTop()
  {
    return getY();
  }

  /**
   * Sets the width of the rectangle
   * 
   * @param width the width of the rectangle to set
   */
  public void setWidth(int width)
  {
    _width = width;
  }

  /**
   * Gets the Width of the rectanagle
   * 
   * @return the width of the rectangle
   */
  public int getWidth()
  {
    return _width;
  }

  /**
   * Sets the x coordinate of the top left corner
   * 
   * @param x the x coordinate of the top left corner to set
   */
  public void setX(int x)
  {
    _x = x;
  }

  /**
   * Gets the x coordinate of the top left corner
   * 
   * @return the x coordinate of the top left corner
   */
  public int getX()
  {
    return _x;
  }

  /**
   * Sets the y coordinate of the top left corner
   * 
   * @param y the x coordinate of the top left corner to set
   */
  public void setY(int y)
  {
    _y = y;
  }

  /**
   * Gets the y coordinate of the top left corner
   * 
   * @return the y coordinate of the top left corner
   */
  public int getY()
  {
    return _y;
  }

  public Object clone()
  {
    return new Rectangle(_x, _y, _width, _height);
  }

  public void copyTo(Rectangle aTarget)
  {
    aTarget.setSize(getWidth(), getHeight());
    aTarget.setLocation(getX(), getY());

  }

  /**
   * The method veriifies, whether a rectangle lies within this rectangle
   * 
   * @param X x coordinate of the rectangle, whose containment is to verify
   * @param Y y coordinate of the rectangle, whose containment is to verify
   * @param W width of the rectangle, whose containment is to verify
   * @param H x height of the rectangle, whose containment is to verify
   * 
   * @return <code>true</code> if the parameter rectangle is withhin this rectangle in other case <code>false</code>
   */
  public boolean contains(int X, int Y, int W, int H)
  {
    int width = _width;
    int height = _height;

    if ( ( width <= 0 ) || ( height <= 0 ) || ( W <= 0 ) || ( H <= 0 ) )
      return false;

    int x = _x;
    int y = _y;
    return ( ( X >= x ) && ( Y >= y ) && ( ( X + W ) <= ( x + width ) ) && ( ( Y + H ) <= ( y + height ) ) );
  }

  /**
   * The method veriifies, whether a rectangle lies within this rectangle
   * 
   * @param rect the rectangle, whose containment is to be verified
   * 
   * @return <code>true</code> if the parameter rectangle is withhin this rectangle in other case <code>false</code>
   */
  public boolean contains(Rectangle rect)
  {
    return contains(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
  }

  /**
   * Returns the square of the distance between the center point of this Rectangle and the specified Rectangle
   * 
   * @param aRect the reference rectange from which distance is to be calculated
   * 
   * @return the squared distance in units as an integer
   */
  public int distanceFrom(Rectangle aRect)
  {
    int x = ( aRect.getX() + ( aRect.getWidth() / 2 ) ) - getX() + ( getWidth() / 2 );
    int y = ( aRect.getY() + ( aRect.getHeight() / 2 ) ) - getY() + ( getHeight() / 2 );
    return ( x * x ) + ( y * y );
  }

  /**
   * Returns the horizontally biased distance between the origin point of this Rectangle and the specified Rectangle.
   * 
   * @param aRect the reference rectange from which distance is to be calculated
   * 
   * @return the horizontal distance in integer units
   */
  public int horizontalDistanceFrom(Rectangle aRect)
  {
    return Math.abs(aRect.getX() - getX());
  }

  /**
   * Returns the horizontally biased distance between the center point of this Rectangle and the specified Rectangle.
   * 
   * @param aRect the reference rectange from which distance is to be calculated
   * 
   * @return horizontal distance in integer units
   */
  public int horizontalDistanceFromCenters(Rectangle aRect)
  {
    return Math.abs(( aRect.getX() + ( aRect.getWidth() / 2 ) ) - getX() + ( getWidth() / 2 ));
  }

  /**
   * The method veriifies, whether a point lies within this rectangle
   * 
   * @param x x coordinate of the point, whose containment is to verify
   * @param y y coordinate of the point, whose containment is to verify
   * 
   * @return <code>true</code> if the point is withhin this rectangle in other case <code>false</code>
   */
  public boolean inside(int x, int y)
  {
    return ( x >= _x ) && ( ( x - _x ) < _width ) && ( y >= _y ) && ( ( y - _y ) < _height );
  }

  /**
   * The method returns an intersection of the rectangle with an other rectangle, that is, the greatest rectangle, that is contained in both.
   * 
   * @param r rectangle to build intersection with this rectangle
   * 
   * @return the intersection rectangle
   */
  public Rectangle intersection(Rectangle r)
  {
    if ( isEmpty() )
      return (Rectangle)this.clone();
    else if ( r.isEmpty() )
      return (Rectangle)r.clone();
    else
    {
      int x1 = Math.max(_x, r.getX());
      int x2 = Math.min(_x + _width, r.getX() + r.getWidth());
      int y1 = Math.max(_y, r.getY());
      int y2 = Math.min(_y + _height, r.getY() + r.getHeight());

      if ( ( ( x2 - x1 ) < 0 ) || ( ( y2 - y1 ) < 0 ) )

        // Width or height is negative. No intersection.
        return new Rectangle(0, 0, 0, 0);

      return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }
  }

  /**
   * Changes the size of the rectangle
   * 
   * @param width new width
   * @param height new height
   */
  public void resize(int width, int height)
  {
    setWidth(width);
    setHeight(height);
  }

  /**
   * Returns the square of the shortest distance between the edge of this Rectangle and the edge of the specified Rectangle
   * 
   * @param aRect the reference rectange from which distance is to be calculated
   * 
   * @return the squared distance in units as an integer
   */
  public int shortestDistanceFrom(Rectangle aRect)
  {
    int x = 0;

    if ( getRight() <= aRect.getLeft() )
      x = aRect.getLeft() - getRight();
    else if ( getLeft() >= aRect.getRight() )
      x = aRect.getRight() - getLeft();
    else
      x = 0; // overlapping X

    int y = 0;

    if ( getTop() >= aRect.getBottom() )
      y = aRect.getBottom() - getTop();
    else if ( getBottom() <= aRect.getTop() )
      y = aRect.getTop() - getBottom();
    else
      y = 0; // overlapping Y

    return ( x * x ) + ( y * y );
  }

  public String toString()
  {
    return "[x=" + _x + ",y=" + _y + ",width=" + _width + ",height=" + _height + ",isEmpty=" + isEmpty() + "]";
  }

  /**
   * The method returns an union of the rectangle with an other rectangle, that is, the smallest rectangle, that contains both.
   * 
   * @param r rectangle to build union with this rectangle
   * 
   * @return the union rectangle
   */
  public Rectangle union(Rectangle r)
  {
    if ( isEmpty() )
      return (Rectangle)r.clone();
    else if ( r.isEmpty() )
      return (Rectangle)this.clone();
    else
    {
      int x1 = Math.min(_x, r.getX());
      int x2 = Math.max(_x + _width, r.getX() + r.getWidth());
      int y1 = Math.min(_y, r.getY());
      int y2 = Math.max(_y + _height, r.getY() + r.getHeight());
      return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }
  }

  /**
   * Returns the vertical distance between the origin point of this Rectangle and the specified Rectangle.
   * 
   * @param aRect the reference rectange from which distance is to be calculated
   * 
   * @return vertical distance in integer units
   */
  public int verticalDistanceFrom(Rectangle aRect)
  {
    return Math.abs(aRect.getY() - getY());
  }

  /**
   * Returns the vertical distance between the center point of this Rectangle and the specified Rectangle.
   * 
   * @param aRect the reference rectange from which distance is to be calculated
   * 
   * @return vertical distance in integer units
   */
  public int verticalDistanceFromCenters(Rectangle aRect)
  {
    return Math.abs(( aRect.getY() + ( aRect.getHeight() / 2 ) ) - getY() + ( getHeight() / 2 ));
  }
}