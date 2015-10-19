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
public class StreetTile extends Tile {

    public static final int U = 0, D = 1, L = 2, R = 3, MAX_VEHICLES = 16;
    private int way;
    private List<Vehicle> vehicles;
    public Tile neighbours[] = new Tile[4];

    public StreetTile(int way) {
        super(Tile.STREET);
        color = Color.GRAY;
        this.way = way;
        vehicles = new ArrayList<Vehicle>();
    }
    
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    
    public int getWay() {
        return way;
    }

    public void setNeighbours(Tile[] neighbours) {
        this.neighbours = neighbours;
    }

    public void addVehicles(Vehicle v) {
        vehicles.add(v);
    }

    public int availableSpace() {
        return MAX_VEHICLES - vehicles.size();
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        Rectangle2D r;
        g.setPaint(Color.GRAY);
        r = new Rectangle2D.Float(x, y, 20, 20);
        g.fill(r);

        if (way == U || way == D) {
            g.setPaint(Color.WHITE);
            r = new Rectangle2D.Float(x + 9, y + 5, 2, 10);
            g.fill(r);
        } else {
            g.setPaint(Color.WHITE);
            r = new Rectangle2D.Float(x + 5, y + 9, 10, 2);
            g.fill(r);
        }

        g.setPaint(Color.YELLOW);
        for (int i = 1; i <= vehicles.size(); i++) {
            switch (way) {
                case U:
                    r = new Rectangle2D.Float(x + 1, y, 3, 3);
                    x += 5;
                    if(i % 4 == 0){
                        y += 5;
                        x -= 20;
                    }
                    g.fill(r);
                    break;
                   case D:
                    r = new Rectangle2D.Float(x + 1, y + 16, 3, 3);
                    x += 5;
                    if(i % 4 == 0){
                        y -= 5;
                        x -= 20;
                    }
                    g.fill(r);
                    break;
                case L:
                    r = new Rectangle2D.Float(x, y + 1, 3, 3);
                    y += 5;
                    if(i % 4 == 0){
                        y -= 20;
                        x += 5;
                    }
                    g.fill(r);
                    break;
                case R:
                    r = new Rectangle2D.Float(x + 16, y + 1, 3, 3);
                    y += 5;
                    if(i % 4 == 0){
                        y -= 20;
                        x -= 5;
                    }
                    g.fill(r);
                    break;

            }

        }
    }

}
