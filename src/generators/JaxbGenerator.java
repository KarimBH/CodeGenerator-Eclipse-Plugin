package generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.kbh.classesfromxsd.preferencePages.GeneralPreferencesPage;

import utils.DirectoryUtils;
import utils.PluginUtils;

public class JaxbGenerator {
	String xjcPath;
	String packageName;
	String destinationDirectory;
	List<String> sourcePath;

	String commandResult;

	public JaxbGenerator(String destinationDirectory, String packageName,
			List<String> sourcePath) {
		setXjcCommandPath();
		this.destinationDirectory = destinationDirectory;
		DirectoryUtils.createDirectory(destinationDirectory);
		this.packageName = packageName;
		this.sourcePath = sourcePath;

	}

	public void generatesJaxbClasses() {

		StringBuffer resultOfXjcCommand = new StringBuffer();

		try {

			List<String> processParameters = new ArrayList<String>();
			processParameters.add(xjcPath);
			processParameters.add("-nv");
			processParameters.add("-no-header");
			processParameters.add("-d");
			processParameters.add(destinationDirectory);

			boolean customXJCPackage = PluginUtils
					.getDefaultBooleanValueFromStore(GeneralPreferencesPage.XJC_CUSTOM_PACKAGE);
			if (packageName != null) {
				processParameters.add("-p");
				processParameters.add(packageName);
			} else if (customXJCPackage) {
				processParameters.add("-p");
				String customPackageName = PluginUtils
						.getDefaultStringValueFromStore(GeneralPreferencesPage.XJC_CUSTOM_PACKAGE);
				processParameters.add(customPackageName);
			}
			processParameters.addAll(sourcePath);

			Process process = new ProcessBuilder(processParameters).start();

			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				resultOfXjcCommand.append(line + "\n");
			}

		} catch (IOException e) {
			resultOfXjcCommand.append(e.getMessage() + "\n");
		}
		commandResult = resultOfXjcCommand.toString();

	}

	/**
	 * Sets the xjc command path if the default one is not found in the plugin's
	 * preferences store
	 */
	private void setXjcCommandPath() {
		String osName = System.getProperty("os.name").toLowerCase();
		String javaHome = System.getenv("JAVA_HOME");
		String fileExtension = new String();

		if (osName.indexOf("mac") >= 0) {
			fileExtension = ".sh";
		} else if (osName.indexOf("win") >= 0) {
			fileExtension = ".exe";
		} else if ((osName.indexOf("nix") >= 0) || (osName.indexOf("nux") >= 0)
				|| (osName.indexOf("aix") >= 0)
				|| osName.indexOf("solaris") >= 0) {
			fileExtension = ".sh";
		}
		xjcPath = javaHome + "\\bin\\xjc" + fileExtension;
	}

	public String getCommandResult() {
		return this.commandResult;
	}
}
