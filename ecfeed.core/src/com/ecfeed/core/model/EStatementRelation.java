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

public enum EStatementRelation{

	EQUAL("="), 
	NOT("\u2260");

	String fValue;

	public static final String RELATION_EQUAL = "=";
	public static final String RELATION_NOT = "\u2260";


	private EStatementRelation(String value) {
		fValue = value;
	}

	public String toString() {
		return fValue; 
	}

	public static EStatementRelation getRelation(String text) {

		switch(text)
		{
		case RELATION_EQUAL:
			return EQUAL;
		case RELATION_NOT:
			return NOT;
		}
		return NOT;
	}
}
