package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.core.resources.ResourceHelper;
import com.testify.ecfeed.core.serialization.export.TestCasesExportParser;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;

public class TestCasesExportDialog extends TitleAreaDialog {

	private int fMethodParametersCount;
	private Text fTemplateText;
	private TestCasesExportParser fExportParser;
	private Text fTargetFileText;
	private String fTargetFile;

	public TestCasesExportDialog(Shell parentShell, int methodParametersCount) {
		super(parentShell);
		fMethodParametersCount = methodParametersCount;
		fExportParser = new TestCasesExportParser(); 
	}

	public static boolean isAdvancedMode() {
		return false;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setDialogTitle(this);
		setDialogMessage(this);

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = DialogHelper.createGridContainer(area, 1);

		if (isAdvancedMode()) {
			createTemplateDefinitionContainer(container);
		}

		createTargetFileContainer(container);
		return area;
	}

	public void setDialogTitle(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_TITLE = "Export test data";
		setTitle(EXPORT_TEST_DATA_TITLE);
	}

	public void setDialogMessage(TitleAreaDialog dialog)	{
		if (isAdvancedMode()) {
			final String EXPORT_TEST_DATA_MESSAGE = "Define template for data export and select target file";
			setMessage(EXPORT_TEST_DATA_MESSAGE);
		} else {
			final String SELECT_TARGET = "Select target export file";
			setMessage(SELECT_TARGET);
		}
	}

	private void createTemplateDefinitionContainer(Composite parent) {
		Composite container = DialogHelper.createGridContainer(parent, 1);
		createTemplateComposite(container);
	}

	private void createTemplateComposite(Composite parent) {
		final String DEFINE_TEMPLATE = "Define template for export data.";
		DialogHelper.createLabel(parent, DEFINE_TEMPLATE);		

		fTemplateText = DialogHelper.createText(parent, 300, readTemplateFromResource());		
	}

	private String readTemplateFromResource() {
		final String DEFAULT_TEMPLATE_TEXT_FILE = "res/TestCasesExportTemplate.txt";
		String templateText = null;

		try {
			templateText = ResourceHelper.readTextFromResource(this.getClass(), DEFAULT_TEMPLATE_TEXT_FILE);
		} catch (Exception e) {
			ExceptionCatchDialog.display("Can not read template", e.getMessage());
		}

		return templateText;
	}

	private void createTargetFileContainer(Composite parent) {
		final String TARGET_FILE = "Target file";
		DialogHelper.createLabel(parent, TARGET_FILE);		

		Composite targetFileContainer = DialogHelper.createGridContainer(parent, 2);
		fTargetFileText = DialogHelper.createFileSelectionText(targetFileContainer);
		DialogHelper.createBrowseButton(targetFileContainer, new BrowseAdapter()); 
	}

	@Override
	protected void okPressed(){
		String template = null;

		if (fTemplateText != null) {
			template = fTemplateText.getText();
		}

		fExportParser.createSubTemplates(isAdvancedMode(), template, fMethodParametersCount);
		fTargetFile = fTargetFileText.getText();

		super.okPressed();
	}

	public String getHeaderTemplate(){
		return fExportParser.getHeaderTemplate();
	}

	public String getTestCaseTemplate(){
		return fExportParser.getTestCaseTemplate();
	}

	public String getFooterTemplate(){
		return fExportParser.getFooterTemplate();
	}

	public String getTargetFile(){
		return fTargetFile;
	}

	class BrowseAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			FileDialog dialog = new FileDialog(getParentShell());
			fTargetFileText.setText(dialog.open());
		}
	}
}

class DialogHelper {

	public static Composite createGridContainer(Composite parent, int countOfColumns) {

		Composite container = new Composite(parent, SWT.NONE);

		container.setLayout(new GridLayout(countOfColumns, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		return container;
	}

	public static Text createText(Composite parent, int minimumHeight, String initialText) {
		Text templateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = minimumHeight;
		templateText.setLayoutData(gridData);

		if (initialText != null) {
			templateText.setText(initialText);
		}

		return templateText;
	}

	public static Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	public static Text createFileSelectionText(Composite targetFileContainer) {
		Text targetFileText = new Text(targetFileContainer, SWT.BORDER);
		targetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return targetFileText;
	}

	public static Button createButton(
			Composite parent, 
			String buttonText, 
			SelectionListener selectionListener) {
		Button browseButton = new Button(parent, SWT.NONE);
		browseButton.setText(buttonText);
		browseButton.addSelectionListener(selectionListener);

		return browseButton;
	}

	public static Button createBrowseButton(Composite parent, SelectionListener selectionListener) {
		return createButton(parent, "Browse...", selectionListener);
	}

}