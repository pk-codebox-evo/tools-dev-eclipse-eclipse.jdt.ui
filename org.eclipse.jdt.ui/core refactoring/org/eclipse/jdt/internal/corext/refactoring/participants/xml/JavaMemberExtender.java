/*******************************************************************************
 * Copyright (c) 2003 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.participants.xml;

import org.eclipse.core.expressions.PropertyTester;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.Assert;
import org.eclipse.jdt.internal.corext.util.JdtFlags;


public class JavaMemberExtender extends PropertyTester {

	private static final String PROPERTY_IS_STATIC= "isStatic"; //$NON-NLS-1$
	private static final String PROPERTY_IS_PRIVATE= "isPrivate"; //$NON-NLS-1$
	
	public boolean test(Object receiver, String method, Object[] args, Object expectedValue) {
		IMember member= (IMember)receiver;
		try {
			if (PROPERTY_IS_STATIC.equals(method)) {
				return JdtFlags.isStatic(member);
			} else if (PROPERTY_IS_PRIVATE.equals(method)) {
				return JdtFlags.isPrivate(member);
			}
		} catch (JavaModelException e) {
			return false;
		}
		Assert.isTrue(false);
		return false;
	}
}
