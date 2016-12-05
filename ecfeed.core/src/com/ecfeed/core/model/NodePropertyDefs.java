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

import com.ecfeed.core.utils.JavaTypeHelper;


public class NodePropertyDefs {

	private static final String JAVA_RUNNER = "Java Runner";
	private static final String WEB_DRIVER = "Web Runner";

	private static final String FALSE_VALUE = "false";
	private static final String TRUE_VALUE = "true";

	private static final String BROWSER_CHROME = "Chrome";
	private static final String BROWSER_FIREFOX = "Firefox";
	private static final String BROWSER_IEXPLORER = "IExplorer";
	private static final String BROWSER_OPERA = "Opera";
	private static final String BROWSER_SAFARI = "Safari";

	private static final String ACTION_SEND_KEYS = "Send keys";
	private static final String ACTION_CLICK = "Click";
	private static final String ACTION_SUBMIT = "Submit";

	private static final String EMPTY_STR = "";
	private static final String UNMAPPED = "Unmapped";


	private static String[] falseTrueArray = new String[]{FALSE_VALUE, TRUE_VALUE};

	static NodePropertyDef runOnAndroid = 
			new NodePropertyDef("runOnAndroid", JavaTypeHelper.TYPE_NAME_BOOLEAN, FALSE_VALUE, falseTrueArray);

	static NodePropertyDef androidRunner = 
			new NodePropertyDef("androidRunner", JavaTypeHelper.TYPE_NAME_STRING, null, null);	

	static NodePropertyDef methodRunner = 
			new NodePropertyDef(
					"methodRunner", JavaTypeHelper.TYPE_NAME_STRING, JAVA_RUNNER,
					new String[]{JAVA_RUNNER, WEB_DRIVER});

	static NodePropertyDef mapBrowserToParam =  
			new NodePropertyDef("mapBrowserToParam", JavaTypeHelper.TYPE_NAME_BOOLEAN, FALSE_VALUE, falseTrueArray);

	static NodePropertyDef webBrowser = 
			new NodePropertyDef(
					"webBrowser", JavaTypeHelper.TYPE_NAME_STRING, BROWSER_CHROME, 
					new String[]{BROWSER_CHROME, BROWSER_FIREFOX, BROWSER_IEXPLORER, BROWSER_OPERA, BROWSER_SAFARI});

	static NodePropertyDef browserDriver = new NodePropertyDef("browserDriver", JavaTypeHelper.TYPE_NAME_STRING, null, null);	

	static NodePropertyDef mapStartUrlToParam = 
			new NodePropertyDef("mapStartUrlToParam", JavaTypeHelper.TYPE_NAME_BOOLEAN, FALSE_VALUE, falseTrueArray);

	static NodePropertyDef startUrl = new NodePropertyDef("startUrl", JavaTypeHelper.TYPE_NAME_STRING, null, null);

	static NodePropertyDef findByTypeOfElement = 
			new NodePropertyDef(
					"findByTypeOfElement", JavaTypeHelper.TYPE_NAME_STRING, UNMAPPED,
					new String[]{ UNMAPPED, "Id", "Class name", "Tag name", "Name", "Link text", "Partial link text", "CSS selector", "Xpath" });

	static NodePropertyDef findByValueOfElement = new NodePropertyDef("findByValueOfElement", JavaTypeHelper.TYPE_NAME_STRING, null, null);

	static NodePropertyDef action = 
			new NodePropertyDef(
					"action", JavaTypeHelper.TYPE_NAME_STRING, UNMAPPED,
					new String[]{ UNMAPPED, ACTION_SEND_KEYS, ACTION_CLICK, ACTION_SUBMIT });


	public enum PropertyId {
		PROPERTY_RUN_ON_ANDROID(0),
		PROPERTY_ANDROID_RUNNER(1),

