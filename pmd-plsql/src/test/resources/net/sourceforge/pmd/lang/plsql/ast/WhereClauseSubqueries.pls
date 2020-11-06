--
-- Where Clause with Subqueries
--

BEGIN

SELECT id INTO v_id FROM my_table
  WHERE id = (SELECT id FROM other_table);

UPDATE my_table SET name = 'a'
  WHERE id = (SELECT id FROM other_table);

END;
/
