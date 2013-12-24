
package jcurses.system;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import jcurses.util.Rectangle;
import jcurses.util.TextUtils;

/**
 * This class is the 'work factory' of the jcurses library. It contains methods for primitive input and output operations and is only interface
 * to platform dependent libraries. A developer must not usually call methods of this class. These methods are used in implementing widgets
 * and in jcurses core.
 *
 */
@SuppressWarnings("rawtypes")
public class Toolkit {
    /**
     *  Effectively an enum
     */     
    public static final int VERTICAL = 0;
    /**
     *  Effectively an enum
     */     
    public static final int HORIZONTAL = 1;
    /**
     *  Effectively an enum
     */ 
    public static final short LL_CORNER = 2;
    /**
     *  Effectively an enum
     */ 
    public static final short LR_CORNER = 3;
    /**
     *  Effectively an enum
     */ 
    public static final short UL_CORNER = 4;
    /**
     *  Effectively an enum
     */ 
    public static final short UR_CORNER = 5;

    
    private static long []__attributes = {0 ,0 ,0} ;
    private static short []__basicColors = {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0} ;
    private static short [][]__colorpairs = new short [8][8];
    private static Hashtable __clips = new Hashtable();
    private static short __maxColorPairNumber = - 1;
    private static String __encoding;
    // private static final String JAR_RESOURCE         = "jar:file:";
    // private static final String FILE_RESOURCE        = "file:";
    private static final String LIBRARY_NAME = "jcurses";

    static {
    	List<String> searchPaths = new ArrayList<String>();
    	final String osName = System.getProperty("os.name").toLowerCase();
    	String libExtension = null;
		if (osName.indexOf("linux") >= 0) {
    		libExtension = ".so";
		} else if (osName.indexOf("win") >= 0) {
    		libExtension = ".dll";
		} else if (osName.indexOf("mac") >= 0) {
    		libExtension = ".jnilib"; // TODO not sure
    	}
    	// $PWD/jcurses.so
    	searchPaths.add(System.getProperty("user.dir") + "/" + LIBRARY_NAME + libExtension);
    	// $PWD/libjcurses.so
    	searchPaths.add(System.getProperty("user.dir") + "/lib" + LIBRARY_NAME + libExtension);
    	// $PWD/lib/jcurses.so
    	searchPaths.add(System.getProperty("user.dir") + "/lib/" + LIBRARY_NAME + libExtension);
    	// $PWD/lib/libjcurses.so
    	searchPaths.add(System.getProperty("user.dir") + "/lib/lib" + LIBRARY_NAME + libExtension);

    	// $HOME/jcurses.so
    	searchPaths.add(System.getProperty("user.home") + "/" + LIBRARY_NAME + libExtension);
    	// $HOME/libjcurses.so
    	searchPaths.add(System.getProperty("user.home") + "/lib" + LIBRARY_NAME + libExtension);
    	// $PWD/.local/lib/jcurses.so
    	searchPaths.add(System.getProperty("user.home") + ".local/lib/" + LIBRARY_NAME + libExtension);
    	// $PWD/.local/lib/libjcurses.so
    	searchPaths.add(System.getProperty("user.home") + ".local/lib/lib" + LIBRARY_NAME + libExtension);

    	
    	boolean foundInPath = false;
		for (String sp : searchPaths) {
    		try {
    			System.load(sp);
    			foundInPath = true;
    		} catch (UnsatisfiedLinkError e) {
    			System.out.println("Not found in " + sp);
    		}
    	}
		if (!foundInPath) {
			try {
				System.loadLibrary(LIBRARY_NAME);
			} catch (UnsatisfiedLinkError e) {
				System.out.println("Found Library neither in the default search paths, not in the java.system.path. Stopping");
				System.exit(-1);
			}
		}
        // loadLibrary();
        fillBasicColors(__basicColors);
        fillAttributes(__attributes);
        fillColorPairs();
        initEncoding();
        init();
    }

