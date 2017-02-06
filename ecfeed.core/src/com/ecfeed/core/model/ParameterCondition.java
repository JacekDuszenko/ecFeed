/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;

import com.ecfeed.core.model.IStatementCondition;


public class ParameterCondition implements IStatementCondition {

	private MethodParameterNode fLeftParameterNode;
	private EStatementRelation fRelation;
	private MethodParameterNode fRightParameterNode;

	public ParameterCondition(
			MethodParameterNode parameter, EStatementRelation relation, MethodParameterNode rightParameter) {

		fLeftParameterNode = parameter;
		fRelation = relation;
		fRightParameterNode = rightParameter;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> choices) {

		String leftChoice = StatementConditionHelper.getChoiceForMethodParameter(choices, fLeftParameterNode).getValueString();
		String rightChoice = StatementConditionHelper.getChoiceForMethodParameter(choices, fRightParameterNode).getValueString();

		String typeName = fLeftParameterNode.getType(); // TODO WHEN 2 PARAMETERS HAVE DIFFERENT TYPES

		if (StatementConditionHelper.isRelationMatch(fRelation, typeName, leftChoice, rightChoice)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public ParameterCondition getCopy() {
		return new ParameterCondition(fLeftParameterNode.makeClone(), fRelation, fRightParameterNode.makeClone());
	}

	@Override
	public boolean updateReferences(MethodParameterNode parameter) {
		return true;
	}

	@Override
	public Object getCondition(){
		return null; // TODO
	}

	@Override
	public boolean compare(IStatementCondition otherCondition) {

		if (!(otherCondition instanceof ParameterCondition)) {
			return false;
		}

		ParameterCondition otherParamCondition = (ParameterCondition)otherCondition;

		if (fLeftParameterNode != otherParamCondition.fLeftParameterNode) {
			return false;
		}

		if (fRelation != otherParamCondition.fRelation) {
			return false;
		}		

		if (fRightParameterNode != otherParamCondition.fRightParameterNode) {
			return false;
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	public String toString() {
		return StatementConditionHelper.createParameterDescription(fRightParameterNode.getName());
	}

	public MethodParameterNode getRightMethodParameterNode() {
		return fRightParameterNode;
	}	

}	


