package jcurses.themes;

import java.util.HashMap;
import java.util.Map;

import jcurses.system.CharColor;

/**
 * @author alewis
 *  
 */
public class BaseTheme implements Theme
{
  protected Map themeKeys = new HashMap();

  /*
   * (non-Javadoc)
   * 
   * @see themes.Theme#getColor(java.lang.String)
   */
  public CharColor getColor(String aKey)
  {
    CharColor mColor = (CharColor)themeKeys.get(aKey);
    
    if ( mColor == null )
      mColor = (CharColor)themeKeys.get(Theme.COLOR_DEFAULT);
    
    if ( mColor == null )
      mColor = new CharColor(CharColor.BLACK, CharColor.WHITE);
    
    return mColor;
  }
  
  public void setColor(String aKey, CharColor aColor)
  {
    themeKeys.put(aKey, aColor);
  }
  

}