    /**
     * The method sets the clippping rectangle for the current thread. All the output operations, that are performed by this thread after a call of this method,
     * will paint only within the clip rectangle. If other clips were set before this, then the used clip rectangle is the intersection of all clip rectangles set
     * by current thread.
     *
     *
     * @param  clipRect  clip rectangle to be set
     */ 
    @SuppressWarnings("unchecked")
	public static void setClipRectangle(Rectangle clipRect) {
        ArrayList clips = (ArrayList) __clips.get(Thread.currentThread());

        if (clips == null) {
            clips = new ArrayList();
            __clips.put(Thread.currentThread(), clips);
        }

        clips.add(clipRect);
    }

    /**
     * The method sets java encoding for string input and output operations
     *
     *
     * @param  encoding  DOCUMENT ME!
     */ 
    public static void setEncoding(String encoding) {
        __encoding = encoding;
    }

    /**
     * Gets java encoding for string input and output operations
     *
     *
     * @return    the java encoding used by sring input and output operations
     */ 
    public static String getEncoding() {
        return __encoding;
    }

    /**
     * The method returns the screen height
     *
     *
     * @return    the screen height
     */ 
    public static synchronized native int getScreenHeight();

    /**
     * The method returns the screen width
     *
     *
     * @return    the screen height
     */ 
    public static synchronized native int getScreenWidth();

    /**
     * The method to make an audio alert. Works only with terminals, that support 'beeps', under windows currenty does nothing.
     */ 
    public static synchronized native void beep();

    /**
     * The method changes the background and the foreground colors of an given rectangle on the schreen
     *
     *
     * @param  aRect   rectangle, whose colors are to be changed
     * @param  aColor  new colors
     */ 
    public static void changeColors(Rectangle aRect, CharColor aColor) {
        Rectangle clipRect = getCurrentClipRectangle();

        if (clipRect != null) {
            aRect = aRect.intersection(clipRect);
        }

        if (! aRect.isEmpty()) {
            changeColors(aRect.getX(), aRect.getY(), aRect.getWidth(),
                     aRect.getHeight(), aColor.getPairNo(), aColor.getAttribute());

        }
    }

    /**
     * The method clears the screen and fills it with the background color of color
     *
     *
     * @param  color  color the color to fill the screen, only background part is used
     */ 
    public static void clearScreen(CharColor color) {
        clearScreen(color.getPairNo(), color.getAttribute());
    }

    /**
     * The method draws a border ( empty rectangle )
     *
     *
     * @param  rect   bounds of the border to be painted
     * @param  color  color attributes of the border
     */ 
    public static void drawBorder(Rectangle rect, CharColor color) {
        drawBorder(rect.getX(), rect.getY(), rect.getWidth(),
                 rect.getHeight(), color);

    }

    /**
     * The method draws a border on the screen.
     *
     * We brea kdow nthe logic of line drawing here so we can properly take advantage of the clipping functionality at this level, rather than using the
     * underlying curses border routines. We pass anything we can to underlying primitives however.
     *
     *
     * @param  aX       the x coordinate of the top left corner of the border to be painted
     * @param  aY       the y coordinate of the top left corner of the border to be painted
     * @param  aWidth   the width of the border to be painted
     * @param  aHeight  the height of the border to be painted
     * @param  aColor   color attributes of the border
     */ 
    public static void drawBorder(int aX, int aY, int aWidth, int aHeight,
             CharColor aColor) {


        short mPair = aColor.getPairNo();
        long mAttr = aColor.getAttribute();

        drawCornerClip(aX, aY, UL_CORNER, mPair, mAttr);
        drawVLineClip(aX, aY + 1, aY + aHeight - 2, aColor);
        drawHLineClip(aX + 1, aY, aX + aWidth - 2, aColor);
        drawCornerClip(aX + aWidth - 1, aY, UR_CORNER, mPair, mAttr);
        drawVLineClip(aX + aWidth - 1, aY + 1, aY + aHeight - 2, aColor);
        drawCornerClip(aX, aY + aHeight - 1, LL_CORNER, mPair, mAttr);
        drawHLineClip(aX + 1, aY + aHeight - 1, aX + aWidth - 1, aColor);
        drawCornerClip(aX + aWidth - 1, aY + aHeight - 1, LR_CORNER, mPair,
                 mAttr);


    }

