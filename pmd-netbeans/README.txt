PMD for netbeans.
------------------------
INSTALL
------------------------

 1) Start netbeans 
 2) Goto Tools --> Update Center
 3) Choose "Install Manually Downloaded Modules(.nbm Files).". 
 4) Press Next
 5) Chosse Add
 6) Goto the directory where you downloaded pmd-netbeans and choose the file "pmd.nbm".
 7) Press OK
 8) Press Next
 9) Press Next
10) Press Accept
11) Choose Include 
12) Press Finish
13) Choose OK to restart

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
1) Start netbeans 
2) Goto Tools --> Options
3) Navigate to Options/IDE Configuration/Server And External Tool Settings/PMD Settings
4) Click on "Rules"
5) Click on the [...] button
6) Select the different rules to see information and examples of the rule
7) Use the <, <<, >, >>, buttons to choose which rules to use.
8) Press OK to save the configuration
