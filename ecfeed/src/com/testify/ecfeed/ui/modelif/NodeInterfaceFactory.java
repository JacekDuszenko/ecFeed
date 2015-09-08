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

package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.external.IFileInfoProvider;
import com.testify.ecfeed.utils.SystemLogger;

public class NodeInterfaceFactory{

	private static class InterfaceProvider  implements IModelVisitor {

		private IFileInfoProvider fFileInfoProvider;
		private IModelUpdateContext fContext;

		public InterfaceProvider(IModelUpdateContext context, IFileInfoProvider fileInfoProvider) {
			fContext = context;
			fFileInfoProvider = fileInfoProvider;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			RootInterface nodeIf = new RootInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			ClassInterface nodeIf = new ClassInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			MethodInterface nodeIf = new MethodInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			AbstractParameterInterface nodeIf = new MethodParameterInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			AbstractParameterInterface nodeIf = new GlobalParameterInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			TestCaseInterface nodeIf = new TestCaseInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ConstraintInterface nodeIf = new ConstraintInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			ChoiceInterface nodeIf = new ChoiceInterface(fContext, fFileInfoProvider);
			nodeIf.setTarget(node);
			return nodeIf;
		}
	}

	public static AbstractNodeInterface getNodeInterface(
			AbstractNode node, 
			IModelUpdateContext context, 
			IFileInfoProvider fileInfoProvider){
		try{
			return (AbstractNodeInterface)node.accept(new InterfaceProvider(context, fileInfoProvider));
		}
		catch(Exception e){SystemLogger.logCatch(e.getMessage());}
		AbstractNodeInterface nodeIf = new AbstractNodeInterface(context, fileInfoProvider);
		nodeIf.setTarget(node);
		return nodeIf;
	}
}
