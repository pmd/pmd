package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import net.sourceforge.pmd.util.datasource.ZipDataSource;
import net.sourceforge.pmd.util.filter.AndFilter;
import net.sourceforge.pmd.util.filter.Filter;
import net.sourceforge.pmd.util.filter.Filters;
import net.sourceforge.pmd.util.filter.OrFilter;

/**
 * This is a utility class for working with Files.
 */
public class FileUtil {

    /**
     * Collects a list of DataSources using a comma separated list of input file
     * locations to process.  If a file location is a directory, the directory
     * hierarchy will be traversed to look for files.  If a file location is a
     * ZIP or Jar the archive will be scanned looking for files.  If a file
     * location is a file, it will be used.  For each located file, a
     * FilenameFilter is used to decide whether to return a DataSource.
     *
     * @param fileLocations A comma-separated list of file locations.
     * @param filenameFilter  The FilenameFilter to apply to files.
     * @return A list of DataSources, one for each file collected.
     */
    public static List<DataSource> collectFiles(String fileLocations, FilenameFilter filenameFilter) {
	List<DataSource> dataSources = new ArrayList<DataSource>();
	for (String fileLocation : fileLocations.split(",")) {
	    collect(dataSources, fileLocation, filenameFilter);
	}
	return dataSources;
    }

    private static List<DataSource> collect(List<DataSource> dataSources, String fileLocation,
	    FilenameFilter filenameFilter) {
	File file = new File(fileLocation);
	if (!file.exists()) {
	    throw new RuntimeException("File " + file.getName() + " doesn't exist");
	}
	if (!file.isDirectory()) {
	    if (fileLocation.endsWith(".zip") || fileLocation.endsWith(".jar")) {
		ZipFile zipFile;
		try {
		    zipFile = new ZipFile(fileLocation);
		    Enumeration<? extends ZipEntry> e = zipFile.entries();
		    while (e.hasMoreElements()) {
			ZipEntry zipEntry = e.nextElement();
			if (filenameFilter.accept(null, zipEntry.getName())) {
			    dataSources.add(new ZipDataSource(zipFile, zipEntry));
			}
		    }
		} catch (IOException ze) {
		    throw new RuntimeException("Archive file " + file.getName() + " can't be opened");
		}
	    } else {
		dataSources.add(new FileDataSource(file));
	    }
	} else {
	    // Match files, or directories which are not excluded.
	    // FUTURE Make the excluded directories be some configurable option
	    Filter<File> filter = new OrFilter<File>(Filters.toFileFilter(filenameFilter), new AndFilter<File>(Filters
		    .getDirectoryFilter(), Filters.toNormalizedFileFilter(Filters.buildRegexFilterExcludeOverInclude(
		    Collections.singletonList("SCCS"), null))));
	    FileFinder finder = new FileFinder();
	    List<File> files = finder.findFilesFrom(file.getAbsolutePath(), Filters.toFilenameFilter(filter), true);
	    for (File f : files) {
		dataSources.add(new FileDataSource(f));
	    }
	}
	return dataSources;
    }
}