		PROPERTY_METHOD_RUNNER(2),
		PROPERTY_MAP_BROWSER_TO_PARAM(3),
		PROPERTY_WEB_BROWSER(4),
		PROPERTY_BROWSER_DRIVER(5),
		PROPERTY_MAP_START_URL_TO_PARAM(6),
		PROPERTY_START_URL(7),

		PROPERTY_WEB_ELEMENT_TYPE(8),
		PROPERTY_FIND_BY_TYPE_OF_ELEMENT(9),
		PROPERTY_FIND_BY_VALUE_OF_ELEMENT(10),
		PROPERTY_ACTION(11);

		private final int fIndex; 

		private PropertyId(int index) {
			fIndex = index;
		}

		private final int getIndex() {
			return fIndex;
		}
	}

	static NodePropertyDef[] fPropertyDefs = 
		{
		runOnAndroid,
		androidRunner,
		methodRunner,
		mapBrowserToParam,
		webBrowser,
		browserDriver,
		mapStartUrlToParam,
		startUrl,
		NodePropertyDefElemType.parameterType,
		findByTypeOfElement,
		findByValueOfElement,
		action
		};

	public static String getPropertyName(PropertyId propertyId) {
		return getDefinition(propertyId).getName();
	}

	public static String getPropertyType(PropertyId propertyId) {
		return getDefinition(propertyId).getType();
	}	

	public static String getPropertyDefaultValue(PropertyId propertyId, String parentValue) {
		if (propertyId == NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE) {
			return NodePropertyDefElemType.getDefaultValue(parentValue);
		}

		return getDefinition(propertyId).getDefaultValue();
	}	

	public static String[] getPossibleValues(PropertyId propertyId, String parentValue) {
		if (propertyId == NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE) {
			return NodePropertyDefElemType.getPossibleValues(parentValue);
		}

		return getDefinition(propertyId).getPossibleValues();
	}	

	public static boolean isOneOfPossibleValues(String value, PropertyId propertyId, String parentValue) {
		String[] possibleValues = getPossibleValues(propertyId, parentValue);
		NodePropertyValueSet valueSet = new NodePropertyValueSet(null, possibleValues);
		return valueSet.isOneOfPossibleValues(value);
	}

	private static NodePropertyDef getDefinition(PropertyId propertyId) {
		return fPropertyDefs[propertyId.getIndex()];
	}

	public static boolean isSeleniumRunnerMethod(String value) {
		if (value.equals(WEB_DRIVER)) {
			return true;
		}
		return false;
	}

	public static boolean isJavaRunnerMethod(String value) {
		if (value.equals(JAVA_RUNNER)) {
			return true;
		}
		return false;
	}	

	public static String browserNameChrome() {
		return BROWSER_CHROME;
	}

	public static String browserNameFirefox() {
		return BROWSER_FIREFOX;
	}

	public static String getEmptyElement() {
		return EMPTY_STR;
	}

	public static String browserNameIExplorer() {
		return BROWSER_IEXPLORER;
	}

	public static String browserNameOpera() {
		return BROWSER_OPERA;
	}	

	public static String browserNameSafari() {
		return BROWSER_SAFARI;
	}

	public static boolean isValidBrowser(String browserName) {
		if (webBrowser.matchesPossibleValue(browserName)) {
			return true;
		}
		return false;
	}

	public static boolean isActionSendKeys(String value) {
		if (value.equals(ACTION_SEND_KEYS)) {
			return true;
		}
		return false;
	}	

	public static boolean isActionClick(String value) {
		if (value.equals(ACTION_CLICK)) {
			return true;
		}
		return false;
	}

	public static boolean isActionSubmit(String value) {
		if (value.equals(ACTION_SUBMIT)) {
			return true;
		}
		return false;
	}

	public static boolean isFindByAvailable(String webElementType) {
		if (NodePropertyDefElemType.isChildElementAvailable(webElementType)) {
			return true;
		}
		return false;
	}

	public static boolean isActionAvailable(String webElementType) {
		if (!NodePropertyDefElemType.isActionAvailable(webElementType)) {
			return false;
		}
		return true;
	}	

}