    /**
     *  The method draws a corner that is constrained within a rect
     *
     * @param  aX     the x coordinate of the top left corner of the clip to be painted
     * @param  aY     the y coordinate of the top left corner of the clip to be painted
     * @param  aPos   Position enum (UL,LL,UR,LR)
     * @param  mPair  Color pair number
     * @param  mAttr  Attribute
     */ 
    private static void drawCornerClip(int aX, int aY, int aPos, short mPair,
             long mAttr) {

        Rectangle mClip = getCurrentClipRectangle();
        if (mClip == null || (isBetween(aX, mClip.getLeft(),
                 mClip.getRight()) && isBetween(aY, mClip.getTop(), mClip.getBottom()))) {

            drawCorner(aX, aY, aPos, mPair, mAttr);
        }
    }

    /**
     *  The method draws a corner
     *
     * @param  aX     the x coordinate of the top left corner of the clip to be painted
     * @param  aY     the y coordinate of the top left corner of the clip to be painted
     * @param  aPos   Position enum (UL,LL,UR,LR)
     * @param  mPair  Color pair number
     * @param  mAttr  Attribute
     */ 
    private static synchronized native void drawCorner(int aX, int aY,
             int aPos, short colorPairNumber, long attr);


    /**
     * The method draws a horizontal line
     *
     *
     * @param  startX  the x coordinate of the start point
     * @param  startY  the y coordinate of the start point
     * @param  endX    the x coordinate of the end point
     * @param  color   DOCUMENT ME!
     */ 
    public static void drawHLineClip(int startX, int startY, int endX,
             CharColor color) {

        Rectangle mClip = getCurrentClipRectangle();

        if (mClip != null) {
            startX = Math.max(startX, mClip.getLeft());
            endX = Math.min(endX, mClip.getRight());
        }

        if (mClip == null || (isBetween(startY, mClip.getTop(),
                 mClip.getBottom()) && startX <= mClip.getRight() && endX >= mClip.getLeft())) {

            drawHorizontalLine(startX, startY, endX, color.getPairNo(),
                     color.getAttribute());

        }
    }

    /**
     * The method draws a horizontal thick line
     *
     *
     * @param  startX  the x coordinate of the start point
     * @param  startY  the y coordinate of the start point
     * @param  endX    the x coordinate of the end point
     * @param  color   DOCUMENT ME!
     */ 
    public static void drawHorizontalThickLine(int startX, int startY,
             int endX, CharColor color) {

        Rectangle mClip = getCurrentClipRectangle();

        if (mClip != null) {
            startX = Math.max(startX, mClip.getLeft());
            endX = Math.min(endX, mClip.getRight());
        }

        if (mClip == null || (isBetween(startY, mClip.getTop(),
                 mClip.getBottom()) && startX <= mClip.getRight() && endX >= mClip.getLeft())) {

            drawHorizontalThickLine(startX, startY, endX, color.getPairNo(),
                     color.getAttribute());

        }
    }

    /**
     * The method draws a rectangle on the screen, filled with background part of <code>color</code>
     *
     *
     * @param  aRect   rectangle ( that is, bounds of rectangle) to be painted
     * @param  aColor  color to fill the rectangle, only background part is used
     */ 
    public static void drawRectangle(Rectangle aRect, CharColor aColor) {
        drawRectangle(aRect.getX(), aRect.getY(), aRect.getWidth(),
                 aRect.getHeight(), aColor);

    }

    /**
     * The method draws a rectangle on the screen, filled with background part of <code>color</code>
     *
     * @param  aX      the x coordinate of the top left corner of the rectangle to be painted
     * @param  aY      the y coordinate of the top left corner of the rectangle to be painted
     * @param  aWide   the width of the rectangle to be painted
     * @param  aHigh   the height of the rectangle to be painted
     * @param  aColor  color to fill the rectangle, only background part is used
     */ 
    public static void drawRectangle(int aX, int aY, int aWide, int aHigh,
             CharColor aColor) {

        drawRectangle(aX, aY, aWide, aHigh, getCurrentClipRectangle(), aColor);
    }

