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
import com.ecfeed.core.adapter.operations.ChoiceOperationSetValue;
import com.ecfeed.core.adapter.operations.StatementOperationSetRelation;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.ecfeed.ui.common.Messages;

public class ExpectedValueStatementInterface extends AbstractStatementInterface{

	public ExpectedValueStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public boolean setRelation(EStatementRelation relation) {
		if(relation != getOwnStatement().getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(getOwnStatement(), relation);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String newValue) {
		IModelOperation operation = new ChoiceOperationSetValue(getOwnStatement().getCondition(), newValue, new EclipseTypeAdapterProvider());
		return 	execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public String getConditionValue() {
		return getOwnStatement().getCondition().getValueString();
	}

	@Override
	public ExpectedValueStatement getOwnStatement(){
		return (ExpectedValueStatement)super.getOwnStatement();
	}

}
