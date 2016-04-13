//
//  J2OCCharArray.h
//  j2oc
//
//  Created by William Woody on 2/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCCharArray : NSObject 
{
	int32_t length;
	unichar *array;
}

- (id)initArrayWithSize:(int32_t)size;
- (id)initArrayWithDataNoCopy:(unichar *)ch length:(int32_t)length;
- (unichar)charAtIndex:(int32_t)index;
- (void)replaceCharAtIndex:(int32_t)index withObject:(unichar)value;
- (int32_t)arrayLength;
- (NSObject *)clone__;

- (unichar *)array;

@end
