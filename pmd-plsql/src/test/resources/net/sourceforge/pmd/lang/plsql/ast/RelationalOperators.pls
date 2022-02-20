CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE IS
   --
   l_total_objects NUMBER(10);
   --
BEGIN
   --
   SELECT COUNT(1)
     INTO l_total_objects
     FROM USER_OBJECTS
    WHERE namespace < = 1
       OR namespace > = 0
       OR namespace >
       = 0
       ;
   --
   DBMS_OUTPUT.Put_Line('Total number of objects: ' || l_total_objects);
   --
END EXAMPLE_PROCEDURE;
