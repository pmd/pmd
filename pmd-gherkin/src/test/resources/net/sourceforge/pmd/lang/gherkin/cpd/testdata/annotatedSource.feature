@AnnotatedSource 
Feature: Annotated Source
  The annotated source displays violations in the source file. It opens in a new window.
  
  Rules:
  1 Annotation metrics can be selected with a dropdown menu. Only enabled metrics must be shown.
  2 When the annotated source is opened while a specific metric is selected, only that metric should be selected.
  3 A user can scroll through the violations using two buttons (illustrated by up and down arrows). The arrows wrap around the document.
  4 The table collumn "Type" is only shown when multiple metrics are selected

  Scenario: Select a metric type
   Given the Annotated Source for file "HIE://11261-37/main/monop/execute.c"
    When a user opens the dropdown menu containing "Metric:"
     And the user clicks on the dropdown option "Violations/Coding Standard Violations"
    Then the selected annotation in the source code should be on line 38 
     And the selected annotation in the table should be on line 38

  Scenario: The user can use the arrows, or "a" and "z" keys, to scroll through the annotations
  Given the Annotated Source for file "HIE://11261-37/main/monop/execute.c"
    And metric "Coding Standard Violation Annotations" is selected
   When the user clicks on the down arrow
    And the user presses the "Z" key
    And the user clicks on the up arrow
    And the user clicks on the up arrow
    And the user presses the "A" key
   Then the selected annotation in the source code should be on line 254 

  @Rule2
  Scenario Outline: If the user opens the annotated source from e.g. the dashboard for a metric,
    only the related annotations should be shown.
  Given the Dashboard
    And filtering by Project "17607"
    And grouping by "File"
    And metric "<metric>" is selected
   When a user opens the file "clalgorithm_settings.c" using the metric table
   Then a new browser window with an "Annotated Source" should be opened  
   Then only the annotations "<annotations>" should be selected
   
  Examples:
  | metric                     | annotations                           |
  | TQI Coding Standards       | Coding Standard Violation Annotations |  
  | Coding Standard Violations | Coding Standard Violation Annotations |  
  | TQI Compiler Warnings      | Compiler Warning Annotations          |  
  | Fan Out (%)                | Fan Out Annotations                   |  
  | TQI Dead Code              | Dead Code Annotations                 |  
  | TQI Code Duplication       | Code Duplication Annotations          |
  
  Scenario: The user should be able to filter Coding Standard Violations by Level
    Given the Annotated Source for file "HIE://11514/trunk/components/java/BuildUtil/src/com/tiobe/util/BuildProperties.java"
    And   the metric "Coding Standard Violations" is selected
    When  the user opens the dropdown menu "Level"
    And   the user clicks on the dropdown option "5"
    Then  there should be 1 violation
    And   the selected annotation in the source code should be on line 57
    And   the annotation should be of level 5 

  @PR27030
  Scenario Outline: The user should be able to filter Coding Standard Violations by Level, Category, Rule, etc
    Given the Annotated Source for file "HIE://12939/main/Implementatie/DRGL/src/DirectDoorvoerenAdmin.cpp"
    And metric "<metric>" is selected
    When the user opens the dropdown menu containing "<filter>" inside the filter bar
    And the user clicks on the dropdown option "<option>"
    Then there should be <number> violations
    
  Examples:
    | metric                      | filter        | option      | number  |
    | Coding Standard Violations  | Level         | 2           | 7       | 
    | Coding Standard Violations  | Category      | Comments    | 1       |
    | Coding Standard Violations  | Rule          | CFL#011     | 2       |
    | Coding Standard Violations  | Suppressions  | Yes         | 0       |
    | TQI Code Coverage           | Coverage Type | Decision    | 7       |
    | TQI Code Coverage           | Kind          | Issue       | 10      |

