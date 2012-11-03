package com.jillesvangurp.efficientstring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;

@Test
public class EfficientStringTest {
    
    public void shouldCreateNewString() {
        int next = EfficientString.nextIndex();
        String str = "foo"+System.currentTimeMillis();
        EfficientString es = EfficientString.fromString(str);
        assertThat(es.index(), is(next));
        assertThat(EfficientString.get(next), is(es));
    }
    
    public void shouldShareSameInstance() {
        EfficientString es1 = EfficientString.fromString("imunique");
        EfficientString es2 = EfficientString.fromString("imunique");
        assertThat("should be same instance", es1==es2);
    }
    
    public void shouldBeAbleToRetrieveUsingIndex() {
        EfficientString es = EfficientString.fromString("bar");
        assertThat(EfficientString.get(es.index()).toString(), is("bar"));
    }
    
    public void shouldStoreAndRetrieveManyStrings() {
        Set<EfficientString> created = Sets.newHashSet();
        //exceed the average bucket size to force growth of the buckets
        for(int i=0;i<6*EfficientString.HASH_MODULO;i++) { 
            EfficientString fromString = EfficientString.fromString(""+i);
            assertThat(fromString, notNullValue());
            created.add(fromString);            
        }
        for (EfficientString efficientString : created) {
            assertThat(EfficientString.get(efficientString.index()), is(efficientString));
            assertThat(EfficientString.fromString(efficientString.toString()), is(efficientString));
        }
    }
}
