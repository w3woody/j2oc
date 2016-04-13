/*	j2oc.m
 *
 *		Define the various components that are used for my j2oc engine
 */
 
#import "j2oc.h"
 
#import "java_lang_Boolean.h"
#import "java_lang_Character.h"
#import "java_lang_Number.h"
#import "java_lang_Integer.h"
#import "java_lang_ArrayStoreException.h"
#import "java_lang_IndexOutOfBoundsException.h"
 
/************************************************************************/
/*																		*/
/*	Globals																*/
/*																		*/
/************************************************************************/

jmp_buf GExceptState;
id GException;

/************************************************************************/
/*																		*/
/*	Support Functions													*/
/*																		*/
/************************************************************************/

/*	lcmp operator */
int32_t j2oc_lcmp(int64_t left, int64_t right)
{
	if (left < right) return -1;
	if (left > right) return 1;
	return 0;
}

/* dcmpg/fcmpg operator */
int32_t j2oc_fcmpg(double left, double right)
{
	if (isnan(left) || isnan(right)) return 1;
	if (left < right) return -1;
	if (left > right) return 1;
	return 0;
}

/* dcmpl/fcmpl operator */
int32_t j2oc_fcmpl(double left, double right)
{
	if (isnan(left) || isnan(right)) return -1;
	if (left < right) return -1;
	if (left > right) return 1;
	return 0;
}

/* dmod/fmod operator */
int32_t j2oc_fmod(double left, double right)
{
	return fmod(left,right);
}

/* I wrap longjmp to give a common breakpoint for Java exceptions */
void j2oc_longjmp(jmp_buf env, int val)
{
	longjmp(env,val);
}


/*	j2oc_monitor_enter
 *
 *		Monitor enter
 */

void j2oc_monitor_enter(id mobj)
{
}

/*	j2oc_monitor_exit
 *
 *		Monitor exit
 */

void j2oc_monitor_exit(id mobj)
{
}


/************************************************************************/
/*																		*/
/*	String format support												*/
/*																		*/
/************************************************************************/

/*	j2oc_format
 *
 *		Called from java.lang.String; used to format
 */

NSString *j2oc_format(NSString *format, J2OCRefArray *ref)
{
	NSMutableString *str = [[[NSMutableString alloc] init] autorelease];	
	int len = [format length];
	int i = 0;
	int pos;
	int rpos = 0;
	int rval;
	int npos;

	while (i < len) {
		// Get unformatted component
		pos = i;
		while (i < len) {
			unichar ch = [format characterAtIndex:i];
			if (ch == '%') break;
			++i;
		}
		if (pos < i) {
			NSRange range;
			
			range.location = pos;
			range.length = i - pos;
			NSString *tmp = [format substringWithRange:range];
			[str appendString:tmp];
		}
		if (i >= len) break;
		
		// Get formatter (from % to first character or '%')
		++i;		// skip '%'
		pos = i;
		rval = 0;
		npos = -1;
		unichar ch = 0;
		while (i < len) {
			ch = [format characterAtIndex:i];
			++i;
			if ((ch == '%') || isalpha(ch)) break;
			if (ch == '$') {
				npos = rval;
				pos = i;
			}
			if ((ch >= '0') && (ch <= '9')) {
				rval = rval * 10 + (ch - '0');
			}
		}
		if (npos < 0) npos = rpos++;
		
		// At this point npos is the index of the object to format, and
		// the string range from pos to i contains the formatter object.
		
		/* At this point ch is 'b', 'B', etc... For complex formatter we cheat and
		   defer to the built-in formatter from Objective C */
		id obj = [ref refAtIndex:npos];
		if (obj == nil) {
			[str appendString:@"null"];
		} else {
			// Figure out what's going on
			switch (ch) {
				case 'b':
				case 'B':
					if ([obj isKindOfClass:[java_lang_Boolean class]]) {
						[str appendString:[(java_lang_Boolean *)obj toString_java_lang_String__]];
					} else {
						[str appendString:@"true"];
					}
					break;
					
				case 'h':
				case 'H':
					[str appendString:[java_lang_Integer toHexString_java_lang_String___int:[(java_lang_Object *)obj hashCode_int__]]];
					break;
					
				case 's':
				case 'S':
					[str appendString:[(java_lang_Object *)obj toString_java_lang_String__]];
					break;
					
				case 'c':
				case 'C':
					if ([obj isKindOfClass:[java_lang_Character class]]) {
						[str appendFormat:@"%c",[(java_lang_Character *)obj charValue_char__]];
					} else if ([obj isKindOfClass:[java_lang_Number class]]) {
						[str appendFormat:@"%c",(unichar)[(java_lang_Number *)obj intValue_int__]];
					} else {
						[str appendString:@"?"];
					}
					break;
					
				case 'd':
				case 'o':
				case 'x':
				case 'X':
					/* Integer format -- extract long value */
					/* Rather than format ourselves, we cheat, package up the formatter,
					   and invoke the default formatter on long values */
					if ([obj isKindOfClass:[java_lang_Number class]]) {
						int64_t val = [(java_lang_Number *)obj longValue_long__];
						
						NSRange range;
						range.location = pos;
						range.length = i - pos - 1;
						NSMutableString *tmpForm = [[NSMutableString alloc] init];
						[tmpForm appendString:@"%"];
						[tmpForm appendString:[format substringWithRange:range]];
						
						if (ch == 'd') {
							[tmpForm appendString:@"qi"];
						} else if (ch == 'o') {
							[tmpForm appendString:@"qo"];
						} else if (ch == 'x') {
							[tmpForm appendString:@"qx"];
						} else {
							[tmpForm appendString:@"qX"];
						}
						
						[str appendFormat:tmpForm, val];
						[tmpForm release];
					} else {
						[str appendString:@"?"];
					}
					break;

				case 'e':
				case 'E':
				case 'f':
				case 'g':
				case 'G':
				case 'a':
				case 'A':
					/* Float format -- extract double value */
					/* Rather than format ourselves, we cheat, package up the formatter,
					   and invoke the default formatter on double values */
					if ([obj isKindOfClass:[java_lang_Number class]]) {
						double val = [(java_lang_Number *)obj doubleValue_double__];
						
						NSRange range;
						range.location = pos;
						range.length = i - pos;
						NSMutableString *tmpForm = [[NSMutableString alloc] init];
						[tmpForm appendString:@"%"];
						[tmpForm appendString:[format substringWithRange:range]];
						
						[str appendFormat:tmpForm, val];
						[tmpForm release];
					} else {
						[str appendString:@"?"];
					}
					break;
					
				case '%':
					[str appendFormat:@"%"];
					break;
					
				case 'n':
					[str appendFormat:@"\n"];
					break;
			}
		}
	}
	
	return str;
}

