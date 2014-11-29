package com.kbh.classesfromxsd.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kbh.classesfromxsd.preferencePages.GeneralPreferencesPage;

public class GenerationResultDialog extends Dialog {
	String result;
	Button dontShowAgainButton;
	Label resultLabel;
	StyledText resultText;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public GenerationResultDialog(Shell parentShell, String result) {
		super(parentShell);
		if (result != null) {
			this.result = result;
		} else {
			result = "An excpected error has occured";
		}
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		GridData gd;
		GridLayout layout = new GridLayout();

		int ncol = 1;
		int nrows = 5;
		layout.numColumns = ncol;
		layout.verticalSpacing = nrows;
		parent.setLayout(layout);

		resultLabel = new Label(parent, SWT.None);
		resultLabel.setText("Generation results");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		resultLabel.setLayoutData(gd);

		resultText = new StyledText(parent, SWT.BORDER | SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL);
		resultText.setMargins(7, 7, 7, 7);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = ncol;
		gd.verticalSpan = 4;
		resultText.setText(result);
		resultText.setLayoutData(gd);

		// don't show again check button
		dontShowAgainButton = new Button(parent, SWT.CHECK);
		dontShowAgainButton.setText("Don't show this again");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol;
		dontShowAgainButton.setLayoutData(gd);
		dontShowAgainButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.widget == dontShowAgainButton) {
					// selected == don't show
					boolean selected = dontShowAgainButton.getSelection();
					Activator
							.getDefault()
							.getPreferenceStore()
							.setValue(
									GeneralPreferencesPage.SHOW_GENERATION_RESULT,
									!selected);
				}
			}
		});
		return parent;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		// createButton(parent, IDialogConstants.CANCEL_ID,
		// IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 350);
	}

}
