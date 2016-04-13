/*  java_lang_String.h
 *
 *      Autogenerated by j2oc (http://www.j2oc.com) at Mon Feb 15 08:02:53 PST 2010
 *
 *      Generated from class java/lang/String
 *      Do not change here; change in original class.
 */


// includes
#import <Foundation/Foundation.h>
#import "j2oc.h"
#import "java_lang_Object.h"
#import "java_io_Serializable.h"
#import "java_lang_CharSequence.h"
#import "java_lang_Comparable.h"

// class forwards
@class java_lang_CharSequence;
@class java_lang_StringBuffer;
@class java_lang_StringBuilder;
@class java_util_Comparator;
@class java_lang_String;

// Declarations

@interface NSString (category_java_lang_String)

- (id)initJ;

+ (java_util_Comparator *)CASE_INSENSITIVE_ORDER_;
+ (void)setCASE_INSENSITIVE_ORDER_:(java_util_Comparator *)value;
- (void)__init_java_lang_String__;
- (void)__init_java_lang_String___byte_ARRAYTYPE:(J2OCByteArray *)v1_a;
- (void)__init_java_lang_String___byte_ARRAYTYPE_int_int:(J2OCByteArray *)v1_a :(int32_t)v2_i :(int32_t)v3_i;
- (void)__init_java_lang_String___byte_ARRAYTYPE_int_int_java_lang_String:(J2OCByteArray *)v1_a :(int32_t)v2_i :(int32_t)v3_i :(java_lang_String *)v4_a;
- (void)__init_java_lang_String___byte_ARRAYTYPE_java_lang_String:(J2OCByteArray *)v1_a :(java_lang_String *)v2_a;
- (void)__init_java_lang_String___char_ARRAYTYPE:(J2OCCharArray *)v1_a;
- (void)__init_java_lang_String___char_ARRAYTYPE_int_int:(J2OCCharArray *)v1_a :(int32_t)v2_i :(int32_t)v3_i;
- (void)__init_java_lang_String___java_lang_String:(java_lang_String *)v1_a;
- (void)__init_java_lang_String___java_lang_StringBuffer:(java_lang_StringBuffer *)v1_a;
- (void)__init_java_lang_String___java_lang_StringBuilder:(java_lang_StringBuilder *)v1_a;
- (unichar)charAt_char___int:(int32_t)v1_i;
- (int32_t)compareToIgnoreCase_int___java_lang_String:(java_lang_String *)v1_a;
- (int32_t)compareTo_int___java_lang_Object:(java_lang_Object *)v1_a;
- (int32_t)compareTo_int___java_lang_String:(java_lang_String *)v1_a;
- (java_lang_String *)concat_java_lang_String___java_lang_String:(java_lang_String *)v1_a;
- (BOOL)contains_boolean___java_lang_CharSequence:(java_lang_CharSequence *)v1_a;
- (BOOL)contentEquals_boolean___java_lang_CharSequence:(java_lang_CharSequence *)v1_a;
- (BOOL)contentEquals_boolean___java_lang_StringBuffer:(java_lang_StringBuffer *)v1_a;
+ (java_lang_String *)copyValueOf_java_lang_String___char_ARRAYTYPE:(J2OCCharArray *)v0_a;
+ (java_lang_String *)copyValueOf_java_lang_String___char_ARRAYTYPE_int_int:(J2OCCharArray *)v0_a :(int32_t)v1_i :(int32_t)v2_i;
- (BOOL)endsWith_boolean___java_lang_String:(java_lang_String *)v1_a;
- (BOOL)equalsIgnoreCase_boolean___java_lang_String:(java_lang_String *)v1_a;
- (BOOL)equals_boolean___java_lang_Object:(java_lang_Object *)v1_a;
+ (java_lang_String *)format_java_lang_String___java_lang_String_java_lang_Object_ARRAYTYPE:(java_lang_String *)v0_a :(J2OCRefArray *)v1_a;
- (J2OCByteArray *)getBytes_byte_ARRAYTYPE__;
- (J2OCByteArray *)getBytes_byte_ARRAYTYPE___java_lang_String:(java_lang_String *)v1_a;
- (void)getChars___int_int_char_ARRAYTYPE_int:(int32_t)v1_i :(int32_t)v2_i :(J2OCCharArray *)v3_a :(int32_t)v4_i;
- (J2OCCharArray *)getValue_char_ARRAYTYPE__;
- (int32_t)hashCode_int__;
- (int32_t)indexOf_int___int:(int32_t)v1_i;
- (int32_t)indexOf_int___int_int:(int32_t)v1_i :(int32_t)v2_i;
- (int32_t)indexOf_int___java_lang_String:(java_lang_String *)v1_a;
- (int32_t)indexOf_int___java_lang_String_int:(java_lang_String *)v1_a :(int32_t)v2_i;
+ (void)initialize;
- (java_lang_String *)intern_java_lang_String__;
- (int32_t)lastIndexOf_int___int:(int32_t)v1_i;
- (int32_t)lastIndexOf_int___int_int:(int32_t)v1_i :(int32_t)v2_i;
- (int32_t)lastIndexOf_int___java_lang_String:(java_lang_String *)v1_a;
- (int32_t)lastIndexOf_int___java_lang_String_int:(java_lang_String *)v1_a :(int32_t)v2_i;
- (int32_t)length_int__;
- (BOOL)regionMatches_boolean___boolean_int_java_lang_String_int_int:(BOOL)v1_i :(int32_t)v2_i :(java_lang_String *)v3_a :(int32_t)v4_i :(int32_t)v5_i;
- (BOOL)regionMatches_boolean___int_java_lang_String_int_int:(int32_t)v1_i :(java_lang_String *)v2_a :(int32_t)v3_i :(int32_t)v4_i;
//- (java_lang_String *)replace___char_char:(unichar)v1_i :(unichar)v2_i;
//- (java_lang_String *)replace___java_lang_CharSequence_java_lang_CharSequence:(java_lang_CharSequence *)v1_a :(java_lang_CharSequence *)v2_a;
- (BOOL)startsWith_boolean___java_lang_String:(java_lang_String *)v1_a;
- (BOOL)startsWith_boolean___java_lang_String_int:(java_lang_String *)v1_a :(int32_t)v2_i;
- (java_lang_CharSequence *)subSequence_java_lang_CharSequence___int_int:(int32_t)v1_i :(int32_t)v2_i;
- (java_lang_String *)substring_java_lang_String___int:(int32_t)v1_i;
- (java_lang_String *)substring_java_lang_String___int_int:(int32_t)v1_i :(int32_t)v2_i;
- (J2OCCharArray *)toCharArray_char_ARRAYTYPE__;
- (java_lang_String *)toLowerCase_java_lang_String__;
- (java_lang_String *)toString_java_lang_String__;
- (java_lang_String *)toUpperCase_java_lang_String__;
- (java_lang_String *)trim_java_lang_String__;
+ (java_lang_String *)valueOf_java_lang_String___boolean:(BOOL)v0_i;
+ (java_lang_String *)valueOf_java_lang_String___char:(unichar)v0_i;
+ (java_lang_String *)valueOf_java_lang_String___char_ARRAYTYPE:(J2OCCharArray *)v0_a;
+ (java_lang_String *)valueOf_java_lang_String___char_ARRAYTYPE_int_int:(J2OCCharArray *)v0_a :(int32_t)v1_i :(int32_t)v2_i;
+ (java_lang_String *)valueOf_java_lang_String___double:(double)v0_d;
+ (java_lang_String *)valueOf_java_lang_String___float:(float)v0_f;
+ (java_lang_String *)valueOf_java_lang_String___int:(int32_t)v0_i;
+ (java_lang_String *)valueOf_java_lang_String___java_lang_Object:(java_lang_Object *)v0_a;
+ (java_lang_String *)valueOf_java_lang_String___long:(int64_t)v0_l;

@end
