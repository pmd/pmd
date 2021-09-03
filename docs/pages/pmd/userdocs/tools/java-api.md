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
        configuration.setRuleSets("rulesets/java/quickstart.xml");
        configuration.setReportFormat("xml");
        configuration.setReportFile("/home/workspace/pmd-report.xml");

        PMD.runPMD(configuration);
    }
}
```

## Programmatically, variant 2

This gives you more control over which files are processed, but is also more complicated.
You can also provide your own custom renderers.

1.  First we create a `PMDConfiguration`. This is currently the only way to specify a ruleset:
    
    ```java
    PMDConfiguration configuration = new PMDConfiguration();
    configuration.setMinimumPriority(RulePriority.MEDIUM);
    configuration.setRuleSets("rulesets/java/quickstart.xml");
    ```
    
2.  In order to support type resolution, PMD needs to have access to the compiled classes and dependencies
    as well. This is called "auxclasspath" and is also configured here.
    Note: you can specify multiple class paths separated by `:` on Unix-systems or `;` under Windows.
    
    ```java
    configuration.prependClasspath("/home/workspace/target/classes:/home/.m2/repository/my/dependency.jar");
    ```
    
3.  Then we need to load the rulesets. This is done by using the configuration, taking the minimum priority into
    account:
    
    ```java
    RuleSetLoader ruleSetLoader = RuleSetLoader.fromPmdConfig(configuration);
    List<RuleSet> ruleSets = ruleSetLoader.loadFromResources(Arrays.asList(configuration.getRuleSets().split(",")));
    ```
    
4.  PMD operates on a list of `DataSource`. You can assemble a own list of `FileDataSource`, e.g.
    
    ```java
    List<DataSource> files = Arrays.asList(new FileDataSource(new File("/path/to/src/MyClass.java")));
    ```
    
5.  For reporting, you can use a built-in renderer, e.g. `XMLRenderer` or a custom renderer implementing
    `Renderer`. Note, that you must manually initialize
    the renderer by setting a suitable `Writer` and calling `start()`. After the PMD run, you need to call
    `end()` and `flush()`. Then your writer should have received all output.
    
    ```java
    StringWriter rendererOutput = new StringWriter();
    Renderer xmlRenderer = new XMLRenderer("UTF-8");
    xmlRenderer.setWriter(rendererOutput);
    xmlRenderer.start();
    ```
    
6.  Now, all the preparations are done, and PMD can be executed. This is done by calling
    `PMD.processFiles(...)`. This method call takes the configuration, the rulesets, the files
    to process, and the list of renderers. Provide an empty list, if you don't want to use
    any renderer. Note: The auxclasspath needs to be closed explicitly. Otherwise the class or jar files may
    remain open and file resources are leaked.
    
    ```java
    try {
        PMD.processFiles(configuration, ruleSets, files, Collections.singletonList(renderer));
    } finally {
        ClassLoader auxiliaryClassLoader = configuration.getClassLoader();
        if (auxiliaryClassLoader instanceof ClasspathClassLoader) {
            ((ClasspathClassLoader) auxiliaryClassLoader).close();
        }
    }
    ```
    
7.  After the call, you need to finish the renderer via `end()` and `flush()`.
    Then you can check the rendered output.
    
    ``` java
    renderer.end();
    renderer.flush();
    System.out.println("Rendered Report:");
    System.out.println(rendererOutput.toString());
    ```

Here is a complete example:

``` java
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

public class PmdExample2 {

    public static void main(String[] args) throws IOException {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(RulePriority.MEDIUM);
        configuration.setRuleSets("rulesets/java/quickstart.xml");
        configuration.prependClasspath("/home/workspace/target/classes");
        RuleSetLoader ruleSetLoader = RuleSetLoader.fromPmdConfig(configuration);
        List<RuleSet> ruleSets = ruleSetLoader.loadFromResources(Arrays.asList(configuration.getRuleSets().split(",")));

        List<DataSource> files = determineFiles("/home/workspace/src/main/java/code");

        Writer rendererOutput = new StringWriter();
        Renderer renderer = createRenderer(rendererOutput);
        renderer.start();

        try {
            PMD.processFiles(configuration, ruleSets, files, Collections.singletonList(renderer));
        } finally {
            ClassLoader auxiliaryClassLoader = configuration.getClassLoader();
            if (auxiliaryClassLoader instanceof ClasspathClassLoader) {
                ((ClasspathClassLoader) auxiliaryClassLoader).close();
            }
        }

        renderer.end();
        renderer.flush();
        System.out.println("Rendered Report:");
        System.out.println(rendererOutput.toString());
    }

    private static Renderer createRenderer(Writer writer) {
        XMLRenderer xml = new XMLRenderer("UTF-8");
        xml.setWriter(writer);
        return xml;
    }

    private static List<DataSource> determineFiles(String basePath) throws IOException {
        Path dirPath = FileSystems.getDefault().getPath(basePath);
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");

        final List<DataSource> files = new ArrayList<>();

        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (matcher.matches(path.getFileName())) {
                    System.out.printf("Using %s%n", path);
                    files.add(new FileDataSource(path.toFile()));
                } else {
                    System.out.printf("Ignoring %s%n", path);
                }
                return super.visitFile(path, attrs);
            }
        });
        System.out.printf("Analyzing %d files in %s%n", files.size(), basePath);
        return files;
    }
}
```


