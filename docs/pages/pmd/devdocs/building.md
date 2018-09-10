---
title: Building PMD from source
tags: [devdocs]
permalink: pmd_devdocs_building.html
author: Tom Copeland, Xavier Le Vourch <xlv@users.sourceforge.net>
---

<!-- Gives visibility -->
{%include note.html content="TODO add IDE specific indications" %}

# Compiling PMD

*   JDK 10 or higher

{% include note.html content="While Java 10 is required for building, running PMD only requires Java 7 (or Java 8 for Apex and the Designer)." %}

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
[INFO] Reactor Summary:
[INFO]
[INFO] PMD ................................................ SUCCESS [  3.061 s]
[INFO] PMD Core ........................................... SUCCESS [ 25.675 s]
[INFO] PMD Test Framework ................................. SUCCESS [  0.457 s]
[INFO] PMD C++ ............................................ SUCCESS [  1.893 s]
[INFO] PMD C# ............................................. SUCCESS [  0.619 s]
[INFO] PMD Fortran ........................................ SUCCESS [  0.609 s]
[INFO] PMD Go ............................................. SUCCESS [  0.103 s]
[INFO] PMD Java ........................................... SUCCESS [01:08 min]
[INFO] PMD JavaScript ..................................... SUCCESS [  3.279 s]
[INFO] PMD JSP ............................................ SUCCESS [  3.944 s]
[INFO] PMD Matlab ......................................... SUCCESS [  1.342 s]
[INFO] PMD Objective-C .................................... SUCCESS [  2.281 s]
[INFO] PMD PHP ............................................ SUCCESS [  0.536 s]
[INFO] PMD PL/SQL ......................................... SUCCESS [ 10.973 s]
[INFO] PMD Python ......................................... SUCCESS [  1.758 s]
[INFO] PMD Ruby ........................................... SUCCESS [  0.438 s]
[INFO] PMD Velocity ....................................... SUCCESS [  3.941 s]
[INFO] PMD XML and XSL .................................... SUCCESS [  2.174 s]
[INFO] PMD Scala .......................................... SUCCESS [ 11.901 s]
[INFO] PMD Distribution Packages .......................... SUCCESS [ 11.366 s]
[INFO] PMD Java 8 Integration ............................. SUCCESS [  0.560 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 02:36 min
[INFO] Finished at: 2015-11-14T17:46:06+01:00
[INFO] Final Memory: 63M/765M
[INFO] ------------------------------------------------------------------------
[tom@hal pmd-src-{{site.pmd.version}}]$
```

Now the source and binary distribution zip files can be found in the folder `pmd-dist/target`.

**Notes:**

*   The rules that have already been written are specified in the `src/main/resources/rulesets/` directories of
the specific languages, e.g. `pmd-java/src/main/resources/rulesets`.
They’re also in the jar file that’s included with both the source and binary distributions.

A paucity of detail, I’m sure you’d agree. If you think this document can be improved,
please post [here](http://sourceforge.net/p/pmd/discussion/188192) and let me know how. Thanks!
