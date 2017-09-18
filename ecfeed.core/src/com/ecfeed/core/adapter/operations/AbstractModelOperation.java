/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.AbstractNode;

public abstract class AbstractModelOperation implements IModelOperation {

	private boolean fModelUpdated;
	private String fName;
	private AbstractNode fNodeToBeSelectedAfterTheOperation = null;

	public AbstractModelOperation(String name){
		fName = name;
	}
	
	@Override
	public boolean modelUpdated() {
		return fModelUpdated;
	}

	protected void markModelUpdated(){
		fModelUpdated = true;
	}
	
	@Override
	public String getName(){
		return fName;
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
	@Override
	public void setNodeToBeSelectedAfterTheOperation(AbstractNode nodeToBeSelectedAfterTheOperation) {
		fNodeToBeSelectedAfterTheOperation = nodeToBeSelectedAfterTheOperation;
	}
	
	public AbstractNode getNodeToBeSelectedAfterTheOperation() {
		return fNodeToBeSelectedAfterTheOperation;
	}
	
}
