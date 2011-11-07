package no.uio.tools.testdoc.data;

import java.util.LinkedList;

public class TestDocPlanData implements Comparable {

    private String title;
    private int sortOrder;
    private String className;
    private LinkedList<TestDocTestData> tests;


    public void setTitle(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }


    public void setTests(LinkedList<TestDocTestData> tests) {
        this.tests = tests;
    }


    public LinkedList<TestDocTestData> getTests() {
        return tests;
    }


    public void setClassName(String className) {
        this.className = className;
    }


    public String getClassName() {
        return className;
    }


    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }


    public int getSortOrder() {
        return sortOrder;
    }


    public int compareTo(Object o) {
        TestDocPlanData other = (TestDocPlanData) o;
        return this.sortOrder - other.getSortOrder();
    }

}
