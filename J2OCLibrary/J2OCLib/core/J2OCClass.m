//
//  J2OCClass.m
//  j2oc
//
//  Created by William Woody on 2/15/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "J2OCClass.h"


/************************************************************************/
/*																		*/
/*	Class Management													*/
/*																		*/
/************************************************************************/

static NSMutableArray *GClassList = nil;


@implementation J2OCClass

- (id)initWithClassRef:(Class)cl
{
	if (nil != (self = [super init])) {
		c = cl;
	}
	return self;
}

- (Class)getClass
{
	return c;
}

+ (J2OCClass *)classWithClass:(Class)c
{
	J2OCClass *cl;
	
	if (GClassList == nil) {
		GClassList = [[NSMutableArray alloc] initWithCapacity:64];
	}
	
	int i,ct = [GClassList count];
	for (i = 0; i < ct; ++i) {
		cl = [GClassList objectAtIndex:i];
		if ([cl getClass] == c) return cl;
	}
	
	cl = [[[J2OCClass alloc] initWithClassRef:c] autorelease];
	[GClassList addObject:cl];
	return cl;
}

@end
