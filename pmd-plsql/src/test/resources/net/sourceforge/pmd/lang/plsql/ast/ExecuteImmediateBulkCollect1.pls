--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE IS
   --
   TYPE t_data IS TABLE OF dual%ROWTYPE INDEX BY BINARY_INTEGER;
   --
   l_data t_data;
   l_sql  VARCHAR2(500);
   --
BEGIN
   --
   l_sql := 'SELECT * FROM DUAL';
   --
   EXECUTE IMMEDIATE l_sql BULK COLLECT
      INTO l_data;
   --
END EXAMPLE_PROCEDURE;
