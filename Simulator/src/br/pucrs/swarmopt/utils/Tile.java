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
public abstract class Tile {
    protected Rectangle2D rec;
    private int tileType;
    protected Color color;
    
    public Tile(int type) {
        tileType = type;
    } 
    
    public abstract void draw(Graphics2D g, int x, int y);
    
    public static final int SINGLE_WAY = 1, DOUBLE_WAY = 2, 
            TRAFFIC_LIGHT = 3, BUILDING = 3, STREET = 4, W = 20, H = 20;
    
    public Rectangle2D getRectangle(){        
        return rec;
    }
    
    public void updateState(){
        
    }

    public Color getColor() {
        return color;
    }
}
