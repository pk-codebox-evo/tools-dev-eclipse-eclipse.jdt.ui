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
package org.eclipse.jdt.internal.ui.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation;


/**
 * Determines all markers for the given line and collects, concatenates, and formates
 * their messages.
 */
public class JavaAnnotationHover implements IAnnotationHover {
	
	/**
	 * Returns the distance to the ruler line. 
	 */
	protected int compareRulerLine(Position position, IDocument document, int line) {
		
		if (position.getOffset() > -1 && position.getLength() > -1) {
			try {
				int javaAnnotationLine= document.getLineOfOffset(position.getOffset());
				if (line == javaAnnotationLine)
					return 1;
				if (javaAnnotationLine <= line && line <= document.getLineOfOffset(position.getOffset() + position.getLength()))
					return 2;
			} catch (BadLocationException x) {
			}
		}
		
		return 0;
	}
	
	/**
	 * Selects a set of markers from the two lists. By default, it just returns
	 * the set of exact matches.
	 */
	protected List select(List exactMatch, List including) {
		return exactMatch;
	}
	
	/**
	 * Returns one marker which includes the ruler's line of activity.
	 */
	protected List getJavaAnnotationsForLine(ISourceViewer viewer, int line) {
		
		IDocument document= viewer.getDocument();
		IAnnotationModel model= viewer.getAnnotationModel();
		
		if (model == null)
			return null;
			
		List exact= new ArrayList();
		List including= new ArrayList();
		
		Iterator e= model.getAnnotationIterator();
		HashMap messagesAtPosition= new HashMap();
		while (e.hasNext()) {
			Object o= e.next();
			if (o instanceof IJavaAnnotation) {
				IJavaAnnotation a= (IJavaAnnotation)o;
				if (!a.hasOverlay()) {
					Position position= model.getPosition((Annotation)a);
					if (position == null)
						continue;

					if (isDuplicateJavaAnnotation(messagesAtPosition, position, a.getMessage()))
						continue;
	
					switch (compareRulerLine(position, document, line)) {
						case 1:
							exact.add(a);
							break;
						case 2:
							including.add(a);
							break;
					}
				}
			}
		}
		
		return select(exact, including);
	}

	private boolean isDuplicateJavaAnnotation(Map messagesAtPosition, Position position, String message) {
		if (messagesAtPosition.containsKey(position)) {
			Object value= messagesAtPosition.get(position);
			if (message.equals(value))
				return true;

			if (value instanceof List) {
				List messages= (List)value;
				if  (messages.contains(message))
					return true;
				else
					messages.add(message);
			} else {
				ArrayList messages= new ArrayList();
				messages.add(value);
				messages.add(message);
				messagesAtPosition.put(position, messages);
			}
		} else
			messagesAtPosition.put(position, message);
		return false;
	}
		
	/*
	 * @see IVerticalRulerHover#getHoverInfo(ISourceViewer, int)
	 */
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		List javaAnnotations= getJavaAnnotationsForLine(sourceViewer, lineNumber);
		if (javaAnnotations != null) {
			
			if (javaAnnotations.size() == 1) {
				
				// optimization
				IJavaAnnotation javaAnnotation= (IJavaAnnotation) javaAnnotations.get(0);
				String message= javaAnnotation.getMessage();
				if (message != null && message.trim().length() > 0)
					return formatSingleMessage(message);
					
			} else {
					
				List messages= new ArrayList();
				
				Iterator e= javaAnnotations.iterator();
				while (e.hasNext()) {
					IJavaAnnotation javaAnnotation= (IJavaAnnotation) e.next();
					String message= javaAnnotation.getMessage();
					if (message != null && message.trim().length() > 0)
						messages.add(message.trim());
				}
				
				if (messages.size() == 1)
					return formatSingleMessage((String) messages.get(0));
					
				if (messages.size() > 1)
					return formatMultipleMessages(messages);
			}
		}
		
		return null;
	}
	
	/*
	 * Formats a message as HTML text.
	 */
	private String formatSingleMessage(String message) {
		StringBuffer buffer= new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}
	
	/*
	 * Formats several message as HTML text.
	 */
	private String formatMultipleMessages(List messages) {
		StringBuffer buffer= new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(JavaUIMessages.getString("JavaAnnotationHover.multipleMarkersAtThisLine"))); //$NON-NLS-1$
		
		HTMLPrinter.startBulletList(buffer);
		Iterator e= messages.iterator();
		while (e.hasNext())
			HTMLPrinter.addBullet(buffer, HTMLPrinter.convertToHTMLContent((String) e.next()));
		HTMLPrinter.endBulletList(buffer);	
		
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}
}
