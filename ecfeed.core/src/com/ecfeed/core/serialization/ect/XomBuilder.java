/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization.ect;

import static com.ecfeed.core.serialization.ect.Constants.ANDROID_RUNNER_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.BASIC_COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CLASS_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_CONSEQUENCE_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_PREMISE_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.EXPECTED_PARAMETER_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.LABEL_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.LABEL_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.METHOD_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.NODE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.Constants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PROPERTIES_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PROPERTY_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.PROPERTY_ATTRIBUTE_TYPE;
import static com.ecfeed.core.serialization.ect.Constants.PROPERTY_ATTRIBUTE_VALUE;
import static com.ecfeed.core.serialization.ect.Constants.PROPERTY_TAG_NAME;
import static com.ecfeed.core.serialization.ect.Constants.ROOT_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.RUN_ON_ANDROID_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.STATEMENT_LABEL_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE;
import static com.ecfeed.core.serialization.ect.Constants.STATEMENT_OPERATOR_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE;
import static com.ecfeed.core.serialization.ect.Constants.STATEMENT_RELATION_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.STATEMENT_STATIC_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.STATIC_STATEMENT_FALSE_VALUE;
import static com.ecfeed.core.serialization.ect.Constants.STATIC_STATEMENT_TRUE_VALUE;
import static com.ecfeed.core.serialization.ect.Constants.TEST_CASE_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.TEST_PARAMETER_NODE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.TEST_SUITE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.Constants.TYPE_COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.serialization.ect.Constants.TYPE_NAME_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.Constants.VALUE_ATTRIBUTE;
import static com.ecfeed.core.serialization.ect.Constants.VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.serialization.ect.Constants.VERSION_ATTRIBUTE;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.ChoicesParentStatement.ChoiceCondition;
import com.ecfeed.core.model.ChoicesParentStatement.ICondition;
import com.ecfeed.core.model.ChoicesParentStatement.LabelCondition;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.IStatementVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.NodePropertyDescriptions;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.serialization.WhiteCharConverter;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.StringHelper;

public abstract class XomBuilder implements IModelVisitor, IStatementVisitor {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract String getParameterNodeName();
	protected abstract String getStatementParameterAttributeName();
	protected abstract String getChoiceNodeName();
	protected abstract String getChoiceAttributeName();
	protected abstract String getStatementChoiceAttributeName();
	protected abstract int getModelVersion();

	@Override
	public Object visit(RootNode node) throws Exception{
		Element element = createAbstractElement(ROOT_NODE_NAME, node);

		String versionStr = Integer.toString(node.getModelVersion());
		Attribute versionAttr = new Attribute(VERSION_ATTRIBUTE, versionStr);
		element.addAttribute(versionAttr);

		for(ClassNode classNode : node.getClasses()){
			element.appendChild((Element)visit(classNode));
		}

		for(GlobalParameterNode parameter : node.getGlobalParameters()){
			element.appendChild((Element)visit(parameter));
		}

		return element;
	}

	@Override
	public Object visit(ClassNode classNode) throws Exception {
		Element element = createAbstractElement(CLASS_NODE_NAME, classNode);

		addAndroidValues(classNode, element);

		for(MethodNode method : classNode.getMethods()){
			element.appendChild((Element)visit(method));
		}

		for(GlobalParameterNode parameter : classNode.getGlobalParameters()){
			element.appendChild((Element)visit(parameter));
		}

		return element;
	}

	private void addAndroidValues(ClassNode classNode, Element classElement) {

		if (ModelVersionDistributor.isAndroidAttributeInTheClass(getModelVersion())) {
			addAndroidValuesAsAttributes(classNode, classElement);
		} else {
			addAndroidValuesAsProperties(classNode, classElement);
		}
	}

	private void addAndroidValuesAsAttributes(ClassNode classNode, Element classElement) {
		boolean runOnAndroid = classNode.getRunOnAndroid();

		classElement.addAttribute(
				new Attribute(
						RUN_ON_ANDROID_ATTRIBUTE_NAME,  
						Boolean.toString(runOnAndroid)));

		String androidBaseRunner = classNode.getAndroidRunner();

		if (!runOnAndroid && StringHelper.isNullOrEmpty(androidBaseRunner)) {
			return;
		}

		if (androidBaseRunner == null) {
			androidBaseRunner = "";
		}

		classElement.addAttribute(new Attribute(ANDROID_RUNNER_ATTRIBUTE_NAME, androidBaseRunner));
	}