/************************************************************************/
/*																		*/
/*	Array copy (Implements System.arraycopy)							*/
/*																		*/
/************************************************************************/

static void ThrowArrayStoreException()
{
	java_lang_ArrayStoreException *ex = [[java_lang_ArrayStoreException alloc] init];
	[ex __init_java_lang_ArrayStoreException__];
	GException = [ex autorelease];
	j2oc_longjmp(GExceptState,1);
}

static void ThrowIndexOutOfBoundsException()
{
	java_lang_IndexOutOfBoundsException *ex = [[java_lang_IndexOutOfBoundsException alloc] init];
	[ex __init_java_lang_IndexOutOfBoundsException__];
	GException = [ex autorelease];
	j2oc_longjmp(GExceptState,1);
}

/*	j2oc_arraycopy
 *
 *		Copies from src to dest
 */

void j2oc_arraycopy(NSObject *src, int32_t srcOff, NSObject *dest, int32_t destOff, int32_t len)
{
	// Initial fast check for types
	if ([src class] != [dest class]) ThrowArrayStoreException();
	
	// initial range checks
	if ((srcOff < 0) || (destOff < 0) || (len < 0)) ThrowIndexOutOfBoundsException();
	
	// Run through each of the types and handle copy
	if ([src isKindOfClass:[J2OCBooleanArray class]]) {
		J2OCBooleanArray *tmpSrc = (J2OCBooleanArray *)src;
		J2OCBooleanArray *tmpDst = (J2OCBooleanArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(int8_t) * len);
	
	} else if ([src isKindOfClass:[J2OCByteArray class]]) {
		J2OCByteArray *tmpSrc = (J2OCByteArray *)src;
		J2OCByteArray *tmpDst = (J2OCByteArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(int8_t) * len);
	
	} else if ([src isKindOfClass:[J2OCCharArray class]]) {
		J2OCCharArray *tmpSrc = (J2OCCharArray *)src;
		J2OCCharArray *tmpDst = (J2OCCharArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(unichar) * len);
	
	} else if ([src isKindOfClass:[J2OCShortArray class]]) {
		J2OCShortArray *tmpSrc = (J2OCShortArray *)src;
		J2OCShortArray *tmpDst = (J2OCShortArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(int16_t) * len);
	
	} else if ([src isKindOfClass:[J2OCIntArray class]]) {
		J2OCIntArray *tmpSrc = (J2OCIntArray *)src;
		J2OCIntArray *tmpDst = (J2OCIntArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(int32_t) * len);
	
	} else if ([src isKindOfClass:[J2OCLongArray class]]) {
		J2OCLongArray *tmpSrc = (J2OCLongArray *)src;
		J2OCLongArray *tmpDst = (J2OCLongArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(int64_t) * len);
	
	} else if ([src isKindOfClass:[J2OCFloatArray class]]) {
		J2OCFloatArray *tmpSrc = (J2OCFloatArray *)src;
		J2OCFloatArray *tmpDst = (J2OCFloatArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(float) * len);
	
	} else if ([src isKindOfClass:[J2OCDoubleArray class]]) {
		J2OCDoubleArray *tmpSrc = (J2OCDoubleArray *)src;
		J2OCDoubleArray *tmpDst = (J2OCDoubleArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		memmove(destOff + [tmpDst array],srcOff + [tmpSrc array],sizeof(double) * len);
	
	} else if ([src isKindOfClass:[J2OCRefArray class]]) {
		J2OCRefArray *tmpSrc = (J2OCRefArray *)src;
		J2OCRefArray *tmpDst = (J2OCRefArray *)dest;
		
		if (srcOff + len > [tmpSrc arrayLength]) ThrowIndexOutOfBoundsException();
		if (destOff + len > [tmpDst arrayLength]) ThrowIndexOutOfBoundsException();
		
		int i;
		for (i = 0; i < len; ++i) {
			[tmpDst replaceRefAtIndex:i + destOff withObject:[tmpSrc refAtIndex:i + srcOff]];
		}
	} else {
		ThrowArrayStoreException();
	}
}


