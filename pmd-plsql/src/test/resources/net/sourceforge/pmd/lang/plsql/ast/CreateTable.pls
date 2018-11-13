--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

-- this is the customers.sql file:
CREATE TABLE customers
( customer_id number(10) NOT NULL,
customer_name varchar2(50) NOT NULL,
city varchar2(50),
CONSTRAINT customers_pk PRIMARY KEY (customer_id)
);
