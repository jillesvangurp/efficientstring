package com.jillesvangurp.efficientstring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

@Test
public class EfficientStringBiMapTest {
    
    public void shouldPutAndGetStuff() {
        EfficientStringBiMap map = new EfficientStringBiMap();
        EfficientString es = EfficientString.fromString("foo");
        map.put(es, 42);
        assertThat(map.get(es), is(42));        
        assertThat(map.get(42), is(es));        
    }
}
