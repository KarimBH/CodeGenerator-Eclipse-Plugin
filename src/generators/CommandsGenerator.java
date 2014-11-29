package generators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import utils.FileUtils;
import utils.PathUtils;
import utils.PluginUtils;

import com.kbh.classesfromxsd.wizards.CurrentProjectInfos;

/**
 * Generation of the commands classes (using the saved template) in the
 * destination package
 * 
 * @author Karim
 * 
 */
public class CommandsGenerator {
	static final String commandModelFilePath = "models/commandModel.txt";
	static final String superCommandModelFilePath = "models/superCommandModel.txt";
	static final String delimiter = "\\";

	List<String> createdCommandClassesPaths;
	List<String> classPaths;
	String workspacePath;

	String commandModelContent;
	String destinationPackage;
	String superCommandClassName;
	String packageImport;

	public CommandsGenerator(String destinationPackage, List<String> classPaths) {
		this.classPaths = classPaths;
		workspacePath = PluginUtils.getWorkspacePath();
		this.destinationPackage = destinationPackage;
		commandModelContent = FileUtils
				.getBundleFileContent(commandModelFilePath);
		packageImport = PathUtils
				.classFilePathToPackageName(destinationPackage.replace("src.", ""));
		commandModelContent = commandModelContent.replace("${packageName}",
				packageImport);
		createdCommandClassesPaths = new ArrayList<String>();
	}

	public void generateCommandsClasses() {

		// generates the super command file
		generateSuperCommandCLass();
		// generates the commands files for a given list of java beans
		for (Iterator<String> iterator = classPaths.iterator(); iterator
				.hasNext();) {
			String classPath = (String) iterator.next();
			handleCommand(classPath);
		}
	}

	/**
	 * generates the java file of the super command class
	 */
	private void generateSuperCommandCLass() {
		String projectCommandModelContent = FileUtils
				.getBundleFileContent(superCommandModelFilePath);
		String projectName = CurrentProjectInfos.projectName.replace("/", "");
		projectName = projectName.replace(".", "");
		if (projectName == null) {
			projectName = "Project";
		}
		superCommandClassName = projectName + "Command";
		projectCommandModelContent = projectCommandModelContent.replace(
				"${projectName}", projectName);
		projectCommandModelContent = projectCommandModelContent.replace(
				"${packageName}", PathUtils
						.classFilePathToPackageName(PathUtils
								.getPackageFromPath(destinationPackage,
										workspacePath, delimiter)));
		String destinationClassLocation = CurrentProjectInfos.projectPath
				+ delimiter + destinationPackage + delimiter + projectName
				+ ".java";
		FileUtils.createFileAndSetContent(destinationClassLocation,
				projectCommandModelContent);
	}

	/**
	 * Handles one command class generation
	 * 
	 * @param classPath
	 */
	private void handleCommand(String classPath) {
		String className = PathUtils.getClassNameFromPath(classPath, ".");
		className = className.replace("Request", "");

		String requestClassImportCode = classPath.substring(1);
		// if (requestClassImportCode.indexOf(".") == 0) {
		// }
		String responseClassImportCode = requestClassImportCode.replace(
				"Request", "Response");

		String classContent = generateCommandClassContent(className,
				requestClassImportCode, responseClassImportCode,
				superCommandClassName);
		String destinationClassLocation = CurrentProjectInfos.projectPath
				+ delimiter + destinationPackage + delimiter + className
				+ "Command" + ".java";
		FileUtils.createFileAndSetContent(destinationClassLocation,
				classContent);

		createdCommandClassesPaths.add(destinationClassLocation);
	}

	/**
	 * Returns the code of the endpoint by replacing the tags in the endpoint
	 * model file by the actual value for this endpoint
	 * 
	 * @param operationName
	 *            Name of the operation
	 * @param operationRequestClass
	 *            location of the operationRequest class
	 * @param operationResponseClass
	 *            location of the operationResponse class
	 * @param operationCommandClass
	 *            location of the operationCommand Class
	 * @return
	 */
	private String generateCommandClassContent(String operationName,
			String operationRequestClass, String operationResponseClass,
			String superCommandClass) {
		String commandCode = commandModelContent;
		commandCode = commandCode.replace("${superCommandClassName}",
				superCommandClass);
		commandCode = commandCode.replace("${operationName}", operationName);
		commandCode = commandCode.replace("${operationResponseClass}",
				operationResponseClass);
		commandCode = commandCode.replace("${operationRequestClass}",
				operationRequestClass);

		return commandCode;
	}

	/**
	 * Returns the list of created command classes paths
	 * 
	 * @return
	 */
	public List<String> getCreatedCommandClassesPaths() {
		return createdCommandClassesPaths;
	}
}
