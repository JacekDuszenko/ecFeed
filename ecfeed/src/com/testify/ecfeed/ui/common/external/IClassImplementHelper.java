/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

public interface IClassImplementHelper {

	boolean classImplemented(String thePackage, String classNameWithoutExtension);
	boolean classImplemented(String thePackage, String classNameWithoutExtension, String superclassName);
	void implementClass(String thePackage,String classNameWithoutExtension,String content);
}
