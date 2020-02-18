/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

/*
 * This file is to test the JavaCC java grammer, whether we can parse specific java constructs without
 * throwing a syntax error.
 *
 * Java 7, see: http://docs.oracle.com/javase/7/docs/technotes/guides/language/enhancements.html#javase7
 */
public class ParserCornerCases17 {

	public ParserCornerCases17() {
		super();
	}

	public void binaryLiterals() {
		// An 8-bit 'byte' value:
		byte aByte = (byte)0b00100001;

		// A 16-bit 'short' value:
		short aShort = (short)0b1010000101000101;

		// Some 32-bit 'int' values:
		int anInt1 = 0b10100001010001011010000101000101;
		int anInt2 = 0b101;
		int anInt3 = 0B101; // The B can be upper or lower case.

		// A 64-bit 'long' value. Note the "L" suffix:
		long aLong = 0b1010000101000101101000010100010110100001010001011010000101000101L;

		int[] phases = {
			  0b00110001,
			  0b01100010,
			  0b11000100,
			  0b10001001,
			  0b00010011,
			  0b00100110,
			  0b01001100,
			  0b10011000
		};

		int instruction = 0;
		if ((instruction & 0b11100000) == 0b00000000) {
		    final int register = instruction & 0b00001111;
		    switch (instruction & 0b11110000) {
		      case 0b00000000: break;
		      case 0b00010000: break;
		      case 0b00100000: break;
		      case 0b00110000: break;
		      case 0b01000000: break;
		      case 0b01010000: break;
		      case 0b01100000: break;
		      case 0b01110000: break;
		      default: throw new IllegalArgumentException();
		    }
		}
	}

	public void underscoreInNumericLiterals() {
		long creditCardNumber = 1234_5678_9012_3456L;
		long socialSecurityNumber = 999_99_9999L;
		float pi = 	3.14_15F;
		long hexBytes = 0xFF_EC_DE_5E;
		long hexWords = 0xCAFE_BABE;
		long maxLong = 0x7fff_ffff_ffff_ffffL;
		byte nybbles = 0b0010_0101;
		long bytes = 0b11010010_01101001_10010100_10010010;

		int _52 = 1;
		int x1 = _52;              // This is an identifier, not a numeric literal
		int x2 = 5_2;              // OK (decimal literal)
		int x4 = 5_______2;        // OK (decimal literal)
		int x7 = 0x5_2;            // OK (hexadecimal literal)
		int x9 = 0_52;             // OK (octal literal)
		int x10 = 05_2;            // OK (octal literal)
	}

	public String stringsInSwitchStatements() {
		 String dayOfWeekArg = "Wednesday";
		 String typeOfDay;
	     switch (dayOfWeekArg) {
	         case "Monday":
	             typeOfDay = "Start of work week";
	             break;
	         case "Tuesday":
	         case "Wednesday":
	         case "Thursday":
	             typeOfDay = "Midweek";
	             break;
	         case "Friday":
	             typeOfDay = "End of work week";
	             break;
	         case "Saturday":
	         case "Sunday":
	             typeOfDay = "Weekend";
	             break;
	         default:
	             throw new IllegalArgumentException("Invalid day of the week: " + dayOfWeekArg);
	     }
	     return typeOfDay;
	}

	class MyClass<X> {
		<T> MyClass(T t) {
		}
	}

	public void typeInferenceForGenericInstanceCreation() {
		Map<String, List<String>> myMap = new HashMap<>();

		List<String> list = new ArrayList<>();
		list.add("A");
		List<? extends String> list2 = new ArrayList<>();
		list.addAll(list2);

		MyClass<Integer> myObject = new MyClass<>("");
	}

	public void theTryWithResourcesStatement() throws IOException {
		String path = "/foo";
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
		    String first = br.readLine();
		}

		// Two resources declared
		String outputFileName = "/foo-out";
		String zipFileName = "/foo.zip";
		java.nio.charset.Charset charset = java.nio.charset.Charset.forName("US-ASCII");
	    java.nio.file.Path outputFilePath = java.nio.file.Paths.get(outputFileName);

	    // Open zip file and create output file with try-with-resources statement
	    try (
	      java.util.zip.ZipFile zf = new java.util.zip.ZipFile(zipFileName);
	      java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(outputFilePath, charset)
	    ) {

	      // Enumerate each entry

	      for (Enumeration<? extends ZipEntry> entries = zf.entries(); entries.hasMoreElements();) {

	        // Get the entry name and write it to the output file

	        String newLine = System.getProperty("line.separator");
	        String zipEntryName = ((java.util.zip.ZipEntry)entries.nextElement()).getName() + newLine;
	        writer.write(zipEntryName, 0, zipEntryName.length());
	      }
	    }
	}

	public void catchingMultipleExceptionTypes() throws IOException, SQLException {
		try {
			if (new File("foo").createNewFile()) {
				throw new SQLException();
			}

		} catch (IOException|SQLException ex) {
			ex.printStackTrace();
		    throw ex;
		}
	}

	// With java 8 lambda grammar enhancement, this caused a problem, to not be identified as lambda...
	public void expressionInCastExpression() {
        // grammar/parser: don't get confused with this...
        int initialSizeGlobal = (int) (profilingContext.m_profileItems.size() * (150.0 * 0.30));
	}
}
