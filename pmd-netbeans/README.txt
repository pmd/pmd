PMD for NetBeans.

------------------------
INSTALL
------------------------

 1) Start NetBeans 
 2) Goto Tools --> Update Center
 3) Choose "Install Manually Downloaded Modules (.nbm Files).". 
 4) Press Next
 5) Choose Add
 6) Goto the directory where you downloaded pmd-netbeans and choose the file "pmd.nbm".
 7) Press OK
 8) Press Next
 9) Press Next
10) Press Accept
11) Choose Include 
12) Press Finish
13) Choose OK to restart, if so prompted

------------------------
USE
------------------------

Right click on one or more folders or Java files, choose Tools -> PMD and watch the tool
find your flaws. The tool is also located under the global Tools menu. Double 
click on the error messages in the output pane to go to the line in the
Java source file to correct the problem PMD discovered.

XXXXXXXXXXXXXXXXXXXXXXXX
X        OPTIONS       X
XXXXXXXXXXXXXXXXXXXXXXXX

------------------------
RULES
------------------------
1) Start NetBeans 
2) Goto Tools --> Options
3) Navigate to Options/IDE Configuration/Server And External Tool Settings/PMD Settings
4) Click on "Rules"
5) Click on the [...] button
6) Select the different rules to see information and examples of the rule
7) Use the <, <<, >, >>, buttons to choose which rules to use.
8) Press OK to save the configuration

------------------------
CUSTOM RULESETS
------------------------
1) Start NetBeans 
2) Goto Tools --> Options
3) Navigate to Options/IDE Configuration/Server And External Tool Settings/PMD Settings
4) Click on the Expert tab
5) Click on "Rulesets"
6) Click "Add RuleSet" to locate the ruleset xml file you want to include
7) Click "Add Rule jar" to locate the jar containing the rules
8) Check "Include PMD standard rules" to include the rules from the pmd distribution
9) Press OK to save the configuration
10) Go to Rules to select the new rules.

------------------------
REALTIME SCANNING
------------------------
1) Start NetBeans 
2) Goto Tools --> Options
3) Navigate to Options/IDE Configuration/Server And External Tool Settings/PMD Settings
4) Click on "Enable scan"
5) Choose true to enable scan, false otherwise.
6) Click on "Scan interval"
7) Enter the number of seconds between each scan
XX) NetBeans requires a restart to disable scanning
