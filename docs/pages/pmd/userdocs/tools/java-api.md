---
title: PMD Java API
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_java_api.html
---

The easiest way to run PMD is to just use a build plugin in your favorite build tool
like [Apache Ant](pmd_userdocs_tools_ant.html), [Apache Maven](pmd_userdocs_tools_maven.html) or
[Gradle](pmd_userdocs_tools_gradle.html).

There are also many integrations for IDEs available, see [Tools](pmd_userdocs_tools.html).

If you have your own build tool or want to integrate PMD in a different way, you can call PMD programmatically,
as described here.

## Dependencies

You'll need to add the dependency to the language, you want to analyze. For Java, it will be
`net.sourceforge.pmd:pmd-java`. If you use Maven, you can add a new (compile time) dependency like this:

``` xml
<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-java</artifactId>
    <version>${pmdVersion}</version>
</dependency>
```

Note: You'll need to select a specific version. This is done in the example via the property `pmdVersion`.

This will transitively pull in the artifact `pmd-core` which contains the API.

## Command line interface

The easiest way is to call PMD with the same interface as from command line. The main class is
`net.sourceforge.pmd.PMD`:

``` java
import net.sourceforge.pmd.PMD;

public class Example {
    public static void main(String[] args) {
        String[] pmdArgs = {
            "-d", "/home/workspace/src/main/java/code",
            "-R", "rulesets/java/quickstart.xml",
            "-f", "xml",
            "-r", "/home/workspace/pmd-report.xml"
        };
        PMD.main(pmdArgs);
    }
}
```

It uses the same options as described in [PMD CLI reference](pmd_userdocs_cli_reference.html).

## Programmatically, variant 1

This is very similar:

``` java
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;

public class PmdExample {

    public static void main(String[] args) {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputPaths("/home/workspace/src/main/java/code");
        configuration.addRuleSet("rulesets/java/quickstart.xml");
        configuration.setReportFormat("xml");
        configuration.setReportFile("/home/workspace/pmd-report.xml");

        PMD.runPmd(configuration);
    }
}
```

## Programmatically, variant 2

This gives you more control over which files are processed, but is also more complicated.
You can also provide your own custom renderers.

1.  First we create a `PMDConfiguration` and configure it, first the rules:
    
    ```java
    PMDConfiguration configuration = new PMDConfiguration();
    configuration.setMinimumPriority(RulePriority.MEDIUM);
    configuration.addRuleSet("rulesets/java/quickstart.xml");
    ```
    
2.  Then we configure, which paths to analyze:
    
    ```java
    configuration.setInputPaths("/home/workspace/src/main/java/code");
    ```
    
3.  The we configure the default language version for Java. And in order to support type resolution,
    PMD needs to have access to the compiled classes and dependencies as well. This is called
    "auxclasspath" and is also configured here.

    Note: you can specify multiple class paths separated by `:` on Unix-systems or `;` under Windows.
    
    ```java
    configuration.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("11"));
    configuration.prependAuxClasspath("/home/workspace/target/classes:/home/.m2/repository/my/dependency.jar");
    ```
    
4.  Then we configure the reporting. Configuring the report file is optional. If not specified, the report
    will be written to `stdout`.
    
    ```java
    configuration.setReportFormat("xml");
    configuration.setReportFile("/home/workspace/pmd-report.xml");
    ```
    
5.  Now an optional step: If you want to use additional renderers as in the example, set them up before
    calling PMD. You can use a built-in renderer, e.g. `XMLRenderer` or a custom renderer implementing
    `Renderer`. Note, that you must manually initialize the renderer by setting a suitable `Writer`:
    
    ```java
    Writer rendererOutput = new StringWriter();
    Renderer renderer = createRenderer(rendererOutput);

    // ...
    private static Renderer createRenderer(Writer writer) {
        XMLRenderer xml = new XMLRenderer("UTF-8");
        xml.setWriter(writer);
        return xml;
    }
    ```

6.  Finally we can start the PMD analysis. There is the possibility to fine-tune the configuration
    by adding additional files to analyze or adding additional rulesets or renderers:
    
    ```java
    try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
        // optional: add more rulesets
        pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource("custom-ruleset.xml"));
        // optional: add more files
        pmd.files().addFile(Paths.get("src", "main", "more-java", "ExtraSource.java"));
        // optional: add more renderers
        pmd.addRenderer(renderer);

        // or just call PMD
        pmd.performAnalysis();
    }
    ```
    
    The renderer will be automatically flushed and closed at the end of the analysis.

7.  Then you can check the rendered output.
    
    ``` java
    System.out.println("Rendered Report:");
    System.out.println(rendererOutput.toString());
    ```

Here is a complete example:

``` java
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

public class PmdExample2 {

    public static void main(String[] args) throws IOException {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(RulePriority.MEDIUM);
        configuration.addRuleSet("rulesets/java/quickstart.xml");

        configuration.setInputPaths("/home/workspace/src/main/java/code");

        configuration.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("11"));
        configuration.prependAuxClasspath("/home/workspace/target/classes");

        configuration.setReportFormat("xml");
        configuration.setReportFile("/home/workspace/pmd-report.xml");

        Writer rendererOutput = new StringWriter();
        Renderer renderer = createRenderer(rendererOutput);

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            // optional: add more rulesets
            pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource("custom-ruleset.xml"));
            // optional: add more files
            pmd.files().addFile(Paths.get("src", "main", "more-java", "ExtraSource.java"));
            // optional: add more renderers
            pmd.addRenderer(renderer);

            // or just call PMD
            pmd.performAnalysis();
        }

        System.out.println("Rendered Report:");
        System.out.println(rendererOutput.toString());
    }

    private static Renderer createRenderer(Writer writer) {
        XMLRenderer xml = new XMLRenderer("UTF-8");
        xml.setWriter(writer);
        return xml;
    }
}
```


