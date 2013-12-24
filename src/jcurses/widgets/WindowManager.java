/* -*- tab-width: 8; indent-tabs-mode: t; c-basic-offset: 8 -*- */

package jcurses.widgets;

import java.util.Vector;

import jcurses.event.WindowManagerBlockingCondition;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;
import jcurses.themes.DefaultThemeImpl;
import jcurses.themes.Theme;
import jcurses.util.Rectangle;

/**
 * This class is a jcurses-internal class, whose task is to manage jcurses text based windows. It shouldn't be used writing applications.
 */
class WindowManager
{
  private static Vector                   __windowsStack     = null;
  private static Window                   __topVisibleWindow = null;
  private static WindowManagerInputThread _inthread          = new WindowManagerInputThread();
  private static Theme                    _theme             = new DefaultThemeImpl();

  public static boolean isInputThread()
  {
    return _inthread.isCurrentThread();
  }

  public static void blockInputThread(WindowManagerBlockingCondition cond)
  {
    _inthread.block(cond);
  }

  public static synchronized void closeAll()
  {
    while ( ! __windowsStack.isEmpty() )
      removeWindow((Window)__windowsStack.get(__windowsStack.size() - 1));
  }

  private static synchronized void init()
  {
    __windowsStack = new Vector();
    Toolkit.init();
    Toolkit.clearScreen(_theme.getColor(Theme.COLOR_DEFAULT));
    startInputThread();
  }

  protected static void establishTopWindow()
  {
    int mIdx;
    for ( mIdx = __windowsStack.size() - 1; mIdx >= 0; mIdx-- )
      if ( ( (Window)__windowsStack.elementAt(mIdx) ).isVisible() )
        break;

    Window mWindow = ( mIdx == - 1 ) ? null : (Window)__windowsStack.elementAt(mIdx);

    if ( mWindow != __topVisibleWindow )
    {
      if ( __topVisibleWindow != null )
        __topVisibleWindow.deactivate();
      __topVisibleWindow = mWindow;
      if ( __topVisibleWindow != null )
        __topVisibleWindow.activate();
    }
  }

  protected static void createWindow(Window aWindow)
  {
    if ( __windowsStack == null )
      init();

    if ( ! __windowsStack.contains(aWindow) )
      __windowsStack.add(aWindow);
  }

  protected static synchronized void handleInput(InputChar input)
  {
    //Toolkit.startPainting();

    if ( __topVisibleWindow != null )
    {
      try
      {
        __topVisibleWindow.handleInput(input);
      }
      catch (Throwable e)
      {
        //Toolkit.shutdown();
        e.printStackTrace();
        //System.exit(1);
      }
    }

    //if ( _inthread.isRunning() )
    //  Toolkit.endPainting();
  }

  protected static void doWindowVisibilityChange(Window aWindow)
  {
    checkWindow(aWindow);
    establishTopWindow();
    repaintWindows(aWindow.getRectangle());
  }

  protected static void removeWindow(Window aWindow)
  {
    checkWindow(aWindow);
    __windowsStack.remove(aWindow);
    aWindow.closed();
    if ( __windowsStack.isEmpty() )
      shutdown();
  }

  private static void checkWindow(Window aWindow)
  {
    if ( ! __windowsStack.contains(aWindow) )
      throw new IllegalArgumentException("Window [" + aWindow
          + "] not found - Can not use a window before call to createWindow() or after call to removeWindow()");
  }

  protected static void moveToTop(Window aWindow)
  {
    checkWindow(aWindow);

    __windowsStack.remove(aWindow);
    __windowsStack.add(aWindow);

    if ( aWindow.isVisible() )
    {
      establishTopWindow();
      repaintWindows(aWindow.getRectangle());
    }
  }

  /**
   * Method to be used by external threads wishing to perform safe calls to jcurses widgets. Access to this method is provided from
   * WidgetUtilities.invokeAndWait().
   * 
   * @param r a <code>Runnable</code> containing the code to be executed in a thread-safe manner.
   */
  static synchronized void invokeAndWait(Runnable r)
  {
    r.run();
  }

  private static synchronized void deactivateInputThread()
  {
    _inthread.deactivate();
  }

  private static void repaintWindows(Rectangle aClip)
  {
    Rectangle mClip = ( aClip != null ) ? aClip : Toolkit.getScreen();
    Toolkit.startPainting();
    Toolkit.drawRectangle(mClip.getX(), mClip.getY(), mClip.getWidth(), mClip.getHeight(), _theme.getColor(Theme.COLOR_DEFAULT));

    for ( int i = 0; i < __windowsStack.size(); i++ )
    {
      Window mWindow = (Window)__windowsStack.elementAt(i);
      if ( mWindow.isVisible() )
      {
        Rectangle mWinClip = mClip.intersection(mWindow.getClipRectangle());

        if ( ! mWinClip.isEmpty() )
        {
          Toolkit.setClipRectangle(mWinClip);
          //mWindow.repaint();
          mWindow.paint();
          Toolkit.unsetClipRectangle();
        }
      }
    }

    Toolkit.endPainting();
  }

  private static synchronized void shutdown()
  {
    deactivateInputThread();
    stopInputThread();
    __windowsStack = null;
    Toolkit.clearScreen(_theme.getColor(Theme.COLOR_DEFAULT));
    Toolkit.shutdown();
  }

  private static synchronized void startInputThread()
  {
    _inthread.start();
  }

  private static synchronized void stopInputThread()
  {
    _inthread.end();
  }

  public static Theme getTheme()
  {
    return _theme;
  }

  public static void setTheme(Theme aTheme)
  {
    _theme = aTheme;
  }
}

class WindowManagerInputThread implements Runnable // extends Thread
{
  private Thread  thd;
  private boolean _read = true;
  private boolean _run  = true;

  public void run()
  {
    while ( isRunning() )
    {
      if ( isReading() )
      {
        InputChar inputChar = Toolkit.readCharacter();

        if ( inputChar != null )
          WindowManager.handleInput(inputChar);
      }
    }
  }

  public synchronized void start()
  {
    if ( thd == null )
    {
      thd = new Thread(this);
      thd.start();
    }
  }

  protected boolean isCurrentThread()
  {
    return ( Thread.currentThread() == thd );
  }

  protected synchronized boolean isReading()
  {
    return _read;
  }

  protected synchronized boolean isRunning()
  {
    return _run;
  }

  protected void block(WindowManagerBlockingCondition cond)
  {
    Toolkit.endPainting();

    while ( cond.evaluate() && isRunning() )
    {
      if ( isReading() )
      {
        InputChar inputChar = Toolkit.readCharacter();

        if ( inputChar != null )
          WindowManager.handleInput(inputChar);
      }
    }
  }

  protected synchronized void deactivate()
  {
    _read = false;
  }

  protected synchronized void end()
  {
    _run = false;
    thd = null;
  }
}