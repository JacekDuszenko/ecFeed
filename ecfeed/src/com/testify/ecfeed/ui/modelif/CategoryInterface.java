package com.testify.ecfeed.ui.modelif;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ITypeAdapterProvider;
import com.testify.ecfeed.modeladp.java.JavaUtils;
import com.testify.ecfeed.modeladp.operations.CategoryOperationSetDefaultValue;
import com.testify.ecfeed.modeladp.operations.CategoryOperationSetExpected;
import com.testify.ecfeed.modeladp.operations.CategoryOperationSetType;
import com.testify.ecfeed.modeladp.operations.FactoryRenameOperation;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TypeAdapterProvider;

public class CategoryInterface extends PartitionedNodeInterface {
	
	private CategoryNode fTarget;
	private ITypeAdapterProvider fAdapterProvider;
	
	public CategoryInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fAdapterProvider = new TypeAdapterProvider();
	}

	public void setTarget(CategoryNode target){
		super.setTarget(target);
		fTarget = target;
	}

	public String getDefaultValue(String type) {
		return new EclipseModelBuilder().getDefaultExpectedValue(type);
	}

	public boolean setName(String newName) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(FactoryRenameOperation.getRenameOperation(fTarget, newName), Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE);
	}

	public boolean setType(String newType) {
		if(newType.equals(fTarget.getType())){
			return false;
		}
		return execute(new CategoryOperationSetType(fTarget, newType, fAdapterProvider), Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE);
	}
	
	public boolean setExpected(boolean expected){
		if(expected != fTarget.isExpected()){
			MethodNode method = fTarget.getMethod();
			if(method != null){
				boolean testCases = method.getTestCases().size() > 0;
				boolean constraints = method.mentioningConstraints(fTarget).size() > 0;
				if(testCases || constraints){
					String message = "";
					if(testCases){
						if(expected){
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_ALTERED + "\n";
						}
						else{
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_REMOVED + "\n";
						}
					}
					if(constraints){
						message += Messages.DIALOG_SET_CATEGORY_EXPECTED_CONSTRAINTS_REMOVED;
					}
					if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
							Messages.DIALOG_SET_CATEGORY_EXPECTED_WARNING_TITLE, message) == false){
						return false;
					}
				}
			}
			return execute(new CategoryOperationSetExpected(fTarget, expected), Messages.DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean setDefaultValue(String valueString) {
		if(fTarget.getDefaultValue().equals(valueString) == false){
			IModelOperation operation = new CategoryOperationSetDefaultValue(fTarget, valueString, fAdapterProvider.getAdapter(fTarget.getType()));
			return execute(operation, Messages.DIALOG_SET_DEFAULT_VALUE_PROBLEM_TITLE);
		}
		return false;
	}

	public static boolean hasLimitedValuesSet(String type) {
		return !isPrimitive(type) || isBoolean(type);
	}

	public static boolean hasLimitedValuesSet(CategoryNode category) {
		return hasLimitedValuesSet(category.getType());
	}

	public static boolean isPrimitive(String type) {
		return Arrays.asList(JavaUtils.supportedPrimitiveTypes()).contains(type);
	}
	
	public static boolean isUserType(String type) {
		return !isPrimitive(type);
	}
	
	public static boolean isBoolean(String type){
		return type.equals(JavaUtils.getBooleanTypeName());
	}

	public static List<String> getSpecialValues(String type) {
		return new EclipseModelBuilder().getSpecialValues(type);
	}

	public String getType() {
		return fTarget.getType();
	}

	public static String[] supportedPrimitiveTypes() {
		return JavaUtils.supportedPrimitiveTypes();
	}

	public String getDefaultValue() {
		return fTarget.getDefaultValue();
	}

	public boolean isExpected() {
		return fTarget.isExpected();
	}
	
	public String[] defaultValueSuggestions(){
		Set<String> items = new HashSet<String>(getSpecialValues());
		if(JavaUtils.isPrimitive(getType()) == false){
			for(PartitionNode p : fTarget.getLeafPartitions()){
				items.add(p.getValueString());
			}
			if(items.contains(fTarget.getDefaultValue())== false){
				items.add(fTarget.getDefaultValue());
			}
		}
		return items.toArray(new String[]{});
	}
}
