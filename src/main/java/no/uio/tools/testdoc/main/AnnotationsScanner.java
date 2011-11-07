package no.uio.tools.testdoc.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import no.uio.tools.testdoc.annotations.TestDocPlan;
import no.uio.tools.testdoc.annotations.TestDocTask;
import no.uio.tools.testdoc.annotations.TestDocTasks;
import no.uio.tools.testdoc.annotations.TestDocTest;
import no.uio.tools.testdoc.data.TestDocPlanData;
import no.uio.tools.testdoc.data.TestDocTaskData;
import no.uio.tools.testdoc.data.TestDocTestData;

import org.apache.maven.reporting.MavenReportException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class AnnotationsScanner {

    public static boolean debug = false;


    /* Returns a list of all classes annotated with the @TestDocTest or @TestDocPlan annotations. */
    public static List<Class<?>> findAllAnnotatedClasses() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage(""))
                .setScanners(new ResourcesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner()));

        List<Class<?>> annotatedClasses = findClassesAnnotatedWithTestDocTest(reflections);
        findClassesAnnotatedWithTestDocPlan(reflections, annotatedClasses);
        return annotatedClasses;
    }


    private static void findClassesAnnotatedWithTestDocPlan(Reflections reflections, List<Class<?>> annotatedClasses) {
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(no.uio.tools.testdoc.annotations.TestDocPlan.class);
        for (Class<?> clazz : annotated) {
            if (!annotatedClasses.contains(clazz)) {
                annotatedClasses.add(clazz);
            }
        }
    }


    private static List<Class<?>> findClassesAnnotatedWithTestDocTest(Reflections reflections) {
        Set<Method> annotatedMethods = reflections
                .getMethodsAnnotatedWith(no.uio.tools.testdoc.annotations.TestDocTest.class);

        List<Class<?>> annotatedClasses = new ArrayList<Class<?>>();
        for (Method method : annotatedMethods) {
            Class<?> clazz = method.getDeclaringClass();
            if (!annotatedClasses.contains(clazz)) {
                annotatedClasses.add(clazz);
            }
        }
        return annotatedClasses;
    }


    private static boolean hasTestDocAnnotations(Method method) {
        TestDocTest testDocTest = (TestDocTest) method.getAnnotation(TestDocTest.class);
        TestDocTasks testDocTasks = (TestDocTasks) method.getAnnotation(TestDocTasks.class);
        TestDocTask testDocTask = (TestDocTask) method.getAnnotation(TestDocTask.class);

        // System.out.println("Method: " + method.getName());
        // if (testDocTest != null && testDocTest.value() != null) {
        // System.out.println("  Test : " + testDocTest.value());
        // }
        //
        // if (testDocTasks != null && testDocTasks.value() != null) {
        // System.out.println("  Tasks: " + testDocTasks.value().length);
        // }
        // if (testDocTask != null && testDocTask.task() != null) {
        // System.out.println("  Task : " + testDocTask.task());
        // }

        return (testDocTest != null && testDocTest.value() != null)
                || (testDocTasks != null && testDocTasks.value() != null)
                || (testDocTask != null && testDocTask.task() != null);
    }


    /* Read annotations from a class and return data objects. */
    public static TestDocPlanData getAnnotationsFromClass(Class<?> clazz, boolean failIfMissingTestPlanTitle)
            throws ClassNotFoundException, MavenReportException {

        TestDocPlanData testDocPlanData = new TestDocPlanData();
        LinkedList<TestDocTestData> testsList = new LinkedList<TestDocTestData>();
        TestDocPlan testdocPlan = (TestDocPlan) clazz.getAnnotation(TestDocPlan.class);

        if (testdocPlan != null) {
            if (testdocPlan.title() != null) {
                testDocPlanData.setTitle(testdocPlan.title());
            }
            testDocPlanData.setSortOrder(testdocPlan.sortOrder());
            testDocPlanData.setClassName(clazz.getName());
        } else {
            testDocPlanData.setTitle(null);
        }

        if (failIfMissingTestPlanTitle && (testDocPlanData.getTitle() == null || testDocPlanData.getTitle().equals(""))) {
            throw new MavenReportException("TestDoc Error: Missing tag @TestDocplan(title=...) in class "
                    + testDocPlanData.getClassName());
        }

        int testsCount = 0;

        Method[] methods = null;
        methods = clazz.getMethods();
        if (methods == null) {
            return null;
        }

        for (Method m : methods) {

            if (hasTestDocAnnotations(m)) {

                TestDocTest testDocTestAnnotations = (TestDocTest) m.getAnnotation(TestDocTest.class);
                TestDocTasks testDocTasksAnnotations = (TestDocTasks) m.getAnnotation(TestDocTasks.class);
                TestDocTask testDocTaskAnnotations = (TestDocTask) m.getAnnotation(TestDocTask.class);
                org.junit.Test testAnnotation = (org.junit.Test) m.getAnnotation(org.junit.Test.class);

                TestDocTestData testDocTestData = new TestDocTestData();
                /* Set the implemented flag to false if method has noe @Test annotation. */
                testDocTestData.setImplemented((testAnnotation != null));

                /* Example: @TestDocTest("Test login page") */
                if (testDocTestAnnotations != null) {
                    testDocTestData.setTitle(testDocTestAnnotations.value());
                } else {
                    testDocTestData.setTitle("(no title!!!)"); // TODO Remove
                }

                /* @TestDocTasks(@TestDocTask(task = "Go to login page",check="Is there a login form?") */
                LinkedList<TestDocTaskData> tasksLists = new LinkedList<TestDocTaskData>();
                if (testDocTasksAnnotations != null) {

                    TestDocTask[] tasks = (TestDocTask[]) testDocTasksAnnotations.value();

                    for (int i = 0; i < tasks.length; i++) {
                        TestDocTaskData taskData = new TestDocTaskData();
                        taskData.setTitle(tasks[i].task());
                        String[] checks = tasks[i].checks();
                        LinkedList<String> checksData = new LinkedList<String>();
                        for (int j = 0; j < checks.length; j++) {
                            checksData.add(checks[j]);
                        }
                        if (checks.length > 0) {
                            taskData.setChecks(checksData);
                        }
                        tasksLists.add(taskData);

                    }

                }

                /* If unit test has a single TestDocTask annoation, then add it to the list. */
                // testDocTask = (TestDocTask) m.getAnnotation(TestDocTask.class);
                if (testDocTaskAnnotations != null) {
                    TestDocTaskData taskData = new TestDocTaskData();
                    taskData.setTitle(testDocTaskAnnotations.task());

                    String[] checks = testDocTaskAnnotations.checks();
                    LinkedList<String> checksData = new LinkedList<String>();
                    for (int j = 0; j < checks.length; j++) {
                        checksData.add(checks[j]);
                    }
                    if (checks.length > 0) {
                        taskData.setChecks(checksData);
                    }
                    tasksLists.add(taskData);

                }

                if (tasksLists.size() > 0) {
                    testDocTestData.setTasks(tasksLists);
                }

                if (testDocTestData.getTitle() != null || testDocTestData.getTasks() != null) {
                    testsCount = testsCount + 1;
                    testDocTestData.setNumber(testsCount);
                    testDocPlanData.setClassName(clazz.getName());
                    testsList.add(testDocTestData);
                }
            }
        }
        if (testsList.size() > 0) {
            testDocPlanData.setTests(testsList);
            testDocPlanData.setClassName(clazz.getName());
        }
        if (testDocPlanData.getTitle() == null && testDocPlanData.getTests() == null) {
            return null;
        }
        return testDocPlanData;
    }
}