    /**
     *  The method draws a rectangle on the screen constrainted within a clipping rect and
     *  filled with background part of <code>color</code>
     *
     * @param  aX      the x coordinate of the top left corner of the rectangle to be painted
     * @param  aY      the y coordinate of the top left corner of the rectangle to be painted
     * @param  aWide   the width of the rectangle to be painted
     * @param  aHigh   the height of the rectangle to be painted
     * @param  aClip   clipping rect
     * @param  aColor  color to fill the rectangle, only background part is used
     */ 
    public static void drawRectangle(int aX, int aY, int aWide, int aHigh,
             Rectangle aClip, CharColor aColor) {

        if (aClip == null) {
            drawRectangle(aX, aY, aWide, aHigh, aColor.getPairNo(),
                     aColor.getAttribute());

        } else {
            int mX = Math.max(aX, aClip.getLeft());
            int mY = Math.max(aY, aClip.getTop());
            int mWide = Math.min(aWide, aClip.getRight() - mX + 1);
            int mHigh = Math.min(aHigh, aClip.getBottom() - mY + 1);

            if (mWide > 0 && mHigh > 0) {
                drawRectangle(mX, mY, mWide, mHigh, aColor.getPairNo(),
                         aColor.getAttribute());

            }
        }
    }

    /**
     * The method draws a vertical line
     *
     *
     * @param  startX  the x coordinate of the start point
     * @param  startY  the y coordinate of the start point
     * @param  endY    the y coordinate of the end point
     * @param  color   color to draw line
     */ 
    public static void drawVLineClip(int startX, int startY, int endY,
             CharColor color) {

        Rectangle mClip = getCurrentClipRectangle();

        if (mClip != null) {
            startY = Math.max(startY, mClip.getTop());
            endY = Math.min(endY, mClip.getBottom());
        }

        if (mClip == null || (isBetween(startX, mClip.getLeft(),
                 mClip.getRight()) && startY <= mClip.getBottom() && endY >= mClip.getTop())) {

            drawVerticalLine(startX, startY, endY, color.getPairNo(),
                     color.getAttribute());

        }
    }

    /**
     * The method draws a vertical thick line
     *
     *
     * @param  startX  the x coordinate of the start point
     * @param  startY  the y coordinate of the start point
     * @param  endY    the y coordinate of the end point
     * @param  color   color to draw line
     */ 
    public static void drawVerticalThickLine(int startX, int startY, int endY,
             CharColor color) {

        Rectangle mClip = getCurrentClipRectangle();

        if (mClip != null) {
            startY = Math.max(startY, mClip.getTop());
            endY = Math.min(endY, mClip.getBottom());
        }

        if (mClip == null || (isBetween(startX, mClip.getLeft(),
                 mClip.getRight()) && startY <= mClip.getBottom() && endY >= mClip.getTop())) {

            drawVerticalThickLine(startX, startY, endY, color.getPairNo(),
                     color.getAttribute());

        }
    }

    /**
     * The method ends a new painting action, containing possible many painting operations The call of this method must already follow a call of
     * <code>startPainting</code>
     */ 
    public static synchronized native void endPainting();

    /**
     * The method tells whether a terminal is color-capable.
     *
     *
     * @return    <code>true</code> if the terminal can do color painting, <code>false</code> otherwise.
     */ 
    public static boolean hasColors() {
        return (hasColorsAsInteger() != 0);
    }

    /**
     * The method initializes the jcurses library, must be called only one time before all painting and input operations.
     */ 
    public static synchronized native void init();

    /**
     * The method prints a string on the screen
     *
     *
     * @param  aText     string to be printed
     * @param  aTextBox  the rectangle, within which the string must lie. If the string doesn't fit within the rectangle it will be broken.
     * @param  aColor    attributes of the string
     */ 
    public static void printString(String aText, Rectangle aTextBox,
             CharColor aColor) {

        printString(aText, aTextBox.getLeft(), aTextBox.getTop(),
                 aTextBox.getWidth(), aTextBox.getHeight(), aColor);

    }

