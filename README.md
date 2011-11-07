TestDoc
=======

TestDoc is a set of java annotations and a maven reporting plugin used to annotate tests. The generated report can be used to document what the tests does for non technical users, or be used by as test recipes for manual testers.


Usage
=====

1. Add annotation to your testscode describing in plain langauge what the tests do.

```java

    import no.uio.tools.testdoc.*;

    @TestDocPlan("Authentication tests")
    public class BasicExample {

      @TestDocTest("Test user login")
      @TestDocTasks({ @TestDocTask(task = "Go to login page", checks = "Check 1") })
      public void testUserLogin() {
          // Testcode here
          assert (true);
      }

    }
```

2. Run maven site

```
    $ mvn site
```

3. View HTML report in target/site/testdoc/

Installation
============

    $ git clone git@github.com:thomasfl/testdoc_java.git
    $ mvn package

To deploy jar to a nexus repository

    $ mvn deploy
