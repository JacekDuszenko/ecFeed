/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.utils;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.testify.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.ui.editor.ModelEditor;

public class EclipseHelper {
	public static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static ModelEditor getActiveModelEditor() {
		IEditorPart editorPart = EclipseHelper.getActiveEditor();		
		if (editorPart == null) {
			return null;
		}

		if (!(editorPart instanceof ModelEditor)) {
			return null;
		}

		return (ModelEditor)editorPart;
	}

	public static FileStoreEditorInput getFileStoreEditorInput(ModelEditor modelEditor) {
		IEditorInput editorInput = modelEditor.getEditorInput();
		return getFileStoreEditorInput(editorInput);
	}	

	public static FileStoreEditorInput getFileStoreEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileStoreEditorInput)) {
			return null;
		}
		return  (FileStoreEditorInput)editorInput;
	}	

	public static FileEditorInput getFileEditorInput(IEditorInput editorInput) {
		if (!(editorInput instanceof FileEditorInput)) {
			return null;
		}
		return  (FileEditorInput)editorInput;
	}

	public static Shell getActiveShell() {
		return Display.getDefault().getActiveShell();
	}

	public static IWorkbenchPage getActiveWorkBenchPage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	public static void openEditorOnExistingExtFile(String pathWithFileName) {
		IWorkbenchPage page = getActiveWorkBenchPage();
		IFileStore fileStore = EclipseHelper.getFileStoreForExistingFile(pathWithFileName);
		if (fileStore == null) {
			ExceptionHelper.reportRuntimeException("Can not open editor on file: " + pathWithFileName + " .");
		}
		openEditorOnFileStore(page, fileStore);
	}

	private static IFileStore getFileStoreForExistingFile(String filePath) {
		if (filePath == null) {
			return null;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		if (!file.isFile()) {
			return null;
		}

		return getFileStore(file.toURI());
	}

	private static IFileStore getFileStore(URI uri) {
		return EFS.getLocalFileSystem().getStore(uri);
	}

	private static void openEditorOnFileStore(IWorkbenchPage page, IFileStore fileStore) {
		try {
			IDE.openEditorOnFileStore(page, fileStore);
		} catch (PartInitException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

}
