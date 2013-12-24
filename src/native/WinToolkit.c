#include "include/jcurses_system_Toolkit.h"
#include <conio.h>
#include <stdio.h>
#include <windows.h>

typedef struct  {
	jshort background;
	jshort foreground;
} color_pair;

FILE * logStream = NULL;

void initLog() {
	/*logStream = fopen("jcurses2.log","a");
	fprintf(logStream, "native logging initialized!\n");
	fflush(logStream);*/
}


#define ACS_HLINE 196
#define ACS_VLINE 179
#define ACS_LLCORNER 192
#define ACS_LRCORNER 217
#define ACS_ULCORNER 218
#define ACS_URCORNER 191
#define ACS_CHESSBOARD 178

#define KEY_DOWN        0402            /* Down-arrow */
#define KEY_UP          0403  	/* Up-arrow */
#define KEY_LEFT        0404		/* Left-arrow */
#define KEY_RIGHT       0405            /* Right-arrow */
#define KEY_HOME        0406            /* Right-arrow */
#define KEY_BACKSPACE   0407            /* Backspace (unreliable) */
#define KEY_F1          0411            /* Function keys.  Space for 64 */
#define KEY_F2        	0412    		/* Function keys */
#define KEY_F3        	0413    		/* Function keys */
#define KEY_F4        	0414 		/* Function keys */
#define KEY_F5        	0415    		/* Function keys */
#define KEY_F6        	0416    		/* Function keys */
#define KEY_F7        	0417    		/* Function keys */
#define KEY_F8        	0420    		/* Function keys */
#define KEY_F9        	0421    		/* Function keys */
#define KEY_F10        	0422    		/* Function keys */
#define KEY_F11        	0423    		/* Function keys */
#define KEY_F12        	0424    		/* Function keys */
#define KEY_DC          0512            /* Delete character */
#define KEY_IC          0513            /* Insert char or enter insert mode */
#define KEY_NPAGE       0522            /* Next page */
#define KEY_PPAGE       0523            /* Previous page */
#define KEY_PRINT       0532            /* Print */
#define KEY_END			0550	      /* End */


#define JCURSES_BLACK 	0
#define JCURSES_WHITE 	1
#define JCURSES_RED 	2
#define JCURSES_GREEN 	3
#define JCURSES_BLUE 	4
#define JCURSES_YELLOW 	5
#define JCURSES_MAGENTA 6
#define JCURSES_CYAN 	7

#define JCURSES_NORMAL 0
#define JCURSES_BOLD 1
#define JCURSES_REVERSE 2


PCONSOLE_SCREEN_BUFFER_INFO info;
SHORT COLS=0;
SHORT LINES=0;
HANDLE inputHandle;
HANDLE outputHandle;
WORD lastMode;



JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_getScreenWidth (JNIEnv * env, jclass class) {
	return COLS;
}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_getScreenHeight (JNIEnv * env, jclass class) {
	return LINES;
}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_hasColorsAsInteger (JNIEnv * env, jclass class) {
	return 1;
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_fillAttributes (JNIEnv * env , jclass class, jlongArray attributes) {
	jlong attrs[3];
	attrs[0] = JCURSES_NORMAL;
	attrs[1] = JCURSES_REVERSE;
	attrs[2] = JCURSES_BOLD;
	(*env)->SetLongArrayRegion(env,attributes,0,3,attrs);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_fillBasicColors (JNIEnv * env , jclass class, jshortArray basicColors) {
	short colors[8];
	colors[0] = JCURSES_BLACK;
	colors[1] = JCURSES_RED;
	colors[2] = JCURSES_GREEN;
	colors[3] = JCURSES_YELLOW;
	colors[4] = JCURSES_BLUE;
	colors[5] = JCURSES_MAGENTA;
	colors[6] = JCURSES_CYAN;
	colors[7] = JCURSES_WHITE;
	(*env)->SetShortArrayRegion(env,basicColors,0,8,colors);

}





WORD getForegroundColor(int color) {
	WORD result;
	switch(color) {
		case JCURSES_BLACK:
			result = 0;
			break;
		case JCURSES_WHITE:
			result = FOREGROUND_BLUE|FOREGROUND_RED|FOREGROUND_GREEN;
			break;
		case JCURSES_BLUE:
			result = FOREGROUND_BLUE;
			break;
		case JCURSES_GREEN:
			result = FOREGROUND_GREEN;
			break;
		case JCURSES_RED:
			result = FOREGROUND_RED;
			break;
		case JCURSES_YELLOW:
			result = FOREGROUND_RED|FOREGROUND_GREEN;
			break;
		case JCURSES_MAGENTA:
			result = FOREGROUND_RED|FOREGROUND_BLUE;
			break;
		case JCURSES_CYAN:
			result = FOREGROUND_BLUE|FOREGROUND_GREEN;
			break;
		default:result = 0;
	}

	return result;
}

WORD getBackgroundColor(int color) {
	WORD result;
	switch(color) {
		case JCURSES_BLACK:
			result = 0;
			break;
		case JCURSES_WHITE:
			result = BACKGROUND_BLUE|BACKGROUND_RED|BACKGROUND_GREEN;
			break;
		case JCURSES_BLUE:
			result = BACKGROUND_BLUE;
			break;
		case JCURSES_GREEN:
			result = BACKGROUND_GREEN;
			break;
		case JCURSES_RED:
			result = BACKGROUND_RED;
			break;
		case JCURSES_YELLOW:
			result = BACKGROUND_RED|BACKGROUND_GREEN;
			break;
		case JCURSES_MAGENTA:
			result = BACKGROUND_RED|BACKGROUND_BLUE;
			break;
		case JCURSES_CYAN:
			result = BACKGROUND_BLUE|BACKGROUND_GREEN;
			break;
		default:result = 0;
	}

	return result;
}





color_pair pairs [65];

void init_pair (jshort number, jshort background, jshort foreground) {
	 pairs[number].background =  background;
	 pairs[number].foreground =  foreground;

}




JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_initColorPair (JNIEnv * env, jclass class, jshort background,
																	 jshort foreground, jshort number) {
	init_pair(number+1,background, foreground);
}


jint computeChtype(jshort number) {
	return 0;
}

JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_computeChtype (JNIEnv * env, jclass class, jshort number) {
	return computeChtype(number);
}


int getAbsoluteX(int relativeX) {
	return relativeX+info->srWindow.Left;
}


int getAbsoluteY(int relativeY) {
	return relativeY+info->srWindow.Top;
}


void init() {
	initLog();
	inputHandle=GetStdHandle(STD_INPUT_HANDLE);
	outputHandle=GetStdHandle(STD_OUTPUT_HANDLE);
	info = malloc(sizeof(CONSOLE_SCREEN_BUFFER_INFO));
	GetConsoleScreenBufferInfo(outputHandle, info);
	COLS=info->srWindow.Right-info->srWindow.Left+1;
	LINES=info->srWindow.Bottom-info->srWindow.Top+1;
	lastMode = info->wAttributes;
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_init (JNIEnv * env, jclass class) {
	init();
}





void setTextMode(jshort number, jlong attr) {
	WORD background = getBackgroundColor(pairs[number].background);
	WORD foreground = getForegroundColor(pairs[number].foreground);
	if (attr == JCURSES_BOLD) {
		foreground = foreground|FOREGROUND_INTENSITY;
	}
	if (attr == JCURSES_REVERSE) {
		background = getBackgroundColor(pairs[number].foreground);
		foreground = getForegroundColor(pairs[number].background);
	}
	SetConsoleTextAttribute(outputHandle,background|foreground);
}


WORD getTextMode(jshort number, jlong attr) {
	WORD tmp;
	WORD background = getBackgroundColor(pairs[number].background);
	WORD foreground = getForegroundColor(pairs[number].foreground);
	if (attr == JCURSES_BOLD) {
		foreground = foreground|FOREGROUND_INTENSITY;
	}
	if (attr == JCURSES_REVERSE) {
		background = getBackgroundColor(pairs[number].foreground);
		foreground = getForegroundColor(pairs[number].background);
	}

	return background|foreground;

}

void clear_box2(int x, int y, int width, int height, WORD colors) {
	int i, j;
	COORD coord;
	DWORD buf;
	for (j=0; j<height; j++) {
		coord.X = getAbsoluteX(x);
		coord.Y = getAbsoluteY(y+j);
		FillConsoleOutputCharacter(outputHandle,' ',width,coord,&buf);
		FillConsoleOutputAttribute(outputHandle,colors,width,coord,&buf);
	}

	fflush(logStream);
}

void clear_box(int x, int y, int width, int height, jshort number, jlong attr) {
	clear_box2(x,y,width,height,getTextMode(number+1, attr));
}





void clearScreen(jshort number, jlong attr) {
	clear_box(0,0,COLS,LINES,number, attr);
}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_clearScreen (JNIEnv * env, jclass class, jshort number, jlong attr) {
	clearScreen(number,attr);
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawRectangle (JNIEnv * env , jclass class, jint x, jint y,
																	jint width, jint height, jshort number, jlong attr) {
	clear_box(x, y, width,height,number, attr);
}


void _shutdown() {
	clear_box2(0,0,COLS,LINES,lastMode);
	SetConsoleTextAttribute(outputHandle,lastMode);
}




JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_shutdown (JNIEnv * env, jclass class) {
	_shutdown();
}


void drawHorizontalLineWithChars(jint startX, jint endX, jint Y, jshort number,jlong attr, char ch) {
	int width = endX-startX+1;
	COORD coord;
	DWORD buf;
	WORD colors = getTextMode(number+1, attr);
	coord.X = getAbsoluteX(startX);
	coord.Y = getAbsoluteY(Y);
	FillConsoleOutputCharacter(outputHandle,ch,width,coord,&buf);
	FillConsoleOutputAttribute(outputHandle,colors,width,coord,&buf);

}


void drawChar(jint X, jint Y, jshort number, jlong attr,char ch) {
	drawHorizontalLineWithChars(X,X,Y,number, attr,ch);
}




void drawLineWithChars(jint start, jint end, jint end2, jshort number, jlong attr,short alignment, char ch) {

	int x1, x2, i;
  /*Zeichnen vorbereiten*/
   setTextMode(number+1, attr);

	/*Grenzen ermitteln*/
	if (start < end) {
		x1 = start;
		x2 = end;
	}  else {
		x1 = end;
		x2 = start;
	}

	/*Zeichnen*/

	if (!alignment) {
		for (i=x1; i<=x2; i++) {
			drawChar(end2,i,number, attr,ch);
		}
	} else {
		drawHorizontalLineWithChars(x1,x2,end2,number, attr,ch);
	}

}




void drawLine(jint start, jint end, jint end2, jshort number, jlong attr,short alignment) {

	if (alignment) {
		drawLineWithChars(start, end, end2, number, attr, alignment, ACS_HLINE);
	} else {
	    drawLineWithChars(start, end, end2, number, attr, alignment, ACS_VLINE);
	}
}



JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawHorizontalLine (JNIEnv * env, jclass class, jint startX, jint startY
																	  ,jint endX, jshort number, jlong attr) {
   drawLine(startX, endX, startY, number, attr,1);

}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawVerticalLine (JNIEnv * env, jclass class, jint startX,
																	 jint startY, jint endY, jshort number, jlong attr) {
	drawLine(startY, endY, startX, number, attr,0);
}


void drawThickLine(jint start, jint end, jint end2, jshort number, jlong attr,short alignment) {
	drawLineWithChars(start, end, end2, number, attr, alignment, ACS_CHESSBOARD);
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawHorizontalThickLine (JNIEnv * env, jclass class, jint startX, jint startY
																	  ,jint endX, jshort number,jlong attr) {
   drawThickLine(startX, endX, startY, number, attr,1);

}

JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawVerticalThickLine (JNIEnv * env, jclass class, jint startX,
																	 jint startY, jint endY, jshort number,jlong attr) {
	drawThickLine(startY, endY, startX, number, attr, 0);
}



JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawCorner (JNIEnv * env, jclass class, jint x1, jint y1,
																jint x2, jint y2, jshort number, jlong attr,jshort alignment) {
	int cornerX, cornerY, otherX, otherY;
	char character;

   	setTextMode(number+1, attr);


	if ((x1 == x2) && (y1 == y2)) {
		switch (alignment) {
			case 2:
				character = ACS_LLCORNER;
				break;
			case 3:
				character = ACS_LRCORNER;
				break;
			case 4:
				character = ACS_ULCORNER;
				break;
			case 5:
				character = ACS_URCORNER;
				break;
			 default:
				character = ACS_LLCORNER;
				break;
		}
		drawChar(x1, y1,number, attr,character);
	} else if (x1 == x2) {
	   drawLine(y1, y2, x1, number, attr, 0);
	}  else if (y1 == y2) {
	   drawLine(x1, x2, y1, number, attr, 1);
	}  else {

	   if (alignment) {
	   	  if (y1 > y2) {
			  cornerY = y1;
			  otherY = y2;
			  cornerX = x2;
			  otherX = x1;
		  } else {
			  cornerY = y2;
			  otherY = y1;
			  cornerX = x1;
			  otherX = x2;
		  }
	   } else {
	   	  if (y1 > y2) {
			  cornerY = y2;
			  otherY = y1;
			  cornerX = x1;
			  otherX = x2;
		  } else {
			  cornerY = y1;
			  otherY = y2;
			  cornerX = x2;
			  otherX = x1;
		  }
	   }
	   if ((cornerX < otherX) && (cornerY < otherY)) {
	   	  character = ACS_ULCORNER;
	   } else if ((cornerX > otherX) && (cornerY  < otherY)) {
		 character = ACS_URCORNER;
	   } else if ((cornerX < otherX) && (cornerY  > otherY)) {
		 character = ACS_LLCORNER;
	   } else if ((cornerX > otherX) && (cornerY  > otherY)) {
		 character = ACS_LRCORNER;
	   }

	   drawLine(cornerX, otherX, cornerY, number, attr, 1);
	   drawLine(cornerY, otherY, cornerX, number, attr, 0);
	   drawChar(cornerX, cornerY,number, attr,character);

	}

}




JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_drawBorder(JNIEnv * env, jclass class, jint x, jint y, jint width,
																jint height, jshort number, jlong attr) {
   /*Zeichnen vorbereiten*/
   int i=0;

   /*Obere Linke Ecke setzen*/
   drawChar(x,y,number, attr,ACS_ULCORNER);

   /*Obere Seite ziehen*/
   drawHorizontalLineWithChars(x+1,x+width-2,y,number, attr,ACS_HLINE);



   /*Obere Rechte Ecke setzen*/
   drawChar(x+width-1,y,number, attr,ACS_URCORNER);

   /*Rechte Seite ziehen*/

   for (i=1; i<(height-1); i++) {
	 drawChar(x+width-1,y+i,number, attr,ACS_VLINE);
   }


   /*Untere rechte Ecke setzen*/
   drawChar(x+width-1,y+height-1,number, attr,ACS_LRCORNER);

   /*Untere Seite ziehen*/
   drawHorizontalLineWithChars(x+1,x+width-2,y+height-1,number, attr,ACS_HLINE);


   /*Untere Linke Ecke setzen*/
   drawChar(x, y+height-1,number, attr,ACS_LLCORNER);

   /*Linke Seite ziehen*/
   for (i=1; i<(height-1); i++) {
	 drawChar(x, y+height-1-i,number, attr,ACS_VLINE);
   }



}


void printSimpleString(unsigned char * charArray, int length, int x, int y, jshort number, jlong attr) {
	COORD coord;
	DWORD buf;
	char c=charArray[length-1];
	WORD colors = getTextMode(number+1, attr);

	coord.X = getAbsoluteX(x);
	coord.Y = getAbsoluteY(y);
	WriteConsoleOutputCharacter(outputHandle,charArray,length,coord,&buf);
	FillConsoleOutputAttribute(outputHandle,colors,length,coord,&buf);
}


void printString(unsigned char * charArray, int length, jint x, jint y, jint width, jint height, jshort number, jlong attr) {

	int j,xpos,ypos;
	unsigned char c;
	char * stringToPrint = malloc(width+1);
	int printLength = 0;
	int toPrint = 0;


	xpos=x-1;
	ypos=y;


	for (j=0; j<length; j++) {
		c = charArray[j];
		if (c!='\r') {
			if ( c == '\t') {
				c = ' ';
			}
			if (c!='\n') {
				xpos++;
				printLength++;
				stringToPrint[printLength-1] = c;
				if (xpos == (x+width)) {
					printSimpleString(stringToPrint,printLength -1,x,ypos,number, attr);
					xpos = x;
					ypos++;
					if (ypos == (y+height)) {
						printLength = 0;
						break;
					}
					printLength = 1;
					stringToPrint[printLength-1] = c;
				}
			}  else {
				printSimpleString(stringToPrint,printLength,x,ypos,number, attr);
				xpos = x-1;
				ypos++;
				if (ypos == (y+height)) {
					printLength = 0;
					break;
				}
				printLength = 0;
			}
		}

	}
	if (printLength > 0) {
	  printSimpleString(stringToPrint,printLength,x,ypos,number, attr);
	}
	free(stringToPrint);
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_printString (JNIEnv * env, jclass class, jbyteArray bytes,
																	jint x, jint y, jint width, jint height, jshort number, jlong attr) {

	int length = (*env)->GetArrayLength(env,bytes);
	unsigned char * charArray = (*env)->GetByteArrayElements(env, bytes, NULL);
	printString(charArray, length, x, y, width ,height, number, attr);


}


JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_readByte (JNIEnv * env, jclass class) {
	int code= getch();
	int nextCode = 0;
	if (code == '\r') {
		code = '\n';
	} else if ((code == 0) || (code == 224)) {
		nextCode = getch();
		switch (nextCode) {
			case 59:
				code = KEY_F1;break;
			case 60:
				code = KEY_F2;break;
			case 61:
				code = KEY_F3;break;
			case 62:
				code = KEY_F4;break;
			case 63:
				code = KEY_F5;break;
			case 64:
				code = KEY_F6;break;
			case 65:
				code = KEY_F7;break;
			case 66:
				code = KEY_F8;break;
			case 67:
				code = KEY_F9;break;
			case 68:
				code = KEY_F10;break;
			case 133:
				code = KEY_F11;break;
			case 134:
				code = KEY_F12;break;
			case 75:
				code = KEY_LEFT;break;
			case 77:
				code = KEY_RIGHT;break;
			case 72:
				code = KEY_UP;break;
			case 80:
				code = KEY_DOWN;break;
			case 82:
				code = KEY_IC;break;
			case 83:
				code = KEY_DC;break;
			case 71:
				code = KEY_HOME;break;
			case 79:
				code = KEY_END;break;
			case 73:
				code = KEY_PPAGE;break;
			case 81:
				code = KEY_NPAGE;break;
			default:
				code = nextCode;
		}

	}  else if (code == 8) {
		code = KEY_BACKSPACE;
	}

	return code;
}



JNIEXPORT jint JNICALL Java_jcurses_system_Toolkit_getSpecialKeyCode (JNIEnv * env, jclass class, jint code) {
	return code;
}



void changeColors(jint x, jint y, jint width,jint height, jshort colorpair, jlong attr) {
	int i,j;
	COORD coord;
	DWORD buf;
	for (j=0; j<height; j++) {
		coord.X = getAbsoluteX(x);
		coord.Y = getAbsoluteY(y+j);
		FillConsoleOutputAttribute(outputHandle,getTextMode(colorpair+1, attr),width,coord,&buf);
	}
}



JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_changeColors (JNIEnv *env, jclass clazz, jint x, jint y, jint width, jint height, jshort colorpair, jlong attr) {
	changeColors(x,y,width,height,colorpair, attr);
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_startPainting (JNIEnv * env, jclass clazz) {
	//nothing, implementing, when all new made with refreshing
}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_endPainting (JNIEnv * env , jclass clazz) {
	//nothing, implementing, when all new made with refreshing
}

//Nur zum Testen
int main() {
	int code;
	while(1) {
		code = getch();
		printf("%d\n",code);
	}

	return 0;

}


JNIEXPORT void JNICALL Java_jcurses_system_Toolkit_beep(JNIEnv * env, jclass clazz) {
	//Beep(880,3000);
}








