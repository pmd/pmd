/*
 * From: https://docs.oracle.com/cd/B13789_01/appdev.101/b10807/13_elems034.htm
 */
DECLARE
   TYPE EmpCurTyp IS REF CURSOR;  -- define weak REF CURSOR type
   emp_cv   EmpCurTyp;  -- declare cursor variable
   my_ename VARCHAR2(15);
   my_sal   NUMBER := 1000;
BEGIN
   OPEN emp_cv FOR  -- open cursor variable
      'SELECT ename, sal FROM emp WHERE sal > :s' USING my_sal;
   open cursorvar for 'query' USING variable;
   open cursorvar for 'query' USING IN variable;
   open cursorvar for 'query' USING OUT variable, IN othervariable;
   open cursorvar for 'query' USING IN OUT variable;
END;
