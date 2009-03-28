
Compilation
===========

'ant plugin' will generate a new version in the 'build' directory.


Setup
=====

If the default configuration is not accurate for your setup, customization
can be done in local.properties to point to the Eclipse installation directory
and the pde and launcher versions:

----- local.properties example -----------------
eclipse.home=/usr/java/eclipse
eclipse.pde.version=3.4.0.v20080604
eclipse.launcher.version=1.0.100.v20080509-1800
------ end -------------------------------------


Importing the projects in Eclipse
=================================

In the Java perspective, select "Import" -> "Existing Java projects", select
the directory where this file is located.  The next panel should have four
projects:

net.sourceforge.pmd.eclipse
net.sourceforge.pmd.eclipse.plugin
pmd-eclipse-test
pmd-eclipse-test-fragment


