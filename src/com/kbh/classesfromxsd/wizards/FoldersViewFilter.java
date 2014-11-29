package com.kbh.classesfromxsd.wizards;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FoldersViewFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer arg0, Object parentElement, Object element) {
		if (element instanceof File) {
			if (((File) element).isDirectory()) return true;
		} else {
			return false;
		}
		return false;
	
	}

}
