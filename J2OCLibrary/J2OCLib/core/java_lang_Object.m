//
//  java_lang_Object.m
//  j2oc
//
//  Created by William Woody on 2/11/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "java_lang_Object.h"
#import "java_lang_Class.h"

@implementation NSObject (category_java_lang_Object)

- (java_lang_Class *)getClass_java_lang_Class__
{
	return [J2OCClass classWithClass:[self class]];
}

- (void)__init_java_lang_Object__
{
}

- (java_lang_Object *)clone_java_lang_Object__
{
	return [[self copy] autorelease];	// ???
}

- (int32_t)hashCode_int__
{
	return [self hash];
}

- (BOOL)equals_boolean___java_lang_Object:(java_lang_Object *)v1_a
{
	return (v1_a == self);
}


- (java_lang_String *)toString_java_lang_String__
{
	return [NSString stringWithFormat:@"(Object-%08X)",(int32_t)self];
}

@end
