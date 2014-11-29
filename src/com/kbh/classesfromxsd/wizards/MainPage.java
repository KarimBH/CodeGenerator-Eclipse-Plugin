package com.kbh.classesfromxsd.wizards;

import generators.Generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import utils.PathUtils;
import utils.PluginUtils;

import com.kbh.classesfromxsd.preferencePages.GeneralPreferencesPage;

/**
 * This class is the main (and only) page of the wizard. It allows the selection
 * of source files / folder and to personalize generation options.
 * 
 * @author KarimBH
 *
 */
public class MainPage extends WizardPage implements Listener {

	IWorkbench workbench;
	IStructuredSelection selection;
	int ncol;
	int nrows;
	GridLayout internNayout;

	Composite parent;
	Button generateJAXBClassesChoice;
	Button generateCommandsAndEndpoints;

	final static String pageTile = "XSD to classes wizard";
	final static String pageDescription = "";
	Button xsdFilePathBrowseButton;

	Group jaxbSourceFolderGroup;
	Label jaxbSourceFolderLabel;
	String jaxbSourceFolderLabelText = "Jaxb source folder:";
	Text jaxbSourceFolderTextField;
	Button jaxbSourceFolderBrowseButton;

	Label directoryDestinationName;
	String directoryDestinationNameText = "Directory Name";
	Text directoryDestinationNameTextField;
	Button directoryDestinationNameBrowseButton;

	Group xjcOptionsGroup;
	Label packageName;
	String packageNameText = "Package Name";
	Text packageNameTextField;
	Button packageNameBrowseButton;

	Button useDefaultOptionsCheckButton;
	Composite nonDefaultOptionsCompo;
	boolean useDefaultOptions = true;

	Button generateCommandsButton;
	Group commandsOptionsGroup;
	Label commandsDestinationPackageLabel;
	Text commandsDestinationPackageText;
	Button commandsDestinationPackageBrowseButton;

	Button generateEndpointsButton;
	Group endpointsOptionsGroup;
	Label endpointsDestinationPackageLabel;
	Text endpointsDestinationPackageText;
	Button endpointsDestinationPackageBrowseButton;

	Group chooseFileGroup;
	TreeViewer checkboxTreeViewer;
	Tree checkTree;
	TreeItem[] selectedItems;

	ScrolledComposite scrolledComposite;
	CheckboxTreeViewer checkboxFilesTreeViewer;
	Tree checkFilesTree;
	TreeItem[] selectedFilesItems;

	List<String> checkedFiles;

	boolean generateJaxb = true;
	boolean generateCommands = true;
	boolean generateEndpoints = true;

	// when the input file is not from a project in the workspace and the user
	// didn't selected a project before launching the wizard, he has
	// to select manually the target project before proceeding to classes
	// generation
	boolean sourceFilesFromFileSystem = false;

	/**
	 * Constructor
	 * 
	 * @param workbench
	 * @param selection
	 * @param expliciteActionFilePath
	 */
	protected MainPage(IWorkbench workbench, IStructuredSelection selection) {
		super("Page1");
		setTitle(pageTile);
		setDescription(pageDescription);

		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		this.parent = parent;

		GridData gd;
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();

		ncol = 3;
		nrows = 10;
		layout.numColumns = ncol;
		layout.verticalSpacing = nrows;
		composite.setLayout(layout);

		generateJAXBClassesChoice = new Button(composite, SWT.RADIO);
		generateJAXBClassesChoice
				.setText("Generate JAXB, Commands and Endpoints classes");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		generateJAXBClassesChoice.setLayoutData(gd);
		generateJAXBClassesChoice.setSelection(true);

		createFolderAndFilesTreeViewers(composite);

		generateCommandsAndEndpoints = new Button(composite, SWT.RADIO);
		generateCommandsAndEndpoints
				.setText("Generate Commands and Endpoints from existing JAXB classes");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		generateCommandsAndEndpoints.setLayoutData(gd);

		createUseDefaultOptionsSection(composite);

		addListeners();

		setControl(composite);
	}

