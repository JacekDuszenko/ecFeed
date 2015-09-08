/*******************************************************************************
 * Copyright (c) 2015 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.external;

import com.testify.ecfeed.ui.common.external.IFileInfoProvider;

public interface IImplementerExt extends IImplementer{

	void initialize(String baseRunner, IFileInfoProvider fFileInfoProvider) throws RuntimeException;
}
