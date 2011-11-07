package no.uio.tools.testdoc.data;

import java.util.LinkedList;

public class TestDocTaskData {

    private String title;

    private LinkedList<String> checks;


    public void setTitle(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }


    public void setChecks(LinkedList<String> checks) {
        this.checks = checks;
    }


    public LinkedList<String> getChecks() {
        return checks;
    }

}