	/**
	 * Creates folder and files tree viewers
	 * 
	 * @param composite
	 */
	private void createFolderAndFilesTreeViewers(Composite composite) {
		GridData gd;

		chooseFileGroup = new Group(composite, SWT.NULL);
		chooseFileGroup.setText("XML Schema file(s) input:");
		internNayout = new GridLayout();
		internNayout.numColumns = 3;
		chooseFileGroup.setLayout(internNayout);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 2;
		chooseFileGroup.setLayoutData(gd);

		checkboxTreeViewer = new TreeViewer(chooseFileGroup, SWT.BORDER);
		ViewerFilter[] folderFilter = new ViewerFilter[] { new FoldersViewFilter() };
		createTreeViewer(chooseFileGroup, checkboxTreeViewer,
				getTreeViewerInitialisation(selection), folderFilter);
		checkTree = checkboxTreeViewer.getTree();

		checkboxFilesTreeViewer = new CheckboxTreeViewer(chooseFileGroup,
				SWT.BORDER);
		ViewerFilter[] xsdFilesFilter = new ViewerFilter[] { new XSDFilesViewFilter() };
		createTreeViewer(chooseFileGroup, checkboxFilesTreeViewer,
				getTreeViewerInitialisation(selection), xsdFilesFilter);
		checkFilesTree = checkboxFilesTreeViewer.getTree();
		checkFilesTree.selectAll();

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 2;
		checkFilesTree.setLayoutData(gd);

		xsdFilePathBrowseButton = new Button(chooseFileGroup, SWT.PUSH);
		xsdFilePathBrowseButton.setText("Browse...");
	}

	/**
	 * Creates a tree viewer to select the input xsd file / folder containing
	 * xsd files
	 * 
	 * @param composite
	 */
	private void createTreeViewer(Composite composite,
			final TreeViewer treeViewer, Object input, ViewerFilter[] filter) {
		GridData gd;
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 2;

		treeViewer.getTree().setLayoutData(gd);
		treeViewer.setContentProvider(new XsdFilesContentProvider());
		treeViewer.setLabelProvider(new XsdFilesLabelProvider());

		treeViewer.setInput(input);

		treeViewer.setFilters(filter);

	}

	private void createUseDefaultOptionsSection(Composite composite) {
		GridData gd;
		createLine(composite, ncol);

		useDefaultOptionsCheckButton = new Button(composite, SWT.CHECK);
		useDefaultOptionsCheckButton.setText("Use default options");
		useDefaultOptionsCheckButton.setSelection(true);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		useDefaultOptionsCheckButton.setLayoutData(gd);
		useDefaultOptionsCheckButton.setSelection(true);

		scrolledComposite = new ScrolledComposite(composite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setVisible(false);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 1;
		gd.exclude = true;
		scrolledComposite.setLayoutData(gd);

		nonDefaultOptionsCompo = new Composite(scrolledComposite, SWT.NONE);
		GridLayout optionsInternNayout = new GridLayout(2, false);

		optionsInternNayout.numColumns = 3;
		optionsInternNayout.verticalSpacing = 5;
		nonDefaultOptionsCompo.setLayout(optionsInternNayout);
		gd = new GridData(GridData.FILL_BOTH);

		gd.horizontalSpan = 3;
		gd.verticalSpan = 5;
		gd.exclude = true;
		nonDefaultOptionsCompo.setLayoutData(gd);
		// nonDefaultOptionsCompo.setVisible(false);

		createXjcOptionsSection(nonDefaultOptionsCompo);
		// commands options

		createCommandOptions(nonDefaultOptionsCompo);

		createEndpointOptions(nonDefaultOptionsCompo);

		createJaxbClassesFolder(nonDefaultOptionsCompo);

		scrolledComposite.setContent(nonDefaultOptionsCompo);
		scrolledComposite.setMinSize(nonDefaultOptionsCompo.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));

	}

