
package jcurses.widgets;

import java.io.File;
import java.io.FileFilter;

/**
 *  Factory for file filters used in FileDialog
 *
 * @see FileDialog
 */
public class DefaultFileFilterFactory implements JCursesFileFilterFactory {
	/**
	 *  Generate a file filter from a string.
	 *
	 * @param  filterString  string showing acceptable file patterns
	 * @return               Filter object modelled on filterString
	 */
	public FileFilter generateFileFilter(String filterString) {
		return new DefaultFileFilter(filterString);
	}
}

/**
 *  A FileFilter extender to be returned by this the default factory
 *
 */
class DefaultFileFilter implements FileFilter {

	String _filterString = null;

	/**
	 *Constructor for the DefaultFileFilter object
	 *
	 * @param  filterString  String specifiying the filter
	 * @see java.io.FileFilter
	 */
	DefaultFileFilter(String filterString) {
		if (filterString != null) {
			_filterString = filterString.trim();
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  fileF  File to test for membership in filter set
	 * @return        true if member
	 */
	public boolean accept(File fileF) {
		if ((_filterString == null) || (fileF == null)) {
			return true;
		}

		String file = fileF.getAbsolutePath().trim();

		if (file.lastIndexOf(File.separator) != -1) {
			file = file.substring(file.lastIndexOf(File.separator) + 1, file.length());
		}

		int index = _filterString.indexOf("*");

		if (index == -1) {
			return (_filterString.equals(file));
		} else if (index == 0) {
			if (_filterString.length() == 1) {
				return true;
			}

			return file.endsWith(_filterString.substring(1, _filterString.length()));
		} else if (index == (_filterString.length() - 1)) {
			return file.startsWith(_filterString.substring(0, _filterString.length() - 1));
		} else {
			return (file.startsWith(_filterString.substring(0, index))) && (file.endsWith(_filterString.substring(index + 1, _filterString.length())));
		}
	}
}
