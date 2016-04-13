/*
 *  This is a placeholder class that generates the correct methods that are all
 *  rewritten by hand in Objective C.
 */

package java.lang;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Formatter;
//import java.util.Locale;
//
//import java.util.regex.Pattern;
//
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.nio.charset.Charset;
//import java.nio.charset.IllegalCharsetNameException;
//import java.nio.charset.UnsupportedCharsetException;
//import java.security.AccessController;
//import java.util.regex.PatternSyntaxException;
//
//import org.apache.harmony.kernel.vm.VM;
//import org.apache.harmony.luni.util.PriviAction;

/**
 * An immutable sequence of characters/code units ({@code char}s). A
 * {@code String} is represented by array of UTF-16 values, such that
 * Unicode supplementary characters (code points) are stored/encoded as
 * surrogate pairs via Unicode code units ({@code char}).
 *
 * @see StringBuffer
 * @see StringBuilder
 * @see Charset
 * @since 1.0
 */
public final class String implements Serializable, Comparable<String>, CharSequence {

    /**
     * CaseInsensitiveComparator compares Strings ignoring the case of the
     * characters.
     */
    private static final class CaseInsensitiveComparator implements Comparator<String>  {

        /**
         * Compare the two objects to determine the relative ordering.
         *
         * @param o1
         *            an Object to compare
         * @param o2
         *            an Object to compare
         * @return an int < 0 if object1 is less than object2, 0 if they are
         *         equal, and > 0 if object1 is greater
         *
         * @exception ClassCastException
         *                if objects are not the correct type
         */
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    /**
     * A comparator ignoring the case of the characters.
     */
    public static final Comparator<String> CASE_INSENSITIVE_ORDER = new CaseInsensitiveComparator();

    /**
     * Creates an empty string.
     */
    public String() 
    {
    }
    
    /**
     * Converts the byte array to a string using the default encoding as
     * specified by the file.encoding system property. If the system property is
     * not defined, the default encoding is ISO8859_1 (ISO-Latin-1). If 8859-1
     * is not available, an ASCII encoding is used.
     * 
     * @param data
     *            the byte array to convert to a string.
     */
    public String(byte[] data) 
    {
    }

    /**
     * Converts the byte array to a string using the default encoding as
     * specified by the file.encoding system property. If the system property is
     * not defined, the default encoding is ISO8859_1 (ISO-Latin-1). If 8859-1
     * is not available, an ASCII encoding is used.
     * 
     * @param data
     *            the byte array to convert to a string.
     * @param start
     *            the starting offset in the byte array.
     * @param length
     *            the number of bytes to convert.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}.
     */
    public String(byte[] data, int start, int length) 
    {
    }
    
    /**
     * Converts the byte array to a string using the specified encoding.
     * 
     * @param data
     *            the byte array to convert to a string.
     * @param start
     *            the starting offset in the byte array.
     * @param length
     *            the number of bytes to convert.
     * @param encoding
     *            the encoding.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}.
     * @throws UnsupportedEncodingException
     *             if {@code encoding} is not supported.
     */
    public String(byte[] data, int start, int length, final String encoding) throws UnsupportedEncodingException 
    {
    }

    /**
     * Converts the byte array to a string using the specified encoding.
     * 
     * @param data
     *            the byte array to convert to a string.
     * @param encoding
     *            the encoding.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws UnsupportedEncodingException
     *             if {@code encoding} is not supported.
     */
    public String(byte[] data, String encoding) throws UnsupportedEncodingException 
    {
    }

    /**
     * Initializes this string to contain the characters in the specified
     * character array. Modifying the character array after creating the string
     * has no effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     */
    public String(char[] data) 
    {
    }

    /**
     * Initializes this string to contain the specified characters in the
     * character array. Modifying the character array after creating the string
     * has no effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @param start
     *            the starting offset in the character array.
     * @param length
     *            the number of characters to use.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}
     */
    public String(char[] data, int start, int length) 
    {
    }

    /**
     * Creates a {@code String} that is a copy of the specified string.
     * 
     * @param string
     *            the string to copy.
     */
    public String(String string) 
    {
    }

