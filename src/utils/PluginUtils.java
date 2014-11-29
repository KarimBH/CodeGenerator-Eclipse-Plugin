package utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;

import com.kbh.classesfromxsd.wizards.Activator;

public class PluginUtils {

	/**
	 * Returns the path of the active project in which the selected schema file
	 * is located
	 * 
	 * @param structuredSelection
	 * @return
	 */

	public static String getActiveProjectPath(
			IStructuredSelection structuredSelection) {
		IProject activeProject;
		if (structuredSelection instanceof ITreeSelection) {
			activeProject = getActiveProject(structuredSelection);
			if (activeProject != null) {
				return activeProject.getLocation().toString();
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * Returns the name of the active project from a structered Selection
	 * 
	 * @param structuredSelection
	 * @return
	 */
	public static String getActiveProjectName(
			IStructuredSelection structuredSelection) {
		IProject activeProject;
		if (structuredSelection instanceof ITreeSelection) {
			activeProject = getActiveProject(structuredSelection);
			if (activeProject != null) {
				return activeProject.getFullPath().toString();
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * Returns an IProject object representing the active project
	 * 
	 * @param structuredSelection
	 * @return
	 */
	public static IProject getActiveProject(
			IStructuredSelection structuredSelection) {
		TreeSelection treeSelection = (TreeSelection) structuredSelection;
		TreePath[] treePaths = treeSelection.getPaths();
		TreePath treePath = treePaths[0];
		// The first segment should be a IProject
		Object firstSegmentObj = treePath.getFirstSegment();
		return (IProject) ((IAdaptable) firstSegmentObj)
				.getAdapter(IProject.class);
	}

	/**
	 * Returns an Iproject object given the project Name
	 * 
	 * @param projectName
	 * @return
	 */
	public static IProject getIProjectFromProjectName(String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	/**
	 * Returns the current workspace path (or null)
	 * 
	 * @return
	 */
	public static String getWorkspacePath() {
		try {
			return Platform.getInstanceLocation().getURL().getPath();
		} catch (Exception e) {
			return "/";
		}
	}

	/**
	 * Refresh the workspace
	 * 
	 * @param project
	 */
	public static void refreshProjectWorkspace(IProject project) {
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the string value of the coreesponding passed key from the
	 * activator preferences store
	 * 
	 * @param key
	 * @return string value
	 */
	public static String getDefaultStringValueFromStore(String key) {
		return Activator.getDefault().getPreferenceStore().getString(key);
	}

	/**
	 * Returns the boolean value of the coreesponding passed key from the
	 * activator preferences store
	 * 
	 * @param key
	 * @return string value
	 */
	public static boolean getDefaultBooleanValueFromStore(String key) {
		return Activator.getDefault().getPreferenceStore().getBoolean(key);
	}
}
