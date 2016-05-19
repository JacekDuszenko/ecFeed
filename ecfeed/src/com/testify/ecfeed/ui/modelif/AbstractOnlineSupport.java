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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.adapter.java.ILoaderProvider;
import com.testify.ecfeed.core.adapter.java.ModelClassLoader;
import com.testify.ecfeed.core.generators.api.IConstraint;
import com.testify.ecfeed.core.generators.api.IGenerator;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.core.runner.JavaTestRunner;
import com.testify.ecfeed.core.runner.RunnerException;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;
import com.testify.ecfeed.ui.dialogs.SetupDialogOnline;
import com.testify.ecfeed.ui.dialogs.basic.ErrorDialog;

public abstract class AbstractOnlineSupport extends TestExecutionSupport {

	public enum Result {
		OK, CANCELED
	}

	private MethodNode fTarget;
	private JavaTestRunner fRunner;
	private IFileInfoProvider fFileInfoProvider;
	private String fTargetFile;
	private String fExportTemplate;
	String fInitialExportTemplate;

	public AbstractOnlineSupport(ITestMethodInvoker testMethodInvoker, IFileInfoProvider fileInfoProvider) {
		this(testMethodInvoker, fileInfoProvider, null);
	}

	public AbstractOnlineSupport(ITestMethodInvoker testMethodInvoker,
			IFileInfoProvider fileInfoProvider, String initialExportTemplate) {
		ILoaderProvider loaderProvider = new EclipseLoaderProvider();
		ModelClassLoader loader = loaderProvider.getLoader(true, null);
		fRunner = new JavaTestRunner(loader, testMethodInvoker);
		fFileInfoProvider = fileInfoProvider;
		fInitialExportTemplate = initialExportTemplate;
	}

	protected abstract SetupDialogOnline createSetupDialog(
			Shell activeShell, MethodNode methodNode,
			IFileInfoProvider fileInfoProvider, String initialExportTemplate);

	protected abstract Result proceedInternal();

	protected abstract void onDisplayTestSummary();

	protected abstract void prepareRun() throws InvocationTargetException;

	protected abstract void processTestCase(List<ChoiceNode> testData) throws RunnerException;

	protected abstract void setRunMethod() throws RunnerException;

	protected abstract void setRunnerTarget(MethodNode target) throws RunnerException;

	public Result proceed() {
		return proceedInternal();
	}

	public void setTargetMethod(MethodNode target) {
		try {
			setRunnerTarget(target);
			fTarget = target;
		} catch (RunnerException e) {
			ErrorDialog.open(Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE,
					e.getMessage());
		}
	}

	protected MethodNode getTargetMethod() {
		return fTarget;
	}

	protected JavaTestRunner getRunner() {
		return fRunner;
	}

	protected Result displayParametersDialogAndRunTests() {
		SetupDialogOnline dialog = createSetupDialog(Display.getCurrent()
				.getActiveShell(), fTarget, fFileInfoProvider,
				fInitialExportTemplate);

		if (dialog.open() != IDialogConstants.OK_ID) {
			return Result.CANCELED;
		}

		IGenerator<ChoiceNode> selectedGenerator = dialog
				.getSelectedGenerator();
		List<List<ChoiceNode>> algorithmInput = dialog.getAlgorithmInput();
		Collection<IConstraint<ChoiceNode>> constraintList = new ArrayList<IConstraint<ChoiceNode>>();
		constraintList.addAll(dialog.getConstraints());
		Map<String, Object> parameters = dialog.getGeneratorParameters();

		runParametrizedTests(selectedGenerator, algorithmInput, constraintList, parameters);

		onDisplayTestSummary();

		fTargetFile = dialog.getTargetFile();
		fExportTemplate = dialog.getExportTemplate();

		return Result.OK;
	}

	private void runParametrizedTests(IGenerator<ChoiceNode> generator,
			List<List<ChoiceNode>> input,
			Collection<IConstraint<ChoiceNode>> constraints,
			Map<String, Object> parameters) {

		GeneratorProgressMonitorDialog progressDialog = new GeneratorProgressMonitorDialog(
				Display.getCurrent().getActiveShell(), generator);

		ParametrizedTestRunnable runnable = new ParametrizedTestRunnable(
				generator, input, constraints, parameters);
		progressDialog.open();
		try {
			progressDialog.run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					Messages.DIALOG_TEST_EXECUTION_PROBLEM_TITLE,
					e.getMessage());
		}
	}

	public String getExportTemplate() {
		return fExportTemplate;
	}

	public String getTargetFile() {
		return fTargetFile;
	}

	private class ParametrizedTestRunnable implements IRunnableWithProgress {

		private IGenerator<ChoiceNode> fGenerator;
		private List<List<ChoiceNode>> fInput;
		private Collection<IConstraint<ChoiceNode>> fConstraints;
		private Map<String, Object> fParameters;

		ParametrizedTestRunnable(IGenerator<ChoiceNode> generator,
				List<List<ChoiceNode>> input,
				Collection<IConstraint<ChoiceNode>> constraints,
				Map<String, Object> parameters) {
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
		}

		@Override
		public void run(IProgressMonitor progressMonitor)
				throws InvocationTargetException, InterruptedException {

			try {
				prepareRun();
				setProgressMonitor(progressMonitor);
				setRunMethod();

				List<ChoiceNode> next;
				fGenerator.initialize(fInput, fConstraints, fParameters);
				beginTestExecution(fGenerator.totalWork());

				while ((next = fGenerator.next()) != null
						&& progressMonitor.isCanceled() == false) {
					try {
						setTestProgressMessage();
						processTestCase(next);
					} catch (RunnerException e) {
						addFailedTest(e);
					}
					addExecutedTest(fGenerator.workProgress());
				}
				progressMonitor.done();
			} catch (Throwable e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}

	}
}
