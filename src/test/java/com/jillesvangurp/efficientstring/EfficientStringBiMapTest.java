package com.jillesvangurp.efficientstring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

@Test
public class EfficientStringBiMapTest {

    public void shouldPutAndGetStuff() {
        EfficientStringBiMap map = new EfficientStringBiMap(EfficientString.HASH_MODULO);
        EfficientString es = EfficientString.fromString("foo");
        map.put(es);
        assertThat(map.get(es), is(es.index()));
        assertThat(map.get(es.index()), is(es));
    }
}
