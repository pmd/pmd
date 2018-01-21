---
title: Similar projects
permalink: pmd_projectdocs_trivia_similarprojects.html
author: Tom Copeland <tom@infoether.org>, David Dixon-Peugh <ddp@apache.org>
---

## Similar to PMD

### Open Source

*   <a href="http://checkstyle.sourceforge.net/">Checkstyle</a> - Very detailed, supports both Maven and Ant.
    Uses ANTLR.
*   <a href="http://doctorj.sourceforge.net">DoctorJ</a> - Uses JavaCC.  Checks Javadoc, syntax and calculates metrics.
*   <a href="http://web.archive.org/web/20110721133755/http://kind.ucd.ie/products/opensource/ESCJava2/">ESC/Java</a> -
    Finds null dereference errors, array bounds errors, type cast errors, and race conditions.
    Uses Java Modeling Language annotations.
*   <a href="http://findbugs.sourceforge.net/">FindBugs</a> - works on bytecode, uses BCEL.  Source code uses
    templates, nifty stuff!
*   <a href="https://spotbugs.github.io/">SpotBugs</a> - SpotBugs is the spiritual successor of FindBugs, carrying on from the point where it left off with support of its community. 
*   <a href="http://www.hammurapi.biz/hammurapi-biz/ef/xmenu/hammurapi-group/products/hammurapi/index.html">Hammurapi</a> -
    Uses ANTLR, excellent documentation, lots of rules
*   <a href="http://grothoff.org/christian/xtc/jamit/">Jamit</a> - bytecode analyzer, nice graphs
*   <a href="http://jcsc.sourceforge.net/">JCSC</a> - Does a variety of coding standard checks, uses JavaCC and
    the GNU Regexp package.
*   <a href="http://jikes.sourceforge.net/">Jikes</a> - More than a compiler; now it reports code warnings too
*   <a href="http://jlint.sourceforge.net/">JLint</a> - Written in C++.  Uses data flow analysis and a lock graph to
    do lots of synchronization checks.  Operates on class files, not source code.
*   <a href="http://javapathfinder.sourceforge.net/">JPathFinder</a> - A verification VM written by NASA;
    supports a subset of the Java packages
*   <a href="http://csdl.ics.hawaii.edu/research/jwiz/">JWiz</a> - Research project, checks some neat stuff, like if
    you create a Button without adding an ActionListener to it.  Neat.

### Commercial

*   <a href="http://www.appperfect.com/products/java-code-test.html">AppPerfect</a> - 750 rules,
    produces PDF/Excel reports, supports auto-fixing problems
*   <a href="http://web.archive.org/web/20070227171100/http://www.tcs.com/0_products/assent/assent_rules.htm#java">Assent</a> -
    The usual stuff, seems pretty complete.
*   <a href="http://web.archive.org/web/20060823080607/http://www.alajava.com/aubjex/products.htm">Aubjex</a> -
    Rules aren't listed online.  Appears to have some code modification stuff, which would be cool to have in PMD. $299.
*   <a href="http://www.andiz.de/azosystems/en/index.html">AzoJavaChecker</a> - Rules aren't listed online so it's
    hard to tell what they have.  Not sure how much it costs since I don't know German.
*   <a href="https://developers.google.com/java-dev-tools/codepro/doc/">CodePro AnalytiX</a> -
    Eclipse plug-in, extensive audit rules, JUnit test generation/editing, code coverage and analysis
*   <a href="http://www.enerjy.com/static-analysis.html">Enerjy Java Code Analyser</a> - 200 rules,
    lots of IDE plugins
*   <a href="http://www.excelsior-usa.com/fd.html">Flaw Detector</a> - In beta, does control/data flow analysis
    to detect NullPointerExceptions
*   <a href="http://www.mmsindia.com/jstyle.html">JStyle</a> - $995, nice folks, lots of metrics and rules
*   <a href="http://www.parasoft.com/jsp/products/jtest.jsp">JTest</a> - Very nice with tons of features,
    but also very expensive and requires a running X server (or Xvfb) to run on
    Linux.  They charge $500 to move a license from one machine to another.
*   <a href="http://www.jutils.com/index.html">Lint4J</a> - Lock graph, DFA, and type analysis, many EJB checks
*   <a href="http://www.solidsourceit.com/products/SolidSDD-code-duplication-cloning-analysis.html">SolidSDD</a> - Code
    duplication detection, nice graphical reporting. Free licensing available for Educational or OSS use.


## Similar to CPD

### Commercial

*   <a href="http://www.harukizaemon.com/simian/">Simian</a> - fast, works with Java, C#, C, CPP, COBOL, JSP, HTML
*   <a href="http://blue-edge.bg/download.html">Simscan</a> - free for open source projects

## High level reporting

*   <a href="http://xradar.sourceforge.net">XRadar</a> - Agregates data from a lot of code quality tool to generate
    a full quality dashboard.
*   <a href="http://www.sonarsource.com/">Sonar</a> - Pretty much like XRadar, but younger project, fully integrated
    to maven 2 (but requires a database)
*   <a href="http://mojo.codehaus.org/dashboard-maven-plugin/">Maven Dashboard</a> - Same kind of agregator but
    only for maven project.
*   <a href="http://qalab.sourceforge.net/">QALab</a> - Yet another maven plugin...
