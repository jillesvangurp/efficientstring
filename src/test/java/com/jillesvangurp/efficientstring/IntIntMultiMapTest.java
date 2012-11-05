package com.jillesvangurp.efficientstring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashSet;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;

@Test
public class IntIntMultiMapTest {    
    public void shouldInsertTwoIntsWithSameKeyAndReturnThem() {
        IntIntMultiMap map = new IntIntMultiMap();
        map.put(42,1);
        map.put(42,1);
        map.put(42,2);
        int[] values = map.get(42);
        assertThat(values.length, is(2));
        assertThat(values[0],is(1));
        assertThat(values[1], is(2));
    }
    
    public void shouldFillBuckets() {
        IntIntMultiMap map = new IntIntMultiMap();
        for(int i=0; i<1000000; i++) {
            map.put(i %100000, i%11);
        }
        
        for(int i=0; i<100000;i++) {
            int[] values = map.get(i);
            assertThat(values.length, greaterThan(9));
            assertThat(values.length, lessThan(12));
            HashSet<Integer> unique = Sets.newHashSet();
            for (int j = 0; j < values.length; j++) {
                unique.add(values[j]);
            }
            assertThat(unique.size(), is(values.length));
        }        
    }
}
