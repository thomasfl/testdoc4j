TestDoc
=======

TestDoc is a set of java annotations and a maven reporting plugin used to annotate tests. The generated report can be used to document what the tests does for non technical users, or be used by as test recipes for manual testers.


Usage
=====

## Step 1 ##
Make sure the jar file is on your classpath.

## Step 2 ##

Add TestDoc annotations to your tests with to dthat describes in plain text what the tests do.

```java

    import no.uio.tools.testdoc.*;

    @TestDocPlan("Authentication tests")
    public class BasicExample {

      @TestDocTest("Test user login")
      @TestDocTasks({ @TestDocTask(task = "Go to login page", checks = "Login form should be visible") })
      public void testUserLogin() {
          // Testcode here
          page = new LoginPage();
          assertNotNull (page.loginForm() ));
      }

    }
```

## Step 3 ##

Add TestDoc to your pom.xml file.

```xml
  <reporting>
    <plugins>

      <plugin>
        <groupId>no.uio.tools</groupId>
        <artifactId>testdoc</artifactId>
        <version>0.0.2</version>
        <configuration>
          <forceTestPlanTitle>true</forceTestPlanTitle><!-- optional setting -->
        </configuration>
      </plugin>

    </plugins>
  </reporting>

```

## Step 4 ##

Run maven site.

```
    $ mvn site
    ...
    [INFO] Generating "TestDoc" report    --- testdoc:0.0.2
    [INFO] ________________ ______________________  _______ _______
    [INFO] \__   __/  ____ \  ____ \__   __/  __  \(  ___  )  ____ \
    [INFO]    ) (  | (    \/ (    \/  ) (  | (  \  ) (   ) | (    \/
    [INFO]    | |  | (__   | (_____   | |  | |   ) | |   | | |
    [INFO]    | |  |  __)  (_____  )  | |  | |   | | |   | | |
    [INFO]    | |  | (           ) |  | |  | |   ) | |   | | |
    [INFO]    | |  | (____/Y\____) |  | |  | (__/  ) (___) | (____/\
    [INFO]    )_(  (_______|_______)  )_(  (______/(_______)_______/
    [INFO]   TestDoc - Show the world what your tests do. Version 0.2.

```

To only generate the TestDoc report takes a fraction of the time.

```
   $ mvn no.uio.tools:testdoc:testdoc
   [INFO] TestDoc: Output report to 'testdoc_testplan.html'.
```

## Step 5 ##

View maven site report in target/site/testdoc/ in a browser.

![Screenshot](https://github.com/thomasfl/testdoc4j/raw/master/screenshot.png)

Installation
============

```
    $ git clone git@github.com:thomasfl/testdoc4j.git
    $ mvn package
```
