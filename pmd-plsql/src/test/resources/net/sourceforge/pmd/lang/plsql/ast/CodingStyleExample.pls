PROCEDURE create_prospect(
  company_info_in       IN            prospects.company_info%TYPE             -- Organization
 ,firstname_in          IN            persons.firstname%TYPE                  -- FirstName
 ,lastname_in           IN            persons.lastname%TYPE                   -- LastName

-- ...
 ,message_in            IN OUT NOCOPY CLOB                                    -- the whole message
  )
  IS
    v_adt_id        academic_titles.adt_id%TYPE := 1;
    v_region_id     addresses.rgn_id%TYPE;
    v_prospect_id   prospects.prosp_id%TYPE;
    v_address_id    addresses.adr_id%TYPE := 1;
    v_message_rec   snapaddy_messages%ROWTYPE;
  BEGIN
    /* I AM A FORBIDDEN COMMENT SINCE I AM A BLOCK COMMENT */

    -- try to find a matching academic title.
    -- this comment is on a separate line, so it is left aligned
    BEGIN                                                                       -- this comment is on the same line as an PL/SQL statement, it is right aligned
      SELECT adt.adt_id
        INTO v_adt_id
        FROM academic_titles adt
       WHERE UPPER(adt.short_description) = UPPER(title_in);
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_adt_id := NULL;
      WHEN TOO_MANY_ROWS THEN
        v_adt_id := NULL;
    END;


    -- try to find a matching region
    --      1. a case insensitive lookup on the name (restricted to the given country)
    BEGIN
      SELECT rgn.rgn_id
        INTO v_region_id
        FROM regions rgn
       WHERE (street_cny_code_in IS NULL
           OR rgn.cny_code = street_cny_code_in)
         AND UPPER(rgn.name) = UPPER(street_rgn_in);
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_region_id     := NULL;
      WHEN TOO_MANY_ROWS THEN
        v_region_id     := NULL;
    END;

    --      2. a case insensitive lookup on the SAP_CODE (restricted to the given country)
    IF v_region_id IS NULL THEN
      BEGIN
        SELECT rgn.rgn_id
          INTO v_region_id
          FROM regions rgn
         WHERE (street_cny_code_in IS NULL
             OR rgn.cny_code = street_cny_code_in)
           AND UPPER(rgn.sap_code) = UPPER(street_rgn_in);
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_region_id := NULL;
        WHEN TOO_MANY_ROWS THEN
          v_region_id := NULL;
      END;
    END IF;

    -- all preparations are done - create the prospect with the available data
    webcrm_marketing.prospect_ins(
      cmp_id_in         => NULL
     ,company_info_in   => company_info_in
     ,firstname_in      => firstname_in
     ,lastname_in       => lastname_in
     -- ...
     ,prosp_id_out      => v_prospect_id
    );

    -- create the address with the available data
    webcrm_marketing.address_ins(
      pa_id_in              => v_prospect_id
     ,street_city_in        => street_city_in
     ,street_tmz_id_in      => NULL
     ,street_cny_code_in    => street_cny_code_in
     ,street_rgn_id_in      => v_region_id
     ,street_postalcode_in  => street_postalcode_in
     ,street_addrline_1_in  => street_addrline_1_in
     ,street_addrline_2_in  => NULL
     ,street_addrline_3_in  => NULL
     ,street_addr_id_out    => v_address_id
    );

    -- store the obtained message
    v_message_rec.snap_msg_id       := seq_snap_msg.NEXTVAL;
    v_message_rec.MESSAGE           := message_in;
    v_message_rec.endpoint          := 'prospect/creation';
    v_message_rec.processing_time   := SYSDATE;
    dml_snap_msg.ins(v_message_rec);
END create_prospect;
