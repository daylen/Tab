package com.daylenyang.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1776876948627606050L;
	private Set<Node> nodes;
	private Map<Object, Node> dataMap;

	public Graph() {
		nodes = new HashSet<Node>();
		dataMap = new HashMap<Object, Node>();
	}

	public void addNode(Node node) {
		nodes.add(node);
		dataMap.put(node.getData(), node);
	}

	public void addEdge(Node from, Node to) {
		nodes.add(from);
		nodes.add(to);

		from.addOutgoingNode(to);
		to.addIncomingNode(from);
	}

	public Node getNodeForData(Object data) {
		return dataMap.get(data);
	}

	public List<Node> getStrongNodes() {
		List<Node> strongNodes = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.getIncomingNodes().isEmpty()
					&& !n.getOutgoingNodes().isEmpty()) {
				strongNodes.add(n);
			}
		}
		return strongNodes;
	}

	public List<Node> getIslandNodes() {
		List<Node> islandNodes = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.getIncomingNodes().isEmpty()
					&& n.getOutgoingNodes().isEmpty()) {
				islandNodes.add(n);
			}
		}
		return islandNodes;
	}

	public List<Node> getWeakNodes() {
		List<Node> weakNodes = new ArrayList<Node>();
		for (Node n : nodes) {
			if (!n.getIncomingNodes().isEmpty()
					&& n.getOutgoingNodes().isEmpty()) {
				weakNodes.add(n);
			}
		}
		return weakNodes;
	}

	public void removeNode(Node node) {
		nodes.remove(node);
		for (Node n : node.getIncomingNodes()) {
			n.removeOutgoingNode(node);
		}
		for (Node n : node.getOutgoingNodes()) {
			n.removeIncomingNode(node);
		}
	}

}
