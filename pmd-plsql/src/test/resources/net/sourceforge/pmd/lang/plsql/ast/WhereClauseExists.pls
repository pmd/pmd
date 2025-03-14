--
-- Where Clause With Exists Condition
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/EXISTS-Condition.html#GUID-20259A83-C42B-4E0D-8DF4-9A2A66ACA8E7
--

BEGIN

SELECT id
  INTO id_out
  FROM some_table
  WHERE EXISTS
    (SELECT NULL
     FROM other_table
     WHERE other_id = other_id_in);

DELETE FROM some_table
  WHERE id = id_in
  AND NOT EXISTS
    (SELECT NULL
     FROM other_table
     WHERE id = id_in);

END;
/
