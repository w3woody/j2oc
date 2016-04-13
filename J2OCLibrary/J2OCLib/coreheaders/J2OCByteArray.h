//
//  J2OCByteArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCByteArray : NSObject
{
	int32_t length;
	int8_t *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (id)initArrayWithBytesNoCopy:(int8_t *)bytes length:(int32_t)size;
- (int8_t)byteAtIndex:(int32_t)index;
- (void)replaceByteAtIndex:(int32_t)index withObject:(int8_t)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;

- (int8_t *)array;

@end
