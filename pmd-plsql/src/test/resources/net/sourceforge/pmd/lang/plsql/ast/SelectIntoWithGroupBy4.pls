--
-- Using the GROUPING SETS Clause: Example
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6__I2066419
--
BEGIN

SELECT channel_desc, calendar_month_desc, co.country_id,
      amount_sold SALES$
   INTO some_record
   FROM sales, customers, times, channels, countries co
   WHERE sales.time_id=times.time_id 
      AND sales.cust_id=customers.cust_id 
      AND sales.channel_id= channels.channel_id 
      AND customers.country_id = co.country_id
      AND channels.channel_desc IN ('Direct Sales', 'Internet') 
      AND times.calendar_month_desc IN ('2000-09', '2000-10')
      AND co.country_iso_code IN ('UK', 'US')
  GROUP BY GROUPING SETS( 
      (channel_desc, calendar_month_desc, co.country_id), 
      (channel_desc, co.country_id), 
      (calendar_month_desc, co.country_id) );

END;
/
