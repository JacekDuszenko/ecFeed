package com.testify.ecfeed.adapter.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.DecomposedNode;

public class GenericMoveOperation extends BulkOperation {

	public GenericMoveOperation(List<? extends AbstractNode> moved, AbstractNode newParent, ITypeAdapterProvider adapterProvider) throws ModelOperationException {
		this(moved, newParent, adapterProvider, -1);
	}

	public GenericMoveOperation(List<? extends AbstractNode> moved, AbstractNode newParent, ITypeAdapterProvider adapterProvider, int newIndex) throws ModelOperationException {
		super(OperationNames.MOVE, true);
		Set<MethodNode> methodsInvolved = new HashSet<>();
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(AbstractNode node : moved){
					if(node instanceof DecomposedNode){
						methodsInvolved.add(((DecomposedNode)node).getParameter().getMethod());
					}
					addOperation((IModelOperation)node.getParent().accept(new FactoryRemoveChildOperation(node, false)));
					if(newIndex != -1){
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node, newIndex, adapterProvider, false)));
					}
					else{
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node, adapterProvider, false)));
					}
					for(MethodNode method : methodsInvolved){
						addOperation(new MethodOperationMakeConsistent(method));
					}
				}
			}
			else if(internalNodes(moved, newParent)){
				GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(moved, newIndex);
				addOperation(operation);
			}
		} catch (Exception e) {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}

	protected boolean externalNodes(List<? extends AbstractNode> moved, AbstractNode newParent){
		for(AbstractNode node : moved){
			if(node.getParent() == newParent){
				return false;
			}
		}
		return true;
	}

	protected boolean internalNodes(List<? extends AbstractNode> moved, AbstractNode newParent){
		for(AbstractNode node : moved){
			if(node.getParent() != newParent){
				return false;
			}
		}
		return true;
	}
}
