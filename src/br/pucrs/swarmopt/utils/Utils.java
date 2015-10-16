/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pucrs.swarmopt.utils;

/**
 *
 * @author gabriel
 */
public class Utils {
    public static int[] trafficLightsTimes(String strInput){
        int times[] = new int[3];
        String timesPain[] = strInput.split(",");
        for (int i = 0; i < timesPain.length; i++) {
            times[i] = Integer.parseInt(timesPain[i].trim());
        }
        return times;
    }
}
