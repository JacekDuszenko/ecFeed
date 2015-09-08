/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Arrays;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.TestCaseOperationUpdateTestData;
import com.testify.ecfeed.generators.api.EcException;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;

public class TestCaseInterface extends AbstractNodeInterface {

	private IFileInfoProvider fFileInfoProvider;

	public TestCaseInterface(IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	protected TestCaseNode getTarget() {
		return (TestCaseNode)super.getTarget();
	}

	public boolean isExpected(ChoiceNode testValue) {
		return getTarget().getMethodParameter(testValue).isExpected();
	}

	public boolean isExecutable(TestCaseNode tc){
		MethodInterface mIf = new MethodInterface(getUpdateContext(), fFileInfoProvider);
		if(tc.getMethod() == null) return false;
		mIf.setTarget(tc.getMethod());
		EImplementationStatus tcStatus = getImplementationStatus(tc);
		EImplementationStatus methodStatus = mIf.getImplementationStatus();
		return tcStatus == EImplementationStatus.IMPLEMENTED && methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
	}

	public boolean isExecutable(){
		return isExecutable(getTarget());
	}

	public void executeStaticTest() throws EcException {
		MethodInterface methodIf = new MethodInterface(getUpdateContext(), fFileInfoProvider);

		TestCaseNode testCaseNode = getTarget();
		MethodNode methodNode = (MethodNode)testCaseNode.getParent();
		methodIf.setTarget(methodNode);

		methodIf.executeStaticTests(
				new ArrayList<TestCaseNode>(Arrays.asList(new TestCaseNode[]{getTarget()})), fFileInfoProvider);
	}

	public boolean updateTestData(int index, ChoiceNode value) {
		IModelOperation operation = new TestCaseOperationUpdateTestData(getTarget(), index, value);
		return execute(operation, Messages.DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE);
	}

	@Override
	public boolean goToImplementationEnabled(){
		return false;
	}
}
