/*******************************************************************************
 * Copyright (c) 2000, 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jdt.internal.ui.text.correction;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.compiler.IProblem;

import org.eclipse.jdt.internal.corext.refactoring.CompositeChange;
import org.eclipse.jdt.internal.corext.refactoring.base.Change;
import org.eclipse.jdt.internal.corext.refactoring.changes.CreatePackageChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.MoveCompilationUnitChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.RenameCompilationUnitChange;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementLabels;


public class ReorgEvaluator {
	
	public static void getWrongTypeNameProposals(ProblemPosition problemPos, ArrayList proposals) throws CoreException {
		String[] args= problemPos.getArguments();
		if (args.length == 2) {
			ICompilationUnit cu= problemPos.getCompilationUnit();
			
			// rename type
			Path path= new Path(args[0]);
			String newName= path.removeFileExtension().lastSegment();
			String label= CorrectionMessages.getFormattedString("ReorgEvaluator.renametype.description", newName); //$NON-NLS-1$
			proposals.add(new ReplaceCorrectionProposal(problemPos, label, newName, 1));
			
			String newCUName= args[1] + ".java"; //$NON-NLS-1$
			final RenameCompilationUnitChange change= new RenameCompilationUnitChange(cu, newCUName);

			label= CorrectionMessages.getFormattedString("ReorgEvaluator.renamecu.description", newCUName); //$NON-NLS-1$
			// rename cu
			proposals.add(new ChangeCorrectionProposal(label, problemPos, 2) {
				protected Change getChange() throws CoreException {
					return change;
				}
			});
		}
	}
	
	public static void getWrongPackageDeclNameProposals(ProblemPosition problemPos, ArrayList proposals) throws CoreException {
		String[] args= problemPos.getArguments();
		if (args.length == 1) {
			ICompilationUnit cu= problemPos.getCompilationUnit();
			
			// correct pack decl
			proposals.add(new CorrectPackageDeclarationProposal(problemPos, 1));

			// move to pack
			IPackageFragment currPack= (IPackageFragment) cu.getParent();
			
			IPackageDeclaration[] packDecls= cu.getPackageDeclarations();
			String newPackName= packDecls.length > 0 ? packDecls[0].getElementName() : ""; //$NON-NLS-1$
				
			
			IPackageFragmentRoot root= JavaModelUtil.getPackageFragmentRoot(cu);
			IPackageFragment newPack= root.getPackageFragment(newPackName);

			String label;
			if (newPack.isDefaultPackage()) {
				label= CorrectionMessages.getFormattedString("ReorgEvaluator.movecu.default.description", cu.getElementName()); //$NON-NLS-1$
			} else {
				label= CorrectionMessages.getFormattedString("ReorgEvaluator.movecu.description", new Object[] { cu.getElementName(), newPack.getElementName() }); //$NON-NLS-1$
			}

			
			final CompositeChange composite= new CompositeChange(label);
			composite.add(new CreatePackageChange(newPack));
			composite.add(new MoveCompilationUnitChange(cu, newPack));

			proposals.add(new ChangeCorrectionProposal(label, problemPos, 2) {
				protected Change getChange() throws CoreException {
					return composite;
				}
			});
		}
	}	
	

}
