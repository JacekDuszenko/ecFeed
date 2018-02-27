/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.commands.IParameter;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.common.utils.SwtObjectHelper;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.ChoiceInterface;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.MethodParameterInterface;

public class ChoiceDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;	
	private ChoicesViewer fChildrenViewer;
	private ChoiceLabelsViewer fLabelsViewer;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fValueCombo;
	private ChoiceInterface fChoiceIf;
	private AbstractCommentsSection fCommentsSection;
	
	private Button fExpectedCheckbox;
	
	public ChoiceDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fChoiceIf = new ChoiceInterface(this, fFileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEditor(getMainComposite());

		addCommentsSection();

		addViewerSection(fChildrenViewer = new ChoicesViewer(this, this, fFileInfoProvider));
		addViewerSection(fLabelsViewer = new ChoiceLabelsViewer(this, this, fFileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite client = super.createTextClientComposite();
		return client;
	}

	@Override
	public void refresh(){
		super.refresh();
		ChoiceNode selectedChoice = getSelectedChoice();
		if(selectedChoice != null){
			fChoiceIf.setOwnNode(selectedChoice);

			String title = getSelectedChoice().toString();
			getMainSection().setText(title);

			fCommentsSection.setInput(selectedChoice);
			fChildrenViewer.setInput(selectedChoice);
			fLabelsViewer.setInput(selectedChoice);

			
			fNameText.setText(selectedChoice.getName());
			refreshValueEditor(selectedChoice);
		}
	}
	

	private void addCommentsSection() {

		if (fFileInfoProvider.isProjectAvailable()) {
			addForm(fCommentsSection = new ChoiceCommentsSection(this, this, fFileInfoProvider));
		} else {
			addForm(fCommentsSection = new SingleTextCommentsSection(this, this, fFileInfoProvider));
		}
	}

	private void refreshValueEditor(ChoiceNode choiceNode) {
		String type = fChoiceIf.getParameter().getType();
		if(fValueCombo != null && fValueCombo.isDisposed() == false){
			fValueCombo.dispose();
		}
		int style = SWT.DROP_DOWN;
		if(AbstractParameterInterface.isBoolean(type)){
			style |= SWT.READ_ONLY;
		}		
		fValueCombo = new ComboViewer(fAttributesComposite, style).getCombo();
		fValueCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Set<String> items = new LinkedHashSet<String>(AbstractParameterInterface.getSpecialValues(type));
		if(JavaTypeHelper.isUserType(type)){
			Set<String> usedValues = fChoiceIf.getParameter().getLeafChoiceValues();
			usedValues.removeAll(items);
			items.addAll(usedValues);
		}
		items.add(fChoiceIf.getValue());
		fValueCombo.setItems(items.toArray(new String[]{}));
		setValueComboText(choiceNode);
		fValueCombo.addSelectionListener(new ValueSelectedListener());
		fValueCombo.addFocusListener(new ValueFocusLostListener());

		if (choiceNode.isAbstract()) {
			fValueCombo.setEnabled(false);
		} else {
			fValueCombo.setEnabled(true);
		}
		
		
		fExpectedCheckbox.setSelection(choiceNode.isRandomizeValue());
		fExpectedCheckbox.setEnabled(isRandomizeCheckboxEnabled());


		fAttributesComposite.layout();
	}
	
	private boolean isRandomizeCheckboxEnabled() {
		String typeName = fChoiceIf.getParameter().getType();
		return !isChoiceNodeAbstract() && isCorrectableType(typeName);
	}
	
	private boolean isChoiceNodeAbstract() {
		ChoiceNode choiceNode = getSelectedChoice();
		return choiceNode!=null && choiceNode.isAbstract();
	}
	
	private boolean isCorrectableType(String typeName) {
		return JavaTypeHelper.isNumericTypeName(typeName) || JavaTypeHelper.isStringTypeName(typeName);
	}

	private void setValueComboText(ChoiceNode choiceNode) {
		if (choiceNode.isAbstract()) {
			fValueCombo.setText(ChoiceNode.ABSTRACT_CHOICE_MARKER);
		} else {
			fValueCombo.setText(fChoiceIf.getValue());
		}
	}

	private ChoiceNode getSelectedChoice(){
		if(getSelectedElement() != null && getSelectedElement() instanceof ChoiceNode) {
			return (ChoiceNode)getSelectedElement();
		}
		return null;
	}

	private void createNameValueEditor(Composite parent) {
		fAttributesComposite = getToolkit().createComposite(parent);
		fAttributesComposite.setLayout(new GridLayout(2, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		getFormObjectToolkit().createLabel(fAttributesComposite, "Name");
		fNameText = getFormObjectToolkit().createGridText(fAttributesComposite, new NameApplier());

		fExpectedCheckbox = 
				getFormObjectToolkit().createGridCheckBox(
						fAttributesComposite, "Randomize value", new ExpectedApplier());
		SwtObjectHelper.setHorizontalSpan(fExpectedCheckbox, 3);
		
		getFormObjectToolkit().createLabel(fAttributesComposite, "Value");
		getFormObjectToolkit().paintBorders(fAttributesComposite);
	}
	
	private class ExpectedApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fChoiceIf.setRandomize(fExpectedCheckbox.getSelection());
			fExpectedCheckbox.setSelection(fChoiceIf.isRandomize());
		}
	}
	
	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return ChoiceNode.class;
	}

	private class NameApplier implements IValueApplier {

		@Override
		public void applyValue() {
			fChoiceIf.setName(fNameText.getText());
			fNameText.setText(fChoiceIf.getName());
		}
	}
	
	private class ValueSelectedListener extends ComboSelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			setValueComboToModel();
		}
	}

	private class ValueFocusLostListener extends FocusLostListener {

		@Override
		public void focusLost(FocusEvent e) {
			setValueComboToModel();
		}

	}

	private void setValueComboToModel() {
		fChoiceIf.setValue(fValueCombo.getText());

		ChoiceNode choiceNode = getSelectedChoice();
		if (choiceNode != null) {
			setValueComboText(choiceNode);
		}

	}

}
