/*
 * component:   "openEHR Reference Implementation"
 * description: "Class XMLTerminologySource"
 * keywords:    "terminology"
 *
 * author:      "Rong Chen <rong.acode@gmail.com>"
 * copyright:   "Copyright (c) 2007 Rong Chen"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL$"
 * revision:    "$LastChangedRevision$"
 * last_change: "$LastChangedDate$"
 */
package org.openehr.terminology;

import java.io.*;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This class provides access to terminology content in XML format 
 * 
 * @author rong.chen
 */
public class XMLTerminologySource implements TerminologySource {	
	
	/**
	 * Gets an terminology source loaded with specified xml content
	 */
	public static XMLTerminologySource getInstance(String xmlfilename) 
			throws Exception {
		return new XMLTerminologySource(xmlfilename);
	}	
	
	public List<CodeSet> getCodeSets() {
		return codeSetList;
	}

	public List<Group> getConceptGroups() {
		return groupList;
	}
	
	/*
	 * Constructs an instance loaded with terminology content
	 */
	private XMLTerminologySource(String filename) throws Exception {
		codeSetList = new ArrayList<CodeSet>();
		groupList = new ArrayList<Group>();
		loadTerminologyFromXML(filename);
	}
	
	private void loadTerminologyFromXML(String filename) throws Exception {		
		SAXReader builder = new SAXReader();
		InputStream input = this.getClass().getResourceAsStream(filename);
		try {
			Document doc = builder.read(input);
			Element root = doc.getRootElement();
			List codesets = root.elements("codeset");
			codeSetList.clear();
			groupList.clear();
			
			for(Iterator it = codesets.iterator(); it.hasNext();) {
				Element element = (Element) it.next();
				codeSetList.add(loadCodeSet(element));
			}
			
			List groups = root.elements("group");
			for(Iterator it = groups.iterator(); it.hasNext();) {
				Element element = (Element) it.next();
				groupList.add(loadGroup(element));
			}
		} finally {
			if(input != null) {
				input.close();
			}
		}
	}
	
	/*
	 * Loads a code set from XML element
	 */
	private CodeSet loadCodeSet(Element element) {
		CodeSet codeset = new CodeSet();
		codeset.openehrId = element.attributeValue("openehr_id");
		codeset.issuer = element.attributeValue("issuer");
		codeset.externalId = element.attributeValue("external_id");
		List children = element.elements("code");
		for(Iterator it = children.iterator(); it.hasNext();) {
			Element code = (Element) it.next();
			codeset.addCode(code.attributeValue("value"));
		}
		return codeset;
	}
	
	/*
	 * Loads a concept group from XML element
	 */
	private Group loadGroup(Element element) {
		Group group = new Group();
		group.name = element.attributeValue("name");
		
		List children = element.elements("concept");
		for(Iterator it = children.iterator(); it.hasNext();) {
			Concept concept = new Concept();
			Element e = (Element) it.next();
			concept.id = (e.attributeValue("id"));
			concept.rubric = (e.attributeValue("rubric"));
			group.addConcept(concept);
		}
		return group;
	}
	
	private List<Group> groupList;
	private List<CodeSet> codeSetList;	
}
/*
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the 'License'); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an 'AS IS' basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 *  The Original Code is XMLTerminologySource.java
 *
 *  The Initial Developer of the Original Code is Rong Chen.
 *  Portions created by the Initial Developer are Copyright (C) 2007
 *  the Initial Developer. All Rights Reserved.
 *
 *  Contributor(s):
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */