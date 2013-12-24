#include "include/jcurses_system_Toolkit.h"
#include <curses.h>
#include <stdio.h>
#include <string.h>

//define JCURSES_ATTRIBUTES(number,att) (has_colors())?(att|COLOR_PAIR(number+1)):((number==0)?A_NORMAL:((number==1)?A_REVERSE:A_BOLD))
#define JCURSES_ATTRIBUTES(number,att) (att|COLOR_PAIR(number+1))

FILE * logStream = NULL;

void initLog()
{
    logStream = fopen("njcurses.log","a");
    fprintf(logStream, "native logging initialized!\n");
    fflush(logStream);
}

static int buffer = 1;
static int paintingAction = 0;

void endPainting()
{
  if(paintingAction <= 0)
    doupdate();
}

void fill_region(int aX, int aY, int aWide, int aHigh, jshort aNumber, jlong aAttr, chtype aCh)
{
    if( aHigh > 0 && aWide > 0 && aX >= 0 && aY >= 0 )
    {
        attrset(JCURSES_ATTRIBUTES(aNumber,aAttr));

        int mY, mX;
        for(mY = aY; mY < (aY + aHigh); mY++)
        {
            move(mY, aX);
            for(mX = aX; mX < (aX + aWide); mX++)
            {
		if(buffer)
              	  addch(aCh);
		else
		{
                  move(mY, mX);
            	  echochar(aCh);
		}
	    }
        }

    }
    endPainting();
}

/*
void clear_region(int aX, int aY, int aWide, int aHigh)
{
    fill_region(aX, aY, aWide, aHigh, number, attr, ' ');
}

void fill_region2(int aX, int aY, int aWide, int aHigh, jshort aNumber, jlong aAttr, chtype aCh)
{
    if( aHigh > 0 && aWide > 0 && aX >= 0 && aY >= 0 )
    {
        int mPos;
        aCh |= JCURSES_ATTRIBUTES(aNumber,aAttr);
        chtype line[aWide];
        //memset(line, sizeof(line), 0);

        for(mPos = 0; mPos < aWide; mPos++)
            line[mPos] = aCh;

        for(mPos = aY; mPos < (aY + aHigh); mPos++)
        {
            mvaddchnstr(mPos, aX, &line[0], aWide);
	    if(!buffer)
              refresh();
        }
    }
    endPainting();
}
*/


