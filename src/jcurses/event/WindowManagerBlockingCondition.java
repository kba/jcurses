
package jcurses.event;

/**
 *  An interface specifying a method the MindowManager uses
 * to check if Dialogs are ready to be modal.
 * 
 *
 */
public interface WindowManagerBlockingCondition {
	/**
	 *  Indicate whether the entity on which this is called is ready to be modal
	 *
	 * @return    true if the entity on which this is called is ready to be modal
	 */
	boolean evaluate();
}
