package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	private Map<Airport, Airport> visita;
	
	public Model() {
		dao=new ExtFlightDelaysDAO();
		idMap=new HashMap<>();
		dao.loadAllAirports(idMap);
	}
	
	public void creaGrafo(int x) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Ho sottoinsieme di vertici (solo quelli con n.Airlines>=x)
		//Aggiungo solo quelli che mi servono
		Graphs.addAllVertices(grafo, dao.getVertici(idMap, x));
		
		//Aggiungo gli archi
		//Se non c'è nessun arco che collega i due nodi allora lo aggiungo
		//Se esite già allora lo prendo e ne aggiorno il peso
		for(Rotta r:dao.getRotte(idMap)) {
			//Controllo se è una rotta di interesse (Airport coinvolti sono effettivamente vertici del grafo)
			if(this.grafo.containsVertex(r.getA1()) && this.grafo.containsVertex(r.getA2())) {
				DefaultWeightedEdge e=this.grafo.getEdge(r.getA1(), r.getA2());
				if(e==null) { //non ho ancora l'arco (ne in un verso ne in quello opposto
					Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getNumVoli());
				} else {
					double pesoVecchio=this.grafo.getEdgeWeight(e);
					double pesoNuovo=pesoVecchio+r.getNumVoli();
					this.grafo.setEdgeWeight(e, pesoNuovo);
				}
			}
		}
		
		System.out.println("Grago creato");
		System.out.println("#Vertici: "+grafo.vertexSet().size());
		System.out.println("#Archi: "+grafo.edgeSet().size());
	}

	public Set<Airport> getVertici() {
		return this.grafo.vertexSet();
	}
	
	public List<Airport> trovaPercorso(Airport a1, Airport a2){
		List<Airport> percorso=new LinkedList<>();
		
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it=new BreadthFirstIterator<>(grafo, a1);
		
		visita=new HashMap<>();
		visita.put(a1, null);
		it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				Airport airport1=grafo.getEdgeSource(e.getEdge());
				Airport airport2=grafo.getEdgeTarget(e.getEdge());
				
				if(visita.containsKey(airport1) && !visita.containsKey(airport2)) {
					visita.put(airport2, airport1); //arrivo ad 'a2' da 'a1'
				} else if(visita.containsKey(airport2) && !visita.containsKey(airport1)) {
					visita.put(airport1, airport2); //arrivo ad 'a1' da 'a2'
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(it.hasNext()) {
			it.next();
		}
		
		//Dall'albero di visita posso andare a ritroso da a2 a a1
		//e vedere se esite un percorso
		
		if(!visita.containsKey(a2) || !visita.containsKey(a1))
			return null;
		
		percorso.add(a2);
		Airport step=a2;
		while(visita.get(step)!=null) {
			step=visita.get(step); // a questo step da chi era preceduto?
			percorso.add(step);
		}
		//così ho percorso al contrario (ci sono metodi per ottenere il percorso nell'ordine corretto)
		//se avrà size==0 -> non esiste percorso
		return percorso;
	}

}
