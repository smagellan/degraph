package de.schauderhaft.degraph.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.schauderhaft.degraph.java.JavaGraph;
import de.schauderhaft.degraph.java.JavaHierarchicGraph;
import de.schauderhaft.degraph.java.NodeBuilder;
import de.schauderhaft.degraph.model.Node;
import de.schauderhaft.degraph.model.SimpleNode;

/**
 * open the degraph visualisation
 * 
 */
public class GuiStarter extends javafx.application.Application {

	/**
	 * Temporal storage
	 * 
	 * This is butt ugly, but JavaFx seems to insist to create the Application
	 * instance through a parameterless constructor. So we have to put the
	 * reference to the data we want to show in a dropbox (private static
	 * variable), so we can pick it up once JavaFx is so kind to start us.
	 */
	private static JavaHierarchicGraph graph;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// now we can access graph here and put it where ever we need it. I
		// hope.
		System.out.println(graph);

		primaryStage.setTitle("degraph");

		Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));

		// find the proper parent node in the SceneGraph, loop through the nodes
		// in graph and add them as JavaFXNodeThingies to the parent.

		// Adding HBox to the scene
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public void show(JavaHierarchicGraph g) {
		graph = g;
		launch(new String[0]);
	}

	/**
	 * TODO: delete this entrypoint
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Geht");
		GuiStarter s = new GuiStarter();
		// s.show(null);

		JavaGraph graph = new JavaGraph();
		graph.add(new SimpleNode("type", "the name"));

		s.show(graph);
	}

	private static Node asNode(String s) {
		return NodeBuilder.create() //
				.name(s) //
				.typ("chesspiece") //
				.createSimpleNode();
	}

}
