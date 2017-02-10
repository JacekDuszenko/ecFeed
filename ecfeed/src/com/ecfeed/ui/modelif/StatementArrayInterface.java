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

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.StatementOperationAddStatement;
import com.ecfeed.core.adapter.operations.StatementOperationChangeOperator;
import com.ecfeed.core.adapter.operations.StatementOperationRemoveStatement;
import com.ecfeed.core.adapter.operations.StatementOperationReplaceChild;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.ui.common.Messages;

public class StatementArrayInterface extends AbstractStatementInterface{

	public StatementArrayInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public boolean addStatement(AbstractStatement statement){
		IModelOperation operation = new StatementOperationAddStatement(getOwnStatement(), statement, getOwnStatement().getChildren().size());
		return execute(operation, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public boolean removeChild(AbstractStatement child){
		IModelOperation operation = new StatementOperationRemoveStatement(getOwnStatement(), child);
		return execute(operation, Messages.DIALOG_REMOVE_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public boolean setOperator(EStatementOperator operator) {
		if(operator != getOwnStatement().getOperator()){
			IModelOperation operation = new StatementOperationChangeOperator(getOwnStatement(), operator);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public EStatementOperator getOperator() {
		return getOwnStatement().getOperator();
	}

	@Override
	public boolean replaceChild(AbstractStatement child, AbstractStatement newStatement) {
		if(child != newStatement){
			IModelOperation operation = new StatementOperationReplaceChild(getOwnStatement(), child, newStatement);
			return execute(operation, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public StatementArray getOwnStatement(){
		return (StatementArray)super.getOwnStatement();
	}
}
