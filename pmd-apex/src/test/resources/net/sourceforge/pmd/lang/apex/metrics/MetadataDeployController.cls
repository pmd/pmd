public with sharing class MetadataDeployController
{
	private class Foo {
    }

	global String ZipData { get; set; }

	public MetadataService.AsyncResult AsyncResult {get; private set;}

	public String getPackageXml(String page)
	{
		return '<?xml version="1.0" encoding="UTF-8"?>' +
			'<Package xmlns="http://soap.sforce.com/2006/04/metadata">' +
    			'<types>' +
        			'<members>HelloWorld</members>' +
        			'<name>ApexClass</name>' +
    			'</types>' +
    			'<version>26.0</version>' +
			'</Package>';
	}

	public String getHelloWorldMetadata()
	{
		return '<?xml version="1.0" encoding="UTF-8"?>' +
			'<ApexClass xmlns="http://soap.sforce.com/2006/04/metadata">' +
			    '<apiVersion>28.0</apiVersion>' +
			    '<status>Active</status>' +
			'</ApexClass>';
	}

	public String getHelloWorld()
	{
		return 'public class HelloWorld' +
			'{' +
				'public static void helloWorld()' +
				'{' +
					'System.debug(\' Hello World\');' +
				'}' +
			'}';
	}
}