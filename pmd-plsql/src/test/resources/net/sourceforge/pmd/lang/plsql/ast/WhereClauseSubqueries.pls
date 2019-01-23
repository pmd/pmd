--
-- Where Clause with Subqueries
--

BEGIN

SELECT id INTO v_id FROM table
  WHERE id = (SELECT id FROM other_table);

UPDATE table SET name = 'a'
  WHERE id = (SELECT id FROM other_table);

END;
/
