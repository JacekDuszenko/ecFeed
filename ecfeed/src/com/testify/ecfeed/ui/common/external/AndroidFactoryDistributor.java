/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.external;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.PluginVersionExceptionReporter;
import com.testify.ecfeed.utils.ExceptionHelper;
import com.testify.ecfeed.utils.SystemLogger;

public class AndroidFactoryDistributor {

	public static IAndroidFactoryExt getFactory() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		final String ID = "com.testify.ecfeed.extensionpoint.definition.androidfactory";

		IConfigurationElement[] config =
				registry.getConfigurationElementsFor(ID);		

		try {
			for (IConfigurationElement element : config) {
				final Object obj = element.createExecutableExtension("class");

				if (obj instanceof IAndroidFactoryExt) {
					return (IAndroidFactoryExt)obj;
				}
			}
		} catch (CoreException e) {
			SystemLogger.logCatch(e.getMessage());
			PluginVersionExceptionReporter.reportRuntimeException(
					e.getMessage(), IAndroidFactoryExt.INTERFACE_NAME, IAndroidFactoryExt.INTERFACE_VERSION);
		}	

		ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_EXTERNAL_DEVICE_CHECKER_NOT_FOUND);
		return null;
	}
}