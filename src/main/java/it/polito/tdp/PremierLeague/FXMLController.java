/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnTopPlayer"
    private Button btnTopPlayer; // Value injected by FXMLLoader

    @FXML // fx:id="btnDreamTeam"
    private Button btnDreamTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtK"
    private TextField txtK; // Value injected by FXMLLoader

    @FXML // fx:id="txtGoals"
    private TextField txtGoals; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	Double x;
    	try {
    		x = Double.parseDouble(this.txtGoals.getText());
    	} catch(NumberFormatException e) {
    		txtResult.appendText("Devi inserire un numero decimale!\n");
    		return;
    	}
    	model.creaGrafo(x);
    	txtResult.appendText(String.format("Grafo creato con %d vertici e %d archi!\n", model.nVertici(), model.nArchi()));
    	this.btnTopPlayer.setDisable(false);
    }

    @FXML
    void doDreamTeam(ActionEvent event) {
    	txtResult.clear();
    	Integer k;
    	try {
    		k = Integer.parseInt(this.txtK.getText());
    	} catch(NumberFormatException e) {
    		txtResult.appendText("Devi inserire un numero intero!\n");
    		return;
    	}
    	List<Player> list = model.getDreamTeam(k);
    	txtResult.appendText(String.format("Dream Team di %d giocatori con max grado titolarita %d!\n", k, model.getMaxGradoTitolarita()));
    	for(Player p: list)
    		txtResult.appendText(p.toString()+"\n");
    }

    @FXML
    void doTopPlayer(ActionEvent event) {
    	txtResult.clear();
    	this.btnDreamTeam.setDisable(false);
    	List<Adiacenza> list = model.getTopPlayer();
    	txtResult.appendText(String.format("Top player: %s\n", list.get(0).getP1()));
    	txtResult.appendText("Giocatori battuti: \n");
    	for(Adiacenza a: list)
    		txtResult.appendText(a.getP2()+" "+a.getPeso()+"\n");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnTopPlayer != null : "fx:id=\"btnTopPlayer\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDreamTeam != null : "fx:id=\"btnDreamTeam\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtK != null : "fx:id=\"txtK\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGoals != null : "fx:id=\"txtGoals\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.btnDreamTeam.setDisable(true);
    	this.btnTopPlayer.setDisable(true);
    }
}
