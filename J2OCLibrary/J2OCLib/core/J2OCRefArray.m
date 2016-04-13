//
//  J2OCRefArray.m
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "J2OCRefArray.h"
#import "java_lang_ArrayIndexOutOfBoundsException.h"

@implementation J2OCRefArray

/************************************************************************/
/*																		*/
/*	Construction														*/
/*																		*/
/************************************************************************/

- (id)initArrayWithSize:(int32_t)size
{
	if (nil != (self = [super init])) {
		array = (id *)malloc(sizeof(id) * size);
		length = size;
		memset(array,0,sizeof(id) * size);
	}
	return self;
}

- (id)initArrayWithType:(int32_t)typeID dimensions:(int32_t)dim, ...
{
	if (nil != (self = [super init])) {
		va_list args;
		int i;
		va_start(args,dim);
		int32_t tmp[64];
		
		for (i = 0; i < dim; ++i) {
			tmp[i] = va_arg(args,int32_t);
		}
		
		/*
		 *	Generate my own array list
		 */
		
		length = tmp[0];
		array = (id *)malloc(sizeof(id) * tmp[0]);
		memset(array,0,sizeof(id) * tmp[0]);
		for (i = 0; i < tmp[0]; ++i) {
			array[i] = [[J2OCRefArray createArray:typeID dimensions:dim-1 count:tmp+1] retain];
		}
	}
	return self;
}

- (id)initArrayWithType:(int32_t)typeID length:(int32_t)len count:(int32_t *)tmp
{
	if (nil != (self = [super init])) {
		int i;
		
		/*
		 *	Generate my own array list
		 */
		
		array = (id *)malloc(sizeof(id) * tmp[0]);
		length = tmp[0];
		memset(array,0,sizeof(id) * tmp[0]);
		for (i = 0; i < tmp[0]; ++i) {
			array[i] = [[J2OCRefArray createArray:typeID dimensions:len-1 count:tmp+1] retain];
		}
	}
	return self;
}

+ (id)createArray:(int32_t)typeID dimensions:(int32_t)dim count:(int32_t *)list
{
	if (dim <= 1) {
		/*
		 *	We are at the base, alloc an array of the specified type
		 */
		
		switch (typeID) {
			case 4:			// T_BOOLEAN:
				return [[[J2OCBooleanArray alloc] initArrayWithSize:list[0]] autorelease];
			case 5:			// T_CHAR:
				return [[[J2OCCharArray alloc] initArrayWithSize:list[0]] autorelease];
			case 6:			// T_FLOAT:
				return [[[J2OCFloatArray alloc] initArrayWithSize:list[0]] autorelease];
			case 7:			// T_DOUBLE:
				return [[[J2OCDoubleArray alloc] initArrayWithSize:list[0]] autorelease];
			case 8:			// T_BYTE:
				return [[[J2OCByteArray alloc] initArrayWithSize:list[0]] autorelease];
			case 9:			// T_SHORT:
				return [[[J2OCShortArray alloc] initArrayWithSize:list[0]] autorelease];
			case 10:		// T_INT:
				return [[[J2OCIntArray alloc] initArrayWithSize:list[0]] autorelease];
			case 11:		// T_LONG:
				return [[[J2OCLongArray alloc] initArrayWithSize:list[0]] autorelease];
			case 2:			// T_ADDR:
			default:
				return [[[J2OCRefArray alloc] initArrayWithSize:list[0]] autorelease];
		}
	} else {
		return [[[J2OCRefArray alloc] initArrayWithType:typeID length:dim count:list] autorelease];
	}
}

- (void)dealloc
{
	if (array) free(array);
	[super dealloc];
}

/************************************************************************/
/*																		*/
/*	Support																*/
/*																		*/
/************************************************************************/

- (id)refAtIndex:(int32_t)index
{
	if ((index < 0) || (index >= length)) {
		java_lang_ArrayIndexOutOfBoundsException *ex = [[java_lang_ArrayIndexOutOfBoundsException alloc] init];
		[ex __init_java_lang_ArrayIndexOutOfBoundsException__];
		GException = [ex autorelease];
		j2oc_longjmp(GExceptState,1);
	}
	return array[index];
}

- (void)replaceRefAtIndex:(int32_t)index withObject:(id)value
{
	if ((index < 0) || (index >= length)) {
		java_lang_ArrayIndexOutOfBoundsException *ex = [[java_lang_ArrayIndexOutOfBoundsException alloc] init];
		[ex __init_java_lang_ArrayIndexOutOfBoundsException__];
		GException = [ex autorelease];
		j2oc_longjmp(GExceptState,1);
	}
	[value retain];
	[array[index] release];
	array[index] = value;
}

- (int32_t)arrayLength
{
	return length;
}

- (NSObject *)clone__
{
	int i;
	// clone is always shallow, which means we simply need to copy me.
	
	J2OCRefArray *c = [[[J2OCRefArray alloc] initArrayWithSize:length] autorelease];
	for (i = 0; i < length; ++i) {
		(c->array)[i] = [array[i] retain];
	}
	return c;
}


@end
