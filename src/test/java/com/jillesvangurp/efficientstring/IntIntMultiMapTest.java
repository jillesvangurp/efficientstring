package com.jillesvangurp.efficientstring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

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
    
    public void shouldIterate() {
        IntIntMultiMap map = new IntIntMultiMap();
        for(int i=0; i<666; i++) {
            for(int j=0;j<13;j++) {
                map.put(i , j);
            }
        }
        int count=0;
        for(Entry<Integer, Integer> e: map) {
            assertThat(e.getKey(), greaterThan(-1));
            assertThat(e.getValue(), greaterThan(-1));
            count++;
        }
        assertThat(count,is(666*13));
    }
    
    public void shouldIterateOverLists() {
        IntIntMultiMap map = new IntIntMultiMap();
        for(int i=0; i<666; i++) {
            for(int j=0;j<13;j++) {
                map.put(i , j);
            }
        }
        int count=0;
        for(Entry<Integer, Set<Integer>> e: map.values()) {
            count+=e.getValue().size();
        }
        assertThat(count,is(666*13));
    }
}
