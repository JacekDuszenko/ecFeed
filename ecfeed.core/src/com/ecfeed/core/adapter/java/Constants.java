/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.java;


public class Constants {

	public static final int MAX_NODE_NAME_LENGTH = 64;
	public static final int MAX_PARTITION_VALUE_STRING_LENGTH = 512;

	public static final String TYPE_NAME_UNSUPPORTED = "unsupported";

	public static final String REGEX_JAVA_IDENTIFIER = "[A-Za-z_$][A-Za-z0-9_$]*";
	public static final String REGEX_ALPHANUMERIC_WITH_SPACES_64 = "[A-Za-z0-9_\\-][A-Za-z0-9_\\- ]{0,63}";	
	public static final String REGEX_ROOT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PACKAGE_NAME = "(\\.|((" + REGEX_JAVA_IDENTIFIER + ")\\.)*)";
	public static final String REGEX_CLASS_NODE_NAME = REGEX_PACKAGE_NAME + REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_METHOD_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_NODE_NAME = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_CATEGORY_TYPE_NAME = REGEX_CLASS_NODE_NAME;
	public static final String REGEX_CONSTRAINT_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_TEST_CASE_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PARTITION_NODE_NAME = REGEX_ALPHANUMERIC_WITH_SPACES_64;
	public static final String REGEX_PARTITION_LABEL = REGEX_ALPHANUMERIC_WITH_SPACES_64;

	public static final String REGEX_USER_TYPE_VALUE = REGEX_JAVA_IDENTIFIER;
	public static final String REGEX_STRING_TYPE_VALUE = "[A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]{0,1024}";
	public static final String REGEX_CHAR_TYPE_VALUE = "[A-Za-z1-9 !@#$%^&*()_+=;':,.<>/?]";

	public static final String[] JAVA_KEYWORDS = new String[]
			{ "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do",
		"if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
		"throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
		"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
		"native", "super", "while", "null", "true", "false" };

	public static final String VALUE_REPRESENTATION_NULL = "/null";
	public static final String VALUE_REPRESENTATION_POSITIVE_INF = "POSITIVE_INFINITY";
	public static final String VALUE_REPRESENTATION_NEGATIVE_INF = "NEGATIVE_INFINITY";

}
