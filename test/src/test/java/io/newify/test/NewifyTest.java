package io.newify.test;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class NewifyTest {

    private io.newify.generated.New subject;

    @Before
    public void setUp() {
        subject = new io.newify.generated.New();
    }

    @Test
    public void newOnEmptyConstructorAddsToNewClass() {
        assertThat(subject.noArgsConstructor(), is(not(nullValue())));
    }
}
