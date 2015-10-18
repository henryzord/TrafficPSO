/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pucrs.swarmopt.utils;

import br.pucrs.swarmopt.gui.TrafficMapGUI;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author gabriel
 */
public class TrafficMap {
    static Tile map[][] = new Tile[20][30];
    public static Random r;
    public static int iterations = 0;
    
    int remaining_cars = -1;
    
    StreetTile inputCars;
//    String mapFilePath = "/home/gabriel/Desktop/cars_app/map.txt";
//    String trafficTimesFilePath = "/home/gabriel/Desktop/cars_app/times.txt";
//    String inputParams = "/home/gabriel/Desktop/cars_app/input_params.txt";
//    String outputParams = "/home/gabriel/Desktop/cars_app/output_params.txt";
    
    String mapFilePath = "c:/temp/cars_app/map.txt";
    String trafficTimesFilePath = "c:/temp/cars_app/times.txt";
    String inputParams = "c:/temp/cars_app/input_params.txt";
    String outputParams = "c:/temp/cars_app/output_params.txt";
    
    List<StreetTile> mapOutput = new ArrayList<StreetTile>();
    List<StreetTile> mapInput = new ArrayList<StreetTile>();
    List<TrafficLight> trafficLights = new ArrayList<>();
    List<Vehicle> vehicles = new ArrayList<Vehicle>();
    String lblBaseText = "";
    String lblIterationsBaseText = "";
    boolean flag = false;
    PrintWriter outputParamsWriter;
    
    
    JFileChooser fChooser = new JFileChooser(System.getProperty("user.home"));

    public TrafficMap() {
        setup();
        lblBaseText = TrafficMapGUI.lblTotalCars.getText();
        lblIterationsBaseText = TrafficMapGUI.lblIterations.getText();
        System.out.println("");
    }

