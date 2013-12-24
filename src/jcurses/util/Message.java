
package jcurses.util;

import java.util.StringTokenizer;

import jcurses.event.ActionEvent;
import jcurses.event.ActionListener;
import jcurses.widgets.Button;
import jcurses.widgets.DefaultLayoutManager;
import jcurses.widgets.Dialog;
import jcurses.widgets.Label;
import jcurses.widgets.WidgetsConstants;

/**
 * This is a class to create and show user defined messages. Such message is a dialog with an user defined title, containing an user defined text and a button
 * to close the window with an user defined label.
 */

public class Message extends Dialog implements ActionListener
{

  String _title  = null;
  String _text   = null;

  Button _button = null;
  Label  _label  = null;

  /**
   * The constructor
   * 
   * @param title the message's title
   * @param text the message's text
   * @param buttonLabel the label on the message's button
   *  
   */
  public Message(String title, String text, String buttonLabel)
  {
    super(getWidth(text, title) + 4, getHeight(text) + 7, true, title);

    DefaultLayoutManager manager = (DefaultLayoutManager)getRootPanel().getLayoutManager();

    _label = new Label(text);
    _button = new Button(buttonLabel);
    _title = title;

    _button.addListener(this);

    manager.addWidget(_label, 0, 0, getWidth(text, _title) + 2, getHeight(text) + 2, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);

    manager.addWidget(_button, 0, getHeight(text) + 2, getWidth(text, _title) + 2, 5, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);

  }

  private static int getWidth(String aLabel, String aTitle)
  {
    int result = 0;

    if ( aLabel != null )
    {
      StringTokenizer tokenizer = new StringTokenizer(aLabel, "\n");
      while ( tokenizer.hasMoreElements() )
      {
        String token = tokenizer.nextToken();
        if ( result < token.length() )
        {
          result = token.length();
        }
      }
    }

    if ( aTitle != null )
      result = Math.max(result, aTitle.length());

    //message must fit on the screen
    return Math.min(result, jcurses.system.Toolkit.getScreenWidth() - 3);
  }

  private static int getHeight(String aLabel)
  {
    int result = 0;
    if(aLabel != null)
    {
      StringTokenizer tokenizer = new StringTokenizer(aLabel, "\n");
      while ( tokenizer.hasMoreElements() )
      {
        tokenizer.nextElement();
        result++;
      }
    }
    return result;
  }

  /**
   * Required for implementing <code>jcurses.event.ActionListener</code>
   */
  public void actionPerformed(ActionEvent event)
  {
    close();
  }

}