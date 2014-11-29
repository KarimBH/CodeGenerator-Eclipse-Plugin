package com.kbh.classesfromxsd.wizards;

import generators.Generator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.kbh.classesfromxsd.preferencePages.GeneralPreferencesPage;

import utils.PluginUtils;

/**
 * Class associated with the popupMenu for the folder Start the wizard in the
 * run method
 */

@SuppressWarnings("restriction")
public class ClassesFromXSDAction implements IObjectActionDelegate {
	IWorkbenchPart part;
	ISelection selection;

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart part) {
		this.part = part;
	}

	/**
	 * @see IActionDelegate#run(IAction) Instantiates the wizard and opens it in
	 *      the wizard container
	 */
	public void run(IAction action) {

		if ((selection instanceof IStructuredSelection) || (selection == null)) {

			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object structeredSelectionFirstElement = structuredSelection
					.getFirstElement();
			String sourcePath = new String();
			// The selection is a file or a directory
			if (structeredSelectionFirstElement instanceof File) {
				File selectedFile = (File) structeredSelectionFirstElement;
				sourcePath = selectedFile.getLocation().toString();
			} else if (structeredSelectionFirstElement instanceof Folder) {
				Folder selectedFolder = (Folder) structeredSelectionFirstElement;
				sourcePath = selectedFolder.getLocation().toString();
			}

			// Sets the current project informations (name, path and Iproject
			// instance)
			CurrentProjectInfos.currentProject = PluginUtils
					.getActiveProject(structuredSelection);
			CurrentProjectInfos.projectName = PluginUtils.getActiveProjectName(
					structuredSelection).substring(1);// removes the /
			CurrentProjectInfos.projectPath = PluginUtils
					.getActiveProjectPath(structuredSelection);

			String defaultDirectory = (String) PluginUtils
					.getDefaultStringValueFromStore(GeneralPreferencesPage.XJC_DESTINATION_DIRECTORY);

			// set the source path list with the selected file/directory
			List<String> sourcesPathList = new ArrayList<String>();
			sourcesPathList.add(sourcePath);

			Generator classGenerator = new Generator(defaultDirectory, null,
					null, null, sourcesPathList);
			classGenerator.generateClasses();

		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}