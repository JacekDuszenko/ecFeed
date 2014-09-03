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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaClassUtils;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.modelif.RootInterface;

public class ClassViewer extends CheckboxTableViewerSection {
	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;

	private TableViewerColumn fNameColumn;
	private RootInterface fRootIf;

	private TableViewerColumn fPackageNameColumn;

	private class AddImplementedClassAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ClassNode addedClass = fRootIf.addImplementedClass(ClassViewer.this, getUpdateListener());
			if(addedClass != null){
				selectElement(addedClass);
				fNameColumn.getViewer().editElement(addedClass, 0);
			}
		}
	}
	
	private class RemoveClassesAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Collection<ClassNode> checkedClasses = new ArrayList<ClassNode>();
			for(Object checked : getCheckedElements()){
				if(checked instanceof ClassNode){
					checkedClasses.add((ClassNode)checked);
				}
			}
			fRootIf.removeClasses(checkedClasses, ClassViewer.this, getUpdateListener());
		}
	}
	
	private class AddNewClassAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ClassNode addedClass = fRootIf.addNewClass(null, ClassViewer.this, getUpdateListener());
			if(addedClass != null){
				selectElement(addedClass);
				fNameColumn.getViewer().editElement(addedClass, 0);
			}
		}
	}

	private class ClassViewerColumnLabelProvider extends ColumnLabelProvider {
		private ColorManager fColorManager = new ColorManager();
		
		@Override
		public Color getForeground(Object element) {
			if (element instanceof ClassNode) {
				if(fRootIf.implementationStatus((ClassNode)element) == ImplementationStatus.IMPLEMENTED){
					return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
				}
			}
			return null;
		}
	}
	
	public ClassViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		ModelOperationManager operationManager = parent.getOperationManager();
		fRootIf = new RootInterface(operationManager);
		fNameColumn.setEditingSupport(new LocalNameEditingSupport(this, operationManager));
		fPackageNameColumn.setEditingSupport(new PackageNameEditingSupport(this, operationManager));

		setText("Classes");
		addButton("Add implemented class", new AddImplementedClassAdapter());
		addButton("New test class", new AddNewClassAdapter());
		addButton("Remove selected", new RemoveClassesAdapter());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}
	
	@Override
	protected void createTableColumns(){
		fNameColumn = addColumn("Class", 150, new ClassViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return JavaClassUtils.getLocalName((ClassNode)element);
			}
		});
		
		fPackageNameColumn = addColumn("Package", 150, new ClassViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return JavaClassUtils.getPackageName((ClassNode)element);
			}
		});
	}
	
	public void setInput(RootNode model){
		super.setInput(model.getClasses());
		fRootIf.setTarget(model);
	}
	
}
