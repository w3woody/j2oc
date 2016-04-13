/*	J2OCString
 *
 *		We extend the NSString class to allow us to do a one-time swap of the
 *	contents with replacement content. This is then used with the NSString
 *	Java String category to allow us to swap our replacement that can then
 *	handle initializations
 */

#import <Foundation/Foundation.h>


@interface J2OCString : NSString 
{
	unichar *str;
	NSUInteger len;
}

- (id)init;
- (void)setStringContent:(unichar *)data withLength:(NSUInteger)len;
- (NSUInteger)length;
- (unichar)characterAtIndex:(NSUInteger)index;

@end
