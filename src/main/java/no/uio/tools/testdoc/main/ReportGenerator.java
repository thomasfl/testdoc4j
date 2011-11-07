package no.uio.tools.testdoc.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import no.uio.tools.testdoc.data.TestDocPlanData;

import org.apache.commons.io.FileUtils;
import org.apache.maven.reporting.MavenReportException;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ReportGenerator {

    public static boolean debug = false;


    public static String generateTestDocForClasses(List<Class<?>> classes, boolean failIfMissingTestPlanTitle)
            throws ClassNotFoundException, IOException, TemplateException, MavenReportException {
        HashMap<String, LinkedList<TestDocPlanData>> datamodel = scanClasses(classes, failIfMissingTestPlanTitle);
        String output = processFreemarkerTemplate(datamodel, "testdoc.ftl");
        return output;
    }


    @SuppressWarnings("rawtypes")
    public static HashMap<String, LinkedList<TestDocPlanData>> scanClasses(List classes,
            boolean failIfMissingTestPlanTitle) throws ClassNotFoundException, IOException, MavenReportException {

        HashMap<String, LinkedList<TestDocPlanData>> datamodel = new HashMap<String, LinkedList<TestDocPlanData>>();
        LinkedList<TestDocPlanData> testplans = new LinkedList<TestDocPlanData>();

        for (Iterator<Class> iterator = classes.iterator(); iterator.hasNext();) {
            Class clazz = (Class) iterator.next();
            String className = clazz.getName();
            if (debug == true) {
                System.out.println("TestDoc: Scanning: " + className);
            }
            TestDocPlanData testDocPlanData = AnnotationsScanner.getAnnotationsFromClass(clazz,
                    failIfMissingTestPlanTitle);
            if (testDocPlanData != null) {
                if (debug == true) {
                    System.out.println("Reading: " + className);
                }
                testplans.add(testDocPlanData);
            }
        }

        Collections.sort(testplans);
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

        cfg.setClassForTemplateLoading(classLoader.getClass(), "");

        ClassTemplateLoader ctl = new ClassTemplateLoader(classLoader.getClass(), "/");
        cfg.setTemplateLoader(ctl);

        cfg.setLocalizedLookup(false);

        String templateText = readTextResource("/" + template);
        Template tpl = new Template(template, new StringReader(templateText), cfg);
        tpl.process(datamodel, output);
        return output.toString();
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


    /* Reads text file from jar archive. Used to read template files. */
    public static String readTextResource(String s) {
        InputStream is = null;
        BufferedReader br = null;
        String line;
        ArrayList<String> list = new ArrayList<String>();

        try {
            is = FileUtils.class.getResourceAsStream(s);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine())) {
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String retVal = "";
        Iterator it = list.iterator();
        while (it.hasNext()) {
            retVal = retVal + (String) it.next();

        }
        return retVal;
    }

}