	private void addAndroidValuesAsProperties(ClassNode classNode, Element targetElement) {
		boolean runOnAndroid = classNode.getRunOnAndroid();

		appendProperty(
				getPropertyName(NodePropertyDescriptions.PropertyId.RUN_ON_ANDROID),
				getPropertyType(NodePropertyDescriptions.PropertyId.RUN_ON_ANDROID), 
				BooleanHelper.toString(runOnAndroid), targetElement);

		String androidBaseRunner = classNode.getAndroidRunner();
		if (androidBaseRunner == null) {
			return;
		}

		appendProperty(
				getPropertyName(NodePropertyDescriptions.PropertyId.ANDROID_RUNNER), 
				getPropertyType(NodePropertyDescriptions.PropertyId.ANDROID_RUNNER),  
				androidBaseRunner, targetElement);
	}

	private String getPropertyName(NodePropertyDescriptions.PropertyId propertyId) {
		return NodePropertyDescriptions.getPropertyName(propertyId);
	}

	private String getPropertyType(NodePropertyDescriptions.PropertyId propertyId) {
		return NodePropertyDescriptions.getPropertyType(propertyId);
	}	

	private void appendProperty(String key, String type, String value, Element targetElement) {
		Element propertiesBlock = getPropertiesBlock(targetElement);
		Element propertyElement = createCommonPropertyElement(key, type, value);
		propertiesBlock.appendChild(propertyElement);
	}

	private Element getPropertiesBlock(Element parentElement) {
		Elements propiertiesBlocks = parentElement.getChildElements(PROPERTIES_BLOCK_TAG_NAME);

		if (propiertiesBlocks.size() == 0) {
			Element propertiesBlock = new Element(PROPERTIES_BLOCK_TAG_NAME);
			parentElement.appendChild(propertiesBlock);
			return propertiesBlock;
		}

		return propiertiesBlocks.get(0);
	}

	private Element createCommonPropertyElement(String name, String type, String value) {
		Element propertyElement = new Element(PROPERTY_TAG_NAME);

		Attribute attributeName = new Attribute(PROPERTY_ATTRIBUTE_NAME, name);
		propertyElement.addAttribute(attributeName);

		Attribute attributeType = new Attribute(PROPERTY_ATTRIBUTE_TYPE, type);
		propertyElement.addAttribute(attributeType);

		Attribute attributeValue = new Attribute(PROPERTY_ATTRIBUTE_VALUE, value);
		propertyElement.addAttribute(attributeValue);

		return propertyElement;
	}



	@Override
	public Object visit(MethodNode node) throws Exception {
		Element element = createAbstractElement(METHOD_NODE_NAME, node);

		for(MethodParameterNode parameter : node.getMethodParameters()){
			element.appendChild((Element)parameter.accept(this));
		}

		for(ConstraintNode constraint : node.getConstraintNodes()){
			element.appendChild((Element)constraint.accept(this));
		}

		for(TestCaseNode testCase : node.getTestCases()){
			element.appendChild((Element)testCase.accept(this));
		}

		return element;
	}

	@Override
	public Object visit(MethodParameterNode node)  throws Exception {
		Element element = createAbstractElement(getParameterNodeName(), node);

		appendTypeComments(element, node);

		encodeAndAddAttribute(element, new Attribute(TYPE_NAME_ATTRIBUTE, node.getRealType()));

		encodeAndAddAttribute(element, new Attribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, Boolean.toString(node.isExpected())));

