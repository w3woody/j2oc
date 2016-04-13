/*	J2OCString
 *
 *		Internal string representation used for Java strings. This has the
 *	property that I can replace the contents easily with the setStringContent
 *	call
 */

#import "J2OCString.h"


@implementation J2OCString

/************************************************************************/
/*																		*/
/*	Construction/Destruction											*/
/*																		*/
/************************************************************************/

- (id)init
{
	if (nil != (self = [super init])) {
		str = nil;
		len = 0;
	}
}

- (void)dealloc
{
	if (str != nil) free(str);
    [super dealloc];
}


- (void)setStringContent:(unichar *)data withLength:(NSUInteger)l
{
	if (str != nil) {
		free(str);
	}
	str = (unichar *)malloc(sizeof(unichar) * l);
	memcpy(str,data,sizeof(unichar) * l);
	len = l;
}

- (NSUInteger)length
{
	return len;
}

- (unichar)characterAtIndex:(NSUInteger)index
{
	return str[index];
}

@end
