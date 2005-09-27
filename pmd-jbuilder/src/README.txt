To install this plugin, simply place the PMDOpenTool.jar and the
pmd-3.3.jar (again, whatever version) into the JBuilder/lib/ext directory.  PLEASE NOTE:  if you
have been using previous verisons of this opentool, and the version of the pmd.jar file has changed,
you need to remove the old pmd.jar file (pmd-3.2.jar for example)

The PMD_Jbuilder OpenTool requres the pmd-x.x.jar file to run (where x.x is the major/minor version id).
Although the openTool has no other special requirements, the PMD utility does.  PMD also requires that Xerces
and Ant be available in the JBuilder classpath as well.  This is not an issue for JBuilder 6/7 Enterprise users
since those libraries come preinstalled.  However, users of the Professional/Personal versions may have to
put those libraries into their JBuilder lib directory manually.  I have not tested on anything other than
JBuilder 7 Enterprise, but the tool should run fine on most of them given the aforementioned changes.

JBuilder 4 Users - The PMD Check Project and CPD Check Project operations will only work if you have packages 
defined in your project view.  This is because of some of the differences in JBuilder 7 (at least) and JBuilder 4 in handling packages within the project view.  I'm working on a way to remedy this.