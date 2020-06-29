package it.polito.tdp.PremierLeague.model;

public class Giocatore {
	
	private Player player;
	private Integer goals;
	
	public Giocatore(Player player, Integer goals) {
		super();
		this.player = player;
		this.goals = goals;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Integer getGoals() {
		return goals;
	}

	public void setGoals(Integer goals) {
		this.goals = goals;
	}

}
