/*******************************************************************************
 * Copyright (c) 2016 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.testify.ecfeed.core.utils.DiskFileHelper;
import com.testify.ecfeed.core.utils.SystemHelper;
import com.testify.ecfeed.utils.EctFileHelper;

public class EditorInMemFileHelper {

	private static final String TMP_FILE_MEM_DIR =
			DiskFileHelper.joinSubdirectory(SystemHelper.getSystemTemporaryDir(), "ecFeed") + DiskFileHelper.pathSeparator();

	private static int fTmpFileCounter = 1;

	public static String createNewTmpFileName() {
		String tmpFile = TMP_FILE_MEM_DIR + "Untitled" + fTmpFileCounter + ".ect";
		fTmpFileCounter++;
		return tmpFile;
	}

	public static boolean isInMemFile(String pathWithFileName) {
		String path = DiskFileHelper.extractPath(pathWithFileName);

		if (path.endsWith(TMP_FILE_MEM_DIR)) {
			return true;
		}

		return false;
	}

	public static InputStream getInitialInputStream(String pathWithFileName) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
		String fileName = DiskFileHelper.extractFileName(pathWithFileName);
		String modelName = DiskFileHelper.extractFileNameWithoutExtension(fileName);
		EctFileHelper.serializeEmptyModel(modelName, outputStream);		
		String fileContent = outputStream.toString();

		return new ByteArrayInputStream(fileContent.getBytes());
	}	

}
