--
-- Where Clause With Is Of Type Condition
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/IS-OF-type-Condition.html#GUID-7254E4C7-0194-4C1F-A3B2-2CFB0AD907CD
--

BEGIN

SELECT *
   INTO rec
   FROM persons p 
   WHERE VALUE(p) IS OF TYPE (employee_t);

SELECT *
   INTO rec
   FROM persons p 
   WHERE VALUE(p) IS OF (ONLY part_time_emp_t);

END;
/