jint computeChtype(jshort number)
{
    /* Hardcoded NORMAL is fine here - computeChtype only used for pair */
    return JCURSES_ATTRIBUTES(number,A_NORMAL); 
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_adjustColor (JNIEnv *aEnv, jclass aClass, jshort aColorNo, jshort aRed, jshort aGreen, jshort aBlue)
{
    init_color(aColorNo, aRed, aGreen, aBlue);
}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_getScreenWidth (JNIEnv * env, jclass class)
{
    return COLS;
}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_getScreenHeight (JNIEnv * env, jclass class)
{
    return LINES;
}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_hasColorsAsInteger (JNIEnv * env, jclass class)
{
    return has_colors();
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_fillAttributes (JNIEnv * env , jclass class, jlongArray attributes)
{
    jlong attrs[3];
    attrs[0] = A_NORMAL;
    attrs[1] = A_REVERSE;
    attrs[2] = A_BOLD;
    (*env)->SetLongArrayRegion(env,attributes,0,3,attrs);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_fillBasicColors (JNIEnv * env , jclass class, jshortArray basicColors)
{
    short colors[8];

    colors[0] = COLOR_BLACK;
    colors[1] = COLOR_RED;
    colors[2] = COLOR_GREEN;
    colors[3] = COLOR_YELLOW;
    colors[4] = COLOR_BLUE;
    colors[5] = COLOR_MAGENTA;
    colors[6] = COLOR_CYAN;
    colors[7] = COLOR_WHITE;
    (*env)->SetShortArrayRegion(env,basicColors,0,8,colors);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_initColorPair (JNIEnv * env, jclass class, jshort background, jshort foreground, jshort number)
{
    init_pair(number+1,foreground, background);
}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_computeChtype (JNIEnv * env, jclass class, jshort number)
{
    return computeChtype(number);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_init (JNIEnv * env, jclass class)
{
    initLog();
    initscr();  
    keypad(stdscr, TRUE);
    noecho();
    halfdelay(2);
    curs_set(0);
    if (has_colors())
        start_color();
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_clearScreen (JNIEnv * env, jclass class, jshort number, jlong attr)
{
    fill_region(0, 0, COLS, LINES, number, attr, ' ' /* getbkgd(stdscr) */);
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawRectangle (JNIEnv * env , jclass class, jint x, jint y, jint width, jint height, jshort number, jlong attr)
{
    fill_region(x, y, width, height, number, attr, ' ' /* getbkgd(stdscr) */);
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_shutdown (JNIEnv * env, jclass class)
{
    endwin();
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawHorizontalLine (JNIEnv * env, jclass class, jint aX, jint aY ,jint aEndX, jshort aNumber, jlong aAttr)
{
    //mvhline(aY, aX, ACS_HLINE, aEndX-aX+1);
    fill_region(aX, aY, aEndX-aX+1, 1, aNumber, aAttr, ACS_HLINE);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawVerticalLine (JNIEnv * env, jclass class, jint aX, jint aY ,jint aEndY, jshort aNumber, jlong aAttr)
{
    //mvvline(aY, aX, ACS_VLINE, aEndY-aY+1);
    fill_region(aX, aY, 1, aEndY-aY+1, aNumber, aAttr, ACS_VLINE);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawHorizontalThickLine (JNIEnv * env, jclass class, jint aX, jint aY ,jint aEndX, jshort aNumber, jlong aAttr)
{
    //mvhline(aY, aX, ACS_CKBOARD, aEndX-aX+1);
    fill_region(aX, aY, aEndX-aX+1, 1, aNumber, aAttr, ACS_CKBOARD);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawVerticalThickLine (JNIEnv * env, jclass class, jint aX, jint aY ,jint aEndY, jshort aNumber, jlong aAttr)
{
    //mvvline(aY, aX, ACS_CKBOARD, aEndY-aY+1);
    fill_region(aX, aY, 1, aEndY-aY+1, aNumber, aAttr, ACS_CKBOARD);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawCorner (JNIEnv *env, jclass class, jint aX, jint aY, jint aPos, jshort number, jlong attr)
{
    chtype ch;

    switch( aPos )
    {
    case jcurses_system_Toolkit_LL_CORNER:
        ch = ACS_LLCORNER;
        break;

    case jcurses_system_Toolkit_LR_CORNER:
        ch = ACS_LRCORNER;
        break;

    case jcurses_system_Toolkit_UL_CORNER:
        ch = ACS_ULCORNER;
        break;

    case jcurses_system_Toolkit_UR_CORNER:
        ch = ACS_URCORNER;
        break;

    default:
        return;
    }

    attrset(JCURSES_ATTRIBUTES(number,attr));
    move(aY, aX);
	
    if(buffer)
      addch(ch);
    else
      echochar(ch);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_printString (JNIEnv * env, jclass class, jbyteArray bytes, jint x, jint y, jint width, jint height, jshort number, jlong attr)
{
    int j=0;
    int xpos,ypos;
    int length = (*env)->GetArrayLength(env,bytes);
    unsigned char c;
    unsigned char * charArray = (*env)->GetByteArrayElements(env, bytes, NULL);

    attrset(JCURSES_ATTRIBUTES(number,attr));

    move(y, x);
    xpos=x-1;
    ypos=y;

    for( j=0; j<length; j++ )
    {
        c = charArray[j];

        if( c =='\r' )
            continue;

        xpos++;
        if( (xpos >= x+width) || (c=='\n') )
        {
            xpos = x;
            ypos++;
            move(ypos, xpos);
            if( ypos == y+height )
                break;
        }

        if( c == '\n' )
            continue;

        if( c == '\t' )
            c = ' ';

	if(buffer)
          addch(c);
	else
          echochar(c);
    }

    endPainting();
}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_readByte (JNIEnv * env, jclass class)
{
    return wgetch(stdscr);
}


/*
int keycodes[2][] = 
{
    { 0401, KEY_MIN },
    { 0402, KEY_DOWN },
    { 0403, KEY_UP },
    { 0404, KEY_LEFT },
    { 0405, KEY_RIGHT },
    { 0406, KEY_HOME },
    { 0407, KEY_BACKSPACE },
    { 0410, KEY_F0 },
    { 0411, KEY_F(1) },
    { 0412, KEY_F(2) },
    { 0413, KEY_F(3) },
    { 0414, KEY_F(4) },
    { 0415, KEY_F(5) },
    { 0416, KEY_F(6) },
    { 0417, KEY_F(7) },
    { 0420, KEY_F(010) },
    { 0421, KEY_F(011) },
    { 0422, KEY_F(012) },
    { 0423, KEY_F(013) },
    { 0424, KEY_F(014) },
    { 0510, KEY_DL },
    { 0511, KEY_IL },
    { 0512, KEY_DC },
    { 0513, KEY_IC },
    { 0514, KEY_EIC },
    { 0515, KEY_CLEAR },
    { 0516, KEY_EOS },
    { 0517, KEY_EOL },
    { 0520, KEY_SF },
    { 0521, KEY_SR },
    { 0522, KEY_NPAGE },
    { 0523, KEY_PPAGE },
    { 0524, KEY_STAB },
    { 0525, KEY_CTAB },
    { 0526, KEY_CATAB },
    { 0527, KEY_ENTER },
    { 0530, KEY_SRESET },
    { 0531, KEY_RESET },
    { 0532, KEY_PRINT },
    { 0533, KEY_LL },
    { 0534, KEY_A1 },
    { 0535, KEY_A3 },
    { 0536, KEY_B2 },
    { 0537, KEY_C1 },
    { 0540, KEY_C3 },
    { 0541, KEY_BTAB },
    { 0542, KEY_BEG },
    { 0543, KEY_CANCEL },
    { 0544, KEY_CLOSE },
    { 0545, KEY_COMMAND },
    { 0546, KEY_COPY },
    { 0547, KEY_CREATE },
    { 0550, KEY_END },
    { 0551, KEY_EXIT },
    { 0552, KEY_FIND },
    { 0553, KEY_HELP },
    { 0554, KEY_MARK },
    { 0555, KEY_MESSAGE },
    { 0556, KEY_MOVE },
    { 0557, KEY_NEXT },
    { 0560, KEY_OPEN },
    { 0561, KEY_OPTIONS },
    { 0562, KEY_PREVIOUS },
    { 0563, KEY_REDO },
    { 0564, KEY_REFERENCE },
    { 0565, KEY_REFRESH },
    { 0566, KEY_REPLACE },
    { 0567, KEY_RESTART },
    { 0570, KEY_RESUME },
    { 0571, KEY_SAVE },
    { 0572, KEY_SBEG },
    { 0573, KEY_SCANCEL },
    { 0574, KEY_SCOMMAND },
    { 0575, KEY_SCOPY },
    { 0576, KEY_SCREATE },
    { 0577, KEY_SDC },
    { 0600, KEY_SDL },
    { 0601, KEY_SELECT },
    { 0602, KEY_SEND },
    { 0603, KEY_SEOL },
    { 0604, KEY_SEXIT },
    { 0605, KEY_SFIND },
    { 0606, KEY_SHELP },
    { 0607, KEY_SHOME },
    { 0610, KEY_SIC },
    { 0611, KEY_SLEFT },
    { 0612, KEY_SMESSAGE },
    { 0613, KEY_SMOVE },
    { 0614, KEY_SNEXT },
    { 0615, KEY_SOPTIONS },
    { 0616, KEY_SPREVIOUS },
    { 0617, KEY_SPRINT },
    { 0620, KEY_SREDO },
    { 0621, KEY_SRIGHT },
    { 0622, KEY_SREPLACE },
    { 0623, KEY_SRSUME },
    { 0624, KEY_SSAVE },
    { 0625, KEY_SSUSPEND },
    { 0626, KEY_SUNDO },
    { 0627, KEY_SUSPEND },
    { 0630, KEY_UNDO },
    { 0777, KEY_MAX }
}
*/


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_getSpecialKeyCode (JNIEnv * env, jclass class, jint code)
{
    int result = KEY_MAX; 
    switch( code )
    {
    case 0401:
        result = KEY_MIN;
        break;
    case 0402:
        result = KEY_DOWN;
        break;
    case 0403:
        result = KEY_UP;
        break;
    case 0404:
        result = KEY_LEFT;
        break;
    case 0405:
        result = KEY_RIGHT;
        break;
    case 0406:
        result = KEY_HOME;
        break;
    case 0407:
        result = KEY_BACKSPACE;
        break;
    case 0410:
        result = KEY_F0;
        break;
    case 0411:
        result = KEY_F(1);
        break;
    case 0412:
        result = KEY_F(2);
        break;
    case 0413:
        result = KEY_F(3);
        break;
    case 0414:
        result = KEY_F(4);
        break;
    case 0415:
        result = KEY_F(5);
        break;
    case 0416:
        result = KEY_F(6);
        break;
    case 0417:
        result = KEY_F(7);
        break;
    case 0420:
        result = KEY_F(010);
        break;
    case 0421:
        result = KEY_F(011);
        break;
    case 0422:
        result = KEY_F(012);
        break;
    case 0423:
        result = KEY_F(013);
        break;
    case 0424:
        result = KEY_F(014);
        break;

    case 0510:
        result = KEY_DL;
        break;
    case 0511:
        result = KEY_IL;
        break;
    case 0512:
        result = KEY_DC;
        break;
    case 0513:
        result = KEY_IC;
        break;
    case 0514:
        result = KEY_EIC;
        break;
    case 0515:
        result = KEY_CLEAR;
        break;
    case 0516:
        result = KEY_EOS;
        break;
    case 0517:
        result = KEY_EOL;
        break;

    case 0520:
        result = KEY_SF;
        break;
    case 0521:
        result = KEY_SR;
        break;
    case 0522:
        result = KEY_NPAGE;
        break;
    case 0523:
        result = KEY_PPAGE;
        break;
    case 0524:
        result = KEY_STAB;
        break;
    case 0525:
        result = KEY_CTAB;
        break;
    case 0526:
        result = KEY_CATAB;
        break;
    case 0527:
        result = KEY_ENTER;
        break;

    case 0530:
        result = KEY_SRESET;
        break;
    case 0531:
        result = KEY_RESET;
        break;
    case 0532:
        result = KEY_PRINT;
        break;
    case 0533:
        result = KEY_LL;
        break;
    case 0534:
        result = KEY_A1;
        break;
    case 0535:
        result = KEY_A3;
        break;
    case 0536:
        result = KEY_B2;
        break;
    case 0537:
        result = KEY_C1;
        break;


    case 0540:
        result = KEY_C3;
        break;
    case 0541:
        result = KEY_BTAB;
        break;
    case 0542:
        result = KEY_BEG;
        break;
    case 0543:
        result = KEY_CANCEL;
        break;
    case 0544:
        result = KEY_CLOSE;
        break;
    case 0545:
        result = KEY_COMMAND;
        break;
    case 0546:
        result = KEY_COPY;
        break;
    case 0547:
        result = KEY_CREATE;
        break;

    case 0550:
        result = KEY_END;
        break;
    case 0551:
        result = KEY_EXIT;
        break;
    case 0552:
        result = KEY_FIND;
        break;
    case 0553:
        result = KEY_HELP;
        break;
    case 0554:
        result = KEY_MARK;
        break;
    case 0555:
        result = KEY_MESSAGE;
        break;
    case 0556:
        result = KEY_MOVE;
        break;
    case 0557:
        result = KEY_NEXT;
        break;

    case 0560:
        result = KEY_OPEN;
        break;
    case 0561:
        result = KEY_OPTIONS;
        break;
    case 0562:
        result = KEY_PREVIOUS;
        break;
    case 0563:
        result = KEY_REDO;
        break;
    case 0564:
        result = KEY_REFERENCE;
        break;
    case 0565:
        result = KEY_REFRESH;
        break;
    case 0566:
        result = KEY_REPLACE;
        break;
    case 0567:
        result = KEY_RESTART;
        break;

    case 0570:
        result = KEY_RESUME;
        break;
    case 0571:
        result = KEY_SAVE;
        break;
    case 0572:
        result = KEY_SBEG;
        break;
    case 0573:
        result = KEY_SCANCEL;
        break;
    case 0574:
        result = KEY_SCOMMAND;
        break;
    case 0575:
        result = KEY_SCOPY;
        break;
    case 0576:
        result = KEY_SCREATE;
        break;
    case 0577:
        result = KEY_SDC;
        break;

    case 0600:
        result = KEY_SDL;
        break;
    case 0601:
        result = KEY_SELECT;
        break;
    case 0602:
        result = KEY_SEND;
        break;
    case 0603:
        result = KEY_SEOL;
        break;
    case 0604:
        result = KEY_SEXIT;
        break;
    case 0605:
        result = KEY_SFIND;
        break;
    case 0606:
        result = KEY_SHELP;
        break;
    case 0607:
        result = KEY_SHOME;
        break;

    case 0610:
        result = KEY_SIC;
        break;
    case 0611:
        result = KEY_SLEFT;
        break;
    case 0612:
        result = KEY_SMESSAGE;
        break;
    case 0613:
        result = KEY_SMOVE;
        break;
    case 0614:
        result = KEY_SNEXT;
        break;
    case 0615:
        result = KEY_SOPTIONS;
        break;
    case 0616:
        result = KEY_SPREVIOUS;
        break;
    case 0617:
        result = KEY_SPRINT;
        break;

    case 0620:
        result = KEY_SREDO;
        break;
    case 0621:
        result = KEY_SRIGHT;
        break;
    case 0622:
        result = KEY_SREPLACE;
        break;
    case 0623:
        result = KEY_SRSUME;
        break;
    case 0624:
        result = KEY_SSAVE;
        break;
    case 0625:
        result = KEY_SSUSPEND;
        break;
    case 0626:
        result = KEY_SUNDO;
        break;
    case 0627:
        result = KEY_SUSPEND;
        break;
    case 0630:
        result = KEY_UNDO;
        break;
    case 777:
        result = KEY_MAX;
        break;
    default:
        result = KEY_MIN;
    }

    return result;
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_changeColors (JNIEnv *env, jclass clazz, jint x, jint y, jint width, jint height, jshort colorpair, jlong attr)
{
    int i,j;
    chtype currentChar = 0;
    attrset(JCURSES_ATTRIBUTES(colorpair,attr));
    for( i=0; i<width; i++ )
    {
        for( j=0;j<height; j++ )
        {
            currentChar = (mvinch(y+j,x+i) & A_CHARTEXT);
	    move(y, x);
      	    if(buffer)
              addch(currentChar);
	    else
              echochar(currentChar);
        }
    }
    endPainting();
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_startPainting (JNIEnv * env, jclass clazz)
{
    paintingAction++;
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_endPainting (JNIEnv * env , jclass clazz)
{
    if( paintingAction > 0 )
      paintingAction--;

    endPainting();
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_beep (JNIEnv * env, jclass clazz)
{
    beep();
}