    /**
     * Creates a {@code String} from the contents of the specified
     * {@code StringBuffer}.
     * 
     * @param stringbuffer
     *            the buffer to get the contents from.
     */
    public String(StringBuffer stringbuffer) 
    {
    }

    /**
     * Creates a {@code String} from the contents of the specified {@code
     * StringBuilder}.
     * 
     * @param sb
     *            the {@code StringBuilder} to copy the contents from.
     * @throws NullPointerException
     *             if {@code sb} is {@code null}.
     * @since 1.5
     */
    public String(StringBuilder sb) 
    {
    }


    /**
     * Returns the character at the specified offset in this string.
     * 
     * @param index
     *            the zero-based index in this string.
     * @return the character at the index.
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0} or {@code index >= length()}.
     */
    public native char charAt(int index) ;

    /**
     * Compares the specified string to this string using the Unicode values of
     * the characters. Returns 0 if the strings contain the same characters in
     * the same order. Returns a negative integer if the first non-equal
     * character in this string has a Unicode value which is less than the
     * Unicode value of the character at the same position in the specified
     * string, or if this string is a prefix of the specified string. Returns a
     * positive integer if the first non-equal character in this string has a
     * Unicode value which is greater than the Unicode value of the character at
     * the same position in the specified string, or if the specified string is
     * a prefix of this string.
     * 
     * @param string
     *            the string to compare.
     * @return 0 if the strings are equal, a negative integer if this string is
     *         before the specified string, or a positive integer if this string
     *         is after the specified string.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public native int compareTo(String string);

    /**
     * Compares the specified string to this string using the Unicode values of
     * the characters, ignoring case differences. Returns 0 if the strings
     * contain the same characters in the same order. Returns a negative integer
     * if the first non-equal character in this string has a Unicode value which
     * is less than the Unicode value of the character at the same position in
     * the specified string, or if this string is a prefix of the specified
     * string. Returns a positive integer if the first non-equal character in
     * this string has a Unicode value which is greater than the Unicode value
     * of the character at the same position in the specified string, or if the
     * specified string is a prefix of this string.
     * 
     * @param string
     *            the string to compare.
     * @return 0 if the strings are equal, a negative integer if this string is
     *         before the specified string, or a positive integer if this string
     *         is after the specified string.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public native int compareToIgnoreCase(String string);

    /**
     * Concatenates this string and the specified string.
     * 
     * @param string
     *            the string to concatenate
     * @return a new string which is the concatenation of this string and the
     *         specified string.
     */
    public native String concat(String string);

    /**
     * Creates a new string containing the characters in the specified character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @return the new string.
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     */
    public static String copyValueOf(char[] data) 
    {
        return new String(data, 0, data.length);
    }

    /**
     * Creates a new string containing the specified characters in the character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @param start
     *            the starting offset in the character array.
     * @param length
     *            the number of characters to use.
     * @return the new string.
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}.
     */
    public native static String copyValueOf(char[] data, int start, int length) ;

    /**
     * Compares the specified string to this string to determine if the
     * specified string is a suffix.
     * 
     * @param suffix
     *            the suffix to look for.
     * @return {@code true} if the specified string is a suffix of this string,
     *         {@code false} otherwise.
     * @throws NullPointerException
     *             if {@code suffix} is {@code null}.
     */
    public native boolean endsWith(String suffix) ;

    /**
     * Compares the specified object to this string and returns true if they are
     * equal. The object must be an instance of string with the same characters
     * in the same order.
     * 
     * @param object
     *            the object to compare.
     * @return {@code true} if the specified object is equal to this string,
     *         {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public native boolean equals(Object object);

    /**
     * Compares the specified string to this string ignoring the case of the
     * characters and returns true if they are equal.
     * 
     * @param string
     *            the string to compare.
     * @return {@code true} if the specified string is equal to this string,
     *         {@code false} otherwise.
     */
    public native boolean equalsIgnoreCase(String string);

