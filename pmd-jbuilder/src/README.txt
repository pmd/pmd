To install this plugin, simply place the pmd-jbuiler-0.8.jar (or whatever version it is) and the
pmd-0.9.jar (again, whatever version) into the JBuilder/lib/ext directory.  PLEASE NOTE:  if you
have been using previous verisons of this opentool, and the version of the pmd.jar file has changed,
you need to remove the old pmd.jar file (pmd-0.8.jar for example

The PMD_Jbuilder OpenTool requres the pmd-x.x.jar file to run (where x.x is the major/minor version id).
Although the openTool has no other special requirements, the PMD utility does.  PMD also requires that Xerces
and Ant be available in the JBuilder classpath as well.  This is not an issue for JBuilder 6/7 Enterprise users
since those libraries come preinstalled.  However, users of the Professional/Personal versions may have to
put those libraries into their JBuilder lib directory manually.  I have not tested on anything other than
JBuilder 7 Enterprise, but the tool should run fine on most of them given the aforementioned changes.