/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.codemanipulation;

public class CodeGenerationSettings {
	
	/**
	 * @deprecated
	 */
	public boolean createFileComments= true;

	public boolean createComments= true;
	
	/**
	 * @deprecated
	 */
	public boolean createNonJavadocComments= true;
	
	public String[] importOrder= new String[0];
	public int importThreshold= 99;
		
	public int tabWidth;
	
	public void setSettings(CodeGenerationSettings settings) {
		settings.createComments= createComments;
		settings.importOrder= importOrder;
		settings.importThreshold= importThreshold;
		settings.tabWidth= tabWidth;
	}
	

}

