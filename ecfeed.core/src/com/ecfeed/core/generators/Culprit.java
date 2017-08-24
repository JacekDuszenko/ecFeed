/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.generators.algorithms.DimensionedString;

public class Culprit {

	private static class OtherData {
		public int fFailureIndex = 0;
	}

	private List<DimensionedString> fTestInput;
	private int fOccurenceCount = 0;	
	private int fFailureCount = 0;
	private OtherData fOtherData;


	public Culprit() {
		this(new ArrayList<DimensionedString>(), 0, 0);		
	}

	public Culprit(List<DimensionedString> testInput) {
		this(testInput, 0, 0);		
	}

	public Culprit(List<DimensionedString> testInput, int occurenceCount, int failureCount) {
		fTestInput = testInput;
		fOccurenceCount = occurenceCount;
		fFailureCount = failureCount;
		fOtherData = new OtherData();
	}

	public Culprit(DimensionedString[] testInput) {
		this(testInput, 0, 0);		
	}

	public Culprit(DimensionedString[] testInput, int occurenceCount, int failureCount) {

		List<DimensionedString> testInputList = new ArrayList<DimensionedString>();

		for (DimensionedString DimensionedString : testInput) {
			testInputList.add(DimensionedString);
		}

		fTestInput = testInputList;
		fOccurenceCount = occurenceCount;
		fFailureCount = failureCount;
	}	

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("[occurences:");
		sb.append(fOccurenceCount);
		sb.append(", fails:");
		sb.append(fFailureCount);
		sb.append(", failureIndex:");
		sb.append(fOtherData.fFailureIndex);
		sb.append(", items:[");

		boolean firstTime = true;

		for (DimensionedString DimensionedString : fTestInput) {

			if (!firstTime) {
				sb.append(", ");
			}

			sb.append(DimensionedString.toString());
			firstTime = false;
		}

		sb.append("]");
		sb.append("]");

		return sb.toString();
	}

	public static int compareForSort(Culprit culprit1, Culprit culprit2) {

		if (culprit1.fOtherData.fFailureIndex > culprit2.fOtherData.fFailureIndex) {
			return -1;
		}

		if (culprit1.fOtherData.fFailureIndex < culprit2.fOtherData.fFailureIndex) {
			return 1;
		}	        

		if (culprit1.fTestInput.size() > culprit2.fTestInput.size()) {
			return -1;
		}

		if (culprit1.fTestInput.size() < culprit2.fTestInput.size()) {
			return 1;
		}    	

		int compareResult;
		int size = culprit1.fTestInput.size();

		for (int index = 0; index < size; index++) {

			DimensionedString DimensionedString1 = culprit1.fTestInput.get(index);
			DimensionedString DimensionedString2 = culprit2.fTestInput.get(index);

			compareResult = DimensionedString.compareForSort(DimensionedString1, DimensionedString2);

			if (compareResult != 0) {
				return compareResult;
			}
		}

		return 0;
	}

	public int getOccurenceCount() {
		return fOccurenceCount;
	}

	public int getFailureCount() {
		return fFailureCount;
	}

	public int getFailureIndex() {
		return fOtherData.fFailureIndex;
	}

	public void setFailureIndex(int index) {
		fOtherData.fFailureIndex = index;
	}	

	public List<DimensionedString> getItem() {
		return fTestInput;
	}

	public void incrementFailures(int failureCount) {
		fFailureCount += failureCount;
	}

	public void incrementOccurenceCount(int occurenceCount) {
		fOccurenceCount += occurenceCount;
	}

	public void aggregateOccurencesAndFailures(Culprit culpritToAggregate) {

		incrementOccurenceCount(culpritToAggregate.getOccurenceCount());
		incrementFailures(culpritToAggregate.getFailureCount());
	}

	public boolean isTupleMatch(Culprit other) {

		if(fTestInput.size() != other.fTestInput.size()) {
			return false;
		}

		for(int index = 0; index < fTestInput.size(); index++) {
			if(!fTestInput.get(index).equals(other.fTestInput.get(index))) {
				return false;
			}
		}
		return true;
	}

	public boolean isBasicMatch(Culprit other) {

		if (other.fOccurenceCount != fOccurenceCount) {
			return false;
		}

		if (other.fFailureCount != fFailureCount) {
			return false;
		}

		if (!isTupleMatch(other)) {
			return false;
		}

		return true;
	}

	public Culprit makeClone() {

		return new Culprit(fTestInput, fOccurenceCount, fFailureCount);
	}

}
