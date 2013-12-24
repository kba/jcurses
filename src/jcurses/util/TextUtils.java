/*
 *  Created on Apr 21, 2004
 *
 * Commented 2008-11-19 by Jack Woehr jwoehr at users dot sourceforge dot net
 *
 */
package jcurses.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for managing text, e.g, wrapping it
 * @author     alewis
 */
public class TextUtils {
	/**
	 *  Wrap String into Array of String per a wrap length
	 *
	 * @param  aMsg  Original text
	 * @param  aLen  wrap length
	 * @return       Array of String representing lines each <= length
	 */
	public static String[] wrapLines(String aMsg, int aLen) {
		List mLines = new LinkedList();

		if (aMsg == null) {
			return new String[0];
		}

		String lines[] = aMsg.split("\n");

		for (int idx = 0; idx < lines.length; idx++) {
			int curLen = lines[idx].length();
			int pos = 0;

			while (curLen > aLen) {
				// find wrap point..
				int brk = pos + aLen;

				while ((brk > pos) && !Character.isWhitespace(lines[idx].charAt(brk))) {
					brk--;
				}

				// handle cases where there is no breaking point
				if (brk <= pos) {
					brk = pos + aLen + 1;
				}

				mLines.add(lines[idx].substring(pos, brk));
				curLen = curLen - (brk - pos);
				pos = brk + 1;
			}

			//if(curLen > 0)
			mLines.add(lines[idx].substring(pos));
		}

		Object tmp[] = mLines.toArray();
		String result[] = new String[tmp.length];

		for (int idx = 0; idx < tmp.length; idx++) {
			result[idx] = (String) tmp[idx];
		}

		return result;
	}

	/**
	 *  Create a String filled with a single character
	 *
	 * @param  aChar  the character to fill with
	 * @param  aLen   how many to fill with
	 * @return        A string consisting of the char repeated aLen times
	 */
	public static String replicate(char aChar, int aLen) {
		StringBuffer mBuf = new StringBuffer(aLen);
		for (int mIdx = 0; mIdx < aLen; mIdx++) {
			mBuf.append(aChar);
		}
		return mBuf.toString();
	}

	/**
	 *  Break a String into lines of text at embedded linefeed
	 * chars or at max width, whichever comes first each line.
	 * Checks for carriage-return chars in if-else but ignores them.
	 *
	 * @param  text      Original String
	 * @param  maxWidth  longest before split if no lf found
	 * @return           List of String
	 */
	public static List breakLines(String text, int maxWidth) {
		ArrayList list = new ArrayList();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (c == '\n') {
				String line = buffer.toString();

				if (line.length() > maxWidth) {
					list.add(line.substring(0, maxWidth));
					list.add(line.substring(maxWidth, line.length()));
				} else {
					list.add(line);
				}

				buffer = new StringBuffer();
			} else if (c == '\r') {
				//ignore
			} else {
				buffer.append(c);
			}
		}

		if (buffer.length() > 0) {
			list.add(buffer.toString());
		}

		return list;
	}

	/**
	 *  Center a String in a field, padding with blank.
	 *
	 * @param  aStr  the string
	 * @param  aLen  length of field
	 * @return       the centered string in field of blank
	 */
	public static String center(String aStr, int aLen) {
		double mDiff = (aLen - aStr.length()) / 2.0;
		return replicate(' ', (int) Math.floor(mDiff)) + aStr + replicate(' ', (int) Math.ceil(mDiff));
	}

	/**
	 *  Right-justify a String in a field, padding left with blank.
	 *
	 * @param  aStr  the string
	 * @param  aLen  length of field
	 * @return       the right-justified string
	 */
	public static String leftPad(String aStr, int aLen) {
		return replicate(' ', aLen - aStr.length()) + aStr;
	}

	/**
	 *  Center each String uniformly in in an Array of String
	 *
	 * @param  aLines  the Strings to center
	 * @param  aLen    uniform field length of output strings
	 * @return         Array of centered String
	 */
	public static String[] center(String aLines[], int aLen) {
		for (int idx = 0; idx < aLines.length; idx++) {
			aLines[idx] = center(aLines[idx], aLen);
		}

		return aLines;
	}

	/**
	 *  Right-justify each String uniformly in in an Array of String,
	 * padding left with blank.
	 *
	 * @param  aLines  the Strings to justify
	 * @param  aLen    uniform field length of output strings
	 * @return         Array of right-justified string
	 */
	public static String[] leftPad(String aLines[], int aLen) {
		for (int idx = 0; idx < aLines.length; idx++) {
			aLines[idx] = leftPad(aLines[idx], aLen);
		}

		return aLines;
	}

	/**
	 *  Array of String becomes String with original lines now
	 * field-separated by linefeed. Terminate with a linefeed.
	 *
	 * @param  aLines  the lines to merge
	 * @return         Resultant String
	 */
	public static String mergeLines(String aLines[]) {
		StringBuffer mOut = new StringBuffer();

		for (int idx = 0; idx < aLines.length; idx++) {
			mOut.append(aLines[idx]).append("\n");
		}

		mOut.append("\n");

		return mOut.toString();
	}

	/**
	 *  Wrap the lines and then marge for wrapped paragraph
	 *
	 * @param  aMsg  Original String
	 * @param  aLen  field length of line in wrapped paragraph
	 * @return       the paragraph
	 */
	public static String wrap(String aMsg, int aLen) {
		return mergeLines(wrapLines(aMsg, aLen));
	}
}

