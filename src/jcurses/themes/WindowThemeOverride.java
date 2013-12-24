/*
 * Created on Jul 23, 2004
 * 
 * TODO To change the template for this generated file go to Window - Preferences - Java - Code Style - Code Templates
 */

package jcurses.themes;

import jcurses.system.CharColor;
import jcurses.widgets.Window;

/**
 * @author alewis
 *  
 */
public class WindowThemeOverride extends BaseTheme
{
  Theme baseTheme = null;

  public WindowThemeOverride()
  {
  // need zero arg contructor
  }

  public WindowThemeOverride(Theme aBaseTheme)
  {
    setBaseTheme(aBaseTheme);
  }

  public CharColor getColor(String aKey)
  {
    CharColor mColor = (CharColor)themeKeys.get(aKey);
    
    if ( mColor == null && baseTheme != null )
      mColor = baseTheme.getColor(aKey);

    if ( mColor == null )
      mColor = Window.getTheme().getColor(aKey);

    return mColor;
  }

  public Theme getBaseTheme()
  {
    return baseTheme;
  }

  public void setBaseTheme(Theme aBaseTheme)
  {
    baseTheme = aBaseTheme;
  }
}