DECLARE
    TYPE t_cmer_ids IS TABLE OF comp_mprc_emp_role.id%TYPE;
    TYPE t_versions IS TABLE OF comp_mprc_emp_role.version%TYPE;
    TYPE t_cmp_ids IS TABLE OF comp_mprc_emp_role.cmp_id%TYPE;
    TYPE t_mprc_codes IS TABLE OF comp_mprc_emp_role.mprc_code%TYPE;
    TYPE t_emp_ids IS TABLE OF comp_mprc_emp_role.emp_id%TYPE;
    TYPE t_emp_role_codes IS TABLE OF comp_mprc_emp_role.emp_role_code%TYPE;
    TYPE t_gsn_ids IS TABLE OF comp_mprc_emp_role.gsn_id%TYPE;
    TYPE t_dep_codes IS TABLE OF comp_mprc_emp_role.dep_code%TYPE;
    TYPE t_validfroms IS TABLE OF DATE;
    TYPE t_validtos IS TABLE OF DATE;
    TYPE t_fl_actives IS TABLE OF VARCHAR2(1);
    TYPE t_reporting_lists IS TABLE OF v_sfm_emp_role_cny_repg.reporting_groups%TYPE;

    v_cmer_ids          t_cmer_ids;
    v_versions          t_versions;
    v_cmp_ids           t_cmp_ids;
    v_mprc_codes        t_mprc_codes;
    v_emp_ids           t_emp_ids;
    v_emp_role_codes    t_emp_role_codes;
    v_gsn_ids           t_gsn_ids;
    v_dep_codes         t_dep_codes;
    v_validfroms        t_validfroms;
    v_validtos          t_validtos;
    v_fl_actives        t_fl_actives;
    v_reporting_lists   t_reporting_lists;
BEGIN
    SELECT   cmer_id
            ,version
            ,cmp_id
            ,mprc_code
            ,emp_id
            ,emp_role_code
            ,gsn_id
            ,dep_code
            ,validfrom
            ,validto
            ,fl_active
            ,reporting_list
    BULK COLLECT INTO v_cmer_ids
            ,v_versions
            ,v_cmp_ids
            ,v_mprc_codes
            ,v_emp_ids
            ,v_emp_role_codes
            ,v_gsn_ids
            ,v_dep_codes
            ,v_validfroms
            ,v_validtos
            ,v_fl_actives
            ,v_reporting_lists
    FROM (SELECT cmer.cmer_id
                ,cmer.version
                ,cmer.cmp_id
                ,cmer.mprc_code
                ,cmer.emp_id
                ,cmer.emp_role_code
                ,cmer.gsn_id
                ,cmer.dep_code
                ,TRUNC(insert_entry.dmltime)    AS validfrom
                ,NULL                           AS validto
                ,'Y'                            AS fl_active
                ,sfm.reporting_groups           AS reporting_list
            FROM webcrm.comp_mprc_emp_role cmer
                JOIN webcrm.jcomp_mprc_emp_role insert_entry
                    ON (insert_entry.cmer_id = cmer.cmer_id
                    AND insert_entry.operation = 'I')
                LEFT JOIN webcrm.v_company_address caddr ON (caddr.cmp_id = cmer.cmp_id)
                LEFT JOIN webcrm.v_sfm_emp_role_cny_repg sfm
                    ON (sfm.emp_id = cmer.emp_id
                    AND sfm.emp_role_code = cmer.emp_role_code
                    AND sfm.cny_code = NVL(caddr.s_cny_code, caddr.m_cny_code))
        UNION
        SELECT   jcmer.cmer_id
                ,jcmer.version
                ,jcmer.cmp_id
                ,jcmer.mprc_code
                ,jcmer.emp_id
                ,jcmer.emp_role_code
                ,jcmer.gsn_id
                ,jcmer.dep_code
                ,TRUNC(insert_entry.dmltime)    AS validfrom
                ,TRUNC(jcmer.dmltime)           AS validfrom
                ,'N'                            AS fl_active
                ,sfm.reporting_groups           AS reporting_list
            FROM webcrm.jcomp_mprc_emp_role jcmer
                JOIN webcrm.jcomp_mprc_emp_role insert_entry
                    ON (insert_entry.cmer_id = jcmer.cmer_id
                    AND insert_entry.operation = 'I')
                LEFT JOIN webcrm.v_company_address caddr ON (caddr.cmp_id = jcmer.cmp_id)
                LEFT JOIN v_sfm_emp_role_cny_repg sfm
                    ON (sfm.emp_id = jcmer.emp_id
                    AND sfm.emp_role_code = jcmer.emp_role_code
                    AND sfm.cny_code = NVL(caddr.s_cny_code, caddr.m_cny_code))
            WHERE jcmer.cmer_id NOT IN (SELECT cmer.cmer_id
                                            FROM webcrm.comp_mprc_emp_role cmer)
                AND jcmer.operation = 'D');
END;
/
