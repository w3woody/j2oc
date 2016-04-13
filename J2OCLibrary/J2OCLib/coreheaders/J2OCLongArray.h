//
//  J2OCLongArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCLongArray : NSObject
{
	int32_t length;
	int64_t *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (int64_t)longAtIndex:(int32_t)index;
- (void)replaceLongAtIndex:(int32_t)index withObject:(int64_t)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;
- (int64_t *)array;

@end
