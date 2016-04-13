//
//  J2OCDoubleArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCDoubleArray : NSObject
{
	int32_t length;
	double *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (double)doubleAtIndex:(int32_t)index;
- (void)replaceDoubleAtIndex:(int32_t)index withObject:(double)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;
- (double *)array;

@end
