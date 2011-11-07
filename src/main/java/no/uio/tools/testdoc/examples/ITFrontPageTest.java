package no.uio.tools.testdoc.examples;

import static junit.framework.Assert.assertTrue;
import no.uio.tools.testdoc.annotations.TestDocPlan;
import no.uio.tools.testdoc.annotations.TestDocTask;
import no.uio.tools.testdoc.annotations.TestDocTasks;
import no.uio.tools.testdoc.annotations.TestDocTest;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

@TestDocPlan(title = "Test av førstesiden inkludert søking!", sortOrder = -1)
public class ITFrontPageTest {

    private static final Logger logger = Logger.getLogger(ITFrontPageTest.class);


    // hentet tester fra https://www.uio.no/tjenester/it/applikasjoner/meldeapp/drift-utvikling/test/testplan.html
    @Test
    @TestDocTest("Test at vi er kommet til riktig side")
    @TestDocTasks(@TestDocTask(task = "Gå til førstesiden", checks = "Sjekk at tittelen er riktig?"))
    public void testTitle() {
        assertTrue(true);
    }


    @Test
    @TestDocTest("Gå til søkesiden")
    @TestDocTask(task = "Gå til søkeside", checks = "Sjekk at tittelen er riktig")
    public void searchForUSIT() {
        simpleSearch();
        detailsPage();
    }


    @TestDocTest("Ikke implementert test")
    @TestDocTask(task = "Ikke implementert", checks = "Ikke implementert")
    public void testNotImplement() {
    }


    @Test
    @TestDocTest("Test enkeltøk")
    @TestDocTasks({ @TestDocTask(task = "Gå inn på startsiden", checks = "Søkefeltet er tilgjengelig og i fokus"),
            @TestDocTask(task = "Søk på 'USIT'", checks = { "Minst fire elementer i trefflisten", "Sjekk mer." }) })
    public void simpleSearch() {
    }


    @Test
    @TestDocTest("Test av detaljside")
    @TestDocTasks({
            @TestDocTask(task = "Klikk på 'cerebrum' i trefflisten", checks = "Detaljsiden kommer frem"),
            @TestDocTask(task = "Les detaljer om personopplsyninger utleveres til andre innen UiOs nett", checks = "Det står 'Ja' og til hvem"),
            @TestDocTask(task = "Sjekk kategorier", checks = "Det står 'studenter','ansatte' og 'andre'") })
    public void detailsPage() {
    }


    @Test
    @TestDocTest("Test av avansert søk")
    @TestDocTasks({
            @TestDocTask(task = "Klikk på linke 'Avansert søk'", checks = "Side for avansert søk vises"),
            @TestDocTask(task = "Se at du er kommert til 'avansert' søkeside", checks = "Personopplysninger utleveres til andre:") })
    public void seeAdvancedSearchPage() {
    }


    @Before
    public void init() {
    }


    @Test
    public void doesNotContainsErrors() {
    }


    @Test
    public void goToAdvancedSearch() {
        seeAdvancedSearchPage();
    }

}