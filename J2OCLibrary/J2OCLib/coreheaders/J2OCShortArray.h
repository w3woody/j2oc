//
//  J2OCShortArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCShortArray : NSObject
{
	int32_t length;
	int16_t *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (int16_t)shortAtIndex:(int32_t)index;
- (void)replaceShortAtIndex:(int32_t)index withObject:(int16_t)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;
- (int16_t *)array;

@end

