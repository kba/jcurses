package jcurses.themes;

import jcurses.system.CharColor;

/**
 * Adds theme support for JCurses components.
 * 
 * @author alewis
 *  
 */
public interface Theme
{
  public final String COLOR_DEFAULT           = "default";

  public final String COLOR_WINDOW_BORDER     = "window.border";
  public final String COLOR_WINDOW_BACKGROUND = "window.background";
  public final String COLOR_WINDOW_TITLE      = "window.title";
  public final String COLOR_WINDOW_TEXT       = "window.text";
  public final String COLOR_WINDOW_SHADOW     = "window.shadow";

  //  public final String COLOR_DIALOG_BORDER = "dialog.border";
  //  public final String COLOR_DIALOG_BACKGROUND = "dialog.background";
  //  public final String COLOR_DIALOG_TITLE = "dialog.title";
  //  public final String COLOR_DIALOG_TEXT = "dialog.text";
  //  public final String COLOR_DIALOG_SHADOW = "dialog.shadow";

  public final String COLOR_WIDGET_BORDER     = "widget.border";
  public final String COLOR_WIDGET_BACKGROUND = "widget.background";
  public final String COLOR_WIDGET_TITLE      = "widget.title";
  public final String COLOR_WIDGET_TEXT       = "widget.text";
  public final String COLOR_WIDGET_ACTION     = "widget.action";
  public final String COLOR_WIDGET_SELECTED   = "widget.selected";
  public final String COLOR_WIDGET_SHORTCUT   = "widget.shortcut";
  public final String COLOR_WIDGET_SCROLLBAR   = "widget.scrollbar";

  CharColor getColor(String aKey);

  void setColor(String aKey, CharColor aColor);

}