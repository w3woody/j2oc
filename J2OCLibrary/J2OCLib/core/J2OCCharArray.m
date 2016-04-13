//
//  J2OCCharArray.m
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "J2OCCharArray.h"
#import "java_lang_ArrayIndexOutOfBoundsException.h"


@implementation J2OCCharArray

- (id)initArrayWithSize:(int32_t)size
{
	if (nil != (self = [super init])) {
		length = size;
		array = (unichar *)malloc(sizeof(unichar) * size);
		memset(array,0,sizeof(unichar) * size);
	}
	return self;
}

- (id)initArrayWithDataNoCopy:(unichar *)ch length:(int32_t)l
{
	if (nil != (self = [super init])) {
		length = l;
		array = ch;
	}
	return self;
}

- (void)dealloc
{
	free(array);
	[super dealloc];
}

- (unichar)charAtIndex:(int32_t)index
{
	if ((index < 0) || (index >= length)) {
		java_lang_ArrayIndexOutOfBoundsException *ex = [[java_lang_ArrayIndexOutOfBoundsException alloc] init];
		[ex __init_java_lang_ArrayIndexOutOfBoundsException__];
		GException = [ex autorelease];
		j2oc_longjmp(GExceptState,1);
	}
	return array[index];
}

- (void)replaceCharAtIndex:(int32_t)index withObject:(unichar)value
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
	
	J2OCCharArray *tmp = [[[J2OCCharArray alloc] initArrayWithSize:length] autorelease];
	for (i = 0; i < length; ++i) {
		(tmp->array)[i] = array[i];
	}
	return tmp;
}

- (unichar *)array
{
	return array;
}


@end
