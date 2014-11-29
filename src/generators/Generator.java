package generators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Shell;

import utils.DirectoryUtils;
import utils.PathUtils;
import utils.PluginUtils;

import com.kbh.classesfromxsd.preferencePages.GeneralPreferencesPage;
import com.kbh.classesfromxsd.wizards.Activator;
import com.kbh.classesfromxsd.wizards.CurrentProjectInfos;
import com.kbh.classesfromxsd.wizards.GenerationResultDialog;

/**
 * This class is responsible for invoking the XJC command, creating the commands
 * and endpoints classes. The default packages can be modified (permanently) throught the
 * plugin preferences.
 * 
 * @author Karim
 * 
 */
public class Generator {
	StringBuffer resultInfos;

	String destinationDirectory;
	String commandsDestinationPackage;
	String endpointsDestinationPackage;
	List<String> sourcePath; // list of files or directories string paths

	String packageName;

	public Generator(String destinationDirectory, String packageName,
			String commandsDestinationPackage,
			String endpointsDestinationPackage, List<String> sourcePath) {

		setValues(destinationDirectory, packageName,
				commandsDestinationPackage, endpointsDestinationPackage);
		this.sourcePath = sourcePath;

	}

	/**
	 * Runs the xjc command on the given source (xsd file, directory)
	 * 
	 * @param sourcePath
	 * @param destinationDirectory
	 * @param packageName
	 * @return response of the xjc command
	 * @throws IOException
	 */
	public void generateClasses(boolean generateJaxb, boolean generateCommands,
			boolean generateEndpoints) {

		try {
			resultInfos = new StringBuffer();

			System.out.println("\n--GENERATE JAXB --> "+generateJaxb);
			System.out.println("\n--GENERATE COMMANDS --> "+generateCommands);
			System.out.println("\n--GENERATE ENDPOINTS --> "+generateEndpoints);
			
			// //////////Generate Jaxb Classes
			if (generateJaxb) {
				long startTime = System.nanoTime();
				JaxbGenerator jaxbGenerator = new JaxbGenerator(
						destinationDirectory, packageName, sourcePath);
				jaxbGenerator.generatesJaxbClasses();
				long estimatedTime = System.nanoTime() - startTime;
				estimatedTime = TimeUnit.MILLISECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS);

				resultInfos.append(jaxbGenerator.getCommandResult());
				resultInfos.append( "Generated in "+estimatedTime+" ms\n");
			}
			boolean getRecentFileOnly = generateJaxb;
			List<String> classPaths = DirectoryUtils.getListOfAllFiles(
					destinationDirectory, new ArrayList<String>(),
					getRecentFileOnly);

			classPaths = PathUtils.formatListOfPaths(classPaths, "Request",
					"\\");

			// ///////////////////Commands generation
			List<String> commandClassesPaths = null;
			if (generateCommands) {
				long startTime = System.nanoTime();
				commandClassesPaths = generateCommands(classPaths);
				long estimatedTime = System.nanoTime() - startTime;
				estimatedTime = TimeUnit.MILLISECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS);
				commandClassesPaths = DirectoryUtils.formatCommandClasseList(
						commandClassesPaths, "/");

				String generatedCommands = PathUtils
						.convertListToformattedString(commandClassesPaths, "\n");
				resultInfos.append("\nGenerated Commands: (Generated in "+estimatedTime +" ms)\n"
						+ generatedCommands);
			}

			// ///////////////////Endpoints generation

			if (generateEndpoints) {
				long startTime = System.nanoTime();
				List<String> endpointClassesPaths = generateEndpoints(
						classPaths, commandClassesPaths);
				long estimatedTime = System.nanoTime() - startTime;
				estimatedTime = TimeUnit.MILLISECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS);
				endpointClassesPaths = DirectoryUtils.formatCommandClasseList(
						endpointClassesPaths, "/");

