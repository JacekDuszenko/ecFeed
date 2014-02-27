package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class TableViewerSection extends ViewerSection {

	public TableViewerSection(BasicDetailsPage parent, FormToolkit toolkit, int style) {
		super(parent, toolkit, style);
	}

	@Override
	protected IContentProvider viewerContentProvider(){
		return new ArrayContentProvider();
	}

	@Override
	protected IBaseLabelProvider viewerLabelProvider(){
		return new LabelProvider();
	}

	@Override
	protected StructuredViewer createViewer(Composite parent, int style) {
		return createTableViewer(parent, style);
	}

	@Override
	protected void createViewerColumns(){
		createTableColumns();
		
		getTable().setHeaderVisible(tableHeaderVisible());
		getTable().setLinesVisible(tableLinesVisible());
	}

	protected TableViewer createTableViewer(Composite parent, int style){
		Table table = createTable(parent, style);
		table.setLayoutData(viewerLayoutData());
		TableViewer tableViewer = new TableViewer(table);
		
		return tableViewer;
	}
	
	protected Table createTable(Composite parent, int style) {
		return new Table(parent, style);
	}

	protected void addSelectionChangedListener(ISelectionChangedListener listener){
		getTableViewer().addSelectionChangedListener(listener);
	}
	
	protected Table getTable(){
		return getTableViewer().getTable();
	}
	
	protected TableViewer getTableViewer(){
		return (TableViewer)getViewer();
	}
	
	protected TableViewerColumn addColumn(String name, int width, ColumnLabelProvider labelProvider){
		TableViewerColumn viewerColumn = new TableViewerColumn(getTableViewer(), SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setWidth(width);
		column.setText(name);
		column.setResizable(true);
		column.setMoveable(true);
		viewerColumn.setLabelProvider(labelProvider);
		return viewerColumn;
	}
	
	protected boolean tableLinesVisible(){
		return true;
	}

	protected boolean tableHeaderVisible(){
		if(getTable().getColumns().length > 0){
			return true;
		}
		return false;
	}

	protected abstract void createTableColumns();
}