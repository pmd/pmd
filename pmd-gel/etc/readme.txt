Files Tested
PMD currently executes on every .java file in the current project. It
examines every source file in the source paths defined in your project
settings (under Project | Properties... | Paths). Make sure that the
files you want tested are contained in one of your source paths and
not just a file in your project manager.

If you do not have an open project, PMD will run on the file that's
currently open in the editor.

Rules
The PMD plugin incorporates the "unusedcode" and "basic" rulesets.  These
rulesets catch things like unused local variables, empty try blocks, and so
forth.  You can view all the PMD rules with examples and details at
http://pmd.sf.net/.

Future Tasks
Continue to respond to the Gel community's feedback