    /**
     * The method prints a string on the screen. If the string doesn't fit within the rectangle bounds, it wiil be broken.
     *
     *
     * @param  aText   string to be printed
     * @param  aX      the x coordinate of the string start point
     * @param  aY      the y coordinate of the string start point
     * @param  aWide   the width of bounds rectangle
     * @param  aHigh   the width of bounds rectangle
     * @param  aColor  color attributes of the string
     */ 
    public static void printString(String aText, int aX, int aY, int aWide,
             int aHigh, CharColor aColor) {

        printString(aText, aX, aY, aWide, aHigh, getCurrentClipRectangle(),
                 aColor);

    }

    /**
     *  Description of the Method
     *
     * The method prints a string on the screen constrained within a clipping
     * rectangle. If the string doesn't fit within the rectangle bounds, it
     * wiil be line-broken.
     *
     *
     * @param  aText   string to be printed
     * @param  aX      the x coordinate of the string start point
     * @param  aY      the y coordinate of the string start point
     * @param  aWide   the width of bounds rectangle
     * @param  aHigh   the width of bounds rectangle
     * @param  aClip   clipping rectangle
     * @param  aColor  color attributes of the string
     */ 
    public static void printString(String aText, int aX, int aY, int aWide,
             int aHigh, Rectangle aClip, CharColor aColor) {

        int mX = (aClip == null) ? aX : Math.max(aX, aClip.getLeft());
        int mY = (aClip == null) ? aY : Math.max(aY, aClip.getTop());

		List mLines = TextUtils.breakLines(aText, aWide);
        int mFirstLine = mY - aY;
        // clip this many lines at the top
 
        int mWide = (aClip == null) ? aWide : Math.min(aWide,
                 aClip.getRight() - mX + 1);

        int mHigh = (aClip == null) ? aHigh : Math.min(aHigh,
                 aClip.getBottom() - mY + 1);

        mHigh = Math.min(mHigh, mLines.size() - mFirstLine);
        // adjust height of box to max number of lines
 
        if (mWide > 0 && mHigh > 0) {
            int mOffset = mX - aX;
            // clip this many leftmost characters per line
            for (int mIdx = 0; mIdx < mHigh; mIdx++) {
                String mLine = (String) mLines.get(mIdx + mFirstLine);
                if (mLine.length() > mOffset) {
                    printStringNoClip(mLine.substring(mOffset), mX, mY + mIdx,
                             mWide, 1, aColor);

                }
            }
        }
    }

    /**
     * The method prints a string on the screen
     *
     *
     * @param  text   string to be printed
     * @param  x      the x coordinate of the string start point
     * @param  y      the y coordinate of the string start point
     * @param  color  color attributes of the string
     */ 
    public static void printString(String text, int x, int y, CharColor color) {
        printString(text, x, y, text.length(), 1, color);
    }

    /**
     * Target for character read synchronizing
     */ 
    private static Integer readSync = new Integer(0);

    /**
     * The method reads the next code (ascii or control ) from an input stream an wraps it into an instance of {@link jcurses.system.InputChar}
     *
     *
     * @return    the next read code
     */ 
    public static InputChar readCharacter() {
        synchronized (readSync) {
            int mChar = readByte();

            // handle escape sequences
            if (mChar == 0x1b) {
                mChar = readByte();
                if (mChar == - 1) {
                    mChar = 0x1b;
                } else {
                    mChar += 1000;
                }
            }

            if (mChar == - 1) {
                return null;
            }

            return new InputChar(mChar);
        }

    }

    /**
     * The method shuts down the jcurses library and recovers the terminal to the state before jcurses application start.
     */ 
    public static synchronized native void shutdown();

    /**
     * The method starts a new painting action, containing possible many painting operations After a call of this method endPainting must be already called, to
     * refersh the screen.
     */ 
    public static synchronized native void startPainting();

    /**
     * Removes the previously set clip rectangle.
     */ 
    public static void unsetClipRectangle() {
        ArrayList clips = (ArrayList) __clips.get(Thread.currentThread());

        if (clips == null) {
            return;
        }

        if (clips.size() > 0) {
            clips.remove(clips.size() - 1);
        }

        if (clips.size() == 0) {
            __clips.remove(Thread.currentThread());
        }
    }

