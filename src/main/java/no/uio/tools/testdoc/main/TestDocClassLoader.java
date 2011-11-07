package no.uio.tools.testdoc.main;

import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class TestDocClassLoader {

    /* Adds all files in the folder ./target/WEB-INF/lib/ to the classpath in the current thread. */
    public static void loadClassesFromTargetFolder() {
        ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader urlClassLoader = null;
        String libFolder = locateLibFolder();
        try {
            urlClassLoader = new URLClassLoader(findClassURIs(libFolder), currentThreadClassLoader);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }


    /* TestDoc needs access to a folder with all necessary jar files and load them with classloader. */
    private static String locateLibFolder() {
        File pomfile = new File(System.getProperty("user.dir") + "/pom.xml");
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new FileReader(pomfile);
            model = mavenreader.read(reader);
            model.setPomFile(pomfile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        File dir = new File(System.getProperty("user.dir") + "/target/");
        if (!dir.exists()) {
            System.err.println("./target/ directory does not exists. Please run 'mvn package' to create it.");
        }

        String libFolder = System.getProperty("user.dir") + "/target/" + model.getArtifactId() + "/WEB-INF/lib/";
        dir = new File(libFolder);
        if (!dir.exists()) {
            System.err.println("Directory " + libFolder + " does not exists. Please run 'mvn package' to create it.");
        }
        return libFolder;
    }


    /* Returns an array with classes and jar files we want to add to classpath when scanning for annotations */
    private static URL[] findClassURIs(String jarDirectory) throws MalformedURLException {
        File dir = new File(jarDirectory);
        List<URL> urls = new ArrayList<URL>();

        String curDir = System.getProperty("user.dir");
        String filename = curDir + "/target/test-classes/";
        URL url = new File(filename).toURI().toURL();
        urls.add(url);

        String[] children = dir.list();
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i = 0; i < children.length; i++) {
                filename = children[i];
                url = new File(jarDirectory + filename).toURI().toURL();
                urls.add(url);
            }
        }

        return urls.toArray(new URL[0]);
    }

}
