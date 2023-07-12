package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private Graph<ArtObject, DefaultWeightedEdge> graph;
	private List<ArtObject> allNodes;
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.allNodes = new ArrayList<>();	
		this.dao = new ArtsmiaDAO();
		this.idMap = new HashMap<>();
	}
	
	private void loadNodes() {     //riempie la lista di vertici (oggetti d'arte) presi dal db
		if(this.allNodes.isEmpty()) 
		this.allNodes = this.dao.listObjects(); 
		
		if(this.idMap.isEmpty()) {
			for(ArtObject a: this.allNodes) {
				this.idMap.put(a.getId(), a);  }
		}	
	}
	
	public void buildGraph() {
		loadNodes();
		Graphs.addAllVertices(this.graph, allNodes);  //aggiunge i vertici nel grafo
		
		//METODO PER I DATABASE PICCOLI
		//for(ArtObject a1: allNodes) {
		//	for(ArtObject a2: allNodes) {
		//		int peso = dao.getWeight(a1.getId(), a1.getId());
		//		Graphs.addEdgeWithVertices(this.graph, a1, a2, peso);
		//	}  }
		
		//METODO PER DATABASE GRANDI
		List<EdgeModel> allEdges = this.dao.getAllWeight(idMap); // lista che contiene tutti gli archi
		for(EdgeModel edgeI: allEdges) {
			Graphs.addEdgeWithVertices(graph, edgeI.getSource(), edgeI.getTarget(), edgeI.getPeso()); }
			
			System.out.println("Il grafo ha " + graph.vertexSet().size() + " vertici.");
			System.out.println("Il grafo ha " + graph.edgeSet().size() + " vertici.");
		
	}
	
	
	public boolean isIDInGraph (Integer objID) {
		if(this.idMap.get(objID) != null) {
			return true;  }
		return false;   }
	
	public String oggettoPresente (Integer objID) {
		for(ArtObject a: this.allNodes) {
			if(a.getId()== objID)  
				return a.toString();  }
		return null;   }
	
	public Integer calcolaConnessa(Integer objID) {
		
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> iterator = 
				new DepthFirstIterator<>(graph, idMap.get(objID));
		
		List<ArtObject> compConnessa = new ArrayList<>();
		
		while(iterator.hasNext()) {   //ti dice solo se l'iteratore ha un nodo dopo (come il result set)
			compConnessa.add(iterator.next());  }
		
		//ALTRO MODO con ConnectivityInspector: crea degli oggetti che ci danno informazioni sulle
		//informazione di connessione dei dati
		ConnectivityInspector<ArtObject, DefaultWeightedEdge> inspector = new ConnectivityInspector<>(graph);  //si crea come un iteratore
		Set<ArtObject> setConnesso = inspector.connectedSetOf(idMap.get(objID)); //metodo che vuole il source: prende un nodo e mi dice la dimensione della componente connessa
		
		return compConnessa.size();
		//oppure equilavente è setConnesso.size();
	}
	
}