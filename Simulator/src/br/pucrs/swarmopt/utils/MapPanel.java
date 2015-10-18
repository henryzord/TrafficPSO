/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pucrs.swarmopt.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author gabriel
 */
public class MapPanel extends JPanel{
    TrafficMap map;

    public void setMap(TrafficMap map) {
        this.map = map;
    }
    
    @Override
    public void paint(Graphics g) {
        if(map == null){
            return;
        }
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);        
        map.printMap(bufferedImage.createGraphics());
        Graphics2D g2 = (Graphics2D) g;        
        g2.drawImage(bufferedImage, null, 0,0);        
    }
    
}
