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
package org.eclipse.jdt.internal.ui.refactoring.nls.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.ResourcesPlugin;

class LineReader extends Object {
	protected static final int fgLF= '\n';
	protected static final int fgCR= '\r';

	private BufferedReader fReader;

	protected int fPushbackChar;
	protected boolean fPushback;

	// Fix for http://dev.eclipse.org/bugs/show_bug.cgi?id=19319
	public LineReader(InputStream in) throws IOException {
		this(new InputStreamReader(in, ResourcesPlugin.getEncoding()));
	}

	public LineReader(Reader reader) {
		fPushback= false;
		fReader= new BufferedReader(reader);
	}

	public int readLine(StringBuffer sb) throws IOException {
		int ch= -1;
		sb.setLength(0);
		if (fPushback) {
			ch= fPushbackChar;
			fPushback= false;
		} else
			ch= fReader.read();
		while (ch >= 0) {
			if (ch == fgLF)
				return 1;
			if (ch == fgCR) {
				ch= fReader.read();
				if (ch == fgLF)
					return 2;
				else {
					fPushbackChar= ch;
					fPushback= true;
					return 1;
				}
			}
			sb.append((char) ch);
			ch= fReader.read();
		}
		return -1;
	}

	public void close() throws IOException {
		fReader.close();
	}
}
