package com.testify.ecfeed.modelif.java.partition;

import java.util.List;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class PartitionOperationAddLabel implements IModelOperation {

	private PartitionNode fTarget;
	private String fLabel;
	private List<PartitionNode> fLabeledDescendants;
	
	private class ReverseOperation implements IModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.removeLabel(fLabel);
			for(PartitionNode p : fLabeledDescendants){
				p.addLabel(fLabel);
			}
		}

		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationAddLabel(fTarget, fLabel);
		}
		
	}

	public PartitionOperationAddLabel(PartitionNode target, String label){
		fTarget = target;
		fLabel = label;
		fLabeledDescendants = target.getLabeledPartitions(fLabel);
	}
	
	@Override
	public void execute() throws ModelIfException {
		fTarget.addLabel(fLabel);
		for(PartitionNode p : fLabeledDescendants){
			p.removeLabel(fLabel);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}
}
