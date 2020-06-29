package it.polito.tdp.PremierLeague.model;

public class Squadra implements Comparable<Squadra>{
	
	private Team team;
	private Integer punti;
	
	public Squadra(Team team, Integer punti) {
		super();
		this.team = team;
		this.punti = punti;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Integer getPunti() {
		return punti;
	}

	public void setPunti(Integer punti) {
		this.punti = punti;
	}
	
	public void incremento(Integer tot) {
		this.punti += tot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Squadra other = (Squadra) obj;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		return true;
	}

	@Override
	public int compareTo(Squadra o) {
		return o.getPunti().compareTo(this.punti);
	}

}
