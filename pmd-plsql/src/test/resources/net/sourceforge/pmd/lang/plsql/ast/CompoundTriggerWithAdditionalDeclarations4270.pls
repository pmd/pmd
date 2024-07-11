--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

-- https://github.com/pmd/pmd/issues/4270

-- TRIGGER EXAMPLE
CREATE OR REPLACE TRIGGER EXAMPLE_TRIGGER
   FOR INSERT ON TEST_TABLE
   COMPOUND TRIGGER
   --
   l_number_one NUMBER(2) := ROUND(DBMS_RANDOM.Value(1, 10));
   l_number_two NUMBER(2) := ROUND(DBMS_RANDOM.Value(1, 10));
   --
   BEFORE EACH ROW IS
      --
      -- This is not officially documented, but might be possible - declarations before the begin block
      l_tot_numbers NUMBER(2);
      --
   BEGIN
      --
      l_tot_numbers := l_number_one + l_number_two;
      --
      :NEW.col_one   := l_number_one;
      :NEW.col_two   := l_number_two;
      :NEW.col_three := l_tot_numbers;
      --
   EXCEPTION
      WHEN OTHERS THEN
         RAISE_APPLICATION_ERROR(-20001, 'ERROR AT EXAMPLE_TRIGGER - BEFORE EACH ROW' || CHR(10) || SQLERRM);
   END BEFORE EACH ROW;
   --
   --
   AFTER STATEMENT IS
   BEGIN
      --
      DBMS_OUTPUT.Put_Line('This is just an example!');
      --
   EXCEPTION
      WHEN OTHERS THEN
         RAISE_APPLICATION_ERROR(-20002, 'ERROR AT EXAMPLE_TRIGGER - AFTER STATEMENT' || CHR(10) || SQLERRM);
   END AFTER STATEMENT;
   --
END EXAMPLE_TRIGGER;

