/*
 *  Created on Apr 21, 2004
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
 * A Confirmation dialog
 * @author     alewis
 *
 */
public class OkDialog {
	/**
	 *  Default title for the dialog
	 */
	public static String DEFAULT_TITLE = "Confirm";
	/**
	 *  Default label for the button
	 */
	public static String DEFAULT_LABEL = "Ok";
	Dialog dialog;

	/**
	 * Constructor for the OkDialog object providing a message with a default
	 * title and label
	 *
	 * @param  aMessage  Message to accompany dialog
	 */
	public OkDialog(String aMessage) {
		this(DEFAULT_TITLE, aMessage, DEFAULT_LABEL);
	}

	private final static int WIDTH_PADDING = 2;
	// number of rows needed to account for borders and padding
	private final static int HEIGHT_FACTOR = 6;
	// number of rows needed to account for borders, padding, and buttons
	private final static int WIDTH_FACTOR = 4;
	// number of rows needed to account for borders and padding

	/**
	 *Constructor for the OkDialog object providing a message, title,
	 * and label
	 *
	 * @param  aTitle    title
	 * @param  aMessage  message
	 * @param  aLabel    button label
	 */
	public OkDialog(String aTitle, String aMessage, String aLabel) {
		int mWidth = Math.min(Toolkit.getScreenWidth() - WIDTH_FACTOR, Math.max(aTitle.length() + 2, 40));
		String mLines[] = TextUtils.wrapLines(aMessage, mWidth);
		int mHeight = Math.min(Toolkit.getScreenHeight() - HEIGHT_FACTOR, mLines.length);

		dialog = new Dialog(mWidth + WIDTH_FACTOR, mHeight + HEIGHT_FACTOR, true, aTitle);

		DefaultLayoutManager layout = new DefaultLayoutManager();
		dialog.getRootPanel().setLayoutManager(layout);

		Button mYes = new Button(aLabel);
		mYes.addListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dialog.close();
				}
			});

		int btnX = (mWidth - aLabel.length() - WIDTH_PADDING) / 2;
		int btnY = mHeight + HEIGHT_FACTOR / 2;
		layout.addWidget(mYes, btnX, btnY, aLabel.length() + WIDTH_FACTOR, 1, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);

		Label mMessage = new Label(TextUtils.mergeLines(mLines));
		layout.addWidget(mMessage, 1, 1, mWidth, mHeight, WidgetsConstants.ALIGNMENT_CENTER, WidgetsConstants.ALIGNMENT_CENTER);
	}

	/**
	 *  Factory for OkDialog providing a message, title,
	 * and label
	 *
	 * @param  aTitle    title
	 * @param  aMessage  message
	 * @param  aLabel    button label
	 */
	public static void execute(String aTitle, String aMessage, String aLabel) {
		new OkDialog(aTitle, aMessage, aLabel).show();
	}

	/**
	 *  Factory for OkDialog  providing a message with a default
	 * title and label
	 *
	 * @param  aMessage  message
	 */
	public static void execute(String aMessage) {
		new OkDialog(aMessage).show();
	}

	/**
	 *  Show this dialog
	 */
	public void show() {
		dialog.show();
	}
}
