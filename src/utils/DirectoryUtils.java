package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kbh.classesfromxsd.wizards.CurrentProjectInfos;

public class DirectoryUtils {
	final static long maxTimeDifference = 1_000;

	public static boolean exitingDirectory(String directoryPath) {
		File directory = new File(directoryPath);
		return directory.exists();
	}

	/**
	 * 
	 * @param directoryPath
	 */
	public static void createDirectory(String directoryPath) {
		File directory = new File(directoryPath);
		directory.getParentFile().mkdirs();
		// if the directory does not exist, create it
		if (!directory.exists()) {
			try {
				directory.mkdir();
			} catch (SecurityException se) {
				// handle it
			}
		}
	}

	/**
	 * Returns the path of all the java files contained in a folder and its
	 * sub-folders
	 * 
	 * @param directoryPath
	 * @param list
	 *            for recursive calls initially null
	 */
	public static List<String> getListOfAllFiles(String rootPath,
			List<String> result, boolean recentlyModifiedOnly) {
		List<String> listOfName = new ArrayList<String>();
		File folder = new File(rootPath);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()
						&& listOfFiles[i].getAbsolutePath().endsWith(".java")) {
					if (recentlyModifiedOnly) {
						if (fileLastelyModified(listOfFiles[i]))
							listOfName.add(listOfFiles[i].getAbsolutePath());
					} else {
						listOfName.add(listOfFiles[i].getAbsolutePath());
					}
				} else if (listOfFiles[i].isDirectory()) {

					listOfName.addAll(getListOfAllFiles(
							listOfFiles[i].getAbsolutePath(), result,
							recentlyModifiedOnly));
				}
			}
		}
		return listOfName;

	}

	/**
	 * Returns true if the file was lastely modified (i.e relatively to the
	 * maxTimeDifference constant) and false otherwise
	 * 
	 * @param file
	 * @return
	 */
	private static boolean fileLastelyModified(File file) {

		try {
			long lastModified = file.lastModified();
			return (System.currentTimeMillis() - lastModified) < maxTimeDifference;

		} catch (Exception e) {

			return false;
		}
	}

	/**
	 * Format a list of string paths by removing the current project path. The
	 * result is formatted as package imports.
	 * 
	 * @param list
	 * @param filterString
	 * @param workspacePath
	 * @param delimiter
	 * @return
	 */
	public static List<String> formatCommandClasseList(List<String> list,
			String delimiter) {
		List<String> resultList = new ArrayList<String>();

		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String path = (String) iterator.next();
			path = path.replace("/", "\\");

			String projectName = CurrentProjectInfos.projectName;
			String projectPath = CurrentProjectInfos.projectPath.replace("/",
					delimiter);
			path = path.replace(projectPath + delimiter, "");
			int index = path.indexOf(projectName);
			path = path.substring(index + projectName.length() + 1);
			path = path.replace(".java", "");
			path = path.replace("/", ".");
			path = path.replace("\\", ".");
			resultList.add(path);

		}
		return resultList;
	}

	/**
	 * Returns the list of files containing Reqest suffix
	 * 
	 * @param fileNames
	 * @return
	 */
	public static List<String> filterListOfFileNames(List<String> fileNames) {
		for (Iterator<String> iterator = fileNames.iterator(); iterator
				.hasNext();) {
			String fileName = (String) iterator.next();
			fileName = fileName.replace(".java", "");
			if (fileName.contains("Request")) {
				fileNames.remove(fileName);
			}
		}
		return fileNames;
	}

}
