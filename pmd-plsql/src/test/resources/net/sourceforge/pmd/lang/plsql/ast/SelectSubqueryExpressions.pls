--
-- Subqueries / Expressions in the SelectList
--

BEGIN

SELECT (SELECT a FROM DUAL) INTO foo FROM DUAL;

SELECT (SELECT MAX(a) FROM DUAL) INTO foo FROM DUAL;

SELECT (AVG(sal)*2) INTO foo FROM bar;

SELECT a.object_name,
  (SELECT MAX(NVL(b.position, -1)) FROM DUAL)
  FROM DUAL;

END;
/