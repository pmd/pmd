PMD for netbeans.

------------------------
INSTALL
------------------------

Start netbeans and goto Tools\Update Center and choose 
"Install Manually Downloaded Modules(.nbm Files). Press Next, Add... and go to 
the directory where you downloaded pmd-netbeans and choose the file pmd.nbm. 
Press Next, accept the license and choose to include the pmd module. Press 
Finish, restart the IDE and you're done :)

------------------------
USE
------------------------

Right click on one or more folders or java files, choose tools, PMD and watch the tool
find your flawes. The tool is also located under the global tools menu. Double 
click on the errormessages in the output pane to go to the line in the
java-file to correct the problem PMD discovered.

------------------------
OPTIONS
------------------------

Go to Options/IDE Configuration/Server And External Tool Settings/PMD Settings
to choose which rules to use. 