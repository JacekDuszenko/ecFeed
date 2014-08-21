/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.modelif.CategoryInterface;

public class CategoryDetailsPage extends BasicDetailsPage {

	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fTypeCombo;
	private Button fExpectedCheckbox;
	private Combo fDefaultValueCombo;

	private CategoryChildrenViewer fPartitionsViewer;
	
	private ModelOperationManager fOperationManager;
	private CategoryInterface fCategoryIf;
	
	private abstract class GenericListener implements SelectionListener{
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}
	
	private class SetNameListener extends GenericListener{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setName(fNameText.getText(), null, CategoryDetailsPage.this);
			fNameText.setText(fCategoryIf.getName());
		}
	}
	
	private class SetTypeListener extends GenericListener{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setType(fTypeCombo.getText(), null, CategoryDetailsPage.this);
			fTypeCombo.setText(fCategoryIf.getType());
		}
	}
	
	private class SetDefaultValueListener extends GenericListener{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setDefaultValue(fDefaultValueCombo.getText(), null, CategoryDetailsPage.this);
			fDefaultValueCombo.setText(fCategoryIf.getDefaultValue());
		}
	}
	
	private class SetExpectedListener extends GenericListener{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fCategoryIf.setExpected(fExpectedCheckbox.getSelection(), null, CategoryDetailsPage.this);
			fExpectedCheckbox.setSelection(fCategoryIf.isExpected());
		}
	}
	
	public CategoryDetailsPage(ModelMasterSection masterSection, ModelOperationManager operationManager) {
		super(masterSection);
		fOperationManager = operationManager;
		fCategoryIf = new CategoryInterface(operationManager);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		
		createAttributesComposite();
		addForm(fPartitionsViewer = new CategoryChildrenViewer(this, getToolkit(), fOperationManager));

		getToolkit().paintBordersFor(getMainComposite());
	}
	
	@Override
	public void refresh(){
		if(getSelectedElement() instanceof CategoryNode){
			CategoryNode category = (CategoryNode)getSelectedElement();
			fCategoryIf.setTarget(category);
			
			getMainSection().setText(category.toString());
			fNameText.setText(category.getName());
			fTypeCombo.setItems(CategoryInterface.supportedPrimitiveTypes());
			fTypeCombo.setText(category.getType());
			recreateDefaultValueCombo(category);
			fExpectedCheckbox.setSelection(category.isExpected());
			
			fPartitionsViewer.setInput(category);
		}
	}
	
	private void recreateDefaultValueCombo(CategoryNode category) {
		if(fDefaultValueCombo != null && fDefaultValueCombo.isDisposed() == false){
			fDefaultValueCombo.dispose();
		}
		if(fCategoryIf.hasLimitedValuesSet()){
			fDefaultValueCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		else{
			fDefaultValueCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN);
		}
		fDefaultValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false, 2, 1));
		List<String> items = fCategoryIf.getSpecialValues();
		for(PartitionNode p : category.getLeafPartitions()){
			items.add(p.getValueString());
		}
		fDefaultValueCombo.setItems(items.toArray(new String[]{}));
		fDefaultValueCombo.setText(category.getDefaultValueString());
		fDefaultValueCombo.addSelectionListener(new SetDefaultValueListener());
		
		fDefaultValueCombo.setEnabled(category.isExpected());
		
		fAttributesComposite.layout();
	}

	private void createAttributesComposite(){
		fAttributesComposite = getToolkit().createComposite(getMainComposite());
		fAttributesComposite.setLayout(new GridLayout(3, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		getToolkit().createLabel(fAttributesComposite, "Parameter name: ", SWT.NONE);
		fNameText = getToolkit().createText(fAttributesComposite, "",SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		Button changeButton = getToolkit().createButton(fAttributesComposite, "Change", SWT.NONE);
		SelectionListener nameListener = new SetNameListener();
		fNameText.addSelectionListener(nameListener);
		changeButton.addSelectionListener(nameListener);
		
		getToolkit().createLabel(fAttributesComposite, "Parameter type: ", SWT.NONE);
		fTypeCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN);
		fTypeCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false,2, 1));
		fTypeCombo.addSelectionListener(new SetTypeListener());
		
		getToolkit().paintBordersFor(fAttributesComposite);

		getToolkit().createLabel(fAttributesComposite, "Default value: ", SWT.NONE);

		fExpectedCheckbox = getToolkit().createButton(getMainComposite(), "Expected", SWT.CHECK);
		fExpectedCheckbox.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fExpectedCheckbox.addSelectionListener(new SetExpectedListener());
	}
}
