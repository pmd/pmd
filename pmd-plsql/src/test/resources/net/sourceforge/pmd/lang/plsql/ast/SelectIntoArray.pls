--
-- See https://github.com/pmd/pmd/issues/3515
--

CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE IS
   --
   TYPE example_data_rt IS RECORD(
      field_one   PLS_INTEGER,
      field_two   PLS_INTEGER,
      field_three PLS_INTEGER);
   --
   TYPE example_data_aat IS TABLE OF example_data_rt INDEX BY BINARY_INTEGER;
   --
   l_example_data example_data_aat;
   --
BEGIN
   --
   SELECT 1 field_value_one, 2 field_value_two, 3 field_value_three
     INTO l_example_data(1).field_one,l_example_data(1).field_two,l_example_data(1).field_three
     FROM DUAL;
   --
END EXAMPLE_PROCEDURE;
