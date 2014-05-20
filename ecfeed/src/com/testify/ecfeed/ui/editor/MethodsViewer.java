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
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.EditTestItemDialog;
import com.testify.ecfeed.utils.ModelUtils;

public class MethodsViewer extends CheckboxTableViewerSection {

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private ColorManager fColorManager;
	private ClassNode fSelectedClass;
	
	private class RemoveSelectedMethodsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_METHODS_TITLE, 
					Messages.DIALOG_REMOVE_METHODS_MESSAGE)){
				removeMethods(getCheckboxViewer().getCheckedElements());
			}
		}

		private void removeMethods(Object[] checkedElements) {
			for(Object object : checkedElements){
				if(object instanceof MethodNode){
					fSelectedClass.removeMethod((MethodNode)object);
				}
			}
			modelUpdated();
		}
	}
	
	private class AddNewMethodAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			String newName = getMethodName();

			if ((newName != null) && (fSelectedClass != null)) {
				addMethod(newName, fSelectedClass);
			}
		}

		private void addMethod(String methodName, ClassNode classNode){
			if (Pattern.matches("\\w+", methodName)) {
				MethodNode methodNode = new MethodNode(methodName);
				classNode.addMethod(methodNode);
				modelUpdated();
			} else {
				MessageDialog.openError(getActiveShell(),
						Messages.DIALOG_METHOD_INVALID_NAME_TITLE,
						Messages.DIALOG_METHOD_INVALID_NAME_MESSAGE);
			}
		}

		private String getMethodName() {
			EditTestItemDialog dialog = new EditTestItemDialog(getActiveShell());
			dialog.setTitle("Add new method");
			dialog.setEditorTitle("Enter new parameterized method name");

			if (dialog.open() == IDialogConstants.OK_ID) {
				return dialog.getNewName();
			}

			return null;
		}
	}

	private class MethodsLabelProvider extends ColumnLabelProvider{
		public MethodsLabelProvider() {
			fColorManager = new ColorManager();
		}
		
		@Override
		public String getText(Object element){
			MethodNode method = (MethodNode)element;
			String result = method.toString();
			if(methodObsolete(method)){
				result += " [obsolete]";
			}
			return result;
		}

		@Override
		public Color getForeground(Object element){
			MethodNode method = (MethodNode)element;
			if (methodObsolete(method)) {
				return fColorManager.getColor(ColorConstants.OBSOLETE_METHOD);
			} else if (ModelUtils.isMethodImplemented(method)) {
				return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
			return null;
		}
		
		private boolean methodObsolete(MethodNode method) {
			List<MethodNode> obsoleteMethods = getObsoleteMethods();
			for(MethodNode obsoleteMethod : obsoleteMethods){
				if(obsoleteMethod.toString().equals(method.toString())){
					return true;
				}
			}
			return false;
		}
		
		private List<MethodNode> getObsoleteMethods(){
			if(fSelectedClass != null){
				return ModelUtils.getObsoleteMethods(fSelectedClass, fSelectedClass.getQualifiedName());
			}
			return new ArrayList<MethodNode>();
		}
	}
	
	public MethodsViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);

		setText("Methods");
		addButton("Add new method", new AddNewMethodAdapter());
		addButton("Remove selected", new RemoveSelectedMethodsAdapter());
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		addColumn("Methods", 800, new MethodsLabelProvider());
	}
	
	public void setInput(ClassNode classNode){
		fSelectedClass = classNode;
		super.setInput(classNode.getMethods());
	}

	@Override
	protected boolean tableLinesVisible() {
		return true;
	}

	@Override
	protected boolean tableHeaderVisible() {
		return false;
	}
	
}