    /**
     *  Gets the basicColors attribute of the Toolkit class
     *
     * @return    The basicColors value
     */ 
    static short []getBasicColors() {
        return __basicColors;
    }

    /**
     *  Gets the specialKeyCode attribute of the Toolkit class
     *
     * @param  code  Description of the Parameter
     * @return       The specialKeyCode value
     */ 
    static synchronized native int getSpecialKeyCode(int code);

    /**
     *  Gets the colorPairNo attribute of the Toolkit class
     *
     * @param  aColor  Description of the Parameter
     * @return         The colorPairNo value
     */ 
    static short getColorPairNo(CharColor aColor) {
        short number = __colorpairs[aColor.getBackground()][aColor.getForeground()];
        if (number == - 1) {
            number = ++__maxColorPairNumber;
            __colorpairs[aColor.getBackground()][aColor.getForeground()] = number;
            initColorPair(mapBasicColor(aColor.getBackground()),
                     mapBasicColor(aColor.getForeground()), number);

            //System.err.println("NEW COLOR: [" + aColor.toString() + "] = [" + number + "] {" + mapBasicColor(aColor.getBackground()) + "} {" +
            // mapBasicColor(aColor.getForeground()) + "}");
        }

        //System.err.println("USE COLOR: [" + aColor.toString() + "] = [" + number + "]");
 
        return number;
    }

    /**
     *  Description of the Method
     *
     * @param  aColor  Description of the Parameter
     * @return         Description of the Return Value
     */ 
    static short mapBasicColor(short aColor) {
        return __basicColors[aColor];
    }

    /**
     *  Description of the Method
     *
     * @param  aAttr  Description of the Parameter
     * @return        Description of the Return Value
     */ 
    static long mapAttribute(short aAttr) {
        return __attributes[aAttr];
    }

    /**
     *  Gets the currentClipRectangle attribute of the Toolkit class
     *
     * @return    The currentClipRectangle value
     */ 
    private static Rectangle getCurrentClipRectangle() {
        ArrayList clips = (ArrayList) __clips.get(Thread.currentThread());

        if ((clips == null) || (clips.size() == 0)) {
            return null;
        }

        Rectangle result = (Rectangle) clips.get(0);

        for (int i = 1; i < clips.size(); i++) {
            Rectangle temp = (Rectangle) clips.get(i);
            result = result.intersection(temp);

            if (result.isEmpty()) {
                return result;
            }
        }

        return result;
    }

    /**
     *  Gets the windows attribute of the Toolkit class
     *
     * @return    The windows value
     */ 
    private static boolean isWindows() {
        return (java.io.File.separatorChar == '\\');
    }

    /**
     *  Gets the between attribute of the Toolkit class
     *
     * @param  aValue  Description of the Parameter
     * @param  aStart  Description of the Parameter
     * @param  aEnd    Description of the Parameter
     * @return         The between value
     */ 
    private static boolean isBetween(int aValue, int aStart, int aEnd) {
        if (aStart > aEnd) {
            int aTmp = aStart;
            aStart = aEnd;
            aEnd = aTmp;
        }
        return ((aStart <= aValue) && (aEnd >= aValue));
    }

    /**
     *  Description of the Method
     *
     * @param  x                Description of the Parameter
     * @param  y                Description of the Parameter
     * @param  width            Description of the Parameter
     * @param  height           Description of the Parameter
     * @param  colorPairNumber  Description of the Parameter
     * @param  attr             Description of the Parameter
     */ 
    private static synchronized native void changeColors(int x, int y,
             int width, int height, short colorPairNumber, long attr);


    /**
     *  Description of the Method
     *
     * @param  colorPairNumber  Description of the Parameter
     * @param  attributes       Description of the Parameter
     */ 
    private static synchronized native void clearScreen(short colorPairNumber,
             long attributes);


    //private static native int computeChtype(short number);
 
    /**
     *  Description of the Method
     *
     * @param  startX           Description of the Parameter
     * @param  startY           Description of the Parameter
     * @param  endY             Description of the Parameter
     * @param  colorPairNumber  Description of the Parameter
     * @param  attr             Description of the Parameter
     */ 
    private static synchronized native void drawHorizontalLine(int startX,
             int startY, int endY, short colorPairNumber, long attr);


