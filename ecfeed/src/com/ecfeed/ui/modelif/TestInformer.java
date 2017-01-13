/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ecfeed.core.runner.RunnerException;
import com.ecfeed.ui.common.Messages;
import com.ecfeed.ui.plugin.Activator;

public class TestInformer {

	IProgressMonitor fProgressMonitor;
	int fTotalWork;
	private int fTotalTestCases = 0;
	private List<Status> fUnsuccesfullExecutionStatuses;


	public TestInformer(){
		fUnsuccesfullExecutionStatuses = new ArrayList<>();
	}

	protected void setProgressMonitor(IProgressMonitor progressMonitor) {
		fProgressMonitor = progressMonitor;
	}

	protected void beginTestExecution(int totalWork) {
		fTotalWork = totalWork;
		fProgressMonitor.beginTask(Messages.EXECUTING_TEST_WITH_PARAMETERS, totalWork);
	}

	protected void setTestProgressMessage() {
		String message = "Total: " + fTotalWork + "  Executed: " + fTotalTestCases + "  Failed: " + fUnsuccesfullExecutionStatuses.size();
		fProgressMonitor.subTask(message);
	}

	protected void incrementTotalTestcases(){
		fTotalTestCases++;
	}

	protected void incrementFailedTestcases(String message){
		fUnsuccesfullExecutionStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
	}

	public boolean anyTestFailed() {
		if (fUnsuccesfullExecutionStatuses.size() > 0 ) {
			return true;
		}
		return false;
	}

	protected void clearFailedTests(){
		fUnsuccesfullExecutionStatuses.clear();
	}

	protected void displayTestStatusDialog() {
		if(fUnsuccesfullExecutionStatuses.size() > 0){
			String msg = Messages.DIALOG_UNSUCCESSFUL_TEST_EXECUTION(fTotalTestCases, fUnsuccesfullExecutionStatuses.size());
			MultiStatus ms = 
					new MultiStatus(
							Activator.PLUGIN_ID, 
							IStatus.ERROR, 
							fUnsuccesfullExecutionStatuses.toArray(new Status[]{}), 
							"Open details to see more", 
							new RunnerException("Problematic test cases"));

			ErrorDialog.openError(null, Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, msg, ms);
			return;
		}
		if (fTotalTestCases > 0) {
			String msg = Messages.DIALOG_SUCCESFUL_TEST_EXECUTION(fTotalTestCases);
			MessageDialog.openInformation(null, Messages.DIALOG_TEST_EXECUTION_REPORT_TITLE, msg);
		}
	}
}
