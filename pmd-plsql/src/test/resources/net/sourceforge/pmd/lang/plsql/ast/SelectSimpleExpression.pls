--
-- Expressions in the SelectList
--

BEGIN

SELECT e.first_name
  INTO test
  FROM employees e;

END;
/