    /**
     * Converts this string to a byte array using the default encoding as
     * specified by the file.encoding system property. If the system property is
     * not defined, the default encoding is ISO8859_1 (ISO-Latin-1). If 8859-1
     * is not available, an ASCII encoding is used.
     * 
     * @return the byte array encoding of this string.
     */
    public native byte[] getBytes();

    /**
     * Converts this string to a byte array using the specified encoding.
     * 
     * @param encoding
     *            the encoding to use.
     * @return the encoded byte array of this string.
     * @throws UnsupportedEncodingException
     *             if the encoding is not supported.
     */
    public native byte[] getBytes(String encoding) throws UnsupportedEncodingException;

    /**
     * Copies the specified characters in this string to the character array
     * starting at the specified offset in the character array.
     * 
     * @param start
     *            the starting offset of characters to copy.
     * @param end
     *            the ending offset of characters to copy.
     * @param buffer
     *            the destination character array.
     * @param index
     *            the starting offset in the character array.
     * @throws NullPointerException
     *             if {@code buffer} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0}, {@code end > length()}, {@code start >
     *             end}, {@code index < 0}, {@code end - start > buffer.length -
     *             index}
     */
    public native void getChars(int start, int end, char[] buffer, int index);

    @Override
    public native int hashCode() ;

    /**
     * Searches in this string for the first index of the specified character.
     * The search for the character starts at the beginning and moves towards
     * the end of this string.
     * 
     * @param c
     *            the character to find.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public native int indexOf(int c) ;

    /**
     * Searches in this string for the index of the specified character. The
     * search for the character starts at the specified offset and moves towards
     * the end of this string.
     * 
     * @param c
     *            the character to find.
     * @param start
     *            the starting offset.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public native int indexOf(int c, int start) ;

    /**
     * Searches in this string for the first index of the specified string. The
     * search for the string starts at the beginning and moves towards the end
     * of this string.
     * 
     * @param string
     *            the string to find.
     * @return the index of the first character of the specified string in this
     *         string, -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public native int indexOf(String string) ;

    /**
     * Searches in this string for the index of the specified string. The search
     * for the string starts at the specified offset and moves towards the end
     * of this string.
     * 
     * @param subString
     *            the string to find.
     * @param start
     *            the starting offset.
     * @return the index of the first character of the specified string in this
     *         string, -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code subString} is {@code null}.
     */
    public native int indexOf(String subString, int start) ;

    /**
     * Searches an internal table of strings for a string equal to this string.
     * If the string is not in the table, it is added. Returns the string
     * contained in the table which is equal to this string. The same string
     * object is always returned for strings which are equal.
     * 
     * @return the interned string equal to this string.
     */
    public native String intern();

    /**
     * Searches in this string for the last index of the specified character.
     * The search for the character starts at the end and moves towards the
     * beginning of this string.
     * 
     * @param c
     *            the character to find.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public native int lastIndexOf(int c) ;

    /**
     * Searches in this string for the index of the specified character. The
     * search for the character starts at the specified offset and moves towards
     * the beginning of this string.
     * 
     * @param c
     *            the character to find.
     * @param start
     *            the starting offset.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public native int lastIndexOf(int c, int start) ;
    
    /**
     * Searches in this string for the last index of the specified string. The
     * search for the string starts at the end and moves towards the beginning
     * of this string.
     * 
     * @param string
     *            the string to find.
     * @return the index of the first character of the specified string in this
     *         string, -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public native int lastIndexOf(String string) ;

    /**
     * Searches in this string for the index of the specified string. The search
     * for the string starts at the specified offset and moves towards the
     * beginning of this string.
     * 
     * @param subString
     *            the string to find.
     * @param start
     *            the starting offset.
     * @return the index of the first character of the specified string in this
     *         string , -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code subString} is {@code null}.
     */
    public native int lastIndexOf(String subString, int start) ;

    /**
     * Returns the size of this string.
     * 
     * @return the number of characters in this string.
     */
    public native int length() ;

