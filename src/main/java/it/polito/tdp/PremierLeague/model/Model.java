package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	private List<Player> best;
	private Integer maxGradoTitolarita;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<>();
	}
	
	public void creaGrafo(Double x) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		dao.listAllPlayers(idMap);
		
		// aggiungo i vertici
		Graphs.addAllVertices(grafo, dao.getVertici(x, idMap));
		
		// aggiungo gli archi
		for(Adiacenza a: dao.getAdiacenze(idMap))
			if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2()))
				Graphs.addEdge(grafo, a.getP1(), a.getP2(), a.getPeso());
	}
	
	public List<Adiacenza> getTopPlayer(){
		List<Adiacenza> list = new ArrayList<>();
		Player p = playerMaxArchiUscenti();
		if(p != null) {
			for(Player vicino: Graphs.successorListOf(grafo, p))
				list.add(new Adiacenza(p, vicino, (int) grafo.getEdgeWeight(grafo.getEdge(p, vicino))));
			Collections.sort(list);
			return list;
		}
		return null;
	}
	
	public List<Player> getDreamTeam(Integer k){
		this.best = new ArrayList<>();
		List<Player> parziale = new ArrayList<>();
		this.maxGradoTitolarita = 0;
		List<Player> battuti = new ArrayList<>();
		cerca(parziale, k, battuti);
		return best;
	}
	
	private void cerca(List<Player> parziale, Integer k, List<Player> battuti) {
		if(parziale.size() == k) {
			Integer gradoTitolarita = calcolo(parziale);
			if(gradoTitolarita > this.maxGradoTitolarita) {
				this.best = new ArrayList<>(parziale);
				this.maxGradoTitolarita = gradoTitolarita;
			}
			return;
		}
		
		for(Player p: grafo.vertexSet()) {
			if(!parziale.contains(p) && !battuti.contains(p)) {
				parziale.add(p);
				List<Player> list = battuti(p);
				battuti.addAll(list);
				cerca(parziale, k, battuti);
				battuti.removeAll(list);
				parziale.remove(p);
			}
		}
	}

	private List<Player> battuti(Player p) {
		List<Player> battuti = new ArrayList<>();
		for(Player battuto: Graphs.successorListOf(grafo, p))
			battuti.add(battuto);
		return battuti;
	}

	private Integer calcolo(List<Player> parziale) {
		int sommaU = 0;
		int sommaE = 0;
		int tot = 0;
		for(Player p: parziale) {
			for(Player battuto: Graphs.successorListOf(grafo, p))
				sommaU += (int) grafo.getEdgeWeight(grafo.getEdge(p, battuto));
			for(Player vincente: Graphs.predecessorListOf(grafo, p))
				sommaE += (int) grafo.getEdgeWeight(grafo.getEdge(vincente, p));
			tot += sommaU - sommaE;
		}
		return tot;
	}

	private Player playerMaxArchiUscenti() {
		int max = 0;
		Player best = null;
		for(Player p: grafo.vertexSet()) {
			if(grafo.outDegreeOf(p) > max) {
				max = grafo.outDegreeOf(p);
				best = p;
			}
		}
		return best;
	}

	public Integer getMaxGradoTitolarita() {
		return maxGradoTitolarita;
	}

	public int nVertici() {
		return grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return grafo.edgeSet().size();
	}
	
	
	
	
	
	
	// ESEMPI
	public void getClassifica() {
		Map<Integer, Team> idMapTeams = new HashMap<>();
		dao.loadTeams(idMapTeams);
		for(Squadra s: dao.getClassifica(idMapTeams))
			System.out.println(s.getTeam().getName()+ " "+s.getPunti()+"\n");
		
		System.out.println("\n"+"\n");
		
		dao.listAllPlayers(idMap);
		for(Giocatore g: dao.getClassificaGoal(idMap))
			System.out.println(g.getPlayer().getName()+" "+g.getGoals()+"\n");
		
		System.out.println("\n"+"\n");
		
		List<Squadra> list = new ArrayList<>();
		for(Team t: idMapTeams.values())
			list.add(dao.getAltra(t));
		Collections.sort(list);
		for(Squadra s: list)
			System.out.println(s.getTeam().getName()+ " "+s.getPunti()+"\n");
	}

}
