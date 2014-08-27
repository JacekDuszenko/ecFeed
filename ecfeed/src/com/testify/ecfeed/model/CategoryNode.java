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

package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryNode extends GenericNode implements IPartitionedNode{
	
	private String fType;
	protected final List<PartitionNode> fPartitions;
	private boolean fExpected;
	private PartitionNode fDefaultValue;
	
	public CategoryNode(String name, String type, String defaultValue, boolean expected) {
		super(name);
		fExpected = expected;
		fType = type;
		fPartitions = new ArrayList<PartitionNode>();
		fDefaultValue = new PartitionNode("default value" , defaultValue);
		fDefaultValue.setParent(this);
	}
	
	@Override
	public int getIndex(){
		if(getMethod() == null){
			return -1;
		}
		return getMethod().getCategories().indexOf(this);
	}

	@Override
	public String toString(){
		if(fExpected){
			return super.toString() + "(" + getDefaultValueString() + "): " + getType();
		}
		return new String(getName() + ": " + getType());
	}
	
	public void addPartitions(List<PartitionNode> partitions) {
		for(PartitionNode p : partitions){
			addPartition(p);
		}
	}

	@Override
	public void addPartition(PartitionNode partition) {
		addPartition(partition, fPartitions.size());
	}
	
	@Override
	public void addPartition(PartitionNode partition, int index) {
			fPartitions.add(index, partition);
			partition.setParent(this);
	}
	
	@Override
	public PartitionNode getPartition(String qualifiedName){
		return (PartitionNode)getChild(qualifiedName);
	}
	
	@Override
	public List<PartitionNode> getPartitions() {
		return fPartitions;
	}

	@Override
	public List<PartitionNode> getLeafPartitions(){
		List<PartitionNode> leafs = new ArrayList<PartitionNode>();
		for(PartitionNode child : fPartitions){
			leafs.addAll(child.getLeafPartitions());
		}
		return leafs;
	}
	
	@Override
	public List<String> getAllPartitionNames(){
		if(fExpected){
			return Arrays.asList(new String[]{fDefaultValue.getName()});
		}
		List<String> names = new ArrayList<String>();
		for(PartitionNode child : getPartitions()){
			names.add(child.getQualifiedName());
			names.addAll(child.getAllPartitionNames());
		}
		return names;
	}

	@Override
	public boolean removePartition(PartitionNode partition){
		if(fPartitions.contains(partition) && fPartitions.remove(partition)){
			partition.setParent(null);
			return true;
		}
		return false;
	}

	@Override
	public boolean removePartition(String qualifiedName) {
		if(fExpected){
			return false;
		}
		return removePartition(getPartition(qualifiedName));
	}

	@Override
	public List<? extends IGenericNode> getChildren(){
		return fPartitions;
	}
	
	@Override
	public CategoryNode getCopy(){
		CategoryNode category = new CategoryNode(getName(), getType(), getDefaultValueString(), isExpected());
		category.setParent(this.getParent());
		if(getDefaultValueString() != null)
			category.setDefaultValueString(getDefaultValueString());
		for(PartitionNode partition : fPartitions){
			category.addPartition(partition.getCopy());
		}
		category.setParent(getParent());
		return category;
	}
	
	@Override
	public CategoryNode getCategory() {
		return this;
	}
	
	public String getType() {
		return fType;
	}

	public void setType(String type) {
		fType = type;
	}

	public MethodNode getMethod() {
		return (MethodNode)getParent();
	}

	public List<String> getPartitionNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(PartitionNode partition : getPartitions()){
			names.add(partition.getName());
		}
		return names;
	}

	public List<String> getLeafPartitionNames(){
		List<PartitionNode> leafPartitions = getLeafPartitions();
		List<String> names = new ArrayList<String>();
		for(PartitionNode leaf : leafPartitions){
			names.add(leaf.getQualifiedName());
		}
		return names;
	}
	
	public Set<String> getLeafPartitionValues(){
		List<PartitionNode> leafPartitions = getLeafPartitions();
		Set<String> values = new HashSet<String>();
		for(PartitionNode leaf : leafPartitions){
			values.add(leaf.getValueString());
		}
		return values;
	}
	
	public Set<String> getLeafLabels(){
		Set<String> result = new HashSet<String>();
		for(PartitionNode p : getLeafPartitions()){
			result.addAll(p.getAllLabels());
		}
		return result;

//		Set<String> labels = new LinkedHashSet<String>();
//		for(PartitionNode p : getPartitions()){
//			labels.addAll(p.getAllDescendingLabels());
//		}
//		return labels;
	}
	
	public PartitionNode getDefaultValuePartition(){
		return fDefaultValue;
	}

	public String getDefaultValueString() {
		return fDefaultValue.getValueString();
	}

	public void setDefaultValueString(String value) {
		fDefaultValue.setValueString(value);
	}
	
	public boolean isExpected(){
		return fExpected;
	}
	
	public void setExpected(boolean isexpected){
		fExpected = isexpected;
	}
	
	public String toShortString(){
		if(fExpected){
			return toString();
		}

		return new String(getName() + ": " + getShortType());
	}
	
	public String getShortType(){
		String type = fType;
		int lastindex = type.lastIndexOf(".");
		if(!(lastindex == -1 || lastindex >= type.length())){
			type = type.substring(lastindex + 1);
		}
		return new String(type);
	}

	@Override
	public boolean compare(IGenericNode node){
		if(node instanceof CategoryNode == false){
			return false;
		}
		CategoryNode comparedCategory = (CategoryNode)node;
		
		if(getType().equals(comparedCategory.getType()) == false){
			return false;
		}

		if(isExpected() != comparedCategory.isExpected()){
			return false;
		}
		
		if(getDefaultValuePartition().compare(comparedCategory.getDefaultValuePartition()) == false){
			return false;
		}

		int partitionsCount = getPartitions().size();
		if(partitionsCount != comparedCategory.getPartitions().size()){
			return false;
		}

		for(int i = 0; i < partitionsCount; i++){
			if(getPartitions().get(i).compare(comparedCategory.getPartitions().get(i)) == false){
				return false;
			}
		}

		return super.compare(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public void replacePartitions(List<PartitionNode> newPartitions) {
		fPartitions.clear();
		fPartitions.addAll(newPartitions);
	}
	
	@Override 
	public int getMaxIndex(){
		if(getMethod() != null){
			return getMethod().getCategories().size();
		}
		return -1;
	}

	public List<PartitionNode> getLabeledPartitions(String label) {
		List<PartitionNode> result = new ArrayList<PartitionNode>();
		for(PartitionNode p : getPartitions()){
			if(p.getLabels().contains(label)){
				result.add(p);
				result.addAll(p.getLabeledPartitions(label));
			}
		}
		return result;
	}
}