    /**
     * Compares the specified string to this string and compares the specified
     * range of characters to determine if they are the same.
     * 
     * @param thisStart
     *            the starting offset in this string.
     * @param string
     *            the string to compare.
     * @param start
     *            the starting offset in the specified string.
     * @param length
     *            the number of characters to compare.
     * @return {@code true} if the ranges of characters are equal, {@code false}
     *         otherwise
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public native boolean regionMatches(int thisStart, String string, int start, int length) ;

    /**
     * Compares the specified string to this string and compares the specified
     * range of characters to determine if they are the same. When ignoreCase is
     * true, the case of the characters is ignored during the comparison.
     * 
     * @param ignoreCase
     *            specifies if case should be ignored.
     * @param thisStart
     *            the starting offset in this string.
     * @param string
     *            the string to compare.
     * @param start
     *            the starting offset in the specified string.
     * @param length
     *            the number of characters to compare.
     * @return {@code true} if the ranges of characters are equal, {@code false}
     *         otherwise.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public native boolean regionMatches(boolean ignoreCase, int thisStart,
            String string, int start, int length);
    
    /**
     * Copies this string replacing occurrences of the specified character with
     * another character.
     * 
     * @param oldChar
     *            the character to replace.
     * @param newChar
     *            the replacement character.
     * @return a new string with occurrences of oldChar replaced by newChar.
     */
    public native String replace(char oldChar, char newChar);

    /**
     * Copies this string replacing occurrences of the specified target sequence
     * with another sequence. The string is processed from the beginning to the
     * end.
     * 
     * @param target
     *            the sequence to replace.
     * @param replacement
     *            the replacement sequence.
     * @return the resulting string.
     * @throws NullPointerException
     *             if {@code target} or {@code replacement} is {@code null}.
     */
    public native String replace(CharSequence target, CharSequence replacement);

    /**
     * Compares the specified string to this string to determine if the
     * specified string is a prefix.
     * 
     * @param prefix
     *            the string to look for.
     * @return {@code true} if the specified string is a prefix of this string,
     *         {@code false} otherwise
     * @throws NullPointerException
     *             if {@code prefix} is {@code null}.
     */
    public native boolean startsWith(String prefix) ;

    /**
     * Compares the specified string to this string, starting at the specified
     * offset, to determine if the specified string is a prefix.
     * 
     * @param prefix
     *            the string to look for.
     * @param start
     *            the starting offset.
     * @return {@code true} if the specified string occurs in this string at the
     *         specified offset, {@code false} otherwise.
     * @throws NullPointerException
     *             if {@code prefix} is {@code null}.
     */
    public native boolean startsWith(String prefix, int start) ;

    /**
     * Copies a range of characters into a new string.
     * 
     * @param start
     *            the offset of the first character.
     * @return a new string containing the characters from start to the end of
     *         the string.
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0} or {@code start > length()}.
     */
    public native String substring(int start) ;

    /**
     * Copies a range of characters into a new string.
     * 
     * @param start
     *            the offset of the first character.
     * @param end
     *            the offset one past the last character.
     * @return a new string containing the characters from start to end - 1
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0}, {@code start > end} or {@code end >
     *             length()}.
     */
    public native String substring(int start, int end) ;

    /**
     * Copies the characters in this string to a character array.
     * 
     * @return a character array containing the characters of this string.
     */
    public native char[] toCharArray();

    /**
     * Converts the characters in this string to lowercase, using the default
     * Locale.
     * 
     * @return a new string containing the lowercase characters equivalent to
     *         the characters in this string.
     */
    public native String toLowerCase();

    /**
     * Returns this string.
     *
     * @return this string.
     */
    @Override
    public native String toString() ;

    /**
     * Converts the characters in this string to uppercase, using the default
     * Locale.
     * 
     * @return a new string containing the uppercase characters equivalent to
     *         the characters in this string.
     */
    public native String toUpperCase();

    /**
     * Copies this string removing white space characters from the beginning and
     * end of the string.
     * 
     * @return a new string with characters <code><= \\u0020</code> removed from
     *         the beginning and the end.
     */
    public native String trim();

