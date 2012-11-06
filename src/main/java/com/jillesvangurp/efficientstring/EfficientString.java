package com.jillesvangurp.efficientstring;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

import com.jillesvangurp.efficientstring.EfficientStringBiMap.Bucket;

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
 * 
 * This class is thread safe and locks at the bucket level.
 */
public class EfficientString {
    static final int HASH_MODULO = 50000;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final byte[] bytes;
    // a fast enough hash function
    private static final CRC32 CRC_32 = new CRC32();

    private final int hashCode;

    private static EfficientStringBiMap allStrings = new EfficientStringBiMap();
    // static int index = 0;
    private static AtomicInteger index = new AtomicInteger(0);
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
        Bucket bucket = allStrings.getOrCreateBucket(efficientString.hashCode);

        int existingIndex = allStrings.get(efficientString);
        if (existingIndex >= 0) {
            return allStrings.get(existingIndex);
        } else {
            synchronized (bucket) {
                existingIndex = bucket.get(efficientString);
                if (existingIndex >= 0) {
                    // conflicting write
                    return allStrings.get(existingIndex);
                }
                // no conflict
                efficientString.myIndex = index.getAndIncrement();
                allStrings.put(efficientString);
            }
        }
        return efficientString;
    }

    /**
     * @param index
     * @return the efficient string or null if it doesn't exist.
     */
    public static EfficientString get(int index) {
        return allStrings.get(index);
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
        return index.get();
    }

    private int calculateHashCode() {
        CRC_32.reset();
        CRC_32.update(bytes);
        // this ensures buckets can contain quite a few entries but it saves memory space
        return (int) (CRC_32.getValue() % HASH_MODULO);
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
    
    /**
     * Clears the registry of existing Strings and resets the index to 0. Use this to free up memory.
     */
    public static void clear() {
        allStrings = new EfficientStringBiMap();
        index.set(0);
    }
}