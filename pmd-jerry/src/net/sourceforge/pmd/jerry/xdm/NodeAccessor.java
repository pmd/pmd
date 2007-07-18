package net.sourceforge.pmd.jerry.xdm;

import java.util.Iterator;

public interface NodeAccessor {
	Iterator<Object> getAttributes(Object node);

	String getBaseURI(Object node);

	Iterator<Object> getChildren(Object node);

	String getDocumentURI(Object node);

	boolean isId(Object node);

	boolean isIdRefs(Object node);

	Iterator<Object> getNamespaceBindings(Object node);

	Iterator<Object> getNamespaceNodes(Object node);

	boolean isNilled(Object node);

	NodeKind getNodeKind(Object node);

	String getNodeName(Object node);

	Object getParent(Object node);

	String getStringValue(Object node);

	String getTypeName(Object node);

	Iterator getTypedValue(Object node);

	String getUnparsedEntityPublicId(Object node);

	String getUnparsedEntitySystemId(Object node);
}
