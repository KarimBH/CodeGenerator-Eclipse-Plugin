package com.kbh.classesfromxsd.preferencePages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.kbh.classesfromxsd.wizards.Activator;

public class GeneralPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public static final String XJC_CUSTOM_PACKAGE = "xjcCustomPackage";
	public static final String XJC_COMMAND_PATH = "xjcCommandPath";
	public static final String XJC_DESTINATION_DIRECTORY = "xjcDestination";
	public static final String JAXB_SOURCE_DIRECTORY = "jaxbSourceDirectory";
	public static final String XJC_PACKAGE_NAME = "xjcPackageName";
	public static final String ENDPOINTS_PACKAGE_NAME = "endpointsPackageName";
	public static final String COMMANDS_PACKAGE_NAME = "commandsPackageName";
	public static final String SHOW_GENERATION_RESULT = "showGenerationResult";
	public static final String GENERATE_COMMANDS = "generateCommands";
	public static final String GENERATE_ENDPOINTS = "generateEndpoints";

	@Override
	public void init(IWorkbench arg0) {
		// TODO Auto-generated method stub
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {

		String title = "Xml schema to classes Configuration page";
		setTitle(title);
		
		
//		BooleanFieldEditor xjcAutoDetect = new BooleanFieldEditor(XJC_AUTO_DETECT,
//				"Auto detect XJC command path", getFieldEditorParent());
//		addField(xjcAutoDetect);
//		
//		FileFieldEditor xjcPathFiledEditor =new FileFieldEditor("test", "XJC command path", getFieldEditorParent());
//	//	xjcPathFiledEditor.setEnabled(xjcAutoDetect.getBooleanValue(), getFieldEditorParent());
//		addField(xjcPathFiledEditor);
		
	
		
		addField(new StringFieldEditor(XJC_DESTINATION_DIRECTORY,
				"Default XJC destination directory:", getFieldEditorParent()));
		
		addField( new BooleanFieldEditor(XJC_CUSTOM_PACKAGE,
		"Custom XJC package", getFieldEditorParent()));
		
		addField(new StringFieldEditor(XJC_PACKAGE_NAME,
				"Default XJC package name:", getFieldEditorParent()));

		addField(new StringFieldEditor(JAXB_SOURCE_DIRECTORY,
				"Default JAXB source folder:", getFieldEditorParent()));

		addField(new StringFieldEditor(ENDPOINTS_PACKAGE_NAME,
				"Default endpoints package name:", getFieldEditorParent()));

		addField(new StringFieldEditor(COMMANDS_PACKAGE_NAME,
				"Default commands package name:", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(GENERATE_COMMANDS,
				"Generate Commands", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(GENERATE_ENDPOINTS,
				"Generate Endpoints", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(SHOW_GENERATION_RESULT,
				"Show generation results", getFieldEditorParent()));

	}

}
