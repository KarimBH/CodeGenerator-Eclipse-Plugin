package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kbh.classesfromxsd.wizards.CurrentProjectInfos;

/**
 * Path utilities
 * @author Karim
 *
 */
public class PathUtils {

	public static String convertListToformattedString(List<String> list,
			String delimiter) {
		StringBuffer result = new StringBuffer();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String element = (String) iterator.next();
			result.append(element.replace("\\", "/") + delimiter);
		}
		return result.toString().trim();
	}

	/**
	 * Returns the project path given the path of the a selected file in the
	 * workspace and the workspace path
	 * 
	 * @param selectedFile
	 * @param workspacePath
	 * @return
	 */
	public static String getProjectPathFromFileInWorkspace(String selectedFile,
			String workspacePath) {
		String toRemove = selectedFile.replace(workspacePath + "\\", "");
		String toAdd = toRemove.substring(0, toRemove.indexOf("\\"));
		String result = workspacePath.replace(toRemove, "") + "\\" + toAdd;
		return result.replace("\\", "/");

	}

	/**
	 * Returns the package name given a string local path with \ or /
	 * separators: \org\example\test\ ---> org.exmaple.test
	 * 
	 * @param localPath
	 * @return
	 */
	public static String classFilePathToPackageName(String localPath) {
		String packageName = localPath.trim();
		packageName = packageName.replaceFirst("^\\W", "");
		packageName = packageName.replaceFirst("\\W$", "");

		packageName = packageName.replace("/", ".");
		packageName = packageName.replace("\\", ".");

		return packageName;
	}

	/**
	 * Returns the import statement code for a given class path
	 * 
	 * @param fullPath
	 * @return
	 */
	public static String getPackageFromPath(String fullPath,
			String workspacePath, String delimiter) {
		String result = new String();

		result = fullPath.replace(workspacePath, "");
		return classFilePathToPackageName(result);
	}

	/**
	 * Returns the name of a class given its full path
	 * 
	 * @param classPath
	 * @return
	 */
	public static String getClassNameFromPath(String classPath, String delimiter) {

		return classPath.substring(classPath.lastIndexOf(delimiter) + 1);
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
	public static List<String> formatListOfPaths(List<String> list,
			String filterString, String delimiter) {
		List<String> resultList = new ArrayList<String>();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String path = (String) iterator.next();

			if (path.contains(filterString)) {
				String projectPath = CurrentProjectInfos.projectPath.replace(
						"/", delimiter);
				path = path.replace(projectPath, "");
				path = path.replace(".java", "");
				path = path.replace(delimiter, ".");
				resultList.add(path);
			}
		}
		return resultList;
	}

}
