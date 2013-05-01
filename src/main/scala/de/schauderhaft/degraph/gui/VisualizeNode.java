package de.schauderhaft.degraph.gui;

import java.awt.Point;

import de.schauderhaft.degraph.model.Node;

/**
 * Die Gr��e einer vNode ist abh�ngig vom seinem Inhalt. Es gibt eine
 * Default-Gr��e, die aber bei mehr als einer beinhaltenden Node �berschritten
 * wird.
 * 
 * Je mehr Nodes diese Node enth�lt, desto gr��er ist die Darstellung!
 * 
 * 
 */
public class VisualizeNode {

	private final Node node;
	private final Point defaultSize;

	public VisualizeNode(Point size, Node node) {
		this.node = node;
		this.defaultSize = size;
	}

	public Node getNode() {
		return node;
	}

	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public NodeController createController() {
		NodeController nodeController = new NodeController(getNode());
		nodeController.setLayoutX(getX());
		nodeController.setLayoutY(getY());
		return nodeController;
	}

	public Point getSize() {
		return defaultSize;

	}

}
