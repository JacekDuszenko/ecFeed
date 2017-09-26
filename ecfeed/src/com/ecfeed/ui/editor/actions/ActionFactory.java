/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor.actions;

import org.eclipse.jface.viewers.ISelectionProvider;

import com.ecfeed.ui.modelif.NodeClipboard;

public class ActionFactory {

	private static AboutAction fAboutAction = null;
	private static SaveAction fSaveAction = null;

	public static AboutAction getAboutAction() {

		if (fAboutAction == null) {
			fAboutAction = new AboutAction();
		}

		return fAboutAction;
	}

	public static SaveAction getSaveAction() {

		if (fSaveAction == null) {
			fSaveAction = new SaveAction();
		}

		return fSaveAction;
	}

}
