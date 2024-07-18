--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PACKAGE BODY update_planned_hrs
IS

PROCEDURE set_new_planned (p_emp_id IN NUMBER, p_project_id IN NUMBER, p_hours IN NUMBER)
IS
BEGIN
    UPDATE employee_on_activity ea
    SET ea.ea_planned_hours = p_hours
    WHERE
        ea.ea_emp_id = p_emp_id
        AND ea.ea_proj_id = p_project_id;

EXCEPTION
        WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR (-20100, 'No such employee or project');

END set_new_planned;

FUNCTION existing_planned (p_emp_id IN NUMBER, p_project_id IN NUMBER) RETURN NUMBER

IS

existing_hours NUMBER(4);

BEGIN
   SELECT ea.ea_planned_hours INTO existing_hours
   FROM employee_on_activity ea
   WHERE
            ea.ea_emp_id = p_emp_id
            AND ea.ea_proj_id = p_project_id;

   RETURN (existing_hours);

   EXCEPTION
          WHEN OTHERS THEN NULL; -- rule violation

   END existing_planned;

END update_planned_hrs;
/
