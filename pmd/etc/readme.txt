PMD

Contents:
Overview
Running PMD from the command-line on Win32
Running PMD from the command-line on Unix


OVERVIEW
PMD is a Java source code analysis tool.  It finds unused variables, empty catch blocks, and so forth.


HOW TO RUN PMD FROM THE COMMAND LINE (WIN32)
-download the binary release
-unzip it somewhere
-cd into the pmd\etc\ directory

C:\tmp\tmp_pmd>cd pmd\etc
C:\tmp\tmp_pmd\pmd\etc>

-run PMD on a Java source file

C:\tmp\tmp_pmd\pmd\etc>run c:\data\pmd\pmd\test-data\UnusedLocal1.java html rulesets/unusedcode.xml
<html><head><title>PMD</title></head><body>
<table><tr>
<th>File</th><th>Line</th><th>Problem</th></tr>
<tr>
<td>c:\data\pmd\pmd\test-data\UnusedLocal1.java</td>
<td>5</td>
<td>Avoid unused local variables such as 'fr'</td>
</tr>
</table></body></html>
C:\tmp\tmp_pmd\pmd\etc>


HOW TO RUN PMD FROM THE COMMAND LINE (UNIX)
-download the binary release
-unzip it

[build@ul020-dmz tmp_pmd]$ unzip -q pmd-bin-1.03.zip

-cd into the pmd/etc/ directory

[build@ul020-dmz tmp_pmd]$ cd pmd/etc/

-run PMD on a Java source file

[build@ul020-dmz etc]$ run Foo.java html rulesets/unusedcode.xml
<html><head><title>PMD</title></head><body>
<table><tr>
<th>File</th><th>Line</th><th>Problem</th></tr>
<tr>
<td>Foo.java</td>
<td>48</td>
<td>Avoid unused private fields such as '_tasks'</td>
</tr>
</table></body></html>
[build@ul020-dmz etc]$

-if you send in a directory as the first parameter, PMD will run recursively on all files and subdirectories
-note that if you're using JDK 1.3, you'll need to use java -cp, not java -jar.  Like this:

java -cp lib\pmd-1.03.jar;lib\xercesImpl-2.0.2.jar;lib\xmlParserAPIs-2.0.2.jar;lib\jaxen-core-1.0-fcs.jar;lib\saxpath-1.0-fcs.jar net.sourceforge.pmd.PMD c:\j2sdk1.4.1_01\src\java\lang xml rulesets/imports.xml






