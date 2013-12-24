
package jcurses.widgets;

import java.util.Vector;

import jcurses.event.ActionEvent;
import jcurses.event.ActionListener;
import jcurses.event.ActionListenerManager;
import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;
import jcurses.themes.Theme;
import jcurses.util.Rectangle;

/**
 * This class implements a button-widget. Such button has a label and is 'clicked'
 * by user typing a special character (default 'enter'). If it is 'clicked', it
 * generates an <code>ActionEvent</code>, that is delegated to registered listeners.
 */
public class Button extends Widget {
    private static InputChar __actionChar = new InputChar('\n');
    private ActionListenerManager _listenerManager = new ActionListenerManager();
    private InputChar _shortCut = null;
    private String _label = null;

    /**
     * The constructor
     *
     * @param  aLabel  button's label
     */ 
    public Button(String aLabel) {
        setLabel(aLabel);
    }

    /**
     * Sets button's label
     *
     * @param  aLabel  buttton's label
     */ 
    public void setLabel(String aLabel) {
        _label = aLabel;
    }

    /**
     * @return    button's label
     */ 
    public String getLabel() {
        return _label;
    }

    /**
     * @return        The focusedButtonColors value
     * @deprecated    Use getSelectedColors()
     */ 
    public CharColor getFocusedButtonColors() {
        return getSelectedColors();
    }

    /**
     * @return        The focusedButtonDefaultColors value
     * @deprecated    Use getDefaultSelectedColors()
     */ 
    public CharColor getFocusedButtonDefaultColors() {
        return getDefaultSelectedColors();
    }

    /**
     * @param  aColor  The new focusedButtonColors value
     * @deprecated     Use setSelectedColors()
     */ 
    public void setFocusedButtonColors(CharColor aColor) {
        setSelectedColors(aColor);
    }

    /**
     * @return    colors button's shortcut char's colors
     */ 
    public CharColor getShortCutColors() {
        return getColors(Theme.COLOR_WIDGET_SHORTCUT);
    }

    /**
     * Sets button's shortcut char's colors. If the button has a shortcut char
     * and this char is contained by the label, than the char within the label will be
     * painted in different colors, set by this method
     *
     * @param  aColor  button's shortcut char's colors
     */ 
    public void setShortCutColors(CharColor aColor) {
        setColors(Theme.COLOR_WIDGET_SHORTCUT, aColor);
    }

    /**
     * Adds a listener to the button.
     *
     * @param  listener  listener to add
     */ 
    public void addListener(ActionListener listener) {
        _listenerManager.addListener(listener);
    }

    /**
     * Removes a listener from the button.
     *
     * @param  listener  listener to remove
     */ 
    public void removeListener(ActionListener listener) {
        _listenerManager.removeListener(listener);
    }

    /**
     * Set's button's shortcut char. If this shortcut is typed, then the button
     * will handle the char, as described by <code>Widget</code>, and generate an
     * Event as if the button were 'clicked'.
     *
     * @param  c  The new shortCut value
     */ 
    public void setShortCut(char c) {
        _shortCut = new InputChar(c);
    }

    /**
     *  Gets the preferredSize attribute of the Button object
     *
     * @return    The preferredSize value
     */ 
    protected Rectangle getPreferredSize() {
        return new Rectangle(_label.length() + 4, 1);
    }

    /**
     *  Paint self.
     */ 
    protected void doPaint() {
        //System.err.println("Button<" + _label + ">.paint()");
        if (hasFocus()) {
            Toolkit.printString("< " + _label + " >", getRectangle(), getSelectedColors());

        } else {
            Toolkit.printString("< " + _label + " >", getRectangle(), getActionColors());


            drawShortCutIfNeeded();
        }
    }

    /**
     *  Gets the shortCutsList attribute of the Button object
     *
     * @return    The shortCutsList value
     */ 
    protected Vector getShortCutsList() {
        if (getShortCut() == null) {
            return null;
        }
        Vector result = new Vector();
        result.add(getShortCut());
        return result;
    }

    /**
     *  Gets the focusable attribute of the Button object
     *
     * @return    The focusable value
     */ 
    protected boolean isFocusable() {
        return true;
    }

    /**
     *  Input handler,just looks for the shortcut char
     * and performs the action if the shortcut char is encountered.
     *
     * @param  ch  Description of the Parameter
     * @return     Description of the Return Value
     */ 
    protected boolean handleInput(InputChar ch) {
        if ((ch.equals(__actionChar)) || ((getShortCut() != null) && (getShortCut().equals(ch)))) {
            doAction();
            return true;
        }

        return false;
    }

    /**
     *  Handle getting focus, paint self.
     */ 
    protected void focus() {
        doPaint();
    }

    /**
     *  Description of the Method
     */ 
    protected void unfocus() {
        doPaint();
    }

    /**
     *  Gets the shortCut character of the Button object
     *
     * @return    The shortCut value
     */ 
    private InputChar getShortCut() {
        return _shortCut;
    }

    /**
     *  Description of the Method
     */ 
    private void drawShortCutIfNeeded() {
        InputChar shortCut = getShortCut();
        if (shortCut != null) {
            String c = shortCut.toString();
            if (_label != null) {
                int index = _label.toLowerCase().indexOf(c.toLowerCase());
                if (index != - 1) {
                    Toolkit.printString(_label.substring(index, index + 1), getAbsoluteX() + index + 2, getAbsoluteY(), getShortCutColors());

                }
            }
        }
    }

    /**
     *  Description of the Method
     */ 
    private void doAction() {
        _listenerManager.handleEvent(new ActionEvent(this));
    }

}
