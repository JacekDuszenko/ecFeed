package com.testify.ecfeed.test.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.runner.EcFeeder;
import com.testify.ecfeed.runner.annotations.EcModel;
import com.testify.ecfeed.runner.annotations.TestSuites;

@RunWith(EcFeeder.class)
@EcModel("test/com/testify/ecfeed/test/runner/ecModel.ect")
public class EcFeederTest{
	public String string;

	@TestSuites
	public static String[] testSuites(){
		return new String[]{"default suite", "other suite"};
	}
	
	@Test
	public void exampleTestFunction(int intParameter, String stringParameter){
		System.out.println("exampleTestFunction(" + intParameter + ", " + stringParameter + ")");
	}

}