    /**
     * Creates a new string containing the characters in the specified character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @return the new string.
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     */
    public native static String valueOf(char[] data) ;

    /**
     * Creates a new string containing the specified characters in the character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @param start
     *            the starting offset in the character array.
     * @param length
     *            the number of characters to use.
     * @return the new string.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0}, {@code start < 0} or {@code start +
     *             length > data.length}
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     */
    public native static String valueOf(char[] data, int start, int length) ;

    /**
     * Converts the specified character to its string representation.
     * 
     * @param value
     *            the character.
     * @return the character converted to a string.
     */
    public native static String valueOf(char value);

    /**
     * Converts the specified double to its string representation.
     * 
     * @param value
     *            the double.
     * @return the double converted to a string.
     */
    public native static String valueOf(double value) ;

    /**
     * Converts the specified float to its string representation.
     * 
     * @param value
     *            the float.
     * @return the float converted to a string.
     */
    public native static String valueOf(float value) ;

    /**
     * Converts the specified integer to its string representation.
     * 
     * @param value
     *            the integer.
     * @return the integer converted to a string.
     */
    public native static String valueOf(int value) ;

    /**
     * Converts the specified long to its string representation.
     * 
     * @param value
     *            the long.
     * @return the long converted to a string.
     */
    public native static String valueOf(long value) ;

    /**
     * Converts the specified object to its string representation. If the object
     * is null return the string {@code "null"}, otherwise use {@code
     * toString()} to get the string representation.
     * 
     * @param value
     *            the object.
     * @return the object converted to a string, or the string {@code "null"}.
     */
    public native static String valueOf(Object value) ;

    /**
     * Converts the specified boolean to its string representation. When the
     * boolean is {@code true} return {@code "true"}, otherwise return {@code
     * "false"}.
     * 
     * @param value
     *            the boolean.
     * @return the boolean converted to a string.
     */
    public native static String valueOf(boolean value) ;

    /**
     * Returns whether the characters in the StringBuffer {@code strbuf} are the
     * same as those in this string.
     * 
     * @param strbuf
     *            the StringBuffer to compare this string to.
     * @return {@code true} if the characters in {@code strbuf} are identical to
     *         those in this string. If they are not, {@code false} will be
     *         returned.
     * @throws NullPointerException
     *             if {@code strbuf} is {@code null}.
     * @since 1.4
     */
    public native boolean contentEquals(StringBuffer strbuf) ;
    
    /**
     * Compares a {@code CharSequence} to this {@code String} to determine if
     * their contents are equal.
     *
     * @param cs
     *            the character sequence to compare to.
     * @return {@code true} if equal, otherwise {@code false}
     * @since 1.5
     */
    public native boolean contentEquals(CharSequence cs) ;

    /**
     * Has the same result as the substring function, but is present so that
     * string may implement the CharSequence interface.
     * 
     * @param start
     *            the offset the first character.
     * @param end
     *            the offset of one past the last character to include.
     * @return the subsequence requested.
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0}, {@code end < 0}, {@code start > end} or
     *             {@code end > length()}.
     * @see java.lang.CharSequence#subSequence(int, int)
     * @since 1.4
     */
    public native CharSequence subSequence(int start, int end) ;

    /**
     * Determines if this {@code String} contains the sequence of characters in
     * the {@code CharSequence} passed.
     *
     * @param cs
     *            the character sequence to search for.
     * @return {@code true} if the sequence of characters are contained in this
     *         string, otherwise {@code false}.
     * @since 1.5
     */
    public native boolean contains(CharSequence cs) ;

    /**
     * Returns a formatted string, using the supplied format and arguments,
     * using the default locale.
     * 
     * @param format
     *            a format string.
     * @param args
     *            arguments to replace format specifiers (may be none).
     * @return the formatted string.
     * @throws NullPointerException
     *             if {@code format} is {@code null}.
     * @throws java.util.IllegalFormatException
     *             if the format is invalid.
     * @see java.util.Formatter
     * @since 1.5
     */
    public static native String format(String format, Object... args);

    /*
     * Returns the character array for this string.
     */
    native char[] getValue();
}
