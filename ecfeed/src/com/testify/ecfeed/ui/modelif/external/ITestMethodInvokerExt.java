/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif.external;


public interface ITestMethodInvokerExt extends ITestMethodInvoker{

	public final String INTERFACE_NAME = "IMPLEMENTER";
	public final String INTERFACE_VERSION = "1.0";
	
	void initialize(String runner); 
}
