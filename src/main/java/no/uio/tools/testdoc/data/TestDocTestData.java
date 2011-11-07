package no.uio.tools.testdoc.data;

import java.util.Iterator;
import java.util.LinkedList;

public class TestDocTestData {

    private String title;
    private int number;
    private boolean implemented;


    public boolean isImplemented() {
        return implemented;
    }


    public void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }

    private LinkedList<TestDocTaskData> tasks;


    public void setTitle(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }


    public void setTasks(LinkedList<TestDocTaskData> tasks) {
        this.tasks = tasks;
    }


    public LinkedList<TestDocTaskData> getTasks() {
        return tasks;
    }


    public void setNumber(int number) {
        this.number = number;
    }


    public int getNumber() {
        return number;
    }


    /**
     * Number number of checks in all tasks.
     * 
     * @return
     */
    public int getChecksCount() {
        int count = 0;
        LinkedList<TestDocTaskData> tasks = getTasks();
        for (Iterator<TestDocTaskData> iterator = tasks.iterator(); iterator.hasNext();) {
            TestDocTaskData task = iterator.next();
            count = count + task.getChecks().size();
        }
        return count;
    }

}
