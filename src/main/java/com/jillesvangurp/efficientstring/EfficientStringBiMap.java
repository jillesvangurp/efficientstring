package com.jillesvangurp.efficientstring;

import static com.jillesvangurp.efficientstring.EfficientString.HASH_MODULO;

import java.util.Arrays;

/**
 * A memory efficient bimap of EfficientString to int that uses the int index of the EfficientStrings rather
 * than object references..
 */
public class EfficientStringBiMap {
    Bucket[] buckets = new Bucket[HASH_MODULO*2];

    public void put(EfficientString key, int value) {
        Object[] entry = new Object[2];
        entry[0] = key;
        entry[1] = value;
        getOrCreateBucket(key.hashCode()).append(entry);        
        getOrCreateBucket(HASH_MODULO+value%HASH_MODULO).append(entry);
    }

    private Bucket getOrCreateBucket(int index) {
        Bucket bucket = buckets[index];
        if (bucket == null) {
            bucket = new Bucket();
            buckets[index] = bucket;
        }
        return bucket;
    }
    
    public Integer get(EfficientString key) {
        Bucket bucket = buckets[key.hashCode()];
        if(bucket == null) {
            return null;
        } else {
            return bucket.get(key);
        }    
    }
    
    public EfficientString get(int value) {
        Bucket bucket = buckets[HASH_MODULO+value%HASH_MODULO];
        if(bucket==null) {
            return null;
        } else {
            return bucket.get(value);
        }
    }
    
    class Bucket {
        Object[] array = new Object[5];

        public void append(Object[] entry) {
            int freeSlot = findFreeOrExistingSlot(((EfficientString)entry[0]));
            if (freeSlot >= array.length) {
                array = Arrays.copyOf(array, (int) Math.round(array.length * 1.3));
            }
            array[freeSlot] = entry;
        }

        private int findFreeOrExistingSlot(EfficientString key) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
                Object[] entry = (Object[]) array[i];
                if (((EfficientString)entry[0]).index() == key.index()) {
                    return i;
                }
            }
            return array.length;
        }

        public Integer get(EfficientString key) {
            for (Object e : array) {
                if(e==null) {
                    break;
                }
                Object[] entry = (Object[]) e;
                EfficientString k = (EfficientString) entry[0];
                if (key.equals(k)) {
                    return (Integer) entry[1];
                }
            }
            return null;
        }
        
        public EfficientString get(Integer value) {
            for (Object e : array) {
                if(e==null) {
                    break;
                }
                Object[] entry = (Object[]) e;
                Integer v = (Integer) entry[1];
                if (value.equals(v)) {
                    return (EfficientString) entry[0];
                }
            }
            return null;
        }

        public boolean contains(EfficientString key) {
            return get(key) != null;
        }
    }
}
