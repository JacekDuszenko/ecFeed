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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class TreeViewerSection extends ViewerSection {

	public TreeViewerSection(ISectionContext sectionContext, IModelUpdateContext updateContext, int style) {
		super(sectionContext, updateContext, style);
	}

	@Override
	protected StructuredViewer createViewer(Composite viewerComposite, int style) {
		return createTreeViewer(viewerComposite, style);
	}

	@Override
	protected void createViewerColumns(){
	}

	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree = new Tree(parent, style);
		tree.setLayoutData(viewerLayoutData());
		TreeViewer treeViewer = new TreeViewer(tree);
		return treeViewer;
	}
	
	protected Tree getTree(){
		return getTreeViewer().getTree();
	}
	
	protected TreeViewer getTreeViewer(){
		return (TreeViewer)getViewer();
	}
}
