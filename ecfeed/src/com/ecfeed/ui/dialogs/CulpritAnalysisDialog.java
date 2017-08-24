/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.generators.Culprit;
import com.ecfeed.core.generators.TestResultDescription;
import com.ecfeed.core.generators.TestResultsAnalysis;
import com.ecfeed.core.generators.TestResultsAnalyzer;
import com.ecfeed.core.generators.TestResultsAnalyzerTest;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.dialogs.TestCasesExportDialog.LoadButtonSelectionAdapter;
import com.ecfeed.ui.dialogs.TestCasesExportDialog.SaveAsButtonSelectionAdapter;
import com.ecfeed.ui.dialogs.basic.DialogObjectToolkit;

public class CulpritAnalysisDialog extends TitleAreaDialog{
	private DialogObjectToolkit fDialogObjectToolkit;
	private int numRow = 0;
	Table table = null;
	private List<TestResultDescription> testResultDescrs = createtestResultDescrs();
	private TestResultsAnalysis testResultsAnalysis = new TestResultsAnalyzer().generateAnalysis(testResultDescrs, 0, 3);

	public CulpritAnalysisDialog(Shell parent) 
	{
		super(parent);
		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}
	
	public CulpritAnalysisDialog()
	{
		super(null);
		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}
	
	public void run()
	{
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle("Test execution report");	
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		Composite ChildComp = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 1);
		final String text = "Select tuple size for combinatoric analysis";
		createLabel(ChildComp, text);
		Composite ChildComp2 = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 4);
		createLabel(ChildComp2, "min");
		createCombo(ChildComp2, 0);
		createLabel(ChildComp2, "max");
		createCombo(ChildComp2, 2);
		Composite ChildComp3 = (Composite) fDialogObjectToolkit.createGridComposite(dialogArea, 2);
		table = createTableContents(ChildComp3, 4);
		Button NextButton = fDialogObjectToolkit.createButton(ChildComp3, "Next Page", new NextButtonSelectionAdapter());
		Button PrevButton = fDialogObjectToolkit.createButton(ChildComp3, "Previous Page", new PrevButtonSelectionAdapter());
		return dialogArea;
	}
	
	private void createCombo(Composite parentComposite, int defaultValue)
	{
		fDialogObjectToolkit.createCombo(parentComposite, 10, defaultValue);	
	}
	
	private void createLabel(Composite parentComposite, String text) 
	{
		fDialogObjectToolkit.createLabel(parentComposite, text);
	}
	
	private Table createTableContents(Composite parentComposite, int ColumnNr)
	{
		Table table = fDialogObjectToolkit.createTable(parentComposite);
		String[] ColumnNames = {"NumTuple", "Tuple", "Occurences", "Fails", "Failure Index"};
		TableColumn[] column = fDialogObjectToolkit.addColumn(table, 5, ColumnNames);
		fillTable(table, testResultDescrs);
		for (int i = 0; i< column.length; i++)
		{
			column[i].pack();
		}
		return table;
	}
	
	private void fillTable(Table table, List<TestResultDescription> testResultDescrs ) {
		table.setRedraw(false);
		
			for (int i = numRow; i<numRow+27; i++)
			{
				TableItem item = new TableItem(table, SWT.NONE);
				int c = 0;
				item.setText(c++, Integer.toString(i));
				item.setText(c++, testResultsAnalysis.getCulprit(i).getItem().toString());
				item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(i).getOccurenceCount()));
				item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(i).getFailureCount()));
				item.setText(c++, Integer.toString(testResultsAnalysis.getCulprit(i).getFailureIndex()));
			}
		
		table.setRedraw(true);	
	}
	
	private void addTestResult(
			String[] testArguments, boolean result, List<TestResultDescription> testResultDescrs) 
	{
		List<String> testArgList = new ArrayList<String>();
		for (String testArgument : testArguments) {
			testArgList.add(testArgument);
		}
		testResultDescrs.add(new TestResultDescription(testArgList, result));
	}
	
	public List<TestResultDescription> createtestResultDescrs()
	{
		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();
		addTestResult(new String[]{ "1", "2", "3", "4", "5" }, false, testResultDescrs);
		addTestResult(new String[]{ "0", "2", "3", "5", "4" }, false, testResultDescrs);
		addTestResult(new String[]{ "5", "2", "3", "7", "8" }, true, testResultDescrs);
		addTestResult(new String[]{ "7", "7", "3", "9", "8" }, false, testResultDescrs);
		addTestResult(new String[]{ "2", "4", "5", "3", "8" }, true, testResultDescrs);
		return testResultDescrs;
	}
	class NextButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			int total = testResultsAnalysis.getCulpritCount();
			if(total - numRow >= 28)
			{
				numRow = numRow + 28;
				System.out.println("number of rows"+ numRow);
			}
			else{
				numRow = numRow + (total-numRow);
			}
			if(numRow < total)
			{
			table.removeAll();
			fillTable(table, testResultDescrs);
			}
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e)
		{

		}
	
	}
	class PrevButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			int total = testResultsAnalysis.getCulpritCount();
			if(numRow >= 28)
			{
				numRow = numRow - 28;
				System.out.println("number of rows"+ numRow);
			}
			else{
				System.out.println("number of rows"+ numRow);
			}
			if(numRow < total)
			{
			table.removeAll();
			fillTable(table, testResultDescrs);
			}
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e)
		{

		}
	
	}

	public static void main(String[] args)
	{
		new CulpritAnalysisDialog().run();
	}
	



	

}