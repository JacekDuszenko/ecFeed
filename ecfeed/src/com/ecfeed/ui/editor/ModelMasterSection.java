/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;

import com.ecfeed.application.ApplicationContext;
import com.ecfeed.core.adapter.EImplementationStatus;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.ui.common.Constants;
import com.ecfeed.ui.common.ImageManager;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.editor.actions.AbstractAddChildAction;
import com.ecfeed.ui.editor.actions.AddChildActionProvider;
import com.ecfeed.ui.editor.actions.BasicActionRunnerProvider;
import com.ecfeed.ui.editor.actions.ExecuteTestCaseAction;
import com.ecfeed.ui.editor.actions.ExportOnlineAction;
import com.ecfeed.ui.editor.actions.IActionRunner;
import com.ecfeed.ui.editor.actions.ModelViewerActionProvider;
import com.ecfeed.ui.editor.actions.TestOnlineAction;
import com.ecfeed.ui.modelif.AbstractNodeInterface;
import com.ecfeed.ui.modelif.AbstractParameterInterface;
import com.ecfeed.ui.modelif.IModelUpdateListener;
import com.ecfeed.ui.modelif.MethodInterface;
import com.ecfeed.ui.modelif.ModelNodesTransfer;
import com.ecfeed.ui.modelif.NodeInterfaceFactory;
import com.ecfeed.ui.modelif.TestCaseInterface;
import com.ecfeed.utils.SeleniumHelper;

public class ModelMasterSection extends TreeViewerSection{

	private static final int AUTO_EXPAND_LEVEL = 3;

	private final ModelMasterDetailsBlock fMasterDetailsBlock;
	private IModelUpdateListener fUpdateListener;
	private IFileInfoProvider fFileInfoProvider;

	private class ModelWrapper{
		private final RootNode fModel;

		public ModelWrapper(RootNode model){
			fModel = model;
		}

		public RootNode getModel(){
			return fModel;
		}
	}

	private class UpdateListener implements IModelUpdateListener{
		@Override
		public void modelUpdated(AbstractFormPart source) {
			source.markDirty();
			refresh();
		}
	}

	private class ModelContentProvider extends TreeNodeContentProvider implements ITreeContentProvider {

		public final Object[] EMPTY_ARRAY = {};

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof ModelWrapper){
				RootNode root = ((ModelWrapper)inputElement).getModel();
				return new Object[]{root};
			}
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			//Because of potentially large amount of children, MethodNode is special case
			//We filter out test suites with too many test cases
			if(parentElement instanceof MethodNode){
				MethodNode method = (MethodNode)parentElement;
				ArrayList<Object> children = new ArrayList<Object>();
				children.addAll(method.getParameters());
				children.addAll(method.getConstraintNodes());
				for(String testSuite : method.getTestSuites()){
					Collection<TestCaseNode> testCases = method.getTestCases(testSuite);
					if(testCases.size() <= Constants.MAX_DISPLAYED_TEST_CASES_PER_SUITE){
						children.addAll(testCases);
					}
				}
				return children.toArray();
			}

