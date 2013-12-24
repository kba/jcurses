/*
 *  Created on Apr 21, 2004
 *
 */
package jcurses.dialogs;

import jcurses.event.ActionEvent;
import jcurses.event.ActionListener;
import jcurses.system.Toolkit;
import jcurses.util.TextUtils;
import jcurses.widgets.Button;
import jcurses.widgets.DefaultLayoutManager;
import jcurses.widgets.Dialog;
import jcurses.widgets.Label;
import jcurses.widgets.WidgetsConstants;

/**
 * A Yes/No dialog with which to query the user.
 *
 * @author     alewis
 */
public class YesNoDialog {
	/**
	 *  Text "Yes"
	 */
	public static String DEFAULT_YES_LABEL = "Yes";
	/**
	 *   Text "No"
	 */
	public static String DEFAULT_NO_LABEL = "No";
	/**
	 *  Default title for a Y/N dlg
	 */
	public static String DEFAULT_TITLE = "Confirm";

	private final static int HEIGHT_FACTOR = 6;
	// number of rows needed to account for borders, padding, and buttons
	private final static int WIDTH_FACTOR = 4;
	// number of rows needed to account for borders and padding
	Dialog dialog;
	boolean result = false;
	Button mYes;
	Button mNo;

	/**
	 *Constructor for the YesNoDialog object with several defaults.
	 *
	 * @param  aMessage  The message to the user
	 */
	public YesNoDialog(String aMessage) {
		this(DEFAULT_TITLE, aMessage);
	}

	/**
	 *Constructor for the YesNoDialog object with default title
	 *
	 * @param  aMessage  The message to the user
	 * @param  aDefault  Default response, true is "Yes"
	 */
	public YesNoDialog(String aMessage, boolean aDefault) {
		this(DEFAULT_TITLE, aMessage, aDefault);
	}

	/**
	 *Constructor for the YesNoDialog object with default
	 * labels.
	 *
	 * @param  aTitle    Title for dlg
	 * @param  aMessage  The message to the user
	 */
	public YesNoDialog(String aTitle, String aMessage) {
		this(aTitle, aMessage, DEFAULT_YES_LABEL, DEFAULT_NO_LABEL);
	}

	/**
	 *Constructor for the YesNoDialog object with default labels
	 *
	 * @param  aTitle    Title for dlg
	 * @param  aMessage  The message to the user
	 * @param  aDefault  Default response, true is "Yes"
	 */
	public YesNoDialog(String aTitle, String aMessage, boolean aDefault) {
		this(aTitle, aMessage, DEFAULT_YES_LABEL, DEFAULT_NO_LABEL, aDefault);
	}

	/**
	 *Constructor for the YesNoDialog object with default response.
	 *
	 * @param  aTitle     Title for dlg
	 * @param  aMessage   The message to the user
	 * @param  aYesLabel  Label for yes button
	 * @param  aNoLabel   Label for no button
	 */
	public YesNoDialog(String aTitle, String aMessage, String aYesLabel, String aNoLabel) {
		this(aTitle, aMessage, aYesLabel, aNoLabel, false);
	}

	/**
	 *Constructor for the YesNoDialog object
	 *
	 * @param  aTitle     Title for dlg
	 * @param  aMessage   The message to the user
	 * @param  aYesLabel  Label for yes button
	 * @param  aNoLabel   Label for no button
	 * @param  aDefault   Default response, true is "Yes"
	 */
	public YesNoDialog(String aTitle, String aMessage, String aYesLabel, String aNoLabel, boolean aDefault) {
		result = aDefault;

		int mWidth = Math.max(aYesLabel.length() + aNoLabel.length() + (4 * WIDTH_FACTOR), Math.max(aTitle.length() + 2, 50));
		mWidth = Math.min(Toolkit.getScreenWidth() - WIDTH_FACTOR, mWidth);

		String mLines[] = TextUtils.wrapLines(aMessage, mWidth);

		int mHeight = Math.min(Toolkit.getScreenHeight() - HEIGHT_FACTOR, mLines.length);

		dialog = new Dialog(mWidth + WIDTH_FACTOR, mHeight + HEIGHT_FACTOR, true, aTitle);

		DefaultLayoutManager layout = new DefaultLayoutManager();
		dialog.getRootPanel().setLayoutManager(layout);

		Label mMessage = new Label(TextUtils.mergeLines(mLines));

		layout.addWidget(mMessage, 1, 1, mWidth, mHeight, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);

		int btnY = mHeight + (HEIGHT_FACTOR / 2);
		int btnWide = WIDTH_FACTOR + Math.max(aYesLabel.length(), aNoLabel.length());
		int btnStep = ((mWidth - (2 * btnWide)) / 3) + 1;

		mYes = new Button(aYesLabel);
		int btnX = btnStep;

		mYes.addListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					result = true;
					dialog.close();
				}
			});
		layout.addWidget(mYes, btnX, btnY, btnWide, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);

		btnX = btnWide + (2 * btnStep);

		mNo = new Button(aNoLabel);
		mNo.addListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					result = false;
					dialog.close();
				}
			});
		layout.addWidget(mNo, btnX, btnY, btnWide, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
	}

	/**
	 * Create and run the dlg and get the boolean user response,
	 * that which is unspecified is defaulted, mapping this factory
	 * method to the similar ctor.
	 *
	 * @param  aTitle     Title for dlg
	 * @param  aMessage   The message to the user
	 * @param  aYesLabel  Label for yes button
	 * @param  aNoLabel   Label for no button
	 * @return            User response, {@code true} is "yes".
	 */
	public static boolean execute(String aTitle, String aMessage, String aYesLabel, String aNoLabel) {
		return new YesNoDialog(aTitle, aMessage, aYesLabel, aNoLabel).show();
	}

	/**
	 * Create and run the dlg and get the boolean user response,
	 * that which is unspecified is defaulted, mapping this factory
	 * method to the similar ctor.
	 *
	 * @param  aTitle    Title for dlg
	 * @param  aMessage  The message to the user
	 * @return           User response, {@code true} is "yes".
	 */
	public static boolean execute(String aTitle, String aMessage) {
		return new YesNoDialog(aTitle, aMessage).show();
	}

	/**
	 * Create and run the dlg and get the boolean user response,
	 * that which is unspecified is defaulted, mapping this factory
	 * method to the similar ctor.
	 *
	 * @param  aMessage  The message to the user
	 * @return           User response, {@code true} is "yes".
	 */
	public static boolean execute(String aMessage) {
		return new YesNoDialog(aMessage).show();
	}

	/**
	 * A non-polymorphic pack-and-set-visible returning the boolean result
	 * of the dialog.
	 *
	 * @return    User response, {@code true} is "yes".
	 */
	public boolean show() {
		dialog.pack();
		if (result) {
			mYes.getFocus();
		} else {
			mNo.getFocus();
		}

		dialog.show();
		return result;
	}
}
