/**
 * OWASP Enterprise Security API (ESAPI)
 *
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * <a href="http://www.owasp.org/index.php/ESAPI">http://www.owasp.org/index.php/ESAPI</a>.
 *
 * Copyright (c) 2010 - Salesforce.com
 *
 * The Apex ESAPI implementation is published by Salesforce.com under the New BSD license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 *
 * @author Yoel Gluck (securecloud .at. salesforce.com) <a href="http://www.salesforce.com">Salesforce.com</a>
 * @created 2010
 */

/**
 * This class is a basic encoder/escaper to help prevent some XSS attacks etc.
 */
global with sharing class SFDCEncoder {

	/* TODO Yoel - all these functions should be converted into a white list aproach - I am using blacklist to be consistent with the VISUALFORCE functions */
	/* TODO Yoel - Do we need to encode ASCII/Unicode white-space/new-line characters? These used to cause some security issues in some browsers not sure if this is still the case */

	/* Note - the order of these encoding strings is very important so we don't end up with double encoding.
	      Each string we search for, must not be found as a result of a previous encoded string replacement */
	/************ CLASS CODE HERE *************/

	public String someParam { get; set; }
}