			if(parentElement instanceof MethodParameterNode){
				MethodParameterNode parameter = (MethodParameterNode)parentElement;
				if(parameter.isExpected() && AbstractParameterInterface.isPrimitive(parameter.getType())){
					return EMPTY_ARRAY;
				}
				if(parameter.isLinked()){
					return EMPTY_ARRAY;
				}
			}
			if(parentElement instanceof AbstractNode){
				AbstractNode node = (AbstractNode)parentElement;
				if(node.getChildren().size() < Constants.MAX_DISPLAYED_CHILDREN_PER_NODE){
					return node.getChildren().toArray();
				}
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element) {
			if(element instanceof AbstractNode){
				return ((AbstractNode)element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}

	private class ModelLabelProvider extends LabelProvider {

		private class TextProvider implements IModelVisitor{

			@Override
			public Object visit(RootNode node) throws Exception {
				return node.toString();
			}

			@Override
			public Object visit(ClassNode node) throws Exception {
				return node.toString();
			}

			@Override
			public Object visit(MethodNode node) throws Exception {
				return JavaUtils.simplifiedToString(node);
			}

			@Override
			public Object visit(MethodParameterNode node) throws Exception {
				String result = JavaUtils.simplifiedToString(node);
				if(node.isLinked()){
					result += "[LINKED]->" + node.getLink().getQualifiedName();
				}
				return result;
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				return JavaUtils.simplifiedToString(node);
			}

			@Override
			public Object visit(TestCaseNode node) throws Exception {
				return node.toString();
			}

			@Override
			public Object visit(ConstraintNode node) throws Exception {
				return node.toString();
			}

			@Override
			public Object visit(ChoiceNode node) throws Exception {
				return node.toString();
			}
		}

		private class ImageProvider implements IModelVisitor{

			@Override
			public Object visit(RootNode node) throws Exception {
				return getImageFromFile("root_node.png");
			}

			@Override
			public Object visit(ClassNode node) throws Exception {
				return getImageFromFile("class_node.png");
			}

			@Override
			public Object visit(MethodNode node) throws Exception {
				return getImageFromFile("method_node.png");
			}

			@Override
			public Object visit(MethodParameterNode node) throws Exception {
				return getImageFromFile("parameter_node.png");
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				return getImageFromFile("parameter_node.png");
			}

			@Override
			public Object visit(TestCaseNode node) throws Exception {
				return getImageFromFile("test_case_node.png");
			}

			@Override
			public Object visit(ConstraintNode node) throws Exception {
				return getImageFromFile("constraint_node.png");
			}

			@Override
			public Object visit(ChoiceNode node) throws Exception {
				return getImageFromFile("choice_node.png");
			}

		}

		@Override
		public String getText(Object element){
			if(element instanceof AbstractNode){
				try {
					return (String)((AbstractNode)element).accept(new TextProvider());
				} catch(Exception e) { 
					SystemLogger.logCatch(e.getMessage());
				}
			}
			return null;
		}

		@Override
		public Image getImage(Object element){
			if(element instanceof AbstractNode){
				try {
					return (Image)((AbstractNode)element).accept(new ImageProvider());
				} catch(Exception e) {
					SystemLogger.logCatch(e.getMessage());
				}
			}
			return getImageFromFile("sample.png");
		}
	}

	private class ModelLabelDecorator implements ILabelDecorator {

		Map<List<Image>, Image> fFusedImages;

		private class DecorationProvider implements IModelVisitor{
			AbstractNodeInterface fNodeInterface;
			boolean fIsProjectAvailable;

			public DecorationProvider(IFileInfoProvider fileInfoProvider, boolean isProjectAvailable){
				fNodeInterface = new AbstractNodeInterface(ModelMasterSection.this, fileInfoProvider);
				fIsProjectAvailable = isProjectAvailable;
			}

			@Override
			public Object visit(RootNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(ClassNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(MethodNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(MethodParameterNode node) throws Exception {
				List<Image> decorations = new ArrayList<Image>();
				decorations.add(implementationStatusDecoration(node));
				if(node.isExpected()){
					decorations.add(getImageFromFile("expected.png"));
				}
				if(node.isLinked()){
					decorations.add(getImageFromFile("linked.png"));
				}
				return decorations;
			}

			@Override
			public Object visit(GlobalParameterNode node) throws Exception {
				List<Image> decorations = new ArrayList<Image>();
				decorations.add(implementationStatusDecoration(node));
				decorations.add(getImageFromFile("global.png"));
				return decorations;
			}

			@Override
			public Object visit(TestCaseNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(ConstraintNode node) throws Exception {
				return Arrays.asList(new Image[]{implementationStatusDecoration(node)});
			}

			@Override
			public Object visit(ChoiceNode node) throws Exception {
				List<Image> decorations = new ArrayList<Image>();
				decorations.add(implementationStatusDecoration(node));
				if(node.isAbstract()){
					decorations.add(getImageFromFile("abstract.png"));
				}
				return decorations;
			}

			private Image implementationStatusDecoration(AbstractNode node) {
				if (!fIsProjectAvailable) {
					return null;
				}

				switch (fNodeInterface.getImplementationStatus(node)){
				case IMPLEMENTED:
					return getImageFromFile("implemented.png");
				case PARTIALLY_IMPLEMENTED:
					return getImageFromFile("partially_implemented.png");
				case NOT_IMPLEMENTED:
					return getImageFromFile("unimplemented.png");
				case IRRELEVANT:
				default:
					return null;
				}
			}
		}

		public ModelLabelDecorator() {
			fFusedImages = new HashMap<List<Image>, Image>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Image decorateImage(Image image, Object element) {
			if(!(element instanceof AbstractNode)){
				return image;
			}

			try {
				List<Image> decorations = (List<Image>)((AbstractNode)element).accept(
						new DecorationProvider(fFileInfoProvider, fFileInfoProvider.isProjectAvailable()));
				List<Image> all = new ArrayList<Image>(decorations);
				all.add(0, image);
				if(fFusedImages.containsKey(all) == false){
					Image decorated = new Image(Display.getCurrent(), image.getImageData());
					for(Image decoration : decorations){
						if(decoration != null){
							decorated = fuseImages(decorated, decoration, 0, 0);
						}
					}
					fFusedImages.put(decorations, decorated);
				}
				return fFusedImages.get(decorations);
			} catch(Exception e) {
				SystemLogger.logCatch(e.getMessage());
			}
			return image;
		}

		@Override
		public String decorateText(String text, Object element) {
			return text;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		private Image fuseImages(Image icon, Image decorator, int x, int y){
			ImageData idIcon = (ImageData)icon.getImageData().clone();
			ImageData idDecorator = decorator.getImageData();
			if(idIcon.width <= x || idIcon.height <= y){
				return icon;
			}
			int rbw = (idDecorator.width + x > idIcon.width) ? (idDecorator.width + x - idIcon.width) : idDecorator.width;
			int rbh = (idDecorator.height + y > idIcon.height) ? (idDecorator.height + y - idIcon.height) : idDecorator.height;

			int indexa = y*idIcon.scanlinePad + x;
			int indexb = 0;

			for(int row = 0; row < rbh; row ++){
				for(int col = 0; col < rbw; col++){
					if(idDecorator.alphaData[indexb] < 0){
						idIcon.alphaData[indexa] = (byte)-1;
						idIcon.data[4*indexa]=idDecorator.data[4*indexb];
						idIcon.data[4*indexa+1]=idDecorator.data[4*indexb+1];
						idIcon.data[4*indexa+2]=idDecorator.data[4*indexb+2];
					}
					indexa += 1;
					indexb += 1;
				}
				indexa += x;
			}
			return new Image(Display.getDefault(), idIcon);
		}
	}

	protected class MasterViewerMenuListener extends ViewerMenuListener{
		public MasterViewerMenuListener(Menu menu) {
			super(menu);
		}

		@Override
		protected void populateMenu() {
			AbstractNode firstSelectedNode = getFirstSelectedNode();
			if (firstSelectedNode == null) {
				return;
			}

			addChildAddingActions(firstSelectedNode);
			addActionsForMethod(firstSelectedNode);
			addActionsForTestCase(firstSelectedNode);
			super.populateMenu();
		}

		private void addChildAddingActions(AbstractNode abstractNode) {
			AddChildActionProvider actionProvider = 
					new AddChildActionProvider(getTreeViewer(), ModelMasterSection.this, fFileInfoProvider);
			List<AbstractAddChildAction> actions = actionProvider.getPossibleActions(abstractNode);

			boolean menuItemAdded = false;
			for(AbstractAddChildAction action : actions) {
				addMenuItem(action.getName(), action);
				menuItemAdded = true;
			}

			if (menuItemAdded) {
				new MenuItem(getMenu(), SWT.SEPARATOR);
			}
		}

		private void addActionsForMethod(AbstractNode abstractNode) {

			if (!(abstractNode instanceof MethodNode)) {
				return;
			}

			MethodNode methodNode = (MethodNode)abstractNode;
			MethodInterface methodInterface = getMethodInterface();
			boolean isAction = false;

			if (addTestOnlineAction(methodInterface)) {
				isAction = true;
			}
			if (addExportOnlineAction(methodNode, methodInterface)) {
				isAction = true;
			}

			if (isAction) {
				new MenuItem(getMenu(), SWT.SEPARATOR);
			}
		}

		private void addActionsForTestCase(AbstractNode abstractNode) {

			if (!(abstractNode instanceof TestCaseNode)) {
				return;
			}

			TestCaseInterface testCaseInterface = getTestCaseInterface();

			AbstractNodeInterface nodeIf = 
					NodeInterfaceFactory.getNodeInterface(testCaseInterface.getMethod(), null, fFileInfoProvider);

			MethodInterface methodInterface = (MethodInterface)nodeIf; 

			if (!isActionExecutable(methodInterface)) {
				return;
			}

			ExecuteTestCaseAction action = new ExecuteTestCaseAction(ModelMasterSection.this, testCaseInterface);
			addMenuItem(action.getName(), action);

			new MenuItem(getMenu(), SWT.SEPARATOR);
		}

		private MethodInterface getMethodInterface() {
			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, fFileInfoProvider);

			if (!(nodeIf instanceof MethodInterface)) {
				final String MSG = "Invalid type of node interface. Method node interface expected"; 
				ExceptionHelper.reportRuntimeException(MSG);
			}

			return (MethodInterface)nodeIf; 
		}

		private TestCaseInterface getTestCaseInterface() {
			AbstractNodeInterface nodeInterface = NodeInterfaceFactory.getNodeInterface(getSelectedNodes().get(0), null, fFileInfoProvider);

			if (!(nodeInterface instanceof TestCaseInterface)) {
				final String MSG = "Invalid type of node interface. Test case interface expected"; 
				ExceptionHelper.reportRuntimeException(MSG);
			}

			return (TestCaseInterface)nodeInterface; 
		}

		private boolean addTestOnlineAction(MethodInterface methodInterface) {

			if (!isActionExecutable(methodInterface)) {
				return false;
			}

			TestOnlineAction testOnlineAction = new TestOnlineAction(fFileInfoProvider, ModelMasterSection.this, methodInterface);
			addMenuItem(testOnlineAction.getName(), testOnlineAction);
			return true;
		}

		private boolean isActionExecutable(MethodInterface methodInterface) {
			MethodNode methodNode = methodInterface.getOwnRootNode();

			if (SeleniumHelper.isSeleniumRunnerMethod(methodNode)) {
				return true;
			}

			if (ApplicationContext.isStandaloneApplication()) {
				return false;
			}

			EImplementationStatus methodStatus = methodInterface.getImplementationStatus();

			if (methodStatus != EImplementationStatus.IMPLEMENTED) {
				return false;
			}

			return true;
		}

		private boolean addExportOnlineAction(MethodNode methodNode, MethodInterface methodInterface) {
			if (methodNode.getParametersCount() == 0) {
				return false;
			}

			ExportOnlineAction exportOnlineAction = new ExportOnlineAction(fFileInfoProvider, ModelMasterSection.this, methodInterface);
			addMenuItem(exportOnlineAction.getName(), exportOnlineAction);
			return true;
		}

	}

	public ModelMasterSection(ModelMasterDetailsBlock parentBlock, IFileInfoProvider fileInfoProvider) {
		super(parentBlock.getMasterSectionContext(), parentBlock.getModelUpdateContext(), fileInfoProvider, StyleDistributor.getSectionStyle());
		fMasterDetailsBlock = parentBlock;
		fFileInfoProvider = fileInfoProvider;

		boolean includeDeleteAction = false;
		if (ApplicationContext.isStandaloneApplication()) {
			includeDeleteAction = true;
		}

		BasicActionRunnerProvider basicActionRunnerProvider = 
				new BasicActionRunnerProvider(
						new SaveActionRunner(),
						new UndoActionRunner(),
						new RedoActionRunner()); 

		ModelViewerActionProvider modelViewerActionProvider = 
				new ModelViewerActionProvider(
						getTreeViewer(), this, parentBlock.getPage().getEditor(), basicActionRunnerProvider, false);

		setActionProvider(modelViewerActionProvider, includeDeleteAction);		

		getTreeViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getTreeViewer()));
		getTreeViewer().addDropSupport(DND.DROP_COPY|DND.DROP_MOVE|DND.DROP_LINK, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDropListener(getTreeViewer(), this, fFileInfoProvider));
	}

	private class SaveActionRunner implements IActionRunner {

		@Override
		public void run() {
			ModelEditorHelper.saveActiveEditor();
		}

	}

	private class UndoActionRunner implements IActionRunner {

		@Override
		public void run() {
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			IUndoContext undoContext = ModelEditorHelper.getActiveModelEditor().getUndoContext();

			try {
				operationHistory.undo(undoContext, null, null);
			} catch (ExecutionException e) {
				SystemLogger.logCatch("Can not undo operation.");
			}
		}

	}

	private class RedoActionRunner implements IActionRunner {

		@Override
		public void run() {
			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			IUndoContext undoContext = ModelEditorHelper.getActiveModelEditor().getUndoContext();

			try {
				operationHistory.redo(undoContext, null, null);
			} catch (ExecutionException e) {
				SystemLogger.logCatch("Can not undo operation.");
			}
		}

	}

	public void setInput(RootNode model){
		setInput(new ModelWrapper(model));
		collapseGlobalParameters();
	}

	@Override
	public void refresh(){
		super.refresh();
		IDetailsPage page = fMasterDetailsBlock.getCurrentPage();
		if(page != null){
			page.refresh();
		}
	}

	@Override
	protected void createContent(){
		super.createContent();
		getSection().setText("Structure");
		getTreeViewer().setAutoExpandLevel(AUTO_EXPAND_LEVEL);
	}

	@Override
	protected IContentProvider createViewerContentProvider() {
		return new ModelContentProvider();
	}

	@Override
	protected IBaseLabelProvider createViewerLabelProvider() {
		return new DecoratingLabelProvider(new ModelLabelProvider(), new ModelLabelDecorator());
	}

	@Override
	protected ViewerMenuListener getMenuListener() {
		return new MasterViewerMenuListener(getMenu());
	}

	@Override
	public List<IModelUpdateListener> getUpdateListeners(){
		if(fUpdateListener == null){
			fUpdateListener = new UpdateListener();
		}
		return Arrays.asList(new IModelUpdateListener[]{fUpdateListener});
	}

	private void collapseGlobalParameters() {
		for(GlobalParameterNode parameter : ((ModelWrapper)getViewer().getInput()).getModel().getGlobalParameters()){
			getTreeViewer().collapseToLevel(parameter, 1);
		}
	}

	private Image getImageFromFile(String file) {
		return ImageManager.getInstance().getImage(file);
	}
}
