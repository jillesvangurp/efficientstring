package com.jillesvangurp.efficientstring;

import java.util.Arrays;

/**
 * Multi map for integers. May be used for storing efficient string indices. This class is thread safe.
 */
public class IntIntMultiMap {
    private static final int BUCKETS = 50000;
    private final Bucket[] buckets = new Bucket[BUCKETS];
    
    public void put(int key, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("put can only take positive values");
        }
        Bucket bucket = getOrCreateBucket(key);
        synchronized (bucket) {
            bucket.put(key, value);
        }
    }

    public int[] get(int key) {
        Bucket bucket = buckets[getBucketIndex(key)];
        if (bucket != null) {
            return bucket.get(key);
        }
        return null;

    }

    private Bucket getOrCreateBucket(int key) {
        int index = getBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null) {
            synchronized (buckets) {
                bucket = buckets[index];
                if (bucket == null) {
                    bucket = new Bucket();
                    buckets[index] = bucket;
                }
            }
        }
        return bucket;
    }

    private int getBucketIndex(int key) {
        return key % BUCKETS;
    }

    class Bucket {
        private int[][] values = new int[5][0];

        public void put(int key, int value) {
            int keyIndex = -1;
            for (int i = 0; i < values.length; i++) {
                int[] list = values[i];
                if (list.length == 0) {
                    // empty slot, use it
                    list = createList(key);
                    values[i] = list;
                    keyIndex = i;
                    break;
                } else {
                    if (list[0] == key) {
                        keyIndex = i;
                        break;
                    }
                }
            }
            if (keyIndex == -1) {
                // we need more space
                int oldLength = values.length;
                values = Arrays.copyOf(values, (int) (oldLength * 1.3));
                values[oldLength] = createList(key);
                keyIndex = oldLength;
            }
            int[] keyList = values[keyIndex];
            for (int i = 1; i < keyList.length; i++) {
                if (keyList[i] == -1) {
                    keyList[i] = value;
                    // we're done
                    return;
                } else if (keyList[i] == value) {
                    // already contained
                    return;
                }
            }
            // the keyList needs to be expanded
            int oldKeyListLength = keyList.length;
            keyList = Arrays.copyOf(keyList, (int) (oldKeyListLength * 1.3));
            for (int i = oldKeyListLength+1; i < keyList.length; i++) {
                keyList[i]=-1;
            }
            values[keyIndex] = keyList;
            keyList[oldKeyListLength] = value;
        }

        private int[] createList(int key) {
            int[] list;
            list = new int[5];
            Arrays.fill(list, -1);
            list[0] = key;
            return list;
        }

        public int[] get(int key) {
            for (int i = 0; i < values.length; i++) {
                int[] keyList = values[i];
                if (keyList.length > 0 && keyList[0] == key) {
                    int empty = 0;
                    for (int j = keyList.length - 1; j >= 0; j--) {
                        if (keyList[j] == -1) {
                            empty++;
                        } else {
                            break;
                        }
                    }

                    return Arrays.copyOfRange(keyList, 1, keyList.length - empty);
                }
            }
            return null;
        }
    }
}
