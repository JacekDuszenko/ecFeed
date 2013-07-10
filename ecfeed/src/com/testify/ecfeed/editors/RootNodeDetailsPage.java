package com.testify.ecfeed.editors;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.utils.EcModelUtils;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.GridData;

public class RootNodeDetailsPage implements IDetailsPage, IModelUpdateListener{

	private IManagedForm fManagedForm;
	private RootNode fSelectedNode;
	private CheckboxTableViewer fClassesViewer;
	private Text fNodeNameText;
	private EcMultiPageEditor fEditor;
	private FormToolkit fToolkit;
	private ModelMasterDetailsBlock fParentBlock;

	private class AddTestClassButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			IType selectedClass = selectClass();

			if(selectedClass != null){
				ClassNode classNode = EcModelUtils.generateClassModel(selectedClass);
				if(!EcModelUtils.classExists(fSelectedNode, classNode)){
					fSelectedNode.addClass(classNode);
					fEditor.updateModel(fSelectedNode);
				}
				else{
					MessageDialog infoDialog = new MessageDialog(Display.getDefault().getActiveShell(), 
							"Class exists", Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 
							"Selected class is already contained in the model", MessageDialog.INFORMATION
							, new String[] {"OK"}, 0);
					infoDialog.open();
				}
			}
		}

		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());
			
			if (dialog.open() == Window.OK) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}

	private class RenameModelButtonSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			String name = fNodeNameText.getText();
			if(!name.equals(fSelectedNode.getName())){
				renameModel(name);
			}
		}
	}

	private class RemoveClassesButtonSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			MessageDialog infoDialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					"Class exists", Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
					"This operation will remove selected test classes from the model. " +
					"All generated test cases will be permanently deleted", 
					MessageDialog.WARNING, new String[] {"OK", "Cancel"}, 0);
			if(infoDialog.open() == 0){
				removeClasses(fClassesViewer.getCheckedElements());
			}
		}

		private void removeClasses(Object[] checkedElements) {
			for(Object element : checkedElements){
				if(element instanceof ClassNode){
					fSelectedNode.removeChild((ClassNode)element);
				}
			}
			fEditor.updateModel(fSelectedNode);
		}
	}


	/**
	 * @wbp.parser.constructor
	 */
	public RootNodeDetailsPage(EcMultiPageEditor editor, ModelMasterDetailsBlock parentBlock){
		fEditor = editor;
		fEditor.registerModelUpdateListener(this);
		fParentBlock = parentBlock;
	}
	
	/**
	 * Initialize the details page.
	 * @param form
	 */
	public void initialize(IManagedForm form) {
		fManagedForm = form;
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		fToolkit = fManagedForm.getToolkit();
		parent.setLayout(new FillLayout());
		Section section = fToolkit.createSection(parent,
				Section.TITLE_BAR);
		section.setText("Model tree root node");
		Composite composite = fToolkit.createComposite(section, SWT.NONE);
		fToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(1, true));
		
		createNodeNameComposite(composite);
		createClassListViewer(composite);
		createBottomButtons(composite);
	}

	private void createClassListViewer(Composite composite) {
		Label classesLabel = new Label(composite, SWT.BOLD);
		classesLabel.setText("Test classes");

		fClassesViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION |SWT.FILL);
		fClassesViewer.setContentProvider(new ArrayContentProvider());
		fClassesViewer.addDoubleClickListener(new ModelDetailsPageDoubleClickListener(fParentBlock));
		
		Table table = fClassesViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		fToolkit.paintBordersFor(table);

		createClassListTableColumns(composite, fClassesViewer);
	}

	private void createClassListTableColumns(Composite composite, TableViewer viewer) {
		TableViewerColumn column = createTableViewerColumn(viewer, "Class", 150, 0);
		column.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getLocalName();
			}
		});
		
		column = createTableViewerColumn(viewer, "Qualified name", 150, 0);
		column.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getQualifiedName();
			}
		});
		
	}

	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int width, final int columnNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(true);
		
		return viewerColumn;
	}

	private void createBottomButtons(Composite composite) {
		Composite bottomButtonsComposite = fToolkit.createComposite(composite, SWT.FILL);
		bottomButtonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		bottomButtonsComposite.setLayout(new GridLayout(2, false));
		
		Button addTestClassButton = new Button(bottomButtonsComposite, SWT.NONE);
		addTestClassButton.setText("Add Test Class...");
		addTestClassButton.addSelectionListener(new AddTestClassButtonSelectionAdapter());
		Button removeClassesButton = new Button(bottomButtonsComposite, SWT.NONE);
		removeClassesButton.setText("Remove selected classes");
		removeClassesButton.addSelectionListener(new RemoveClassesButtonSelectionAdapter());
	}

	private void createNodeNameComposite(Composite composite) {
		Composite nodeNameComposite = fToolkit.createComposite(composite, SWT.FILL);
		nodeNameComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		nodeNameComposite.setLayout(new GridLayout(3, false));
		Label nodeNameLabel = new Label(nodeNameComposite, SWT.BOLD);
		nodeNameLabel.setText("Model name: ");
		fNodeNameText = new Text(nodeNameComposite, SWT.FILL | SWT.BORDER);
		GridData nodeNameTextGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		nodeNameTextGridData.widthHint = SWT.MAX;
		fNodeNameText.setLayoutData(nodeNameTextGridData);
		fNodeNameText.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					renameModel(fNodeNameText.getText());
				}
			}
		});

		Button renameButton = new Button(nodeNameComposite, SWT.NONE);
		renameButton.addSelectionListener(new RenameModelButtonSelectionAdapter());
		renameButton.setText("Rename");
	}

	private void renameModel(String name) {
		fSelectedNode.setName(name);
		fEditor.updateModel(fSelectedNode);
	}

	public void dispose() {
		// Dispose
	}

	public void setFocus() {
		// Set focus
	}

	private void updateDetailsPage() {
		fNodeNameText.setText(fSelectedNode.getName());
		fClassesViewer.setInput(fSelectedNode.getClasses());
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.getFirstElement() instanceof RootNode){
			fSelectedNode = (RootNode)structuredSelection.getFirstElement();
		}
		updateDetailsPage();
	}

	public void commit(boolean onSave) {
		// Commit
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		updateDetailsPage();
	}

	@Override
	public void modelUpdated(RootNode model) {
		fSelectedNode = model;
		updateDetailsPage();
	}

}
