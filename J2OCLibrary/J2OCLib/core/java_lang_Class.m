//
//  java_lang_Class.m
//  j2oc
//
//  Created by William Woody on 2/11/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#include <objc/runtime.h>
#import "java_lang_Class.h"

@implementation J2OCClass (category_java_lang_Class)

- (void)__init_java_lang_Class__
{
}

- (java_lang_String *)getName_java_lang_String__
{
	return [NSString stringWithUTF8String:class_getName([self getClass])];
}

- (java_lang_Class *)getSuperclass_java_lang_Class__
{
	return [J2OCClass classWithClass:class_getSuperclass([self getClass])];
}

- (java_lang_String *)toString_java_lang_String__
{
	return [self getName_java_lang_String__];
}

- (java_lang_Class *)getComponentType_java_lang_Class__
{
	return [J2OCClass classWithClass:[NSObject class]];
}

- (BOOL)isInstance_boolean___java_lang_Object:(java_lang_Object *)v1_a
{
	return [v1_a isKindOfClass:c];
}

- (BOOL)desiredAssertionStatus_boolean__
{
	return NO;
}

@end
