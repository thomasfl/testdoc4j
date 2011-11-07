package no.uio.tools.testdoc.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import no.uio.tools.testdoc.data.TestDocPlanData;
import no.uio.tools.testdoc.data.TestDocTaskData;
import no.uio.tools.testdoc.data.TestDocTestData;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Example:
 * 
 * java -cp ~/.m2/repository/freemarker/freemarker/2.3.8/freemarker-2.3.8.jar:target/testdoc.jar
 * no.uio.tools.testdoc.ClassScanner
 * 
 * @author thomasfl
 * 
 */
public class ClassScanner {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws ClassNotFoundException, IOException, TemplateException {
        String packageName = "no.uio.tools.testdoc";
        System.out.println("Scanning package \"" + packageName + "\"for TestDoc annotations");

        HashMap<String, LinkedList<TestDocPlanData>> datamodel = scanClasses(packageName);

        String output = processFreemarkerTemplate(datamodel, "testdoc.ftl");
        System.out.println(output);
        writeFile("/tmp/testplan.html", output);
    }


    @SuppressWarnings("rawtypes")
    public static HashMap<String, LinkedList<TestDocPlanData>> scanClasses(String packageName)
            throws ClassNotFoundException, IOException {

        HashMap<String, LinkedList<TestDocPlanData>> datamodel = new HashMap<String, LinkedList<TestDocPlanData>>();
        LinkedList<TestDocPlanData> testplans = new LinkedList<TestDocPlanData>();

        List<Class> classes = getClasses(packageName);

        for (Iterator<Class> iterator = classes.iterator(); iterator.hasNext();) {
            String className = iterator.next().getName();
            System.out.println("Scanning: " + className);
            TestDocPlanData testDocPlanData = null; // AnnotationsScanner.getAnnotationsFromClass(className);
            if (testDocPlanData != null) {
                System.out.println("Reading: " + className);
                testplans.add(testDocPlanData);
            }
        }

        datamodel.put("testplans", testplans);
        return datamodel;
    }


    /**
     * Process freemarker template to generate HTML from hashmap
     * 
     * @param datamodel
     * @param template
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    private static String processFreemarkerTemplate(HashMap<String, LinkedList<TestDocPlanData>> datamodel,
            String template) throws IOException, TemplateException {
        StringWriter output = new StringWriter();

        Configuration cfg = new Configuration();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // TODO!! Kommenter inn denne for å lese template fra jar filen.
        cfg.setClassForTemplateLoading(classLoader.getClass(), "/");

        // Kommenter inn denne for å lese freemarker template fra filsystem i stedet for
        // jar fil ved utvikling og testing
        File dir = new File("/Users/thomasfl/workspace/w3-testdoc/src/main/resources/");
        cfg.setDirectoryForTemplateLoading(dir);
        cfg.setLocalizedLookup(false);
        Template tpl = cfg.getTemplate(template);

        tpl.process(datamodel, output);
        return output.toString();
    }


    // TODO Kastes
    // Print datamodel for debug purposes
    private static void printDatamodel(HashMap<String, LinkedList<TestDocPlanData>> datamodel) {
        LinkedList<TestDocPlanData> testplans = datamodel.get("testplans");
        for (Iterator iterator = testplans.iterator(); iterator.hasNext();) {
            TestDocPlanData testplan = (TestDocPlanData) iterator.next();
            System.out.println("Plan:" + testplan.getTitle());

            LinkedList<TestDocTestData> tests = testplan.getTests();
            for (Iterator iterator2 = tests.iterator(); iterator2.hasNext();) {
                TestDocTestData test = (TestDocTestData) iterator2.next();
                System.out.println("  Test: " + test.getTitle() + " => tests: " + test.getTasks().size());
                System.out.println("  Test: " + test.getTitle() + " => chcks: " + test.getChecksCount());

                LinkedList<TestDocTaskData> tasks = test.getTasks();
                for (Iterator iterator3 = tasks.iterator(); iterator3.hasNext();) {
                    TestDocTaskData task = (TestDocTaskData) iterator3.next();
                    System.out.println("    Task: " + task.getTitle() + " => chks: " + task.getChecks().size());
                    LinkedList<String> checks = task.getChecks();
                    for (Iterator iterator4 = checks.iterator(); iterator4.hasNext();) {
                        String check = (String) iterator4.next();
                        System.out.println("      Check: " + check);
                    }
                }

            }
        }
    }


    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     * 
     * @param packageName
     *            The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }


    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     * 
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class _class;
                try {
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                } catch (ExceptionInInitializerError e) {
                    // happen, for example, in classes, which depend on
                    // Spring to inject some beans, and which fail,
                    // if dependency is not fulfilled
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false,
                            Thread.currentThread().getContextClassLoader());
                }
                classes.add(_class);
            }
        }
        return classes;
    }


    public static void writeFile(String filename, String data) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
