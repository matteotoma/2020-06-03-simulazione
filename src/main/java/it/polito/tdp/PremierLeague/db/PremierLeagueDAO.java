package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Giocatore;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Squadra;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer, Player> idMap){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				idMap.put(player.getPlayerID(), player);
				result.add(player);
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getVertici(Double x, Map<Integer, Player> idMap){
		String sql = "SELECT a.PlayerID AS p, AVG(a.Goals) m " + 
				"FROM actions AS a " + 
				"GROUP BY a.PlayerID " + 
				"HAVING m>? ";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setDouble(1, x);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = idMap.get(res.getInt("p"));
				if(player != null)
					result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getAdiacenze(Map<Integer, Player> idMap){
		String sql = "SELECT a1.PlayerID AS p1, a2.PlayerID p2, SUM(a1.TimePlayed) t1, SUM(a2.TimePlayed) t2 " + 
				"FROM actions AS a1, actions a2 " + 
				"WHERE a1.MatchID=a2.MatchID AND a1.PlayerID>a2.PlayerID " + 
				"AND a2.`Starts`=1 AND a1.`Starts`=1 " + 
				"AND a1.TeamID!=a2.TeamID " + 
				"GROUP BY a1.PlayerID, a2.PlayerID " +
				"HAVING t1!=t2 ";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Player p1 = idMap.get(res.getInt("p1"));
				Player p2 = idMap.get(res.getInt("p2"));
				if(p1 != null && p2 != null) {
					int t1 = res.getInt("t1");
					int t2 = res.getInt("t2");
					if(t1 > t2)
						result.add(new Adiacenza(p1, p2, t1-t2));
					else
						result.add(new Adiacenza(p2, p1, t2-t1));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void loadTeams(Map<Integer, Team> idMapTeams) {
		String sql = "SELECT t.TeamID AS id, t.Name n " + 
				"FROM teams AS t ";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

			Team t = new Team(res.getInt("id"), res.getString("n"));
				idMapTeams.put(t.getTeamID(), t);
				result.add(t);
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Squadra> getClassifica(Map<Integer, Team> idMapTeams) {
		String sql = "SELECT m.TeamHomeID AS h, m.TeamAwayID a, m.ResultOfTeamHome r " + 
				"FROM matches AS m " + 
				"ORDER BY m.Date ASC ";
		Map<Team, Squadra> map = new HashMap<>();
		Connection conn = DBConnect.getConnection();
		int i=0;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Team casa = idMapTeams.get(res.getInt("h"));
				Team trasferta  = idMapTeams.get(res.getInt("a"));
				
				if(!map.containsKey(casa)) {
					Squadra home = new Squadra(casa, 0);
					map.put(casa, home);
					i++;
				}
				if(!map.containsKey(trasferta)) {
					Squadra away = new Squadra(trasferta, 0);
					map.put(trasferta, away);
					i++;
				}
					
				// vince la squadra di casa
				if(res.getInt("r") == 1)
					map.get(casa).incremento(3);
					
				// vince la squadra di trasferta
				if(res.getInt("r") == -1)
					map.get(trasferta).incremento(3);
					
				// pareggio
				if(res.getInt("r") == 0) {
					map.get(casa).incremento(1);
					map.get(trasferta).incremento(1);
				}
			}
			
			System.out.println(i+"\n");
			List<Squadra> result = new ArrayList<>(map.values());
			Collections.sort(result);
			
			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Giocatore> getClassificaGoal(Map<Integer, Player> idMap){
		String sql = "SELECT a.PlayerID AS id, SUM(a.Goals) g " + 
				"FROM actions AS a " + 
				"GROUP BY a.PlayerID " + 
				"ORDER BY g DESC ";
		List<Giocatore> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = idMap.get(res.getInt("id"));
				result.add(new Giocatore(player, res.getInt("g")));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Squadra getAltra(Team t){
		String sql = "SELECT m.TeamHomeID AS h, m.TeamAwayID a, m.ResultOfTeamHome r " + 
				"FROM matches AS m " + 
				"WHERE m.TeamHomeID=? OR m.TeamAwayID=? ";
		Squadra s = new Squadra(t, 0);
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, t.getTeamID());
			st.setInt(2, t.getTeamID());
			ResultSet res = st.executeQuery();

			while (res.next()) {
					
				// vince in casa
				if(res.getInt("r")==1 && res.getInt("h")==t.getTeamID()) 
					s.incremento(3);
					
				// vince in trasferta
				if(res.getInt("r")==-1 && res.getInt("a")==t.getTeamID()) 
					s.incremento(3);
					
				// pareggio
				if(res.getInt("r")==0)
					s.incremento(1);					
			}
			
			conn.close();
			return s;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