	private void createXjcOptionsSection(Composite composite) {
		// ///////xjc options
		GridData gd;
		xjcOptionsGroup = new Group(composite, SWT.NULL);
		xjcOptionsGroup.setText("XJC command options");
		internNayout = new GridLayout();
		internNayout.numColumns = 3;
		xjcOptionsGroup.setLayout(internNayout);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 2;
		xjcOptionsGroup.setLayoutData(gd);

		// ///xjc diectory name
		directoryDestinationName = new Label(xjcOptionsGroup, SWT.NONE);
		directoryDestinationName.setText(directoryDestinationNameText);
		directoryDestinationNameTextField = new Text(xjcOptionsGroup,
				SWT.BORDER | SWT.SINGLE);
		String defaultDirectory = (String) PluginUtils
				.getDefaultStringValueFromStore(GeneralPreferencesPage.XJC_DESTINATION_DIRECTORY);
		directoryDestinationNameTextField.setText(defaultDirectory);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		directoryDestinationNameTextField.setLayoutData(gd);

		directoryDestinationNameBrowseButton = new Button(xjcOptionsGroup,
				SWT.PUSH);
		directoryDestinationNameBrowseButton.setText("Browse...");

		// ///xjc package name

		packageName = new Label(xjcOptionsGroup, SWT.NONE);
		packageName.setText(packageNameText);

		packageNameTextField = new Text(xjcOptionsGroup, SWT.BORDER
				| SWT.SINGLE);
		String defaultPackageName = (String) PluginUtils
				.getDefaultStringValueFromStore(GeneralPreferencesPage.XJC_PACKAGE_NAME);
		packageNameTextField.setText(defaultPackageName);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		packageNameTextField.setLayoutData(gd);

		packageNameBrowseButton = new Button(xjcOptionsGroup, SWT.PUSH);
		packageNameBrowseButton.setText("Browse...");
	}

	private void createJaxbClassesFolder(Composite composite) {
		GridData gd;
		jaxbSourceFolderGroup = new Group(composite, SWT.NULL);
		jaxbSourceFolderGroup.setText("JAXB classes folder");
		internNayout = new GridLayout();
		internNayout.numColumns = 3;
		jaxbSourceFolderGroup.setLayout(internNayout);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 2;
		jaxbSourceFolderGroup.setLayoutData(gd);
		jaxbSourceFolderGroup.setVisible(false);

		jaxbSourceFolderLabel = new Label(jaxbSourceFolderGroup, SWT.NONE);
		jaxbSourceFolderLabel.setText(jaxbSourceFolderLabelText);

		jaxbSourceFolderTextField = new Text(jaxbSourceFolderGroup, SWT.BORDER
				| SWT.SINGLE);

		String defaultJaxbFolder = (String) PluginUtils
				.getDefaultStringValueFromStore(GeneralPreferencesPage.JAXB_SOURCE_DIRECTORY);
		jaxbSourceFolderTextField.setText(defaultJaxbFolder);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		jaxbSourceFolderTextField.setLayoutData(gd);

		jaxbSourceFolderBrowseButton = new Button(jaxbSourceFolderGroup,
				SWT.PUSH);
		jaxbSourceFolderBrowseButton.setText("Browse...");
	}

