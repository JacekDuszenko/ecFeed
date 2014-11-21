package com.testify.ecfeed.adapter.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.ModelOperationManager;
import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;
import com.testify.ecfeed.model.ParametersParentNode;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.testutils.ENodeType;

@RunWith(OnlineRunner.class)
@Generator(CartesianProductGenerator.class)
@EcModel("test/com.testify.ecfeed.adapter.operations.ect")
@Constraints(Constraints.ALL)
public class GenericOperationAddParameterTest{

	@Test
	public void executeTest(ENodeType parentType, EIndexValue indexValue, boolean nameExists, boolean success){

//		parentType = ENodeType.METHOD;
//		indexValue = EIndexValue.NO_VALUE;
//		nameExists = false;
//		success = true;
//
		ParametersParentNode parent = getParent(parentType);
		parent.addParameter(new ParameterNode("arg1", "int", "0", false));
		parent.addParameter(new ParameterNode("arg2", "int", "0", false));
		parent.addParameter(new ParameterNode("arg3", "int", "0", false));
		parent.addParameter(new ParameterNode("arg4", "int", "0", false));
		parent.addParameter(new ParameterNode("arg5", "int", "0", false));

		int index = getIndex(indexValue, parent);

		String name = nameExists?"arg1":"newArg";

		ParameterNode parameter = new ParameterNode(name, "int", "0", false);

		IModelOperation operation;
		if(indexValue == EIndexValue.NO_VALUE){
			operation = new GenericOperationAddParameter(parent, parameter);
		}
		else{
			operation = new GenericOperationAddParameter(parent, parameter, index);
		}

		try{
			operation.execute();
			if(success == false){
				fail("Exception expected");
			}
			assertEquals(index, parent.getParameters().indexOf(parameter));
		}catch(Exception e){
			if(success){
				fail("Unexpected exception");
			}
		}
	}

	@Test
	public void undoRedoTest(ENodeType parentType, EIndexValue indexValue){
		ParametersParentNode parent = getParent(parentType);
		parent.addParameter(new ParameterNode("arg1", "int", "0", false));
		parent.addParameter(new ParameterNode("arg2", "int", "0", false));
		parent.addParameter(new ParameterNode("arg3", "int", "0", false));
		parent.addParameter(new ParameterNode("arg4", "int", "0", false));
		parent.addParameter(new ParameterNode("arg5", "int", "0", false));
		int index = getIndex(indexValue, parent);

		ModelOperationManager opManager = new ModelOperationManager();
		IModelOperation operation;

		ParameterNode parameter = new ParameterNode("arg", "int", "0", false);

		if(indexValue != EIndexValue.NO_VALUE){
			operation = new GenericOperationAddParameter(parent, parameter, index);
		}
		else{
			operation = new GenericOperationAddParameter(parent, parameter);
		}
		try{
			opManager.execute(operation);
			assertTrue(parent.getParameters().contains(parameter));
			assertTrue(parent.getParameters().indexOf(parameter) == index);
			assertTrue(opManager.undoEnabled());
			opManager.undo();
			assertFalse(parent.getParameters().contains(parameter));
			assertTrue(opManager.redoEnabled());
			opManager.redo();
			assertTrue(parent.getParameters().contains(parameter));
			assertTrue(parent.getParameters().indexOf(parameter) == index);
			assertTrue(opManager.undoEnabled());
		}
		catch(ModelOperationException e){
			fail("Unexpected exception:" + e.getMessage());
		}
	}

	@Test
	public void randomSeriesTest(ENodeType parentType){
		Random rand = new Random();
		int numOfOperations = 10;
		ParametersParentNode parent = getParent(parentType);
		ModelOperationManager operationManager = new ModelOperationManager();
		List<ParameterNode> parameters = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		try{
			for(int i = 0; i < numOfOperations; ++i){
				int index = rand.nextInt(parent.getParameters().size() + 1);
				ParameterNode parameter = new ParameterNode("arg" + i, "int", "0", rand.nextBoolean());
				IModelOperation operation = new GenericOperationAddParameter(parent, parameter, index);
				operationManager.execute(operation);
				assertTrue(parent.getParameters().contains(parameter));
				assertTrue(parent.getParameters().indexOf(parameter) == index);
				parameters.add(parameter);
				indices.add(index);
			}
			assertTrue(parent.getParameters().size() == numOfOperations);
			for(int i = numOfOperations - 1; i >= 0; --i){
				assertTrue(operationManager.undoEnabled());
				operationManager.undo();
				assertFalse(parent.getParameters().contains(parameters.get(i)));
			}
			for(int i = 0; i < numOfOperations; ++i){
				assertTrue(operationManager.redoEnabled());
				operationManager.redo();
				assertTrue(parent.getParameters().contains(parameters.get(i)));
				assertTrue(parent.getParameters().indexOf(parameters.get(i)) == indices.get(i));
			}
		}catch(ModelOperationException e){
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	private ParametersParentNode getParent(ENodeType parentType) {
		switch(parentType){
		case PROJECT: return new RootNode("Project");
		case CLASS: return new RootNode("Class");
		case METHOD: return new RootNode("method");
		default: return null;
		}
	}

	private int getIndex(EIndexValue indexValue, ParametersParentNode parent) {
		int index = 0;
		switch(indexValue){
		case BEYOND_LENGTH:
			index = parent.getParameters().size() + 10;
			break;
		case MINUS_ONE:
			index = parent.getParameters().size();
			break;
		case NEGATIVE:
			index = -10;
			break;
		case NO_VALUE:
			index = parent.getParameters().size();
			break;
		case ONE:
			index = 1;
			break;
		case SIZE:
			index = parent.getParameters().size() - 1;
			break;
		case SIZE_PLUS_ONE:
			index = parent.getParameters().size();
			break;
		case VALID_POSITIVE:
			index = 1;
			break;
		case ZERO:
			index = 0;
			break;
		default:
			break;
		}
		return index;
	}
}
