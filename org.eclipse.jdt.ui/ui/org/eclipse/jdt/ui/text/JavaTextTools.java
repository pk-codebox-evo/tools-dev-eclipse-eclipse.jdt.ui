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
package org.eclipse.jdt.ui.text;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
//import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
//import org.eclipse.jface.text.rules.RuleBasedPartitioner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.core.runtime.Preferences;

import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.internal.ui.text.JavaPartitionScanner;
import org.eclipse.jdt.internal.ui.text.JavaCommentScanner;
import org.eclipse.jdt.internal.ui.text.SingleTokenJavaScanner;
import org.eclipse.jdt.internal.ui.text.java.JavaCodeScanner;
import org.eclipse.jdt.internal.ui.text.javadoc.JavaDocScanner;


/**
 * Tools required to configure a Java text viewer. 
 * The color manager and all scanner exist only one time, i.e.
 * the same instances are returned to all clients. Thus, clients
 * share those tools.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class JavaTextTools {
	
	private class PreferenceListener implements IPropertyChangeListener, Preferences.IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			adaptToPreferenceChange(event);
		}
		public void propertyChange(Preferences.PropertyChangeEvent event) {
			adaptToPreferenceChange(new PropertyChangeEvent(event.getSource(), event.getProperty(), event.getOldValue(), event.getNewValue()));
		}
	};
		
	/** The color manager */
	private JavaColorManager fColorManager;
	/** The Java source code scanner */
	private JavaCodeScanner fCodeScanner;
	/** The Java multiline comment scanner */
	private JavaCommentScanner fMultilineCommentScanner;
	/** The Java singleline comment scanner */
	private JavaCommentScanner fSinglelineCommentScanner;
	/** The Java string scanner */
	private SingleTokenJavaScanner fStringScanner;
	/** The JavaDoc scanner */
	private JavaDocScanner fJavaDocScanner;
	/** The Java partitions scanner */
	private FastJavaPartitionScanner fPartitionScanner;	
	
	/** The preference store */
	private IPreferenceStore fPreferenceStore;
	/** The core preference store */
	private Preferences fCorePreferenceStore;
	/** The preference change listener */
	private PreferenceListener fPreferenceListener= new PreferenceListener();


	/**
	 * Creates a new Java text tools collection.
	 * @param store the preference store to initialize the text tools. The text tool
	 * instance installs a listener on the passed preference store to adapt itself to 
	 * changes in the preference store. In general <code>PreferenceConstants.
	 * getPreferenceStore()</code> should be used to initialize the text tools.
	 * 
	 * @see org.eclipse.jdt.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.0
	 */
	public JavaTextTools(IPreferenceStore store) {
		this(store, null, true);
	}

	/**
	 * Creates a new Java text tools collection.
	 * 
	 * @param store the preference store to initialize the text tools. The text tool
	 * instance installs a listener on the passed preference store to adapt itself to 
	 * changes in the preference store. In general <code>PreferenceConstants.
	 * getPreferenceStore()</code> shoould be used to initialize the text tools.
	 * @param autoDisposeOnDisplayDispose 	if <code>true</code>  the color manager
	 * automatically disposes all managed colors when the current display gets disposed
	 * and all calls to {@link org.eclipse.jface.text.source.ISharedTextColors#dispose()} are ignored.
	 * 
	 * @see org.eclipse.jdt.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public JavaTextTools(IPreferenceStore store, boolean autoDisposeOnDisplayDispose) {
		this(store, null, autoDisposeOnDisplayDispose);
	}

	/**
	 * Creates a new Java text tools collection.
	 * @param store the preference store to initialize the text tools. The text tool
	 * instance installs a listener on the passed preference store to adapt itself to 
	 * changes in the preference store. In general <code>PreferenceConstants.
	 * getPreferenceStore()</code> should be used to initialize the text tools.
	 * @param coreStore optional preference store to initialize the text tools. The text tool
	 * instance installs a listener on the passed preference store to adapt itself to 
	 * changes in the preference store.
	 * 
	 * @see org.eclipse.jdt.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public JavaTextTools(IPreferenceStore store, Preferences coreStore) {
		this(store, coreStore, true);
	}
	
	/**
	 * Creates a new Java text tools collection.
	 * 
	 * @param store the preference store to initialize the text tools. The text tool
	 * instance installs a listener on the passed preference store to adapt itself to 
	 * changes in the preference store. In general <code>PreferenceConstants.
	 * getPreferenceStore()</code> shoould be used to initialize the text tools.
	 * @param coreStore optional preference store to initialize the text tools. The text tool
	 * instance installs a listener on the passed preference store to adapt itself to 
	 * changes in the preference store.
	 * @param autoDisposeOnDisplayDispose 	if <code>true</code>  the color manager
	 * automatically disposes all managed colors when the current display gets disposed
	 * and all calls to {@link org.eclipse.jface.text.source.ISharedTextColors#dispose()} are ignored.
	 * 
	 * @see org.eclipse.jdt.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public JavaTextTools(IPreferenceStore store, Preferences coreStore, boolean autoDisposeOnDisplayDispose) {
		fPreferenceStore= store;
		fPreferenceStore.addPropertyChangeListener(fPreferenceListener);
		
		fCorePreferenceStore= coreStore;
		if (fCorePreferenceStore != null)
			fCorePreferenceStore.addPropertyChangeListener(fPreferenceListener);
		
		fColorManager= new JavaColorManager(autoDisposeOnDisplayDispose);
		fCodeScanner= new JavaCodeScanner(fColorManager, store);
		fMultilineCommentScanner= new JavaCommentScanner(fColorManager, store, coreStore, IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
		fSinglelineCommentScanner= new JavaCommentScanner(fColorManager, store, coreStore, IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
		fStringScanner= new SingleTokenJavaScanner(fColorManager, store, IJavaColorConstants.JAVA_STRING);
		fJavaDocScanner= new JavaDocScanner(fColorManager, store, coreStore);
		fPartitionScanner= new FastJavaPartitionScanner();
	}
	
	/**
	 * Disposes all the individual tools of this tools collection.
	 */
	public void dispose() {
		
		fCodeScanner= null;
		fMultilineCommentScanner= null;
		fSinglelineCommentScanner= null;
		fStringScanner= null;
		fJavaDocScanner= null;
		fPartitionScanner= null;
		
		if (fColorManager != null) {
			fColorManager.dispose();
			fColorManager= null;
		}
		
		if (fPreferenceStore != null) {
			fPreferenceStore.removePropertyChangeListener(fPreferenceListener);
			fPreferenceStore= null;
			
			if (fCorePreferenceStore != null) {
				fCorePreferenceStore.removePropertyChangeListener(fPreferenceListener);
				fCorePreferenceStore= null;
			}
			
			fPreferenceListener= null;
		}
	}
	
	/**
	 * Returns the color manager which is used to manage
	 * any Java-specific colors needed for such things like syntax highlighting.
	 *
	 * @return the color manager to be used for Java text viewers
	 */
	public IColorManager getColorManager() {
		return fColorManager;
	}
	
	/**
	 * Returns a scanner which is configured to scan Java source code.
	 *
	 * @return a Java source code scanner
	 */
	public RuleBasedScanner getCodeScanner() {
		return fCodeScanner;
	}
	
	/**
	 * Returns a scanner which is configured to scan Java multiline comments.
	 *
	 * @return a Java multiline comment scanner
	 * 
	 * @since 2.0
	 */
	public RuleBasedScanner getMultilineCommentScanner() {
		return fMultilineCommentScanner;
	}

	/**
	 * Returns a scanner which is configured to scan Java singleline comments.
	 *
	 * @return a Java singleline comment scanner
	 * 
	 * @since 2.0
	 */
	public RuleBasedScanner getSinglelineCommentScanner() {
		return fSinglelineCommentScanner;
	}
	
	/**
	 * Returns a scanner which is configured to scan Java strings.
	 *
	 * @return a Java string scanner
	 * 
	 * @since 2.0
	 */
	public RuleBasedScanner getStringScanner() {
		return fStringScanner;
	}
	
	/**
	 * Returns a scanner which is configured to scan JavaDoc compliant comments.
	 * Notes that the start sequence "/**" and the corresponding end sequence
	 * are part of the JavaDoc comment.
	 *
	 * @return a JavaDoc scanner
	 */
	public RuleBasedScanner getJavaDocScanner() {
		return fJavaDocScanner;
	}
	
	/**
	 * Returns a scanner which is configured to scan 
	 * Java-specific partitions, which are multi-line comments,
	 * JavaDoc comments, and regular Java source code.
	 *
	 * @return a Java partition scanner
	 */
	public IPartitionTokenScanner getPartitionScanner() {
		return fPartitionScanner;
	}
	
	/**
	 * Factory method for creating a Java-specific document partitioner
	 * using this object's partitions scanner. This method is a 
	 * convenience method.
	 *
	 * @return a newly created Java document partitioner
	 */
	public IDocumentPartitioner createDocumentPartitioner() {
		
		String[] types= new String[] {
			JavaPartitionScanner.JAVA_DOC,
			JavaPartitionScanner.JAVA_MULTI_LINE_COMMENT,
			JavaPartitionScanner.JAVA_SINGLE_LINE_COMMENT,
			JavaPartitionScanner.JAVA_STRING
		};

		return new DefaultPartitioner(getPartitionScanner(), types);
	}
	
	/**
	 * Returns the names of the document position categories used by the document
	 * partitioners created by this object to manage their partition information.
	 * If the partitioners don't use document position categories, the returned
	 * result is <code>null</code>.
	 *
	 * @return the partition managing position categories or <code>null</code> 
	 * 			if there is none
	 */
	public String[] getPartitionManagingPositionCategories() {
		return new String[] { DefaultPartitioner.CONTENT_TYPES_CATEGORY };
	}
	
	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the behavior of one its contained components.
	 * 
	 * @param event the event to be investigated
	 * @return <code>true</code> if event causes a behavioral change
	 * 
	 * @since 2.0
	 */
	public boolean affectsBehavior(PropertyChangeEvent event) {
		return  fCodeScanner.affectsBehavior(event) ||
					fMultilineCommentScanner.affectsBehavior(event) ||
					fSinglelineCommentScanner.affectsBehavior(event) ||
					fStringScanner.affectsBehavior(event) ||
					fJavaDocScanner.affectsBehavior(event);
	}
	
	/**
	 * Adapts the behavior of the contained components to the change
	 * encoded in the given event.
	 * 
	 * @param event the event to which to adapt
	 * @since 2.0
	 */
	protected void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fMultilineCommentScanner.affectsBehavior(event))
			fMultilineCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineCommentScanner.affectsBehavior(event))
			fSinglelineCommentScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
		if (fJavaDocScanner.affectsBehavior(event))
			fJavaDocScanner.adaptToPreferenceChange(event);
	}
}