	private void createCommandOptions(Composite composite) {
		GridData gd;
		generateCommandsButton = new Button(composite, SWT.CHECK);
		generateCommandsButton.setText("Generate Commands");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		generateCommandsButton.setLayoutData(gd);
		generateCommandsButton.setSelection(true);

		commandsOptionsGroup = new Group(composite, SWT.NULL);
		commandsOptionsGroup.setText("Commands Classes options");
		internNayout = new GridLayout();
		internNayout.numColumns = 3;
		commandsOptionsGroup.setLayout(internNayout);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 2;
		commandsOptionsGroup.setLayoutData(gd);

		commandsDestinationPackageLabel = new Label(commandsOptionsGroup,
				SWT.NONE);
		commandsDestinationPackageLabel
				.setText("Commands destination package:");

		commandsDestinationPackageText = new Text(commandsOptionsGroup,
				SWT.BORDER | SWT.SINGLE);
		String defaultCommandsPackage = (String) PluginUtils
				.getDefaultStringValueFromStore(GeneralPreferencesPage.COMMANDS_PACKAGE_NAME);
		commandsDestinationPackageText.setText(defaultCommandsPackage);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		commandsDestinationPackageText.setLayoutData(gd);

		commandsDestinationPackageBrowseButton = new Button(
				commandsOptionsGroup, SWT.PUSH);
		commandsDestinationPackageBrowseButton.setText("Browse...");
	}

	private void createEndpointOptions(Composite composite) {
		GridData gd;

		generateEndpointsButton = new Button(nonDefaultOptionsCompo, SWT.CHECK);
		generateEndpointsButton.setText("Generate Endpoints");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		generateEndpointsButton.setLayoutData(gd);
		generateEndpointsButton.setSelection(true);

		endpointsOptionsGroup = new Group(composite, SWT.FILL);
		endpointsOptionsGroup.setText("Endpoints options");
		internNayout = new GridLayout();
		internNayout.numColumns = 3;
		endpointsOptionsGroup.setLayout(internNayout);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 2;
		endpointsOptionsGroup.setLayoutData(gd);

		endpointsDestinationPackageLabel = new Label(endpointsOptionsGroup,
				SWT.NONE);
		endpointsDestinationPackageLabel
				.setText("Enpoints destination package:");

		endpointsDestinationPackageText = new Text(endpointsOptionsGroup,
				SWT.BORDER | SWT.SINGLE);
		String defaultEndpointsPackage = (String) PluginUtils
				.getDefaultStringValueFromStore(GeneralPreferencesPage.ENDPOINTS_PACKAGE_NAME);
		endpointsDestinationPackageText.setText(defaultEndpointsPackage);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		endpointsDestinationPackageText.setLayoutData(gd);
		endpointsDestinationPackageBrowseButton = new Button(
				endpointsOptionsGroup, SWT.PUSH);
		endpointsDestinationPackageBrowseButton.setText("Browse...");

	}

	/**
	 * Add the listeners to widgets
	 */
	private void addListeners() {
		xsdFilePathBrowseButton.addListener(SWT.Selection, this);
		useDefaultOptionsCheckButton.addListener(SWT.Selection, this);

		packageNameBrowseButton.addListener(SWT.Selection, this);
		directoryDestinationNameBrowseButton.addListener(SWT.Selection, this);
		commandsDestinationPackageBrowseButton.addListener(SWT.Selection, this);
		endpointsDestinationPackageBrowseButton
				.addListener(SWT.Selection, this);

		packageNameTextField.addListener(SWT.Modify, this);
		directoryDestinationNameTextField.addListener(SWT.Modify, this);
		commandsDestinationPackageText.addListener(SWT.Modify, this);
		endpointsDestinationPackageText.addListener(SWT.Modify, this);

		generateJAXBClassesChoice.addListener(SWT.Selection, this);
		generateCommandsAndEndpoints.addListener(SWT.Selection, this);

		generateCommandsButton.addListener(SWT.Selection, this);
		generateEndpointsButton.addListener(SWT.Selection, this);

		jaxbSourceFolderBrowseButton.addListener(SWT.Selection, this);

		checkTree.addListener(SWT.Selection, this);

		checkboxFilesTreeViewer
				.addCheckStateListener(new ICheckStateListener() {
					public void checkStateChanged(CheckStateChangedEvent event) {
						if (event.getChecked()) {
							// // . . . check all its children
							// checkboxFilesTreeViewer.setSubtreeChecked(event.getElement(),
							// true);
							checkedFiles = null;
							Object[] checkedElements = checkboxFilesTreeViewer
									.getCheckedElements();
							if (checkedElements.length == 0
									|| checkedElements == null) {

							} else {
								// you can continue
								checkedFiles = new ArrayList<String>();
								for (int i = 0; i < checkedElements.length; i++) {
									checkedFiles
											.add(((File) checkedElements[i])
													.getAbsolutePath());
								}
								applyToStatusLine(new Status(IStatus.OK,
										"not_used", 0, "", null));

							}
						}
					}
				});


	}

