//
//  J2OCIntArray.m
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "J2OCIntArray.h"
#import "java_lang_ArrayIndexOutOfBoundsException.h"

@implementation J2OCIntArray

- (id)initArrayWithSize:(int32_t)size
{
	if (nil != (self = [super init])) {
		length = size;
		array = (int32_t *)malloc(sizeof(int32_t) * size);
		memset(array,0,sizeof(int32_t) * size);
	}
	return self;
}

- (void)dealloc
{
	free(array);
	[super dealloc];
}

- (int32_t)intAtIndex:(int32_t)index
{
	if ((index < 0) || (index >= length)) {
		java_lang_ArrayIndexOutOfBoundsException *ex = [[java_lang_ArrayIndexOutOfBoundsException alloc] init];
		[ex __init_java_lang_ArrayIndexOutOfBoundsException__];
		GException = [ex autorelease];
		j2oc_longjmp(GExceptState,1);
	}
	return array[index];
}

- (void)replaceIntAtIndex:(int32_t)index withObject:(int32_t)value
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

- (int32_t *)array
{
	return array;
}

- (NSObject *)clone__
{
	int i;
	
	J2OCIntArray *tmp = [[[J2OCIntArray alloc] initArrayWithSize:length] autorelease];
	for (i = 0; i < length; ++i) {
		(tmp->array)[i] = array[i];
	}
	return tmp;
}

@end
