package no.uio.tools.testdoc.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import freemarker.template.TemplateException;

/**
 * Maven reporting mojo plugin for generating TestDoc reports.
 * 
 * @author twitter: @thomasfl
 * @goal testdoc
 * @phase site
 */
public class TestDocMojo extends AbstractMavenReport {

    /**
     * The greeting to display.
     * 
     * @parameter expression="${sayhi.greeting}" default-value="Hello World!"
     */
    private String forceTestPlanTitle;

    /**
     * The Maven Project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Doxia Site Renderer.
     * 
     * @component
     */
    protected Renderer siteRenderer;

    /**
     * The output directory.
     * 
     * @parameter expression="${project.build.directory}/generated-sources/testdoc"
     * @required
     */
    private File outputDirectory;


    public String getName(final Locale locale) {
        return "TestDoc";
    }


    public String getDescription(final Locale locale) {
        return "Test plans for human testers.";
    }


    public String getOutputName() {
        return "testdoc";
    }


    /* Executed when running 'mvn site'from the command line */
    @Override
    public void executeReport(final Locale locale) throws MavenReportException {
        outputTestDocBannerToLog();

        TestDocClassLoader.loadClassesFromTargetFolder();

        List<Class<?>> classesFound = AnnotationsScanner.findAllAnnotatedClasses();
        classesFound.remove(no.uio.tools.testdoc.examples.AdvancedExample.class);
        getLog().info("TestDoc found " + classesFound.size() + " classes with TestDoc annotations.");

        // Read plugin configuration:
        getLog().info("forceTestPlanTitle: '" + forceTestPlanTitle + "'");
        // Make mvn site fail:
        // if (true) {
        // throw new MavenReportException("Oh no! Maven fail!");
        // }

        try {
            // Read testdoc maven plugin configuration:
            boolean failIfMissingTestPlanTitle = (forceTestPlanTitle != null && forceTestPlanTitle.equals("true"));
            String html = ReportGenerator.generateTestDocForClasses(classesFound, failIfMissingTestPlanTitle);
            generateTestDocReport(getSink(), html);
        } catch (ClassNotFoundException e1) {
            getLog().error(e1.getException());
        } catch (IOException e1) {
            getLog().error(e1.getCause());
        } catch (TemplateException e1) {
            getLog().error(e1.getCauseException());
        }
    }


    public static void generateTestDocReport(final Sink sink, String htmlReport) {
        sink.head();
        sink.title();
        sink.text("TestDoc Testplan");
        sink.title_();
        sink.head_();
        sink.body();

        sink.section1();
        sink.sectionTitle1();
        sink.text("TestDoc testplan");
        sink.sectionTitle1_();
        sink.section1_();

        sink.section1();
        sink.rawText(htmlReport);
        sink.section1_();

        sink.body_();
        sink.flush();
        sink.close();
    }


    public static String currentFolderName() {
        Matcher matcher = Pattern.compile("/([^/]*)$").matcher(System.getProperty("user.dir"));
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }


    /* Executed when running 'mvn no.uio.tools:testdoc:testdoc' from the command line. */
    @Override
    public void execute() {
        try {
            boolean failIfMissingTestPlanTitle = (forceTestPlanTitle != null && forceTestPlanTitle.equals("true"));
            TestDocRunner testDocRunner = new TestDocRunner(getLog(), failIfMissingTestPlanTitle);
            testDocRunner.execute();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (TemplateException e1) {
            e1.printStackTrace();
        } catch (MavenReportException e) {
            System.out.println(e.getMessage());
        }
    }


    private void outputTestDocBannerToLog() {
        getLog().info("________________ ______________________  _______ _______  ");
        getLog().info("\\__   __/  ____ \\  ____ \\__   __/  __  \\(  ___  )  ____ \\ ");
        getLog().info("   ) (  | (    \\/ (    \\/  ) (  | (  \\  ) (   ) | (    \\/  ");
        getLog().info("   | |  | (__   | (_____   | |  | |   ) | |   | | |        ");
        getLog().info("   | |  |  __)  (_____  )  | |  | |   | | |   | | |        ");
        getLog().info("   | |  | (           ) |  | |  | |   ) | |   | | |        ");
        getLog().info("   | |  | (____/Y\\____) |  | |  | (__/  ) (___) | (____/\\  ");
        getLog().info("   )_(  (_______|_______)  )_(  (______/(_______)_______/  ");
        getLog().info("  TestDoc - Show the world what your tests do. Version 0.2.7 (mvn site)");
    }


    @Override
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }


    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    @Override
    protected MavenProject getProject() {
        return project;
    }


    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }


    public void setForceTestPlanTitle(String forceTestPlanTitle) {
        this.forceTestPlanTitle = forceTestPlanTitle;
    }


    public String getForceTestPlanTitle() {
        return forceTestPlanTitle;
    }

}