	/**
	 * Handles the different Events of the widget with listeners
	 */
	@Override
	public void handleEvent(Event event) {

		// Initialize a variable with the no error status
		Status status = new Status(IStatus.OK, "not_used", 0, "", null);

		if (event.widget == generateJAXBClassesChoice) {

			generateJaxb = generateJAXBClassesChoice.getSelection();

			removeWidgetFromComposite(chooseFileGroup, !generateJaxb);
			removeWidgetFromComposite(xjcOptionsGroup, !generateJaxb);
			removeWidgetFromComposite(jaxbSourceFolderGroup, generateJaxb);

			if (generateJaxb && checkedFiles == null) {
				applyToStatusLine(new Status(IStatus.ERROR, "not_used", 0,
						"You have to choose at least one xsd file", null));
			} else if (!generateJaxb || checkedFiles != null) {
				applyToStatusLine(new Status(IStatus.OK, "not_used", 0, "",
						null));
			}
		}

		if (event.widget == generateCommandsButton) {
			generateCommands = generateCommandsButton.getSelection();
			removeWidgetFromComposite(commandsOptionsGroup, !generateCommands);

		}

		if (event.widget == generateEndpointsButton) {
			generateEndpoints = generateEndpointsButton.getSelection();
			removeWidgetFromComposite(endpointsOptionsGroup, !generateEndpoints);

		}

		if (event.widget == xsdFilePathBrowseButton) {
			handleFSDirectoryBrowser();
			sourceFilesFromFileSystem = true;
		}

		// Use default options check button, initially checked == use default
		if ((event.widget == useDefaultOptionsCheckButton)) {
			useDefaultOptions = useDefaultOptionsCheckButton.getSelection();
			System.out.println("USE DEFAULT OPTION" + useDefaultOptions);
			removeWidgetFromComposite(scrolledComposite, useDefaultOptions);
			
		}

		// Handles advanced options components events
		if (!useDefaultOptions) {
			// Handle browse buttons for non default options use

			if (event.widget == packageNameBrowseButton) {
				handleDirectoriesBrowser(packageNameTextField,
						"Select a directory");
			}
			if (event.widget == directoryDestinationNameBrowseButton) {
				handleDirectoriesBrowser(directoryDestinationNameTextField,
						"Select a directory");
			}
			if (event.widget == commandsDestinationPackageBrowseButton) {
				handleDirectoriesBrowser(commandsDestinationPackageText,
						"Select a directory");
			}
			if (event.widget == endpointsDestinationPackageBrowseButton) {
				handleDirectoriesBrowser(endpointsDestinationPackageText,
						"Select a directory");
			}
			if (event.widget == jaxbSourceFolderBrowseButton) {
				handleDirectoriesBrowser(jaxbSourceFolderTextField,
						"Select a folder containing Jaxb classes");
			}
			// Handle text fields
			if (event.widget == packageNameTextField) {
				status = returnStatusForTextField(packageNameTextField);
			}
			if (event.widget == directoryDestinationNameTextField) {
				status = returnStatusForTextField(directoryDestinationNameTextField);
			}
			if (event.widget == commandsDestinationPackageText) {
				status = returnStatusForTextField(commandsDestinationPackageText);
			}
			if (event.widget == endpointsDestinationPackageText) {
				status = returnStatusForTextField(endpointsDestinationPackageText);
			}
		}

		// Folder tree viewer selection event
		if ((event.widget == checkTree)) {
			selectedItems = checkTree.getSelection();

			checkboxFilesTreeViewer
					.setInput(((File) selectedItems[0].getData())
							.getAbsolutePath());
			checkFilesTree.selectAll();

		}

		// refresh the shell's layout
		getContainer().getCurrentPage().getControl().getShell()
				.layout(true, true);
		((WizardDialog) this.getWizard().getContainer()).updateSize();
		applyToStatusLine(status);
		getWizard().getContainer().updateButtons();
	}

