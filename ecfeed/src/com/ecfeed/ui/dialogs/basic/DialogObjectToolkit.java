/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.utils.EclipseHelper;

public class DialogObjectToolkit {
	private static DialogObjectToolkit fInstance = null;

	protected DialogObjectToolkit() {
	}

	public static DialogObjectToolkit getInstance() {
		if (fInstance == null) {
			fInstance = new DialogObjectToolkit();
		}
		return fInstance;
	}

	private static String[] createFileExtensionTab(String fileExtension) {
		String[] fileExtensions = new String[1];
		fileExtensions[0] = fileExtension;
		return fileExtensions;
	}

	public Composite createGridComposite(Composite parent, int countOfColumns) {

		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(countOfColumns, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);

		return composite;
	}

	public Composite createRowComposite(Composite parentComposite) {
		Composite composite = new Composite(parentComposite, SWT.NONE);

		RowLayout rowLayout = new RowLayout();
		composite.setLayout(rowLayout);

		return composite;
	}

	public Composite createFillComposite(Composite parentComposite) {
		Composite composite = new Composite(parentComposite, SWT.NONE);

		FillLayout rowLayout = new FillLayout();
		composite.setLayout(rowLayout);

		return composite;
	}

	public Text createGridText(Composite parentGridComposite, int heightHint,
			String initialText) {
		Text templateText = new Text(parentGridComposite, SWT.WRAP | SWT.MULTI
				| SWT.BORDER | SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = heightHint;
		templateText.setLayoutData(gridData);

		if (initialText != null) {
			templateText.setText(initialText);
		}

		return templateText;
	}

	public Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	public Label createSpacer(Composite parent, int size) {
		return createLabel(parent, StringHelper.createString(" ", size));
	}

	public Text createFileSelectionText(Composite targetFileContainer,
			ModifyListener modifyListener) {
		Text targetFileText = new Text(targetFileContainer, SWT.BORDER);
		targetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));

		if (modifyListener != null) {
			targetFileText.addModifyListener(modifyListener);
		}

		return targetFileText;
	}

	public Button createButton(Composite parent, String buttonText,
			SelectionListener selectionListener) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(buttonText);

		if (selectionListener != null) {
			button.addSelectionListener(selectionListener);
		}

		return button;
	}
	
	public Combo createCombo(Composite parent, int maxLimit)
	{
		final Combo combo = new Combo(parent, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		for(int i = 1; i < maxLimit; i++)
		{
			combo.add(Integer.toString(i));
		}
		combo.select(0);
		return combo;
	}

	public Button createBrowseButton(Composite parent,
			SelectionListener selectionListener) {
		final String BROWSE_LABEL = "Browse...";
		return createButton(parent, BROWSE_LABEL, selectionListener);
	}
	
	public Table createTable(Composite parent)
	{
		final Table table = new Table(parent, SWT.NONE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		return table;
	}

	public Text createFileSelectionComposite(Composite parent,
			String labelText, String[] extensionsFilter, ModifyListener textModifyListener) {
		Composite childComposite = createGridComposite(parent, 2);

		createLabel(childComposite, labelText);

		createSpacer(childComposite, 1);

		Text targetFileText = createFileSelectionText(childComposite,
				textModifyListener);

		SelectionListener browseSelectionListener = 
				new FileDialogSelectionAdapter(SWT.SAVE, extensionsFilter, targetFileText);
		createBrowseButton(childComposite, browseSelectionListener);
		return targetFileText;
	}

	class FileDialogSelectionAdapter extends SelectionAdapter {
		int fDialogStyle;
		String[] fFileExtensions;
		Text fTargetFileText;

		FileDialogSelectionAdapter(int dialogStyle, String[] fileExtensions, Text targetFileText) {
			fDialogStyle = dialogStyle;
			fFileExtensions = fileExtensions;
			fTargetFileText = targetFileText;
		}

		FileDialogSelectionAdapter(int dialogStyle, String fileExtension, Text targetFileText) {
			this(dialogStyle, createFileExtensionTab(fileExtension), targetFileText);
		}

		FileDialogSelectionAdapter(int dialogStyle, Text targetFileText) {
			this(dialogStyle, new String(), targetFileText);
		}		

		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(EclipseHelper.getActiveShell(), fDialogStyle);

			dialog.setFilterExtensions(fFileExtensions);

			String filePath = dialog.open();
			if (filePath == null) {
				return;
			}

			fTargetFileText.setText(filePath);
		}
	}

}