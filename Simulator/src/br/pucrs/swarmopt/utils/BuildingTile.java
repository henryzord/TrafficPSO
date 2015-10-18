/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pucrs.swarmopt.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author gabriel
 */
public class BuildingTile extends Tile{

    public BuildingTile() {
        super(Tile.BUILDING);
        color = new Color(233,229,220);
    }
    
    @Override
    public void draw(Graphics2D g, int x, int y) {
        g.setPaint(color);
        Rectangle2D r = new Rectangle2D.Float(x, y, 20, 20);
        g.fill(r);
    }
    
}