	/**
	 * Returns an error status if the text field is empty.
	 * 
	 * @param textField
	 * @return
	 */
	private Status returnStatusForTextField(Text textField) {
		if ((textField.getText().isEmpty() || textField == null)) {
			textField.setFocus();
			return new Status(IStatus.ERROR, "not_used", 0,
					"A required field is empty", null);
		}
		return new Status(IStatus.OK, "not_used", 0, "", null);
	}

	/**
	 * Applies the status to the status line of a wizard page.
	 */
	private void applyToStatusLine(IStatus status) {
		String message = status.getMessage();
		if (message.length() == 0)
			message = null;
		switch (status.getSeverity()) {
		case IStatus.OK:
			setErrorMessage(null);
			setMessage(message);
			break;
		case IStatus.WARNING:
			setErrorMessage(null);
			setMessage(message, WizardPage.WARNING);
			break;
		case IStatus.INFO:
			setErrorMessage(null);
			setMessage(message, WizardPage.INFORMATION);
			break;
		default:
			setErrorMessage(message);
			setMessage(null);
			break;
		}
	}

	/**
	 * Handles directories browsing and sets the text of the associated text
	 * filed. Returns the first selected folder's path as a string.
	 * 
	 * @param associatedTextFiled
	 */
	private String handleDirectoriesBrowser(Text associatedTextFiled,
			String message) {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), true,
				message);
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				String choosenFolderPath = "";
				if (associatedTextFiled != null)
					choosenFolderPath = ((Path) result[0]).toString();

				associatedTextFiled.setText(choosenFolderPath);
				return choosenFolderPath;
			}
		}
		return null;
	}

	/**
	 * Handles File system directory browsing
	 */
	private void handleFSDirectoryBrowser() {
		DirectoryDialog directoryDialog = new DirectoryDialog(getShell(),
				SWT.MULTI);
		directoryDialog.setText("Select a directory");

		directoryDialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString());
		String selected = directoryDialog.open();
		if (!selected.isEmpty()) {
			checkboxTreeViewer.setInput(selected);
			checkboxFilesTreeViewer.setInput(selected);
		}

	}

	/**
	 * Drawing a line in the parent composite
	 * 
	 * @param parent
	 * @param ncol
	 */
	private void createLine(Composite parent, int ncol) {
		Label line = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.BOLD);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = ncol;
		line.setLayoutData(gridData);
	}

	/**
	 * Returns the active project absolute path if there is one selected before
	 * launching the wizard otherwise returns the workspace absolute path.
	 * 
	 * @param selection
	 * @return
	 */
	public String getTreeViewerInitialisation(IStructuredSelection selection) {
		String projectPath;
		try {
			projectPath = PluginUtils.getActiveProjectPath(selection);
		} catch (Exception e) {
			projectPath = PluginUtils.getWorkspacePath();
		}

		if (projectPath == null || projectPath.isEmpty())
			projectPath = Platform.getInstanceLocation().getURL().getPath();// System.getProperty("eclipse.launcher");
		return projectPath;
	}

	/**
	 * Enabling and disabling a group is not sufficient, all of its children
	 * must be en/dis-abled to gray out the widget.
	 * 
	 * @param ctrl
	 * @param enabled
	 */
	public void recursiveSetEnabled(Control ctrl, boolean enabled) {
		if (ctrl != null && ctrl instanceof Composite) {
			Composite comp = (Composite) ctrl;
			for (Control c : comp.getChildren())
				recursiveSetEnabled(c, enabled);
		} else {
			ctrl.setEnabled(enabled);
		}
	}

	/**
	 * Sets the current project infos
	 */
	private void setCurrentProjectInfos() {
		String projectPath;

		if (!sourceFilesFromFileSystem && generateJaxb && checkedFiles != null) {
			projectPath = PathUtils.getProjectPathFromFileInWorkspace(
					checkedFiles.get(0), PluginUtils.getWorkspacePath())
					.replace("/", "\\");
		} else {
			projectPath = PluginUtils.getWorkspacePath()
					+ handleDirectoriesBrowser(null, "Select target project")
							.replace("/", "\\");

		}
		String projectName = PathUtils.getClassNameFromPath(projectPath, "\\");
		CurrentProjectInfos.projectPath = projectPath;
		CurrentProjectInfos.projectName = projectName;
		CurrentProjectInfos.currentProject = PluginUtils
				.getIProjectFromProjectName(projectName);
	}

	/**
	 * Launchs the generator with the chosen xsd source files or the folder
	 * containing the JAXB classes
	 */
	public void runGenerator() {
		if (CurrentProjectInfos.projectPath == null) {
			setCurrentProjectInfos();
		}

		if (useDefaultOptions) {
			System.out.println("\n USE DEFAULT OPTIONS");
			runGeneratorWithDefaultOptions();
		} else {

			String destinationDirectory = null;
			String commandsDestinationPackage = null;
			String endpointsDestinationPackage = null;
			String packageName = null;

			if (generateJaxb) {
				destinationDirectory = getFormatedInputTextValue(directoryDestinationNameTextField);
				packageName = getFormatedInputTextValue(packageNameTextField);
			} else {
				destinationDirectory = getFormatedInputTextValue(jaxbSourceFolderTextField);
			}
			if (generateCommands)
				commandsDestinationPackage = getFormatedInputTextValue(commandsDestinationPackageText);
			if (generateEndpoints)
				endpointsDestinationPackage = getFormatedInputTextValue(endpointsDestinationPackageText);

			Generator generator = new Generator(destinationDirectory,
					packageName, commandsDestinationPackage,
					endpointsDestinationPackage, checkedFiles);

			generator.generateClasses(generateJaxb, generateCommands,
					generateEndpoints);
		}

	}

	/**
	 * Launches the generator with the default values
	 */
	private void runGeneratorWithDefaultOptions() {
		String defaultDirectory = (String) PluginUtils
				.getDefaultStringValueFromStore(GeneralPreferencesPage.XJC_DESTINATION_DIRECTORY);
		Generator generator = new Generator(defaultDirectory, null, null, null,
				checkedFiles);
		generator.generateClasses();
	}

	/**
	 * Invoked by the Wizard class to enable or disable the finish button
	 * 
	 * @return
	 */
	public Boolean canFinish() {
		if (getErrorMessage() != null || generateJaxb && checkedFiles == null) {
			return false;
		}
		return true;
	}

	/**
	 * Get and format the input text in the textField
	 * 
	 * @param textField
	 * @return
	 */
	private String getFormatedInputTextValue(Text textField) {
		String text = textField.getText();
		text = text.trim().replace("\\", "/")
				.replace("/" + CurrentProjectInfos.projectName + "/", "");
		return text;
	}

	/**
	 * Removes/adds widget (and its space)
	 * 
	 * @param ctrl
	 * @param exclude
	 */
	private void removeWidgetFromComposite(Control ctrl, boolean exclude) {
		GridData data = (GridData) ctrl.getLayoutData();
		data.exclude = exclude;
		ctrl.setVisible(!data.exclude);

	}
}