    /**
     *  Description of the Method
     *
     * @param  startX           Description of the Parameter
     * @param  startY           Description of the Parameter
     * @param  endX             Description of the Parameter
     * @param  colorPairNumber  Description of the Parameter
     * @param  attr             Description of the Parameter
     */ 
    private static synchronized native void drawHorizontalThickLine(int startX,
             int startY, int endX, short colorPairNumber, long attr);


    /**
     *  Description of the Method
     *
     * @param  x                Description of the Parameter
     * @param  y                Description of the Parameter
     * @param  width            Description of the Parameter
     * @param  height           Description of the Parameter
     * @param  colorPairNumber  Description of the Parameter
     * @param  attribute        Description of the Parameter
     */ 
    private static synchronized native void drawRectangle(int x, int y,
             int width, int height, short colorPairNumber, long attribute);


    /**
     *  Description of the Method
     *
     * @param  startX           Description of the Parameter
     * @param  startY           Description of the Parameter
     * @param  endX             Description of the Parameter
     * @param  colorPairNumber  Description of the Parameter
     * @param  attr             Description of the Parameter
     */ 
    private static synchronized native void drawVerticalLine(int startX,
             int startY, int endX, short colorPairNumber, long attr);


    /**
     *  Description of the Method
     *
     * @param  startX           Description of the Parameter
     * @param  startY           Description of the Parameter
     * @param  endY             Description of the Parameter
     * @param  colorPairNumber  Description of the Parameter
     * @param  attr             Description of the Parameter
     */ 
    private static synchronized native void drawVerticalThickLine(int startX,
             int startY, int endY, short colorPairNumber, long attr);


    /**
     *  Description of the Method
     *
     * @param  attributes  Description of the Parameter
     */ 
    private static synchronized native void fillAttributes(long [] attributes);

    /**
     *  Description of the Method
     *
     * @param  basicColors  Description of the Parameter
     */ 
    private static synchronized native void fillBasicColors(short [] basicColors);

