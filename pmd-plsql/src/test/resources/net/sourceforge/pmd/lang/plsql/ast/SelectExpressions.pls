--
-- Expressions in the SelectList
--

BEGIN

SELECT AVG(sal)*2 INTO foo FROM bar;


SELECT
    AVG(salary) * 12 "Average Sal"
  INTO some_record
  FROM some_table;

SELECT
    TO_CHAR(sum(amount_sold) , '9,999,999,999') SALES$
  INTO some_record
  FROM some_table;


END;
/