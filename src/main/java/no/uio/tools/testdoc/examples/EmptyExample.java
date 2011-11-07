package no.uio.tools.testdoc.examples;

import no.uio.tools.testdoc.annotations.TestDocPlan;

import org.junit.Test;

@TestDocPlan(title = "Empty example with no tests annotated with TestDocTest")
public class EmptyExample {

    @Test
    public void testUserLogin() {
        assert (true);
    }

}