		encodeAndAddAttribute(element, new Attribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, node.getDefaultValueForSerialization()));
		encodeAndAddAttribute(element, new Attribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME, Boolean.toString(node.isLinked())));

		if(node.getLink() != null){
			encodeAndAddAttribute(element, new Attribute(PARAMETER_LINK_ATTRIBUTE_NAME, node.getLink().getQualifiedName()));
		}

		for(ChoiceNode child : node.getRealChoices()){
			element.appendChild((Element)child.accept(this));
		}

		return element;
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {
		Element element = createAbstractElement(getParameterNodeName(), node);
		appendTypeComments(element, node);
		encodeAndAddAttribute(element, new Attribute(TYPE_NAME_ATTRIBUTE, node.getType()));

		for(ChoiceNode child : node.getChoices()){
			element.appendChild((Element)child.accept(this));
		}
		return element;
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		Element element = new Element(TEST_CASE_NODE_NAME);
		encodeAndAddAttribute(element, new Attribute(TEST_SUITE_NAME_ATTRIBUTE, node.getName()));
		appendComments(element, node);

		for(ChoiceNode testParameter : node.getTestData()){
			if(testParameter.getParameter() != null && node.getMethodParameter(testParameter).isExpected()){
				Element expectedParameterElement = new Element(EXPECTED_PARAMETER_NODE_NAME);
				Attribute expectedValueAttribute = new Attribute(VALUE_ATTRIBUTE_NAME, testParameter.getValueString());
				encodeAndAddAttribute(expectedParameterElement, expectedValueAttribute);
				element.appendChild(expectedParameterElement);
			}
			else{
				Element testParameterElement = new Element(TEST_PARAMETER_NODE_NAME);
				Attribute choiceNameAttribute = new Attribute(getChoiceAttributeName(), testParameter.getQualifiedName());
				encodeAndAddAttribute(testParameterElement, choiceNameAttribute);
				element.appendChild(testParameterElement);
			}
		}

		return element;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception{
		Element element = createAbstractElement(CONSTRAINT_NODE_NAME, node);

		AbstractStatement premise = node.getConstraint().getPremise();
		AbstractStatement consequence = node.getConstraint().getConsequence();

		Element premiseElement = new Element(CONSTRAINT_PREMISE_NODE_NAME);
		premiseElement.appendChild((Element)premise.accept(this));

		Element consequenceElement = new Element(CONSTRAINT_CONSEQUENCE_NODE_NAME);
		consequenceElement.appendChild((Element)consequence.accept(this));

		element.appendChild(premiseElement);
		element.appendChild(consequenceElement);

		return element;
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {
		Element element = createAbstractElement(getChoiceNodeName(), node);
		String value = node.getValueString();
		//remove disallowed XML characters
		String xml10pattern = "[^"
				+ "\u0009\r\n"
				+ "\u0020-\uD7FF"
				+ "\uE000-\uFFFD"
				+ "\ud800\udc00-\udbff\udfff"
				+ "]";
		String legalValue = value.replaceAll(xml10pattern, "");

		encodeAndAddAttribute(element, new Attribute(VALUE_ATTRIBUTE, legalValue));

		for(String label : node.getLabels()){
			Element labelElement = new Element(LABEL_NODE_NAME);
			encodeAndAddAttribute(labelElement, new Attribute(LABEL_ATTRIBUTE_NAME, label));
			element.appendChild(labelElement);
		}

		for(ChoiceNode child : node.getChoices()){
			element.appendChild((Element)child.accept(this));
		}

		return element;
	}

	@Override
	public Object visit(StaticStatement statement) throws Exception {
		Element statementElement = new Element(CONSTRAINT_STATIC_STATEMENT_NODE_NAME);
		String attrName = STATEMENT_STATIC_VALUE_ATTRIBUTE_NAME;
		String attrValue = statement.getValue()?STATIC_STATEMENT_TRUE_VALUE:

			STATIC_STATEMENT_FALSE_VALUE;
		encodeAndAddAttribute(statementElement, new Attribute(attrName, attrValue));

		return statementElement;
	}

	@Override
	public Object visit(StatementArray statement) throws Exception {
		Element element = new Element(CONSTRAINT_STATEMENT_ARRAY_NODE_NAME);
		Attribute operatorAttribute = null;
		switch(statement.getOperator()){
		case AND:
			operatorAttribute = new Attribute(STATEMENT_OPERATOR_ATTRIBUTE_NAME,
					STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE);
			break;
		case OR:
			operatorAttribute = new Attribute(STATEMENT_OPERATOR_ATTRIBUTE_NAME,
					STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE);
			break;
		}
		encodeAndAddAttribute(element, operatorAttribute);

		for(AbstractStatement child : statement.getChildren()){
			element.appendChild((Element)child.accept(this));
		}
		return element;
	}

	@Override
	public Object visit(ExpectedValueStatement statement) throws Exception {
		String parameterName = statement.getLeftOperandName();
		ChoiceNode condition = statement.getCondition();
		Attribute parameterAttribute =
				new Attribute(getStatementParameterAttributeName(), parameterName);
		Attribute valueAttribute =
				new Attribute(STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME, condition.getValueString());

		Element statementElement = new Element(CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME);
		encodeAndAddAttribute(statementElement, parameterAttribute);
		encodeAndAddAttribute(statementElement, valueAttribute);

		return statementElement;
	}

	@Override
	public Object visit(ChoicesParentStatement statement) throws Exception {

		String parameterName = statement.getParameter().getName();
		Attribute parameterAttribute =
				new Attribute(getStatementParameterAttributeName(), parameterName);
		Attribute relationAttribute =
				new Attribute(STATEMENT_RELATION_ATTRIBUTE_NAME, statement.getRelation().toString());
		ICondition condition = statement.getCondition();
		Element statementElement = (Element)condition.accept(this);

		encodeAndAddAttribute(statementElement, parameterAttribute);
		encodeAndAddAttribute(statementElement, relationAttribute);

		return statementElement;
	}

	@Override
	public Object visit(LabelCondition condition) throws Exception {
		Element element = new Element(CONSTRAINT_LABEL_STATEMENT_NODE_NAME);
		encodeAndAddAttribute(element, new Attribute(STATEMENT_LABEL_ATTRIBUTE_NAME, condition.getLabel()));
		return element;
	}

	@Override
	public Object visit(ChoiceCondition condition) throws Exception {
		ChoiceNode choice = condition.getChoice();
		Element element = new Element(CONSTRAINT_CHOICE_STATEMENT_NODE_NAME);
		encodeAndAddAttribute(element, new Attribute(getStatementChoiceAttributeName(), choice.getQualifiedName()));

		return element;
	}

	private Element createAbstractElement(String nodeTag, AbstractNode node){
		Element element = new Element(nodeTag);
		Attribute nameAttr = new Attribute(NODE_NAME_ATTRIBUTE, node.getName());
		encodeAndAddAttribute(element, nameAttr);
		appendComments(element, node);

		return element;
	}

	private Element appendComments(Element element, AbstractNode node) {
		if(node.getDescription() != null){
			Element commentsBlock = new Element(COMMENTS_BLOCK_TAG_NAME);
			Element basicComments = new Element(BASIC_COMMENTS_BLOCK_TAG_NAME);

			basicComments.appendChild(fWhiteCharConverter.encode(node.getDescription()));
			commentsBlock.appendChild(basicComments);
			element.appendChild(commentsBlock);
			return commentsBlock;
		}
		return null;
	}

	private void appendTypeComments(Element element, MethodParameterNode node) {
		if(node.isLinked() == false){
			appendTypeComments(element, (AbstractParameterNode)node);
		}
	}

	private void appendTypeComments(Element element, AbstractParameterNode node) {
		Elements commentsElement = element.getChildElements(COMMENTS_BLOCK_TAG_NAME);
		Element commentElement;
		if(commentsElement.size() > 0){
			commentElement = commentsElement.get(0);
		}else{
			commentElement = new Element(COMMENTS_BLOCK_TAG_NAME);
			element.appendChild(commentElement);
		}

		Element typeComments = new Element(TYPE_COMMENTS_BLOCK_TAG_NAME);

		typeComments.appendChild(fWhiteCharConverter.encode(node.getTypeComments()));
		commentElement.appendChild(typeComments);
	}

	private void encodeAndAddAttribute(Element element, Attribute attribute) {
		attribute.setValue(fWhiteCharConverter.encode(attribute.getValue()));
		element.addAttribute(attribute);
	}

}
