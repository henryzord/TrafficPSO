
import br.pucrs.swarmopt.gui.TrafficMapGUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author simoes.adm
 */
public class Exec {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	if(args.length != 1) {
	    System.out.println("Syntax: java -jar <jar_file> <path_to_input_files>");
	} else {
	    String path = args[0];
	    
	    TrafficMapGUI tmg = new TrafficMapGUI(path);
	    tmg.start();
	    tmg.setVisible(true);
	}
    }
    
}
