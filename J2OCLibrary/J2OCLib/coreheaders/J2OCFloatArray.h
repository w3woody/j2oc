//
//  J2OCFloatArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCFloatArray : NSObject
{
	int32_t length;
	float *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (float)floatAtIndex:(int32_t)index;
- (void)replaceFloatAtIndex:(int32_t)index withObject:(float)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;
- (float *)array;

@end

