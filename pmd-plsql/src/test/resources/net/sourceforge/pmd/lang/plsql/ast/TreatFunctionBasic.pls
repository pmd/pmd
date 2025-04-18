--
-- Example from https://docs.oracle.com/en/database/oracle/oracle-database/23/sqlrf/TREAT.html
--

SELECT name, TREAT(VALUE(p) AS employee_t).salary salary
   FROM persons p;

SELECT name, TREAT(VALUE(p) AS REF employee_t).salary salary
   FROM persons p;

SELECT name, TREAT(VALUE(p) AS JSON) salary
   FROM persons p;