    private void setup() {
//        JOptionPane.showMessageDialog(null, "Select map file...");
//        fChooser.showOpenDialog(null);
//        mapFilePath = fChooser.getSelectedFile().getAbsolutePath();
//        
//        JOptionPane.showMessageDialog(null, "Select traffic lights time file...");
//        fChooser.showOpenDialog(null);
//        trafficTimesFilePath = fChooser.getSelectedFile().getAbsolutePath();
        
        try {
            FileReader mapFile = new FileReader(mapFilePath);
            BufferedReader mapReader = new BufferedReader(mapFile);

            FileReader trafficTimesFile = new FileReader(trafficTimesFilePath);
            BufferedReader trafficTimesReader = new BufferedReader(trafficTimesFile);

            FileReader inputParamsFile = new FileReader(inputParams);
            BufferedReader inputParamsReader = new BufferedReader(inputParamsFile);

            FileWriter outputParamsFile = new FileWriter(outputParams);
            outputParamsWriter = new PrintWriter(outputParamsFile);
            
	    String str_iters = inputParamsReader.readLine();
	    str_iters = str_iters.substring(0, str_iters.indexOf("//")).trim();
	    iterations = Integer.parseInt(str_iters);
	    
	    String str_cars = inputParamsReader.readLine();
	    str_cars = str_cars.substring(0, str_cars.indexOf("//")).trim();
	    this.remaining_cars = Integer.parseInt(str_cars);
	    
	    String str_random = inputParamsReader.readLine();
	    str_random = str_random.substring(0, str_random.indexOf("//")).trim();
	    
	     r = new Random(Integer.parseInt(str_random));
	    
            String line;
            char tiles[];
            int i = 0;
            while ((line = mapReader.readLine()) != null) {
                tiles = line.toCharArray();
                for (int j = 0; j < tiles.length; j++) {
                    switch (tiles[j]) {
                        case '#':
                            map[i][j] = new BuildingTile();
                            break;
                        case 'R':
                            map[i][j] = new StreetTile(StreetTile.R);
                            break;
                        case 'U':
                            map[i][j] = new StreetTile(StreetTile.U);
                            break;
                        case 'L':
                            map[i][j] = new StreetTile(StreetTile.L);
                            break;
                        case 'D':
                            map[i][j] = new StreetTile(StreetTile.D);
                            break;
                        case '8':
                            map[i][j] = null;
                            break;
                        default:
                            JOptionPane.showMessageDialog(null,
                                    "Invalida map configuration!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                    }

                    if (inputCars == null && tiles[j] == 'R') {
                        inputCars = (StreetTile) map[i][j];
                    }
                }
                i++;

            }

            for (int ii = 0; ii < map.length; ii++) {
                for (int j = 0; j < map[ii].length; j++) {
                    if (map[ii][j] == null) {
                        map[ii][j] = new TrafficLight(getStreetNeighbours(map, ii, j), Utils.trafficLightsTimes(trafficTimesReader.readLine()));
                        trafficLights.add((TrafficLight) map[ii][j]);
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof StreetTile) {
                    ((StreetTile) map[i][j]).setNeighbours(getStreetNeighbours(map, i, j));
                }

                if ((i == 0 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.D)
                        || (j == 0 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.R)
                        || (i == map.length - 1 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.U)
                        || (j == map[0].length - 1 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.L)) {
                    mapInput.add((StreetTile) map[i][j]);
                }

                if ((i == 0 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.U)
                        || (j == 0 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.L)
                        || (i == map.length - 1 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.D)
                        || (j == map[0].length - 1 && map[i][j] instanceof StreetTile && ((StreetTile) map[i][j]).getWay() == StreetTile.R)) {
                    mapOutput.add((StreetTile) map[i][j]);
                }
            }
        }
        System.out.println("Parar!");
    }

    //TODO: all available cars at map.
    //TODO: average trip time.
    //TODO: average trip lenght
    public static Tile nextTile(Tile t, int x, int y) {
        if (map[x][y] instanceof StreetTile) {
            List<Tile> tiles = new ArrayList<>();
            for (Tile aTile : ((StreetTile) map[x][y]).neighbours) {
                if (aTile instanceof StreetTile || aTile instanceof TrafficLight) {
                    tiles.add(aTile);
                }
            }
            return tiles.get(r.nextInt(tiles.size()));
        }

        return null;
    }

    public static Tile[] getNeighbours(Tile[][] map, int i, int j) {
        Tile neightbours[] = new Tile[4];
        
        neightbours[StreetTile.U] = (i == 0)? null : map[i - 1][j];
        neightbours[StreetTile.D] = (i == map.length - 1) ? null : map[i + 1][j];
        neightbours[StreetTile.L] = (j == 0) ? null : map[i][j - 1];
        neightbours[StreetTile.R] = (j == map[i].length - 1) ? null : map[i][j + 1];
        return neightbours;
    }

    public static Tile[] getStreetNeighbours(Tile[][] map, int i, int j) {
        Tile neightbours[] = new Tile[4];
        StreetTile thisTile = (StreetTile) map[i][j];
        StreetTile theStreetTile;
        Tile theTile;

        neightbours = getNeighbours(map, i, j);

        theTile = neightbours[StreetTile.U];
        if (theTile instanceof StreetTile) {
            theStreetTile = (StreetTile) theTile;
            neightbours[StreetTile.U] = (theStreetTile.getWay() == StreetTile.D) ? null : neightbours[StreetTile.U];
        }

        theTile = neightbours[StreetTile.D];
        if (theTile instanceof StreetTile) {
            theStreetTile = (StreetTile) theTile;
            neightbours[StreetTile.D] = (theStreetTile.getWay() == StreetTile.U) ? null : neightbours[StreetTile.D];
        }

        theTile = neightbours[StreetTile.L];
        if (theTile instanceof StreetTile) {
            theStreetTile = (StreetTile) theTile;
            neightbours[StreetTile.L] = (theStreetTile.getWay() == StreetTile.R) ? null : neightbours[StreetTile.L];
        }

        theTile = neightbours[StreetTile.R];
        if (theTile instanceof StreetTile) {
            theStreetTile = (StreetTile) theTile;
            neightbours[StreetTile.R] = (theStreetTile.getWay() == StreetTile.L) ? null : neightbours[StreetTile.R];
        } else {

        }

        if (thisTile instanceof StreetTile) {
            if (thisTile.getWay() == StreetTile.D) {
                neightbours[StreetTile.U] = null;
            }

            if (thisTile.getWay() == StreetTile.U) {
                neightbours[StreetTile.D] = null;
            }

            if (thisTile.getWay() == StreetTile.R) {
                neightbours[StreetTile.L] = null;
            }

            if (thisTile.getWay() == StreetTile.L) {
                neightbours[StreetTile.R] = null;
            }
        }

        return neightbours;
    }

    public void updateState() {
        removeVehicles();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j].updateState();
                if (map[i][j] instanceof StreetTile) {
                    StreetTile st = (StreetTile) map[i][j];
                    Vehicle vs[] = st.getVehicles().toArray(new Vehicle[st.getVehicles().size()]);
                    for (int k = 0; k < vs.length; k++) {
                        vs[k].updatePosition(i, j);
                    }
                }
            }
        }

	if(this.remaining_cars > 0) {
	    addVehicles();
	}
		
	updateVehicles();
        //TODO Create GUI panel display update method.
        TrafficMapGUI.lblTotalCars.setText(lblBaseText + vehicles.size());
        TrafficMapGUI.lblIterations.setText(lblIterationsBaseText + iterations);
        
        if(iterations == 0 || vehicles.size() == 0) {
            outputParamsWriter.println(vehicles.size() + " // carros restantes ao fim da simulação");
	    outputParamsWriter.println(iterations + " // iterações ao fim da simulação");
            outputParamsWriter.flush();
	    System.exit(0);  // termina aplicação
        }
    }

    private void updateVehicles() {
        for (Vehicle vehicle : vehicles) {
            vehicle.updated = false;
        }
    }

    private void removeVehicles() {
        for (StreetTile tile : mapOutput) {
            vehicles.removeAll(tile.getVehicles());
            tile.getVehicles().clear();
        }
    }

    public void addVehicles() {
	for(int i = 0; i < mapInput.size(); i++) {
	    StreetTile tile = mapInput.get(i);
	    
	    int free_space = tile.availableSpace();
	    if(free_space > 0) {
		int s = ((abs(r.nextInt()) % free_space) + 1) % remaining_cars; 
		remaining_cars -= s;

		for (int j = 0; j < s; j++) {
		    Vehicle v = new Vehicle(tile);
		    v.updated = true;
		    vehicles.add(v);
		    tile.addVehicles(v);
		}
	    }
	}
    }

    public void printMap(Graphics2D canvas) {
        int xP = 0, yP = 0;
        for (Tile[] map2 : map) {
            for (Tile map1 : map2) {
                map1.draw(canvas, xP, yP);
                xP += Tile.W;
            }
            xP = 0;
            yP += Tile.H;
        }

    }

}
