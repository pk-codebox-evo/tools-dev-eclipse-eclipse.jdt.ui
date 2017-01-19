/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.dom;

import org.eclipse.jdt.core.dom.AST;

public interface IASTSharedValues {

	/**
	 * This value is subject to change with every release. JDT-UI-internal code typically supports
	 * the latest available {@link AST#apiLevel() AST level} exclusively.
	 */
	public static final int SHARED_AST_LEVEL= AST.JLS8;

	public static final boolean SHARED_AST_STATEMENT_RECOVERY= true;

	public static final boolean SHARED_BINDING_RECOVERY= true;
}
