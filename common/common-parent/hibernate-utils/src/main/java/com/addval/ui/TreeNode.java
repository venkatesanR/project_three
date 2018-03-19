package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable{
	private static final long serialVersionUID = -1109255421191937002L;
	private String title = null;
	private int depth;
	private String  nodeId;
	private Object[] context = null;
	private List<TreeNode> children = null;
	
	public TreeNode(String title,Object[] context){
		this.title = title;
		this.context = context;
	}
    public String getTitle() {
		return title;
	}
	
    public Object[] getContext() {
		return context;
	}

    public void addChild(TreeNode childNode) {
    	if(childNode != null){
    		getChildren().add(childNode);
    	}
    }
	public List<TreeNode> getChildren() {
		if(children == null){
			children = new ArrayList<TreeNode>();
		}
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
    
	public String getNodeId() {
		return nodeId;
	}
	
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getJSOnclick(){
		return (getChildren().size() == 0)? "" : "showHideTreeNode('"+ getNodeId() +"')";
	}

	public static List<String> findActiveNodes(List<TreeNode> nodes, String nodeId,List<String> activeNodes) {
		if(nodeId == null){
			return null;
		}
		for (TreeNode node : nodes) {
			if(nodeId.startsWith(node.getNodeId())){
				activeNodes.add(node.getNodeId());	
			}
			if (nodeId.equalsIgnoreCase(node.getNodeId())) {
				break;
			}
			else {
				activeNodes = findActiveNodes(node.getChildren(),nodeId,activeNodes);
			}
		}
		return activeNodes;
	}

	public static TreeNode findTreeNode(List<TreeNode> nodes, String nodeId) {
		if(nodeId == null){
			return null;
		}
		TreeNode nodeFound = null;
		for (TreeNode node : nodes) {
			if (nodeId.equalsIgnoreCase(node.getNodeId())) {
				nodeFound = node;
				break;
			}
			else {
				nodeFound = findTreeNode(node.getChildren(),nodeId);
				if(nodeFound != null){
					break;
				}
			}
		}
		return nodeFound;
	}

	
	public static void setNodeProperties(List<TreeNode> nodes,String treeId) {
		int i = 0;
		for (TreeNode node : nodes) {
			setNodeProperties(treeId, node, 0, Integer.toString(i));
			i++;
		}
	}

	private static void setNodeProperties(String treeId, TreeNode node, int depth, String nodeId) {
		node.setDepth(depth);
		node.setNodeId(treeId + "_" + nodeId);
		int i = 0;
		for (TreeNode child : node.getChildren()) {
			setNodeProperties(treeId, child, depth + 1, nodeId + "-" + i);
			i++;
		}
	}
}
