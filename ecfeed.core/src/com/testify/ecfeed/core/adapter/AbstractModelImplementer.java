/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.adapter;

import java.util.List;

import com.testify.ecfeed.core.model.AbstractNode;
import com.testify.ecfeed.core.model.AbstractParameterNode;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.IModelVisitor;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.utils.EcException;
import com.testify.ecfeed.core.utils.SystemLogger;

public abstract class AbstractModelImplementer implements IModelImplementer {

	private ImplementableVisitor fImplementableVisitor;
	private NodeImplementer fNodeImplementerVisitor;
	private IImplementationStatusResolver fStatusResolver;

	private class ImplementableVisitor implements IModelVisitor{
		@Override
		public Object visit(RootNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implementable(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return implementable(node);
		}
	}

	private class NodeImplementer implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return implement(node);
		}

	}

	public AbstractModelImplementer(IImplementationStatusResolver statusResolver) {
		fImplementableVisitor = new ImplementableVisitor();
		fNodeImplementerVisitor = new NodeImplementer();
		fStatusResolver = statusResolver;
	}

	@Override
	public boolean implementable(Class<? extends AbstractNode> type){
		if(type.equals(RootNode.class) ||
				(type.equals(ClassNode.class))||
				(type.equals(MethodNode.class))||
				(type.equals(MethodParameterNode.class))||
				(type.equals(GlobalParameterNode.class))||
				(type.equals(TestCaseNode.class))||
				(type.equals(ChoiceNode.class))
				){
			return true;
		}
		return false;
	}

	@Override
	public boolean implementable(AbstractNode node) {
		try{
			return (boolean)node.accept(fImplementableVisitor);
		}catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		return false;
	}

	@Override
	public boolean implement(AbstractNode node) throws Exception {
		if(implementable(node)){
			return (boolean)node.accept(fNodeImplementerVisitor);
		}
		return false;
	}

	@Override
	public EImplementationStatus getImplementationStatus(AbstractNode node) {
		return fStatusResolver.getImplementationStatus(node);
	}

	protected boolean implement(RootNode rootNode) throws Exception{
		for(GlobalParameterNode parameter : rootNode.getGlobalParameters()){
			if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		for(ClassNode classNode : rootNode.getClasses()){
			if(implementable(classNode) && getImplementationStatus(classNode) != EImplementationStatus.IMPLEMENTED){
				implement(classNode);
			}
		}
		return true;
	}

	protected boolean implement(ClassNode classNode) throws Exception{
		for(GlobalParameterNode parameter : classNode.getGlobalParameters()){
			if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		if(classDefinitionImplemented(classNode) == false){
			implementClassDefinition(classNode);
		}

		for(MethodNode method : classNode.getMethods()){
			if(implementable(method) && getImplementationStatus(method) != EImplementationStatus.IMPLEMENTED){
				implement(method);
			}
		}
		return true;
	}

	protected boolean implement(MethodNode methodNode) throws Exception{
		for(MethodParameterNode parameter : methodNode.getMethodParameters()){
			if(implementable(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		for(TestCaseNode testCase : methodNode.getTestCases()){
			if(implementable(testCase) && getImplementationStatus(testCase) != EImplementationStatus.IMPLEMENTED){
				implement(testCase);
			}
		}
		if(methodDefinitionImplemented(methodNode) == false){
			implementMethodDefinition(methodNode);
		}		
		return true;
	}

	protected boolean implement(AbstractParameterNode parameterNode) throws Exception{
		if(parameterDefinitionImplemented(parameterNode) == false){
			implementParameterDefinition(parameterNode);
		}
		for(ChoiceNode choice : parameterNode.getLeafChoices()){
			if(implementable(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				implement(choice);
				CachedImplementationStatusResolver.clearCache(choice);
			}
		}
		return true;
	}

	protected boolean implement(TestCaseNode testCaseNode) throws Exception{
		for(ChoiceNode choice : testCaseNode.getTestData()){
			if(implementable(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				implement(choice);
			}
		}
		return true;
	}

	protected boolean implement(ConstraintNode constraintNode) throws Exception{
		return false;
	}

	protected boolean implement(ChoiceNode choiceNode) throws Exception{
		if(parameterDefinitionImplemented(choiceNode.getParameter()) == false){
			implementParameterDefinition(choiceNode.getParameter());
		}
		if(choiceNode.isAbstract()){
			for(ChoiceNode leaf : choiceNode.getLeafChoices()){
				if(implementable(leaf) && getImplementationStatus(leaf) != EImplementationStatus.IMPLEMENTED){
					implement(leaf);
				}
			}
		}
		else{
			if(implementable(choiceNode) && getImplementationStatus(choiceNode) != EImplementationStatus.IMPLEMENTED){
				implementChoiceDefinition(choiceNode);
			}
		}
		return true;
	}

	protected boolean implementable(RootNode node){
		return hasImplementableNode(node.getClasses());
	}

	protected boolean implementable(ClassNode node) throws EcException {
		return hasImplementableNode(node.getMethods());
	}

	protected boolean implementable(MethodNode node) throws EcException {
		return hasImplementableNode(node.getParameters()) || hasImplementableNode(node.getTestCases());
	}

	protected boolean implementable(MethodParameterNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean implementable(GlobalParameterNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean implementable(ChoiceNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean implementable(TestCaseNode node){
		return hasImplementableNode(node.getTestData());
	}

	protected boolean hasImplementableNode(List<? extends AbstractNode> nodes){
		for(AbstractNode node : nodes){
			if(implementable(node)){
				return true;
			}
		}
		return false;
	}

	protected abstract boolean classDefinitionImplemented(ClassNode node);
	protected abstract boolean methodDefinitionImplemented(MethodNode node);
	protected abstract boolean parameterDefinitionImplemented(AbstractParameterNode node);

	protected abstract void implementClassDefinition(ClassNode node) throws Exception;
	protected abstract void implementMethodDefinition(MethodNode node) throws Exception;
	protected abstract void implementParameterDefinition(AbstractParameterNode node) throws Exception;
	protected abstract void implementChoiceDefinition(ChoiceNode node) throws Exception;
}
