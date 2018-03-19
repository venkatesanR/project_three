package com.addval.action;

import java.io.Serializable;
import java.util.*;

public class ActionMessages implements Serializable {
	private static final long serialVersionUID = 6206263260199351653L;
	public static final String GLOBAL_MESSAGE = "com.addval.action.GLOBAL_MESSAGE";
	protected boolean accessed;
	protected HashMap messages;
	protected int iCount;

	public ActionMessages() {
		accessed = false;
		messages = new HashMap();
		iCount = 0;
	}

	public ActionMessages(ActionMessages messages) {
		accessed = false;
		this.messages = new HashMap();
		iCount = 0;
		add(messages);
	}

	public void add(String property, ActionMessage message) {
		ActionMessageItem item = (ActionMessageItem) messages.get(property);
		List list = null;
		if (item == null) {
			list = new ArrayList();
			item = new ActionMessageItem(list, iCount++, property);
			messages.put(property, item);
		}
		else {
			list = item.getList();
		}
		list.add(message);
	}

	public void add(ActionMessages messages) {
		if (messages == null) {
			return;
		}
		for (Iterator props = messages.properties(); props.hasNext();) {
			String property = (String) props.next();
			List<ActionMessage> msgs = messages.get(property);
			for(ActionMessage msg : msgs){
				this.add(property, msg);
			}

		}

	}

	public void clear() {
		messages.clear();
	}

	public boolean isEmpty() {
		return messages.isEmpty();
	}

	public List<ActionMessage> get() {
		accessed = true;
		if (this.messages.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		ArrayList results = new ArrayList();
		ArrayList actionItems = new ArrayList();
		for (Iterator i = this.messages.values().iterator(); i.hasNext(); actionItems.add(i.next())) {
		}
		Collections.sort(actionItems, actionItemComparator);
		for (Iterator i = actionItems.iterator(); i.hasNext();) {
			ActionMessageItem ami = (ActionMessageItem) i.next();
			for (Iterator messages = ami.getList().iterator(); messages.hasNext(); results.add((ActionMessage)messages.next())) {
			}
		}
		return results;
	}

	public List<ActionMessage> get(String property) {
		accessed = true;
		ActionMessageItem item = (ActionMessageItem) messages.get(property);
		if (item == null) {
			return Collections.EMPTY_LIST;
		}
		else {
			return item.getList();
		}
	}

	public boolean isAccessed() {
		return accessed;
	}

	public Iterator properties() {
		if (messages.isEmpty()) {
			return Collections.EMPTY_LIST.iterator();
		}
		ArrayList results = new ArrayList();
		ArrayList actionItems = new ArrayList();
		for (Iterator i = messages.values().iterator(); i.hasNext(); actionItems.add(i.next())) {
		}
		Collections.sort(actionItems, actionItemComparator);
		ActionMessageItem ami;
		for (Iterator i = actionItems.iterator(); i.hasNext(); results.add(ami.getProperty())) {
			ami = (ActionMessageItem) i.next();
		}

		return results.iterator();
	}

	public int size() {
		int total = 0;
		for (Iterator i = messages.values().iterator(); i.hasNext();) {
			ActionMessageItem ami = (ActionMessageItem) i.next();
			total += ami.getList().size();
		}

		return total;
	}

	public int size(String property) {
		ActionMessageItem item = (ActionMessageItem) messages.get(property);
		return item != null ? item.getList().size() : 0;
	}

	public String toString() {
		return messages.toString();
	}

	protected class ActionMessageItem implements Serializable {

		protected List list;
		protected int iOrder;
		protected String property;

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}

		public int getOrder() {
			return iOrder;
		}

		public void setOrder(int iOrder) {
			this.iOrder = iOrder;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		public String toString() {
			return list.toString();
		}

		public ActionMessageItem(List list, int iOrder, String property) {
			this.list = null;
			this.iOrder = 0;
			this.property = null;
			this.list = list;
			this.iOrder = iOrder;
			this.property = property;
		}
	}

	private static final Comparator actionItemComparator = new Comparator() {

		public int compare(Object o1, Object o2) {
			return ((ActionMessageItem) o1).getOrder() - ((ActionMessageItem) o2).getOrder();
		}

	};
}
