package com.salexandru.xcore.preferencepage;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.salexandru.xcore.interfaces.XEntity;

public class XCorexPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {
	
	private IJavaProject theProject;
	
	public XCorexPropertyPage() {
	    setDescription("Properties of the generated meta-model");
	}
		
	@Override
	protected void createFieldEditors() {
		addField(new XCorexFieldEditor(theProject, getFieldEditorParent()));
	}

	@Override
	public IAdaptable getElement() {
		return theProject;
	}

	@Override
	public void setElement(IAdaptable element) {
		theProject = (IJavaProject)element;
		setPreferenceStore(new XCorePropertyStore(theProject));
	}

	public static class XCorePropertyStore extends PreferenceStore {
		private IJavaProject prj;
		private String pageId = "com.salexandru.xcore.XCorePropertyPage";
		public XCorePropertyStore(IJavaProject prj) {
			this.prj = prj;
			load();
		}
		public void save() {}
		public void doSave() {
			try {
				String[] allNames = super.preferenceNames();
				String value = "";
				for(String aName : allNames) {
					value+= aName + "," + super.getString(aName) + ";";
				}
				prj.getResource().setPersistentProperty(new QualifiedName(pageId, "XCore bindings definition"), value);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		public void doSave(Set<String> toRemove) {
			try {
				String[] allNames = super.preferenceNames();
				String value = "";
				for(String aName : allNames) {
					if(!toRemove.contains(aName))
						value+= aName + "," + super.getString(aName) + ";";
				}
				prj.getResource().setPersistentProperty(new QualifiedName(pageId, "XCore bindings definition"), value);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		public void load() {
			try {
				String value = prj.getResource().getPersistentProperty(new QualifiedName(pageId, "XCore bindings definition"));
				if(value == null || value.equals("")) return;
				String[] allEntries = value.split(";");
				for(String anEntry : allEntries) {
					String values[] = anEntry.split(",");
					super.setValue(values[0],values[1] + "," + values[2]);
					super.setDefault(values[0], Object.class.getCanonicalName() + "," + XEntity.class.getCanonicalName());
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		public void setBindings(String entity, String underlyingType, String upLinkType) {
			super.setValue(entity, underlyingType + "," + upLinkType);
			super.setDefault(entity, Object.class.getCanonicalName() + "," + XEntity.class.getCanonicalName());
		}
		public void setDefaultBindings(String entity) {
			super.setDefault(entity, Object.class.getCanonicalName() + "," + XEntity.class.getCanonicalName());
			super.setToDefault(entity);
		}
		public String getExtendedMetaType(String entity) {
			String tmp = super.getString(entity);
			return !tmp.equals("") ? tmp.substring(tmp.indexOf(",") + 1) : null;
		}

		public String getUnderlyingMetaType(String entity) {
			String tmp = super.getString(entity);
			return !tmp.equals("") ? tmp.substring(0,tmp.indexOf(",")) : null;
		}
				
		public String[][] toMatrix() {
			String[] allNames = super.preferenceNames();
			String[][] res = new String[allNames.length][3];
			for(int i = 0; i < allNames.length; i++ ) {
				res[i][0] = allNames[i];
				res[i][1] = this.getUnderlyingMetaType(allNames[i]);
				res[i][2] = this.getExtendedMetaType(allNames[i]);
			}
			return res;
		}
		public IJavaProject getProject() {
			return prj;
		}

	}

}
