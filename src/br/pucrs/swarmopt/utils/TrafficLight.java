/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pucrs.swarmopt.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gabriel
 */
public class TrafficLight extends Tile {

    int changeStateTime;
    int iterations = 0;
    Tile neighbours[] = new Tile[4];
    Color colors[] = {Color.RED, Color.GREEN};
    public int trafficLightsState[];
    int trafficTimes[] = new int[4];
    int fileTimes[];
    int colorIndex = 0;
    int stateTimeIndex = -1;

    public TrafficLight(Tile neighbours[], int times[]) {
        super(Tile.TRAFFIC_LIGHT);
        this.neighbours = neighbours;
        fileTimes = times;
        color = Color.RED;
        setup();
    }

    private void setup() {
        int timesIndex = 0;

        if (neighbours[StreetTile.U] == null) {
            trafficTimes[StreetTile.U] = fileTimes[timesIndex++];
        }

        if (neighbours[StreetTile.D] == null) {
            trafficTimes[StreetTile.D] = fileTimes[timesIndex++];
        }

        if (neighbours[StreetTile.L] == null) {
            trafficTimes[StreetTile.L] = fileTimes[timesIndex++];
        }

        if (neighbours[StreetTile.R] == null) {
            trafficTimes[StreetTile.R] = fileTimes[timesIndex++];
        }
        
        
        //changeStateTime = (int) (TrafficMap.r.nextDouble() * 5) + 5;
        changeStateTime = getNextStateTime();
        int tCounter = 0;
        for (Tile t : neighbours) {
            tCounter = (t == null) ? tCounter + 1 : tCounter;
        }
        trafficLightsState = new int[tCounter];
        trafficLightsState[0] = 1;
    }

    private int getNextStateTime(){
        do{
            stateTimeIndex++;
            if(stateTimeIndex == fileTimes.length){
                stateTimeIndex = 0;
            }
        }while(fileTimes[stateTimeIndex] == 0);
        return fileTimes[stateTimeIndex];
    }
    
    public StreetTile getNext(){
        List<StreetTile> tiles = new ArrayList<>();
        for(Tile t:neighbours){
            if(t instanceof StreetTile){
                tiles.add((StreetTile)t);
            }
        }
        return tiles.get(TrafficMap.r.nextInt(tiles.size()));
    }
    
    @Override
    public void updateState() {
        if (iterations == changeStateTime) {
            changeStateTime = getNextStateTime();
            iterations = 0;
            change();
            return;
        }
        iterations++;
    }

    private void change() {
        int tmp = trafficLightsState[trafficLightsState.length - 1];
        for (int i = trafficLightsState.length - 1; i > 0; i--) {
            trafficLightsState[i] = trafficLightsState[i - 1];
        }
        trafficLightsState[0] = tmp;
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        int xP = x, yP = y;
        g.setPaint(Color.GRAY);
        Rectangle2D r = new Rectangle2D.Float(xP, yP, W, H);
        g.fill(r);
        g.setPaint(Color.CYAN);
        int tIndex = 0;

        g.setPaint(Color.RED);
        if (trafficTimes[StreetTile.U] != 0) {
            if (trafficLightsState[tIndex] == 1) {
                g.setPaint(Color.GREEN);
            }
            tIndex++;
            r = new Rectangle2D.Float(xP + 7, yP, 6, 6);
            g.fill(r);
        }

        g.setPaint(Color.RED);
        if (trafficTimes[StreetTile.D] != 0) {
            if (trafficLightsState[tIndex] == 1) {
                g.setPaint(Color.GREEN);
            }
            tIndex++;
            r = new Rectangle2D.Float(xP + 7, yP + 14, 6, 6);
            g.fill(r);
        }

        g.setPaint(Color.RED);
        if (trafficTimes[StreetTile.L] != 0) {
            if (trafficLightsState[tIndex] == 1) {
                g.setPaint(Color.GREEN);
            }
            tIndex++;
            r = new Rectangle2D.Float(xP, yP + 7, 6, 6);
            g.fill(r);
        }

        g.setPaint(Color.RED);
        if (trafficTimes[StreetTile.R] != 0) {
            if (trafficLightsState[tIndex] == 1) {
                g.setPaint(Color.GREEN);
            }
            tIndex++;
            r = new Rectangle2D.Float(xP + 14, yP + 7, 6, 6);
            g.fill(r);
        }

    }

}
