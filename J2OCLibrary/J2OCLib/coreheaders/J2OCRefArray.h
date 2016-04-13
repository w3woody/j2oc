//
//  J2OCRefArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface J2OCRefArray : NSObject 
{
	int32_t length;
	id *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (id)initArrayWithType:(int32_t)typeID dimensions:(int32_t)dim, ...;
- (id)initArrayWithType:(int32_t)typeID length:(int32_t)len count:(int32_t *)ct;
- (id)refAtIndex:(int32_t)index;
- (void)replaceRefAtIndex:(int32_t)index withObject:(id)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;

+ (id)createArray:(int32_t)typeID dimensions:(int32_t)dim count:(int32_t *)list;

@end
