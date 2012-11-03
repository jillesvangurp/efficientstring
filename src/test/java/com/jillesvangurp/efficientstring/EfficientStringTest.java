package com.jillesvangurp.efficientstring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

@Test
public class EfficientStringTest {
    
    @BeforeClass
    public void beforeClass() {
        EfficientString.fromString("abc");
        EfficientString.fromString("def");
        EfficientString.fromString("ghi");
        EfficientString.fromString("jkl");
    }

    public void shouldNotCreateNewStrings() {
        assertThat(EfficientString.fromString("abc").index(), is(0));
        assertThat(EfficientString.fromString("def").index(), is(1));
        assertThat(EfficientString.fromString("ghi").index(), is(2));
        assertThat(EfficientString.fromString("jkl").index(), is(3));
    }
    
    public void shouldCreateNewString() {
        assertThat(EfficientString.fromString("foo").index(), greaterThan(3));        
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
