package no.uio.tools.testdoc.examples;

import no.uio.tools.testdoc.annotations.TestDocPlan;
import no.uio.tools.testdoc.annotations.TestDocTask;
import no.uio.tools.testdoc.annotations.TestDocTest;

import org.junit.Test;

@TestDocPlan(title = "Basic TestDoc example with this title")
public class BasicExample {

    @Test
    @TestDocTest("Test description")
    @TestDocTask(task = "Task description", checks = "Desired behaviour description")
    public void testUserLogin() {
        // Testcode goes here
    }

}
