--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE FORCE VIEW "HR_DEV1"."EMP_DETAILS_VIEW" ("EMPLOYEE_ID") AS
SELECT
e.employee_id
FROM
employees e
WITH READ ONLY
;
