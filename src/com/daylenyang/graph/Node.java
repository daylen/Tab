package com.daylenyang.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -956997957724937250L;
	private Object data;
	private List<Node> incomingNodes;
	private List<Node> outgoingNodes;

	public Node(Object data) {
		this.data = data;
		incomingNodes = new ArrayList<Node>();
		outgoingNodes = new ArrayList<Node>();
	}

	public Object getData() {
		return data;
	}

	public List<Node> getIncomingNodes() {
		return incomingNodes;
	}

	public List<Node> getOutgoingNodes() {
		return outgoingNodes;
	}

	public void addIncomingNode(Node n) {
		incomingNodes.add(n);
	}

	public void addOutgoingNode(Node n) {
		outgoingNodes.add(n);
	}

	public void removeIncomingNode(Node n) {
		incomingNodes.remove(n);
	}

	public void removeOutgoingNode(Node n) {
		outgoingNodes.remove(n);
	}

}
