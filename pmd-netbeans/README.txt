PMD for NetBeans.

------------------------
INSTALL
------------------------

 1) Start NetBeans 
 2) Goto Tools --> Plugins
 3) Switch to 'Downloaded' tab
 4) Press 'Add plugins'
 5) Goto the directory where you downloaded pmd-netbeans and choose the file "pmd.nbm".
 6) Press OK
 7) Press Install
 8) Accept the license
 9) Confirm installation of an usigned module

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
3) Navigate to Miscelaneous section and expand PMD node
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
3) Navigate to Miscelaneous section and expand PMD node
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
3) Navigate to Miscelaneous section and expand PMD node
4) Select "Enable scan"
