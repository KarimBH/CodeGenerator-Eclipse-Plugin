package generators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import utils.FileUtils;
import utils.PathUtils;

import com.kbh.classesfromxsd.wizards.CurrentProjectInfos;

/**
 * This class is responsible for generating the endpoint classes in a given
 * package.
 * 
 * @author Karim
 * 
 */
public class EndpointsGenerator {
	
	final static String endpointsModelFilePath = "models/endpointModel.txt";
	String endpointsModelContent;
	String delimiter = "\\";
	String destinationPackage;
	List<String> classPaths;
	List<String> createdCommandClassesPaths;
	List<String> createdEndpointsClassesPaths;

	public EndpointsGenerator(String destinationPackage,
			List<String> classPaths, List<String> createdCommandClassesPaths) {
		createdEndpointsClassesPaths = new ArrayList<String>();
		this.destinationPackage = destinationPackage;
		this.classPaths = classPaths;
		this.createdCommandClassesPaths = createdCommandClassesPaths;

	}

	public void generateEndpointsClasses() {

		endpointsModelContent = FileUtils
				.getBundleFileContent(endpointsModelFilePath);

		String packageImport = PathUtils
				.classFilePathToPackageName(destinationPackage);
		
		endpointsModelContent = endpointsModelContent.replace("${packageName}",
				packageImport.replace("src.", ""));

		for (Iterator<String> iterator = classPaths.iterator(); iterator
				.hasNext();) {
			String classPath = (String) iterator.next();
			handleEndpoint(classPath);
		}
	}

	private void handleEndpoint(String classPath) {
		String className = PathUtils.getClassNameFromPath(classPath, ".");
		className = className.replace("Request", "");

		String requestClassImportCode = classPath.substring(1);
		String responseClassImportCode = requestClassImportCode.replace(
				"Request", "Response");

		String classContent = null;
		if (createdCommandClassesPaths == null
				|| createdCommandClassesPaths.size() == 0) {
			classContent = generateEndpointClassContent(className,
					requestClassImportCode, responseClassImportCode);

		} else {
			String commandClassImportCode = getCommandImportFromList(className);

			classContent = generateEndpointClassContent(className,
					requestClassImportCode, responseClassImportCode,
					commandClassImportCode);
		}
		String destinationClassLocation = CurrentProjectInfos.projectPath
				+ delimiter + destinationPackage + "/" + className + "Endpoint"
				+ ".java";
		createdEndpointsClassesPaths.add(destinationClassLocation);
		FileUtils.createFileAndSetContent(destinationClassLocation,
				classContent);

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
	private String generateEndpointClassContent(String operationName,
			String operationRequestClass, String operationResponseClass,
			String operationCommandClass) {
		String endpointCode = endpointsModelContent;
		endpointCode = endpointCode.replace("${operationName}", operationName);
		endpointCode = endpointCode.replace("${operationRequestClass}",
				operationRequestClass);
		endpointCode = endpointCode.replace("${operationResponseClass}",
				operationResponseClass);
		endpointCode = endpointCode.replace("${operationCommandClass}",
				operationCommandClass);
		return endpointCode;
	}

	private String generateEndpointClassContent(String operationName,
			String operationRequestClass, String operationResponseClass) {
		String endpointCode = endpointsModelContent;
		endpointCode = endpointCode.replace("${operationName}", operationName);
		endpointCode = endpointCode.replace("${operationRequestClass}",
				operationRequestClass);
		endpointCode = endpointCode.replace("${operationResponseClass}",
				operationResponseClass);
		endpointCode = endpointCode.replaceAll("[\\r\\n]\\s*.*Command.*\\s*.*[\\r\\n]", "");
		return endpointCode;
	}

	private String getCommandImportFromList(String name) {

		for (Iterator<String> iterator = createdCommandClassesPaths.iterator(); iterator
				.hasNext();) {
			String importCode = (String) iterator.next();
			if (importCode.indexOf(name) >= 0) {
				return importCode;
			}

		}
		return "// enable to locate command class\n";
	}

	public List<String> getCreatedEndpointsClassesPaths() {
		return createdEndpointsClassesPaths;
	}

}
