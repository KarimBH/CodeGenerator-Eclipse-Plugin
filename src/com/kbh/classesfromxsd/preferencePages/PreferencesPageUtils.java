package com.kbh.classesfromxsd.preferencePages;

import org.eclipse.swt.custom.StyledText;

import utils.FileUtils;

/**
 * Offers methods to handle changes in models
 * 
 * @author Karim
 * 
 */
public class PreferencesPageUtils {

	/**
	 * Restore the content of the commands model file by replacing its content
	 * by the default command model file.
	 */
	public static void restoreDefaultModel(StyledText text,
			String modelFilePath, String defaultModelCode) {

		FileUtils.writeToBundleFile(modelFilePath, defaultModelCode);
		text.setText(defaultModelCode);

		// System.out.println(PluginUtils.getBundleFileContent("models/commandModel.txt"));
	}

	/**
	 * Stores the modified command model code to the command model file.
	 */
	public static void storeModifiedModel(StyledText text, String modelFilePath) {
		String textFieldContent = text.getText();
		FileUtils.writeToBundleFile(modelFilePath, textFieldContent);

		// System.out.println(PluginUtils.getBundleFileContent("models/commandModel.txt"));
	}
}
