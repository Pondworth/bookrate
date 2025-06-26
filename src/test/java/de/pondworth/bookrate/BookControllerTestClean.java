package de.pondworth.bookrate;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookControllerTestClean {

    @Test
    void testWithHamcrest() {
        int actual = 5;
        int expected = 5;
        assertThat(actual, equalTo(expected));
    }
}
