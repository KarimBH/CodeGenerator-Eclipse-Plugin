package com.kbh.classesfromxsd.wizards;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class XSDFilesViewFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer arg0, Object parentElement, Object element) {
		if (element instanceof File) {
			return ((File) element).getAbsolutePath().endsWith(".xsd");
		} else {
			return true;
		}
	
	}

}
