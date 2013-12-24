/**
 * Die Klasse ist die Root-Klasse aller Container-Klassen. Ihre Aufgabe besteht darin eine Reihe von Widgets zu verwalten, die Eingabe an Sie weitezuleiten etc
 */

package jcurses.widgets;

import java.util.Hashtable;
import java.util.Vector;

import jcurses.system.Toolkit;
import jcurses.util.Rectangle;

/**
 * This class is a superclass for widget containers, that is, for widgets, that can contain other widgets
 */
public abstract class WidgetContainer extends Widget
{
  private Hashtable     _constraints   = new Hashtable();

  /** LayoutManager */
  private LayoutManager _layoutManager = null;
  private Vector        _widgets       = new Vector();

  /**
   * The method sets container's layout manager
   * 
   * @param layoutManager new layout manager
   */
  public void setLayoutManager(LayoutManager layoutManager)
  {
    if ( _layoutManager != null )
      _layoutManager.unbindFromContainer();

    _layoutManager = layoutManager;
    _layoutManager.bindToContainer(this);
  }

  /**
   * DOCUMENT ME!
   * 
   * @return container's layout manager
   */
  public LayoutManager getLayoutManager()
  {
    if ( _layoutManager == null )
      setLayoutManager(new DefaultLayoutManager());

    return _layoutManager;
  }

  /**
   * The method paits the container self, that is all except childrens. Is called by <code>doPaint</code>. Must be overrided by derived classes.
   */
  protected abstract void paintSelf();

  /**
   * This method returns a rectangle to be used as painting surface for container's children. By default, the entire container's surface is used. THe object
   * returned is a new instance and may be modified.
   * 
   * @return Rectangle defineing Painting surface for container's children
   */
  protected Rectangle getClientArea()
  {
    return getRectangle();
  }

  /**
   * @deprecated Use getClientArea() instead
   */
  protected Rectangle getChildsRectangle()
  {
    return getClientArea();
  }

  protected Rectangle getClippingRect(Rectangle aClip, Widget aWidget)
  {
    return aClip.intersection(aWidget.getRectangle());
  }

  /**
   * The method returns a list of input widgets within the container.
   * 
   * @return input widgets within container
   */
  protected Vector getListOfFocusables()
  {
    Vector result = new Vector();

    for ( int i = 0; i < _widgets.size(); i++ )
    {
      Widget widget = (Widget)_widgets.elementAt(i);

      if ( widget.isFocusable() && widget.getVisible() )
        result.add(widget);
      else if ( widget instanceof WidgetContainer )
        result.addAll(( (WidgetContainer)widget ).getListOfFocusables());
    }

    return result;
  }

  /**
   * The method returns a list of widgets, that can handle shortcuts, within the container.
   * 
   * @return widgets within container, that can handle shortcuts
   */
  protected Vector getListOfWidgetsWithShortCuts()
  {
    Vector result = new Vector();

    for ( int i = 0; i < _widgets.size(); i++ )
    {
      Widget widget = (Widget)_widgets.elementAt(i);

      if ( widget.getShortCutsList() != null )
        result.add(widget);
      else if ( widget instanceof WidgetContainer )
        result.addAll(( (WidgetContainer)widget ).getListOfWidgetsWithShortCuts());
    }

    return result;
  }

  /**
   * The method adds a widget to the container, declaring widget's lyouting constraints. This method is called by layout manager and cann't be called by
   * developer. To add a widget to the container, a developer must use methods of container's layout manager.
   * 
   * @param widget widget to add
   * @param constraint layouting constraints
   */
  protected void addWidget(Widget widget, Object constraint)
  {
    _widgets.add(widget);
    _constraints.put(widget, constraint);
    widget.setParent(this);
  }

  protected void doPaint()
  {
    paintSelf();
    Rectangle mClient = getClientArea();
    Toolkit.setClipRectangle(mClient);
    for ( int i = 0; i < _widgets.size(); i++ )
      ( (Widget)_widgets.elementAt(i) ).paint();
    Toolkit.unsetClipRectangle();
  }

  protected void doRepaint()
  {
    repaintSelf();
    Toolkit.setClipRectangle(getClientArea());
    for ( int i = 0; i < _widgets.size(); i++ )
      ( (Widget)_widgets.elementAt(i) ).repaint();
    Toolkit.unsetClipRectangle();
  }

  /**
   * The method repaints the container self, that is all, except its children. <br>
   * Is called by <code>doRepaint</code>. The default inmplementation calls paintSelf(). <br>
   * Subclasses should override this method if repaintSelf() can be more efficient that paintSelf().
   */
  protected void repaintSelf()
  {
    paintSelf();
  }

  /**
   * The method layouts all childrens bei the widget, using containers layout manager. The method is called by framework, before it paints a window-
   */
  protected void pack()
  {
    for ( int i = 0; i < _widgets.size(); i++ )
    {
      Widget widget = (Widget)_widgets.elementAt(i);
      packChild(widget, _constraints.get(widget));

      if ( widget instanceof WidgetContainer )
        ( (WidgetContainer)widget ).pack();
    }
  }

  protected void packChild(Widget widget, Object constraint)
  {
    getLayoutManager().layout(widget, constraint);
  }

  /**
   * The method removes a widget from the container. This method is called by layout manager and cann't be called by developer. To remove a widget from the
   * container, a developer must use methods of container's layout manager.
   * 
   * @param widget widget to remove
   */
  protected void removeWidget(Widget widget)
  {
    if ( widget.hasFocus() )
      widget.getWindow().changeFocus();

    _widgets.remove(widget);
    _constraints.remove(widget);
    widget.setParent(null);
  }
}