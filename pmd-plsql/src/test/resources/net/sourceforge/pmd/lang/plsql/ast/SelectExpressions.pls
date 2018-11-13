--
-- Expressions in the SelectList
--

BEGIN

SELECT
   some_col "alias in quotes"
   INTO some_record
   FROM some_table;


SELECT
        COUNT(*) "Total Empl"
   INTO some_record
   FROM some_table;

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