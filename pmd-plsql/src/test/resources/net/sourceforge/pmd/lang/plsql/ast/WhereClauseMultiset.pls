--
-- Where Clause With Multiset Conditions
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/Multiset-Conditions.html#GUID-E8164A15-715A-40A0-944D-26DF4C84DE3F
--

BEGIN

SELECT customer_id, cust_address_ntab
  INTO test
  FROM customers_demo
  WHERE cust_address_ntab IS A SET
  ORDER BY customer_id;

SELECT product_id, TO_CHAR(ad_finaltext) AS text
   INTO test
   FROM print_media
   WHERE ad_textdocs_ntab IS NOT EMPTY 
   ORDER BY product_id, text;

SELECT customer_id, cust_address_ntab
  INTO test
  FROM customers_demo
  WHERE cust_address_typ('8768 N State Rd 37', 47404, 
                         'Bloomington', 'IN', 'US')
  MEMBER OF cust_address_ntab
  ORDER BY customer_id;

SELECT customer_id, cust_address_ntab
  INTO test
  FROM customers_demo
  WHERE cust_address_ntab SUBMULTISET OF cust_address2_ntab
  ORDER BY customer_id;

END;
/
