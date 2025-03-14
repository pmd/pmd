---
title: Building PMD from source
tags: [devdocs]
permalink: pmd_devdocs_building.html
author: Tom Copeland, Xavier Le Vourch <xlv@users.sourceforge.net>
last_updated: January 2025 (7.10.0)
---

# Compiling PMD

*   JDK 11 or higher

{% include note.html content="While Java 11 is required for building, running PMD only requires Java 8." %}

You’ll need to either check out the source code or download the latest source release. Assuming you’ve got the latest source release, unzip it to a directory:

```
[tom@hal building]$ ls -l
total 5716
-rw-rw-r--    1 tom      tom       5837216 Jul 17 13:09 pmd-src-{{site.pmd.version}}.zip
[tom@hal building]$ unzip -q pmd-src-{{site.pmd.version}}.zip
[tom@hal building]$
```

Now cd down into the `pmd` directory:

```
[tom@hal building]$ cd pmd-src-{{site.pmd.version}}
[tom@hal pmd-src-{{site.pmd.version}}]$ ls -l | grep pom.xml
-rw-rw-r--    1 tom      tom          36482 14\. Nov 17:36 pom.xml
[tom@hal pmd-src-{{site.pmd.version}}]$
```

That’s the project configuration for maven… let’s compile!

```
[tom@hal pmd-src-{{site.pmd.version}}]$ ./mvnw clean verify
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO]
[INFO] PMD
[INFO] PMD Core
...
... after a few minutes ...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for PMD {{site.pmd.version}}:
[INFO] 
[INFO] PMD ................................................ SUCCESS [ 31.653 s]
[INFO] PMD Core ........................................... SUCCESS [01:10 min]
[INFO] PMD Test Schema .................................... SUCCESS [  5.408 s]
[INFO] PMD Ant Integration ................................ SUCCESS [  9.623 s]
[INFO] PMD Test Framework ................................. SUCCESS [  7.571 s]
[INFO] PMD language module testing utilities .............. SUCCESS [ 30.390 s]
[INFO] PMD Apex ........................................... SUCCESS [ 56.152 s]
[INFO] PMD Coco ........................................... SUCCESS [01:18 min]
[INFO] PMD C++ ............................................ SUCCESS [ 11.274 s]
[INFO] PMD C# ............................................. SUCCESS [ 16.891 s]
[INFO] PMD Dart ........................................... SUCCESS [  9.317 s]
[INFO] PMD Fortran ........................................ SUCCESS [  6.105 s]
[INFO] PMD Gherkin ........................................ SUCCESS [ 16.105 s]
[INFO] PMD Go ............................................. SUCCESS [  8.768 s]
[INFO] PMD Groovy ......................................... SUCCESS [  9.565 s]
[INFO] PMD HTML ........................................... SUCCESS [  8.741 s]
[INFO] PMD Java ........................................... SUCCESS [03:17 min]
[INFO] PMD JavaScript ..................................... SUCCESS [ 25.659 s]
[INFO] PMD JSP ............................................ SUCCESS [ 17.373 s]
[INFO] PMD Julia .......................................... SUCCESS [ 26.660 s]
[INFO] PMD Kotlin ......................................... SUCCESS [ 41.748 s]
[INFO] PMD Lua ............................................ SUCCESS [ 18.991 s]
[INFO] PMD Matlab ......................................... SUCCESS [ 14.886 s]
[INFO] PMD Modelica ....................................... SUCCESS [ 30.926 s]
[INFO] PMD Objective-C .................................... SUCCESS [  9.005 s]
[INFO] PMD Perl ........................................... SUCCESS [  5.979 s]
[INFO] PMD PHP ............................................ SUCCESS [  6.914 s]
[INFO] PMD PL/SQL ......................................... SUCCESS [02:14 min]
[INFO] PMD Python ......................................... SUCCESS [  6.971 s]
[INFO] PMD Ruby ........................................... SUCCESS [  5.493 s]
[INFO] PMD Rust ........................................... SUCCESS [  7.290 s]
[INFO] PMD Scala Common Source and Settings ............... SUCCESS [  1.874 s]
[INFO] PMD Scala for Scala 2.13 ........................... SUCCESS [ 24.269 s]
[INFO] PMD Swift .......................................... SUCCESS [ 23.196 s]
[INFO] PMD TSql ........................................... SUCCESS [  7.145 s]
[INFO] PMD Visualforce .................................... SUCCESS [ 12.353 s]
[INFO] PMD Velocity Template Language (VTL) ............... SUCCESS [ 10.073 s]
[INFO] PMD XML and XSL .................................... SUCCESS [  7.855 s]
[INFO] PMD Languages Dependencies ......................... SUCCESS [  2.878 s]
[INFO] PMD Documentation Generator (internal) ............. SUCCESS [ 10.902 s]
[INFO] PMD Scala for Scala 2.12 ........................... SUCCESS [ 21.131 s]
[INFO] PMD CLI ............................................ SUCCESS [ 11.246 s]
[INFO] PMD Distribution Packages .......................... SUCCESS [ 11.457 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  17:52 min
[INFO] Finished at: 2025-01-09T15:49:59+01:00
[INFO] ------------------------------------------------------------------------

[tom@hal pmd-src-{{site.pmd.version}}]$
```

Now the source and binary distribution zip files can be found in the folder `pmd-dist/target`.

**Notes:**

-   The rules that have already been written are specified in the `src/main/resources/category/` directories of
    the specific languages, e.g. `pmd-java/src/main/resources/category`.
    They’re also in the jar file that’s included with both the source and binary distributions.
