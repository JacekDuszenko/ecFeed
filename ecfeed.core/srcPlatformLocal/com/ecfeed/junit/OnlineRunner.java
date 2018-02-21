/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.runner.Messages;
import com.ecfeed.core.runner.RunnerException;

public class OnlineRunner extends AbstractOnlineRunner {

	public OnlineRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	protected void addFrameworkMethods(
			FrameworkMethod frameworkMethod,
			IGenerator<ChoiceNode> initializedGenerator,
			List<FrameworkMethod> inOutFrameworkMethods) throws RunnerException {

		List<ChoiceNode> listOfChoices = new ArrayList<>();

		for (;;) {

			try {
				listOfChoices = initializedGenerator.next();

			} catch (Exception e){
				RunnerException.report(Messages.RUNNER_EXCEPTION(e.getMessage()));
			}

			if (listOfChoices == null) {
				break;
			}

			Collection<TestCaseNode> listWithOneTestCase = new ArrayList<TestCaseNode>();
			listWithOneTestCase.add(new TestCaseNode(new String(), listOfChoices));

			inOutFrameworkMethods.add(
					new StaticRunnerMethod(
							frameworkMethod.getMethod(), 
							listWithOneTestCase, 
							getLoader()));
		}

	}

}