    /**
     *  Description of the Method
     */ 
    private static void fillColorPairs() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                __colorpairs[i][j] = - 1;
            }
        }
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */ 
    private static synchronized native int hasColorsAsInteger();

    /**
     *  Description of the Method
     *
     * @param  aNumber  Description of the Parameter
     * @param  aRed     Description of the Parameter
     * @param  aGreen   Description of the Parameter
     * @param  aBlue    Description of the Parameter
     */ 
    public static void adjustBaseColor(short aNumber, int aRed, int aGreen,
             int aBlue) {

        adjustColor(__basicColors[aNumber], (short) aRed, (short) aGreen,
                 (short) aBlue);

    }

    /**
     *  Description of the Method
     *
     * @param  aNumber  Description of the Parameter
     * @param  aRed     Description of the Parameter
     * @param  aGreen   Description of the Parameter
     * @param  aBlue    Description of the Parameter
     */ 
    private static synchronized native void adjustColor(short aNumber,
             short aRed, short aGreen, short aBlue);


    /**
     *  Description of the Method
     *
     * @param  background  Description of the Parameter
     * @param  foreground  Description of the Parameter
     * @param  number      Description of the Parameter
     */ 
    private static synchronized native void initColorPair(short background,
             short foreground, short number);


    /**
     *  Description of the Method
     */ 
    private static void initEncoding() {
        if (isWindows()) {
            setEncoding("CP850");
        }
    }

    //  private static void loadLibrary()
    //  {
    /*
     *  System.out.println(System.mapLibraryName(LIBRARY_NAME));
     *  System.out.println(System.getProperty("java.library.path"));
     */ 
    // System.loadLibrary(LIBRARY_NAME);
    /*
     *  String pathSeparator = System.getProperty("path.separator");
     *  String searchPaths = System.getProperty("java.library.path") + pathSeparator + System.getProperty("java.class.path");
     *  String[] paths = searchPaths.split(pathSeparator);
     *  String mPlatformName = System.mapLibraryName(LIBRARY_NAME);
     *  / get the list of unique directories to check
     *  for ( int idx = 0; idx < paths.length; idx++ )
     *  {
     *  String mPath = paths[idx].toString().trim();
     *  if ( mPath.startsWith(JAR_RESOURCE) )
     *  mPath = mPath.substring(JAR_RESOURCE.length());
     *  else if ( mPath.startsWith(FILE_RESOURCE) )
     *  mPath = mPath.substring(FILE_RESOURCE.length());
     *  File mFile = new File(mPath).getAbsoluteFile();
     *  if ( mFile.isFile() )
     *  mFile = mFile.getParentFile();
     *  if ( mFile.exists() && mFile.isDirectory() )
     *  {
     *  if ( mPlatformName.indexOf('*') == - 1 )
     *  mFile = new File(mFile, mPlatformName);
     *  else
     *  {
     *  int mPos = mPlatformName.indexOf('*');
     *  String mStart = mPlatformName.substring(0, mPos);
     *  String mEnd = mPlatformName.substring(mPos + 1);
     *  File mCandidate = null;
     *  / use wildcard in search
     *  File mFiles[] = mFile.listFiles();
     *  for ( int mIdx = 0; mIdx < mFiles.length; mIdx++ )
     *  if ( mFiles[mIdx].getName().startsWith(mStart) && mFiles[mIdx].getName().endsWith(mEnd) )
     *  if ( mCandidate == null || mFiles[mIdx].getName().compareTo(mCandidate.getName()) > 0 )
     *  mCandidate = mFiles[mIdx];
     *  mFile = mCandidate;
     *  }
     *  if ( mFile != null && mFile.exists() && mFile.isFile() )
     *  {
     *  try
     *  {
     *  /System.err.println("Loading Libary: [" + mFile.getAbsolutePath() + "]");
     *  System.load(mFile.getAbsolutePath());
     *  return;
     *  }
     *  catch (Throwable t)
     *  {
     *  throw new RuntimeException("Native Library " + LIBRARY_NAME + " (" + mFile.getAbsolutePath() + ") could not be loaded. (" + t.getMessage() + ")");
     *  }
     *  }
     *  }
     *  }
     *  throw new RuntimeException("Native Library " + LIBRARY_NAME + " (" + mPlatformName + ") could not be found in the library path or class path.");
     */ 
    //  }

    /**
     *  Description of the Method
     *
     * @param  chars            Description of the Parameter
     * @param  x                Description of the Parameter
     * @param  y                Description of the Parameter
     * @param  width            Description of the Parameter
     * @param  height           Description of the Parameter
     * @param  colorPairNumber  Description of the Parameter
     * @param  attr             Description of the Parameter
     */ 
    private static synchronized native void printString(byte [] chars, int x,
             int y, int width, int height, short colorPairNumber, long attr);


    /**
     *  Description of the Method
     *
     * @param  aText    Description of the Parameter
     * @param  aX       Description of the Parameter
     * @param  aY       Description of the Parameter
     * @param  aWidth   Description of the Parameter
     * @param  aHeight  Description of the Parameter
     * @param  aColor   Description of the Parameter
     */ 
    private static void printStringNoClip(String aText, int aX, int aY,
             int aWidth, int aHeight, CharColor aColor) {

        printString(encodeChars(aText), aX, aY, aWidth, aHeight,
                 aColor.getPairNo(), aColor.getAttribute());

    }

    /**
     *  Description of the Method
     *
     * @param  aText  Description of the Parameter
     * @return        Description of the Return Value
     */ 
    private static byte []encodeChars(String aText) {
        try {
            if (__encoding != null) {
                return aText.getBytes(__encoding);
            }
        } catch (UnsupportedEncodingException e) {
            __encoding = null;
        }
        return aText.getBytes();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */ 
    private static native int readByte();

    /**
     *  Gets the screen attribute of the Toolkit class
     *
     * @return    The screen value
     */ 
    public static Rectangle getScreen() {
        return new Rectangle(0, 0, getScreenWidth(), getScreenHeight());
    }
}
