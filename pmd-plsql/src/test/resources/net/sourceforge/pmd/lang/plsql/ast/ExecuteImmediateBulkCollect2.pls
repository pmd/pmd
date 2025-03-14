--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE IS
   --
   TYPE t_data_key IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;
   TYPE t_data_description IS TABLE OF VARCHAR2(100) INDEX BY BINARY_INTEGER;
   --
   l_data_key t_data_key;
   l_data_description t_data_description;
   l_sql  VARCHAR2(500);
   p_rownum1 NUMBER(20);
   p_rownum2 NUMBER(20);
   --
BEGIN
   --
   p_rownum1 := 10;
   p_rownum2 := 30;
   --
   l_sql := 'SELECT 1, ' || CHR(39) || 'First key' || CHR(39) || 
             ' FROM DUAL '||' WHERE ROWNUM <= :p_rownum1 ' ||
            'UNION ALL '||
            'SELECT 2, ' || CHR(39) || 'Second key ' || CHR(39) || 
             ' FROM DUAL'||' WHERE ROWNUM <= :p_rownum2 ';
   --
   EXECUTE IMMEDIATE l_sql BULK COLLECT
      INTO l_data_key, l_data_description USING p_rownum1, p_rownum2;
   --
END EXAMPLE_PROCEDURE;
