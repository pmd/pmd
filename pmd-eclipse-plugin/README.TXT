
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
# find $eclipse.home -name "*pde*" -type d
eclipse.pde.version=3.8.2.v20121114-140810
# find $eclipse.home -name "*equinox.launcher*" -type f
eclipse.launcher.version=1.3.0.v20120522-1813
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


