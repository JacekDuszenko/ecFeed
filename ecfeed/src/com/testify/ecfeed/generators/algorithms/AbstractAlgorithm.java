/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class AbstractAlgorithm<E> implements IAlgorithm<E> {

	private int fTotalWork;
	private int fProgress;
	private int fTotalProgress;

	private List<List<E>> fInput;
	private Collection<IConstraint<E>> fConstraints;
	
	@Override
	public int totalWork() {
		return fTotalWork;
	}

	@Override
	public int workProgress() {
		int progress = fProgress;
		fProgress = 0;
		return progress;
	}
	
	@Override
	public int totalProgress(){
		return fTotalProgress;
	}

	
	protected void progress(int progress){
		fProgress += progress;
		fTotalProgress += progress;
	}
	
	protected void setTotalWork(int totalWork){
		fTotalWork = totalWork;
	}
	
	public void reset(){
		fProgress = 0;
	}
	
	protected List<E> instance(List<Integer> vector) {
		List<E> instance = new ArrayList<E>();
		for(int i = 0; i < vector.size(); i++){
			instance.add(fInput.get(i).get(vector.get(i)));
		}
		return instance;
	}
	
	protected List<Integer> representation(List<E> vector){
		if(vector == null) return null;
		List<Integer> representation = new ArrayList<Integer>();
		for(int i = 0; i < vector.size(); i++){
			E element = vector.get(i);
			representation.add(fInput.get(i).indexOf(element));
		}
		return representation;
	}

	protected boolean checkConstraints(List<E> vector) {
			if (vector == null) return true;
			for(IConstraint<E> constraint : fConstraints){
				if(constraint.evaluate(vector) == false){
					return false;
				}
			}
			return true;
	}

	@Override
	public void initialize(List<List<E>> input,
			Collection<IConstraint<E>> constraints)
			throws GeneratorException {
		if(input == null || constraints == null){
			throw new GeneratorException("input or constraints of algorithm cannot be null");
		}
		fInput = input;
		fConstraints = constraints;
		reset();
	}

	@Override
	public List<E> getNext() throws GeneratorException {
		return null;
	}
	
	public List<List<E>> getInput(){
		return fInput;
	}

	@Override
	public void addConstraint(IConstraint<E> constraint) {
		fConstraints.add(constraint);
	}

	@Override
	public void removeConstraint(IConstraint<E> constraint) {
		fConstraints.remove(constraint);
	}

	@Override
	public Collection<? extends IConstraint<E>> getConstraints() {
		return fConstraints;
	}

}
