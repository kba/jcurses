
package jcurses.dialogs;

import jcurses.event.ActionEvent;
import jcurses.event.ActionListener;
import jcurses.system.Toolkit;
import jcurses.widgets.Button;
import jcurses.widgets.DefaultLayoutManager;
import jcurses.widgets.Label;
import jcurses.widgets.WidgetsConstants;
import jcurses.widgets.Window;

/**
 * A dialog to indicate progress which can be updated from another thread
 * @author     alewis
 *
 */
public class ProgressDialog {
	private final static int WIDTH_PADDING = 2;
	// number of rows needed to account for borders and padding
	private final static int HEIGHT_FACTOR = 6;
	// number of rows needed to account for borders, padding, and buttons
	private final static int WIDTH_FACTOR = 4;
	// number of rows needed to account for borders and padding
	/**
	 *  Default label for the confirmation button
	 */
	public static String DEFAULT_LABEL = "Ok";
	Label msgLabel = null;
	ActionListener doCancel = null;
	private String message = "Please wait....";
	private String title = "Progress...";
	Window window = null;
	private int current;
	private int max;
	private int min;

	/**
	 *Constructor for the ProgressDialog object providing a title
	 *
	 * @param  aTitle  Title of the ProgressDialog
	 */
	public ProgressDialog(String aTitle) {
		setTitle(aTitle);
	}

	/**
	 *  Sets the current attribute (progress value) of the ProgressDialog
	 *
	 * @param  aCurrent  The new current (progress) value
	 */
	public void setCurrent(int aCurrent) {
		current = aCurrent;
	}

	/**
	 *  Gets the current attribute (progress value) of the ProgressDialog
	 *
	 * @return    The current (progress) value
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 *  Instances the Listener for user input during progress
	 *
	 * @param  aDoCancel  The new doCancel value
	 */
	public void setDoCancel(ActionListener aDoCancel) {
		doCancel = aDoCancel;
	}

	/**
	 *  Sets the max (complete progress) of the ProgressDialog
	 *
	 * @param  aMax  The new max value
	 */
	public void setMax(int aMax) {
		max = aMax;
	}

	/**
	 *  Gets the max (complete progress) of the ProgressDialog
	 *
	 * @return    The max value
	 */
	public int getMax() {
		return max;
	}

	/**
	 *  Sets the message attribute of the ProgressDialog object
	 *
	 * @param  aMessage  The new message value
	 */
	public void setMessage(String aMessage) {
		message = aMessage;
		if (msgLabel != null) {
			msgLabel.setText(message);
			window.pack();
			//window.show();
		}
	}

	/**
	 *  Gets the message attribute of the ProgressDialog object
	 *
	 * @return    The message value
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *  Sets the min (zero progress) of the ProgressDialog
	 *
	 * @param  aMin  The new min value
	 */
	public void setMin(int aMin) {
		min = aMin;
	}

	/**
	 *  Gets the min (zero progress)of the ProgressDialog
	 *
	 * @return    The min value
	 */
	public int getMin() {
		return min;
	}

	/**
	 *  Close the dialog
	 */
	public void close() {
		if (window != null) {
			window.close();
		}
	}

	/**
	 *  Show the dialog
	 */
	public void show() {
		int height = 1;
		int width = Math.min(Toolkit.getScreenWidth() - WIDTH_FACTOR, 40);

		window = new Window(width + WIDTH_FACTOR, height + HEIGHT_FACTOR, true, title);
		DefaultLayoutManager layout = new DefaultLayoutManager();
		window.getRootPanel().setLayoutManager(layout);

		if (doCancel != null) {
			Button mCancel = new Button(DEFAULT_LABEL);
			mCancel.addListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						doCancel.actionPerformed(arg0);
					}
				});

			int btnX = (width - DEFAULT_LABEL.length() - WIDTH_PADDING) / 2;
			int btnY = height + (HEIGHT_FACTOR / 2);
			layout.addWidget(mCancel, btnX, btnY, DEFAULT_LABEL.length() + WIDTH_FACTOR, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
		}

		msgLabel = new Label(message);
		layout.addWidget(msgLabel, 1, 1, width, height, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);

		window.show();
	}

	/**
	 *  Gets the title attribute of the ProgressDialog
	 *
	 * @return    The title value
	 */
	public String getTitle() {
		return title;
	}

	/**
	 *  Sets the title attribute of the ProgressDialog
	 *
	 * @param  aTitle  The new title value
	 */
	public void setTitle(String aTitle) {
		title = aTitle;
	}
}
