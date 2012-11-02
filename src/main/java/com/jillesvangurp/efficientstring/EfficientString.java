package com.jillesvangurp.efficientstring;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.CRC32;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

/**
 * Efficient String enables you to keep tens of millions of strings in memory and manipulate the resulting data set with
 * e.g. hash maps in a memory efficient manner.
 * 
 * The problem with Strings in Java is that Java stores them as UTF-16 internally, which means that for many scripts you
 * are wasting memory. Additionally the hashCode function is not suitable for extremely large hashmaps and results in
 * too few buckets, which means your insert speed becomes progressively slower.
 * 
 * This class solves both problems and in addition adds the ability to use ints as identifiers for your strings. This
 * limits the number of strings to MAXINT (2.147.483.647), about two billion. Having an int as an identifier means you
 * can store string references in int arrays, which is a lot more compact than having arrays of object references, which
 * are 64 bit in Java. To avoid wasting memory, identical strings have the same integer identifier (i.e. no duplicate
 * strings are created).
 * 
 * The hashcode function is based on a CRC hash of the string with a modulo of 50000. This gives you a reasonable
 * distribution of strings over a hashtable without introducing a lot of memory overhead for creating additional
 * buckets. You may want to tweak this function for your use case.
 */
public class EfficientString {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final byte[] bytes;
    // a fast enough hash function
    private static final CRC32 CRC_32 = new CRC32();
    private final int hashCode;
    private static BiMap<EfficientString, Integer> allStrings = Maps.synchronizedBiMap(HashBiMap.<EfficientString, Integer>create());
    static int index = 0;
    private int myIndex = -1;

    private EfficientString(String s) {
        bytes = s.getBytes(UTF8);
        hashCode = calculateHashCode();
    }

    /**
     * @param s
     * @return efficient string object for the given string
     */
    public static EfficientString fromString(String s) {
        EfficientString efficientString = new EfficientString(s);
        synchronized (allStrings) {
            Integer existingIndex = allStrings.get(efficientString);
            if (existingIndex != null) {
                return allStrings.inverse().get(existingIndex);
            } else {
                efficientString.myIndex = index++;
                allStrings.put(efficientString, efficientString.myIndex);
            }
        }

        return efficientString;
    }

    /**
     * @param index
     * @return the efficient string or null if it doesn't exist.
     */
    public static EfficientString get(int index) {
        return allStrings.inverse().get(index);
    }

    /**
     * @return index of this efficient string.
     */
    public int index() {
        return myIndex;
    }

    /**
     * @return the index that will be used for the next string that is created with fromString.
     */
    public static int nextIndex() {
        return index;
    }

    private int calculateHashCode() {
        CRC_32.reset();
        CRC_32.update(bytes);
        // this ensures buckets can contain quite a few entries but it saves memory space
        return (int) (CRC_32.getValue() % 50000);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EfficientString)) {
            return false;
        }
        EfficientString es = (EfficientString) obj;
        // before they are added to the bimap, efficient strings have an index of -1
        if (es.myIndex != -1 && this.myIndex != -1) {
            return es.myIndex == this.myIndex;
        }
        // edge case that only occurs for strings that are being created
        return Arrays.equals(bytes, es.bytes);
    }

    @Override
    public String toString() {
        return new String(bytes, UTF8);
    }
}