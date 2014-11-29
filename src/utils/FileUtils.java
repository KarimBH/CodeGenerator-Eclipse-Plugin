package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * File utilities: get the content of a file, a bundle file, write to a file...
 * 
 * @author Karim
 *
 */
public class FileUtils {

	/**
	 * Returns the content of a file
	 * 
	 * @param FilePath
	 * @return file content
	 */
	public static String getFileContent(String FilePath) {

		StringBuffer in = new StringBuffer();
		Scanner sc = null;
		try {
			sc = new Scanner(new File(FilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (sc.hasNextLine()) {
			in.append(sc.nextLine() + "\n");
		}
		sc.close();
		return in.toString();
	}

	/**
	 * Creates a file and write the given content in it. The file is overwritted
	 * if it already exists and the unexisting folder in the file path are also
	 * created.
	 * 
	 * @param filePath
	 * @param fileContent
	 */
	public static void createFileAndSetContent(String filePath,
			String fileContent) {
		// creates the parent folders of the file
		new File(filePath).getParentFile().mkdirs();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(fileContent);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Copies (Overwrite) the content of the source file to the destination
	 * file. The two files are in the bundle.
	 * 
	 * @param srcPath
	 * @param destPath
	 */
	public static void copyBundleFile(String srcPath, String destPath) {
		String srcContent = FileUtils.getBundleFileContent(srcPath);
		FileUtils.writeToBundleFile(destPath, srcContent);
	}

	/**
	 * Create a file in the bundle (overwrite the file if it already exists) and
	 * sets its content.
	 * 
	 * @param filePath
	 */
	public static void writeToBundleFile(String filePath, String fileContent) {
		// URL url;
		try {
			Bundle bundle = Platform
					.getBundle("com.kbh.classesfromxsd.wizards");
			URL fileURL = bundle.getEntry("/" + filePath);
			File file = null;
			file = new File(FileLocator.resolve(fileURL).toURI());
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the content of a file in the bundle
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getBundleFileContent(String filePath) {
		StringBuffer bundleFileContent = new StringBuffer();
		URL url;
		try {
			url = new URL("platform:/plugin/com.kbh.classesfromxsd.wizards/"
					+ filePath);
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				bundleFileContent.append(inputLine + "\n");
			}

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return bundleFileContent.toString();

	}
}