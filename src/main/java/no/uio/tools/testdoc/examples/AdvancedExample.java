package no.uio.tools.testdoc.examples;

import static junit.framework.Assert.assertTrue;
import no.uio.tools.testdoc.annotations.TestDocPlan;
import no.uio.tools.testdoc.annotations.TestDocTask;
import no.uio.tools.testdoc.annotations.TestDocTasks;
import no.uio.tools.testdoc.annotations.TestDocTest;

import org.junit.Test;

@TestDocPlan(title = "Advanced TestDoc example", sortOrder = 2)
public class AdvancedExample {

    public static void toBeImplemented() {
    }


    @Test
    @TestDocTest("Test av innloggingsside")
    @TestDocTasks({
            @TestDocTask(task = "Gå til innloggingsside", checks = "Siden ser bra ut"),
            @TestDocTask(task = "Fyll ut brukernavn og passord", checks = { "Det er ikke autocomplete",
                    "Login knapp virker" }) })
    public void userLogin() {
        assertTrue(true);
    }


    @Test
    @TestDocTest("First test description")
    @TestDocTask(task = "Single task description", checks = "Desired behaviour")
    public void userLogout() {
    }


    @TestDocTest("Test search- and detailspage")
    @TestDocTasks({
            @TestDocTask(task = "Go to search page", checks = "Searchform is present"),
            @TestDocTask(task = "Search for 'uio'", checks = { "See that you get 4 searchresults",
                    "See that 'usit' is one of the searchresults" }) })
    @TestDocTask(task = "Single task description", checks = "Desired behaviour")
    public void search() {
    }


    @TestDocTest("Test with no tasks and checks")
    public void svadaTest() {
    }


    @Test
    @TestDocTask(task = "Tasks with no description", checks = "Desired behaviour")
    public void loremIpsumTest() {
    }

}
