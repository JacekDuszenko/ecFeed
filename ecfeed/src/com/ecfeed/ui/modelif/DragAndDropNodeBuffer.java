/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.model.AbstractNode;

public class DragAndDropNodeBuffer {

	private static List<AbstractNode> fDraggedNodes;

	public static void setDraggedNodes(List<AbstractNode>nodes) {
		fDraggedNodes = nodes;
		removeDuplicatedChildren(fDraggedNodes);
	}

	public static List<AbstractNode> getDraggedNodes() {
		return fDraggedNodes;
	}

	public static List<AbstractNode> getDraggedNodesCopy() {

		List<AbstractNode> result = new ArrayList<>();

		if (fDraggedNodes != null) {
			for (AbstractNode node : fDraggedNodes) {
				result.add(node.makeClone());
			}
		}

		return result;
	}

	public static void clear() {

		if(fDraggedNodes != null){
			fDraggedNodes.clear();
		}
	}

	private static void removeDuplicatedChildren(List<AbstractNode> nodes) {

		Iterator<AbstractNode> it = nodes.iterator();

		while (it.hasNext()) {
			AbstractNode node = it.next();
			AbstractNode parent = node.getParent();

			while (parent != null) {
				if (nodes.contains(parent)) {
					it.remove();
					break;
				}

				parent = parent.getParent();
			}
		}
	}
}