				String generatedEndpoints = PathUtils
						.convertListToformattedString(endpointClassesPaths,
								"\n");
				resultInfos.append("\n\nGenerated Endpoints: (Generated in "+estimatedTime +" ms)\n"
						+ generatedEndpoints);
			}

			// refresh the workspace after generation
			PluginUtils
					.refreshProjectWorkspace(CurrentProjectInfos.currentProject);

		} finally {
			boolean showGenerationResult = Activator.getDefault()
					.getPreferenceStore()
					.getBoolean(GeneralPreferencesPage.SHOW_GENERATION_RESULT);
			if (showGenerationResult) {
				GenerationResultDialog resultDialog = new GenerationResultDialog(
						new Shell(), resultInfos.toString());
				resultDialog.open();
			}
		}
	}


	public void generateClasses() {
		boolean generateCommands = PluginUtils
				.getDefaultBooleanValueFromStore(GeneralPreferencesPage.GENERATE_COMMANDS);
		boolean generateEndpoints = PluginUtils
				.getDefaultBooleanValueFromStore(GeneralPreferencesPage.GENERATE_ENDPOINTS);
		generateClasses(true, generateCommands, generateEndpoints);

	}

	/**
	 * Sets the commands and endpoints packages. If a null value is passed, the
	 * default package names stored in the plugin preferences are used.
	 * 
	 * @param commandsDestinationPackage
	 * @param endpointsDestinationPackage
	 */
	private void setValues(String destinationDirectory, String packageName,
			String commandsDestinationPackage,
			String endpointsDestinationPackage) {

		// xjc destination directory
		if (destinationDirectory == null) {
			System.out.println("DESTINATION DIRECTORY NULL");
			this.destinationDirectory = CurrentProjectInfos.projectPath
					+ (String) PluginUtils
							.getDefaultStringValueFromStore(GeneralPreferencesPage.XJC_DESTINATION_DIRECTORY);

		} else {
			System.out.println("DESTINATION DIRECTORY NOT NULL");
			this.destinationDirectory = CurrentProjectInfos.projectPath
					+ destinationDirectory;
			System.out.println(this.destinationDirectory);
		}
		DirectoryUtils.createDirectory(this.destinationDirectory);
		
		// xjc package name
		if (packageName == null) {
//			this.packageName = (String) PluginUtils
//					.getDefaultStringValueFromStore(GeneralPreferencesPage.XJC_PACKAGE_NAME);
		} else {
			this.packageName = packageName;

		}
		if (commandsDestinationPackage == null) {
			this.commandsDestinationPackage = (String) PluginUtils
					.getDefaultStringValueFromStore(GeneralPreferencesPage.COMMANDS_PACKAGE_NAME);
		} else {
			this.commandsDestinationPackage = commandsDestinationPackage;

		}
		if (endpointsDestinationPackage == null) {
			this.endpointsDestinationPackage = (String) PluginUtils
					.getDefaultStringValueFromStore(GeneralPreferencesPage.ENDPOINTS_PACKAGE_NAME);
		} else {
			this.endpointsDestinationPackage = endpointsDestinationPackage;

		}

	}

	/**
	 * Instanciate a CommandsGenerator object and runs the
	 * generateCommandsClasses method
	 */
	private List<String> generateCommands(List<String> classPaths) {
		CommandsGenerator commandsGen = new CommandsGenerator(
				commandsDestinationPackage, classPaths);
		DirectoryUtils.createDirectory(commandsDestinationPackage);
		commandsGen.generateCommandsClasses();
		return commandsGen.getCreatedCommandClassesPaths();
	}

	/**
	 * Instanciate an EndpointsGenerator object and runs the
	 * generateEndpointsClasses method
	 */
	private List<String> generateEndpoints(List<String> classPaths,
			List<String> commandClassesPaths) {
		EndpointsGenerator endpointGen = new EndpointsGenerator(
				endpointsDestinationPackage, classPaths, commandClassesPaths);
		DirectoryUtils.createDirectory(endpointsDestinationPackage);
		endpointGen.generateEndpointsClasses();
		return endpointGen.getCreatedEndpointsClassesPaths();
	}
}
