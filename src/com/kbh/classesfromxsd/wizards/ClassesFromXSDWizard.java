package com.kbh.classesfromxsd.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ClassesFromXSDWizard extends Wizard implements INewWizard {


	// wizard pages
	MainPage mainPage;

	// workbench selection when the wizard was started
	protected IStructuredSelection selection;

	// flag indicated whether the wizard can be completed or not
	// if the user has selected an xsd file from the sourcePath or filePath
	protected boolean fileChosen = false;

	// flag indicated whether the wizard can be completed or not
	// if the xsd was successfully validated
	protected boolean fileValidated = false;

	// the workbench instance
	protected IWorkbench workbench;

	public ClassesFromXSDWizard() {
		super();
		setHelpAvailable(false);
		try {

			ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin(
					"com.kbh.classesfromxsd.wizards", "icons/oxiaLogo.jpg");
			setDefaultPageImageDescriptor(image);
			// setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(new
			// URL("icons/action.gif")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addPages() {
		mainPage = new MainPage(workbench, selection);
		addPage(mainPage);

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		boolean canFinish = mainPage.canFinish();
		if (canFinish) {
			mainPage.runGenerator();
			try {
				// waits for the generator to refresh the workspace
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return canFinish;
	}
}
