//
//  J2OCClass.h
//  j2oc
//
//  Created by William Woody on 2/15/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface J2OCClass : NSObject 
{
	Class c;
	J2OCClass *next;
}

+ (J2OCClass *)classWithClass:(Class)c;
- (Class)getClass;

@end
