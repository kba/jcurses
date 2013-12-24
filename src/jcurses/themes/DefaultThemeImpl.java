package jcurses.themes;

import jcurses.system.CharColor;

/**
 * This should eventually have features such as loading ad saving ,load from resource, etc. Currently all color values are hardcoded.
 * 
 * @author alewis
 *  
 */
public class DefaultThemeImpl extends BaseTheme
{
  {
    //Toolkit.adjustBaseColor(CharColor.YELLOW, 1000, 1000, 0);

    setColor(Theme.COLOR_DEFAULT, new CharColor(CharColor.BLACK, CharColor.BLUE));

    setColor(Theme.COLOR_WINDOW_BORDER, new CharColor(CharColor.BLACK, CharColor.CYAN));
    setColor(Theme.COLOR_WINDOW_BACKGROUND, new CharColor(CharColor.BLACK, CharColor.WHITE));
    setColor(Theme.COLOR_WINDOW_TITLE, new CharColor(CharColor.WHITE, CharColor.BLUE, CharColor.BOLD));
    setColor(Theme.COLOR_WINDOW_TEXT, new CharColor(CharColor.BLACK, CharColor.WHITE));
    setColor(Theme.COLOR_WINDOW_SHADOW, new CharColor(CharColor.BLACK, CharColor.BLACK));

    //setColor(Theme.COLOR_DIALOG_BORDER, new CharColor(CharColor.WHITE, CharColor.BLACK));
    //setColor(Theme.COLOR_DIALOG_BACKGROUND, new CharColor(CharColor.WHITE, CharColor.BLACK));
    //setColor(Theme.COLOR_DIALOG_TITLE, new CharColor(CharColor.WHITE, CharColor.RED));
    //setColor(Theme.COLOR_DIALOG_TEXT, new CharColor(CharColor.WHITE, CharColor.BLACK));
    //setColor(Theme.COLOR_DIALOG_SHADOW, new CharColor(CharColor.BLACK, CharColor.BLACK));

    setColor(Theme.COLOR_WIDGET_BORDER, new CharColor(CharColor.BLACK, CharColor.CYAN));
    setColor(Theme.COLOR_WIDGET_BACKGROUND, new CharColor(CharColor.BLACK, CharColor.WHITE));
    setColor(Theme.COLOR_WIDGET_TITLE, new CharColor(CharColor.WHITE, CharColor.BLACK, CharColor.BOLD));
    setColor(Theme.COLOR_WIDGET_TEXT, new CharColor(CharColor.BLACK, CharColor.WHITE));
    setColor(Theme.COLOR_WIDGET_ACTION, new CharColor(CharColor.BLUE, CharColor.WHITE));
    setColor(Theme.COLOR_WIDGET_SELECTED, new CharColor(CharColor.CYAN, CharColor.BLACK, CharColor.REVERSE));
    setColor(Theme.COLOR_WIDGET_SHORTCUT, new CharColor(CharColor.WHITE, CharColor.RED));
    setColor(Theme.COLOR_WIDGET_SCROLLBAR, new CharColor(CharColor.BLACK, CharColor.WHITE, CharColor.REVERSE));
  }
}