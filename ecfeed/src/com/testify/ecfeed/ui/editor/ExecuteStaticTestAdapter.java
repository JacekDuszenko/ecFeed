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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.runner.ParameterizedMethod;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.ConsoleManager;
import com.testify.ecfeed.utils.ModelUtils;

public class ExecuteStaticTestAdapter extends ExecuteTestAdapter {

	private TestCasesViewer fViewerSection;

	public ExecuteStaticTestAdapter(TestCasesViewer viewerSection) {
		fViewerSection = viewerSection;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void widgetSelected(SelectionEvent event){
		ConsoleManager.displayConsole();
		ConsoleManager.redirectSystemOutputToStream(ConsoleManager.getOutputStream());
		try {
			Class testClass = loadTestClass();
			Method testMethod = getTestMethod(testClass, getMethodModel());
			if(testMethod == null){
				MessageDialog.openError(Display.getDefault().getActiveShell(), 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_TITLE, 
					Messages.DIALOG_COULDNT_LOAD_TEST_METHOD_MESSAGE(getMethodModel().toString()));
			}
			if (ModelUtils.isMethodWithParameters(getMethodModel())) {
				Collection<TestCaseNode> selectedTestCases = getSelectedTestCases();
				ParameterizedMethod frameworkMethod = new ParameterizedMethod(testMethod, selectedTestCases);
				frameworkMethod.invokeExplosively(testClass.newInstance(), new Object[]{});
			} else {
				List<TestCaseNode> testCases = new ArrayList<TestCaseNode>();
				testCases.add(new TestCaseNode("", new ArrayList<PartitionNode>()));
				ParameterizedMethod frameworkMethod = new ParameterizedMethod(testMethod, testCases);
				frameworkMethod.invokeExplosively(testClass.newInstance(), new Object[]{});
			}
		} catch (Throwable e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
				Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_TITLE, 
				Messages.DIALOG_TEST_METHOD_EXECUTION_STOPPED_MESSAGE(getMethodModel().toString(), e.getMessage()));
		} 
	}

	protected Collection<TestCaseNode> getSelectedTestCases() {
		Collection<TestCaseNode> testCases = new HashSet<TestCaseNode>();
		for(Object element : fViewerSection.getCheckedElements()){
			if(element instanceof TestCaseNode){
				testCases.add((TestCaseNode)element);
			}
			else if(element instanceof String && !fViewerSection.getCheckboxViewer().getGrayed(element)){
				for (TestCaseNode testCase : getMethodModel().getTestCases((String)element)) {
					if (ModelUtils.isTestCaseImplemented(testCase)) {
						testCases.add((TestCaseNode)testCase);
					}
				}
			}
		}
		return testCases;
	}

	@Override
	protected MethodNode getMethodModel() {
		return fViewerSection.getSelectedMethod();
	}
}
