---
title: PMD Apex Rules
short_title: Apex
tags: [languages]
summary: PMD Apex Rules
series: Supported Languges
weight: 1
last_updated: July 3, 2016
sidebar: mydoc_sidebar
permalink: language_apex.html
folder: mydoc
---

## PMD Has the Following Rules for Apex *(Salesforce)*

### Performance

|   Rule   |   Description   |
|   ---   |   ---   |
|   AvoidSoqlInLoops   |   New objects created within loops should be checked to see if they can created outside them and reused.   |
|   AvoidDmlStatementsInLoops   |   Avoid DML statements inside loops to avoid hitting the DML governor limit. Instead, try to batch up the data into a list and invoke your DML once on that list of data outside the loop.   |

### Complexity

|   Rule   |   Description   |
|   ---   |   ---   |
|   AvoidDeeplyNestedIfStmts   |   Avoid creating deeply nested if-then statements since they are harder to read and error-prone to maintain.   |
|   ExcessiveParameterList   |   Methods with numerous parameters are a challenge to maintain, especially if most of them share the same datatype. These situations usually denote the need for new objects to wrap the numerous parameters.   |
|   ExcessiveClassLength   |   Excessive class file lengths are usually indications that the class may be burdened with excessive responsibilities that could be provided by external classes or functions. In breaking these methods apart the code becomes more managable and ripe for reuse.  |
|   NcssMethodCount   |   This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines of code for a given method. NCSS ignores comments, and counts actual statements. Using this algorithm, lines of code that are split are counted as one.   |
|   NcssTypeCount   |   This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines of code for a given type. NCSS ignores comments, and counts actual statements. Using this algorithm, lines of code that are split are counted as one.   |
|   NcssConstructorCount   |   This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines of code for a given constructor. NCSS ignores comments, and counts actual statements. Using this algorithm, lines of code that are split are counted as one.   |
|   StdCyclomaticComplexity   |   Complexity directly affects maintenance costs is determined by the number of decision points in a method plus one for the method entry. The decision points include ‘if’, ‘while’, ‘for’, and ‘case labels’ calls. Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote high complexity, and 11+ is very high complexity.   |
|   TooManyFields   |   Classes that have too many fields can become unwieldy and could be redesigned to have fewer fields, possibly through grouping related fields in new objects. For example, a class with individual city/state/zip fields could park them within a single Address field.   |
|   ExcessivePublicCount   |   Classes with large numbers of public methods and attributes require disproportionate testing efforts since combinational side effects grow rapidly and increase risk. Refactoring these classes into smaller ones not only increases testability and reliability but also allows new variations to be developed easily.   |

### Style

|   Rule   |   Description   |
|   ---   |   ---   |
|   VariableNamingConventions   |   A variable naming conventions rule - customize this to your liking. Currently, it checks for final variables that should be fully capitalized and non-final variables that should not include underscores.   |
|   MethodNamingConventions   |   Method names should always begin with a lower case character, and should not contain underscores.   |
|   ClassNamingConventions   |   Class names should always begin with an upper case character.   |
|   MethodWithSameNameAsEnclosingClass   |   Non-constructor methods should not have the same name as the enclosing class.   |
|   AvoidLogicInTrigger   |   As triggers do not allow methods like regular classes they are less flexible and suited to apply good encapsulation style. Therefore delegate the triggers work to a regular class (often called Trigger handler class). See more here: <a href="https://developer.salesforce.com/page/Trigger_Frameworks_and_Apex_Trigger_Best_Practices" target="_blank">Trigger Frameworks and Apex Trigger Best Practices</a>  |
|   AvoidGlobalModifier   |   Global classes should be avoided (especially in managed packages) as they can never be deleted or changed in signature. Always check twice if something needs to be global. Many interfaces (e.g. Batch) required global modifiers in the past but don’t require this anymore. Don’t look yourself in.   |


{% include links.html %}
