package com.testify.ecfeed.modelif.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class BulkOperation implements IModelOperation{

	List<IModelOperation> fOperations;
	List<IModelOperation> fExecutedOperations;
	// either all operation or none. if false, all operations are executed
	// otherwise after first error the reverse operation is called
	private boolean fAtomic;
	private boolean fModelUpdated;
	private List<ICheckOperation> fCheckOperations; 
	
	protected interface ICheckOperation{
		public void check() throws ModelIfException;
	}
	
	public BulkOperation(boolean atomic) {
		this(new ArrayList<IModelOperation>(), atomic);
	}
	
	public BulkOperation(List<IModelOperation> operations, boolean atomic) {
		fModelUpdated = false;
		fOperations = operations;
		fExecutedOperations = new ArrayList<IModelOperation>();
		fCheckOperations = new ArrayList<ICheckOperation>();
		fAtomic = atomic;
	}
	
	protected void addOperation(IModelOperation operation) {
		fOperations.add(operation);
	}
	
	protected void addCheckOperation(ICheckOperation operation){
		fCheckOperations.add(operation);
	}
	
	@Override
	public void execute() throws ModelIfException {
		Set<String> errors = new HashSet<String>();
		for(IModelOperation operation : fOperations){
			try{
				operation.execute();
				fModelUpdated = true;
				fExecutedOperations.add(operation);
			}catch(ModelIfException e){
				errors.add(e.getMessage());
				if(fAtomic){
					reverseOperation().execute();
					break;
				}
			}
		}
		for(ICheckOperation operation : fCheckOperations){
			try{
				operation.check();
			}catch(ModelIfException e){
				errors.add(e.getMessage());
				reverseOperation().execute();
				break;
			}
		}
		if(errors.size() > 0){
			String message = Messages.PROBLEM_WITH_BULK_OPERATION;
			for(String error : errors){
				message += "\n" + error;
			}
			throw new ModelIfException(message);
		}
	}

	@Override
	public IModelOperation reverseOperation(){
		return new BulkOperation(reverseOperations(), fAtomic);
	}
	
	
	protected List<IModelOperation> operations(){
		return fOperations;
	}

	protected List<IModelOperation> executedOperations(){
		return fExecutedOperations;
	}

	protected List<IModelOperation> reverseOperations(){
		List<IModelOperation> reverseOperations = new ArrayList<IModelOperation>();
		for(IModelOperation operation : executedOperations()){
			reverseOperations.add(0, operation);
		}
		return reverseOperations;
	}

	public boolean modelUpdated() {
		return fModelUpdated;
	}
}
