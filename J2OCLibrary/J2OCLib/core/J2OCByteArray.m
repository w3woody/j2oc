//
//  J2OCByteArray.m
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "J2OCByteArray.h"
#import "java_lang_ArrayIndexOutOfBoundsException.h"


@implementation J2OCByteArray

- (id)initArrayWithSize:(int32_t)size
{
	if (nil != (self = [super init])) {
		length = size;
		array = (int8_t *)malloc(sizeof(int8_t) * size);
		memset(array,0,sizeof(int8_t) * size);
	}
	return self;
}

// Used internally by java_lang_String
- (id)initArrayWithBytesNoCopy:(int8_t *)bytes length:(int32_t)size
{
	if (nil != (self = [super init])) {
		length = size;
		array = bytes;
	}
	return self;
}

- (void)dealloc
{
	free(array);
	[super dealloc];
}

- (int8_t)byteAtIndex:(int32_t)index
{
	if ((index < 0) || (index >= length)) {
		java_lang_ArrayIndexOutOfBoundsException *ex = [[java_lang_ArrayIndexOutOfBoundsException alloc] init];
		[ex __init_java_lang_ArrayIndexOutOfBoundsException__];
		GException = [ex autorelease];
		j2oc_longjmp(GExceptState,1);
	}
	return array[index];
}

- (void)replaceByteAtIndex:(int32_t)index withObject:(int8_t)value
{
	if ((index < 0) || (index >= length)) {
		java_lang_ArrayIndexOutOfBoundsException *ex = [[java_lang_ArrayIndexOutOfBoundsException alloc] init];
		[ex __init_java_lang_ArrayIndexOutOfBoundsException__];
		GException = [ex autorelease];
		j2oc_longjmp(GExceptState,1);
	}
	array[index] = value;
}

- (int32_t)arrayLength
{
	return length;
}

- (NSObject *)clone__
{
	int i;
	
	J2OCByteArray *tmp = [[[J2OCByteArray alloc] initArrayWithSize:length] autorelease];
	for (i = 0; i < length; ++i) {
		(tmp->array)[i] = array[i];
	}
	return tmp;
}

- (int8_t *)array
{
	return array;
}

@end
