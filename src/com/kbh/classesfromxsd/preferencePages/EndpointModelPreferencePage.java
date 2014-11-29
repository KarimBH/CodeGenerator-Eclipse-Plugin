package com.kbh.classesfromxsd.preferencePages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import utils.FileUtils;

/**
 * Preference page that allows to modify the endpoint model and to restore the
 * default one.
 * 
 * @author Karim
 * 
 */
public class EndpointModelPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, Listener {
	StyledText text;
	Label endpointPreferenceLabel;
	final static String endpointPreferenceLabelText = "Use the tags ${packageName}, ${operationName}, ${operationRequestClass},\n ${operationResponseClass} and ${superCommandClassName}.";

	Button defaultsButton;
	Button applyButton;

	String endpointModelFilePath = "models/endpointModel.txt";
	String defaultEndpointModelFilePath = "models/defaultEndpointModel.txt";
	String defaultEndpointModelCode = FileUtils
			.getBundleFileContent(defaultEndpointModelFilePath);

	/**
	 * Create the preference page.
	 */
	public EndpointModelPreferencePage() {
	}

	/**
	 * Create contents of the preference page.
	 * 
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		GridData gd;
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();

		int ncol = 2;
		int nrows = 10;
		layout.numColumns = ncol;
		layout.verticalSpacing = nrows;
		composite.setLayout(layout);

		endpointPreferenceLabel = new Label(composite, SWT.NONE);
		endpointPreferenceLabel.setText(endpointPreferenceLabelText);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		endpointPreferenceLabel.setLayoutData(gd);

		text = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = ncol;
		gd.verticalSpan = nrows;
		text.setLayoutData(gd);
		text.setMargins(10, 10, 10, 10);

		text.setText(FileUtils.getBundleFileContent(endpointModelFilePath));

		defaultsButton = new Button(composite, SWT.PUSH);
		defaultsButton.setText("Restore defaults");

		applyButton = new Button(composite, SWT.PUSH);
		applyButton.setText("Apply");

		addListeners();
		return composite;
	}

	/**
	 * Adds selection listeners to the defaults and apply buttons
	 */
	private void addListeners() {
		defaultsButton.addListener(SWT.Selection, this);
		applyButton.addListener(SWT.Selection, this);
	}

	@Override
	public void handleEvent(Event event) {
		if ((event.widget == defaultsButton)) {
			PreferencesPageUtils.restoreDefaultModel(text,
					endpointModelFilePath, defaultEndpointModelCode);
		}
		if ((event.widget == applyButton)) {
			PreferencesPageUtils
					.storeModifiedModel(text, endpointModelFilePath);
		}
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		// Remove the defaults and apply buttons as we will redefine these
		// buttons manually
		this.noDefaultAndApplyButton();
	}

}
