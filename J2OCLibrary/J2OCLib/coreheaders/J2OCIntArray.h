//
//  J2OCIntArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCIntArray : NSObject
{
	int32_t length;
	int32_t *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (int32_t)intAtIndex:(int32_t)index;
- (void)replaceIntAtIndex:(int32_t)index withObject:(int32_t)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;
- (int32_t *)array;

@end
