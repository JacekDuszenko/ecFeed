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

package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.ClassUtils;

public class RuntimeMethod extends FrameworkMethod{

	IGenerator<PartitionNode> fGenerator;
	
	public RuntimeMethod(Method method, IGenerator<PartitionNode> initializedGenerator) throws RunnerException{
		super(method);
		fGenerator = initializedGenerator;
	}
	
	@Override
	public Object invokeExplosively(Object target, Object... p) throws Throwable{
		List<PartitionNode> next;
		List<Object> parameters = new ArrayList<Object>();
		try {
			URLClassLoader loader = ClassUtils.getClassLoader(false, getClass().getClassLoader());
			while((next = fGenerator.next()) !=null){
				parameters = new ArrayList<Object>();
				for (PartitionNode partitionNode : next) {
					parameters.add(ClassUtils.getPartitionValueFromString(partitionNode.getExactValueString(), partitionNode.getCategory().getType(), loader));
				}
				super.invokeExplosively(target, parameters.toArray());
			}
		} catch (GeneratorException e) {
			throw new RunnerException("Generator execution fault: " + e.getMessage());
		} catch (Throwable e){
			String message = getName() + "(" + parameters + "): " + e.getMessage();
			throw new Exception(message, e);
		}
		return null;
	}
}
