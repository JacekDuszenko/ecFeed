package com.testify.ecfeed.ui.modelif;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationSetRelation;
import com.testify.ecfeed.modelif.java.partition.PartitionOperationSetValue;

public class ExpectedValueStatementInterface extends BasicStatementInterface{

	ExpectedValueStatement fTarget;
	
	public ExpectedValueStatementInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}
	
	public void setTarget(ExpectedValueStatement target){
		super.setTarget(target);
		fTarget = target;
	}

	public boolean setRelation(Relation relation, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(relation != fTarget.getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(fTarget, relation);
			return execute(operation, source, updateListener, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String newValue, AbstractFormPart source, IModelUpdateListener updateListener) {
		IModelOperation operation = new PartitionOperationSetValue(fTarget.getCondition(), newValue, new TypeAdapterProvider());
		return 	execute(operation, source, updateListener, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}
	
	@Override
	public String getConditionValue() {
		return fTarget.getCondition().getValueString();
	}

}
