/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text;


/**
 * Mix-in for any rule that changes its behavior based on the JLS version.
 * 
 * @since 3.1
 */
public interface IVersionDependent {
	
	/**
	 * Sets the configured java source version to one of the
	 * <code>JavaCore.VERSION_X_Y</code> values.
	 * 
	 * @param version the new java source version
	 * @see org.eclipse.jdt.core.JavaCore
	 */
	void setCurrentVersion(String version);
}