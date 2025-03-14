--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE IS
   --
   CURSOR c_example IS
      SELECT a.owner, u.object_name, p.aggregate
        FROM (USER_OBJECTS u) INNER JOIN (ALL_OBJECTS a) ON
               u.object_name = a.object_name AND u.object_type = a.object_type AND u.object_id = a.object_id
               INNER JOIN (ALL_PROCEDURES p) ON
               p.owner = a.owner AND p.object_name = a.object_name AND p.object_type = a.object_type
       WHERE a.owner = USER;
   --
BEGIN
   --
   FOR l_object IN c_example LOOP
      --
      DBMS_OUTPUT.Put_Line(l_object.owner);
      DBMS_OUTPUT.Put_Line(l_object.object_name);
      DBMS_OUTPUT.Put_Line(l_object.aggregate);
      --
   END LOOP;
   --
END EXAMPLE_PROCEDURE;
