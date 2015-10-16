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
public class Vehicle {

    StreetTile currentTile;
    boolean updated = false;

    public Vehicle(Tile currentTile) {
        this.currentTile = (StreetTile) currentTile;
    }

    public void updatePosition(int x, int y) {
        if (updated) {
            return;
        }
        updated = true;

        if (TrafficMap.nextTile(currentTile, x, y) instanceof StreetTile) {
            StreetTile neighbourTile = (StreetTile) TrafficMap.nextTile(currentTile, x, y);

            if (neighbourTile.availableSpace() > 0) {
                currentTile.getVehicles().remove(this);
                neighbourTile.addVehicles(this);
                currentTile = neighbourTile;
            }
        }

        if (TrafficMap.nextTile(currentTile, x, y) instanceof TrafficLight) {
            TrafficLight neighbourTile = (TrafficLight) TrafficMap.nextTile(currentTile, x, y);
            int open = -1;
            for (open = 0; open < neighbourTile.trafficLightsState.length; open++) {
                if (neighbourTile.trafficLightsState[open] == 1) {
                    break;
                }
            }

            if (neighbourTile.neighbours[StreetTile.U] == null) {
                open--;
                if (open < 0) {
                    if (currentTile.getWay() == StreetTile.D) {
                        passTrafficLight(neighbourTile);
                    }
                    return;
                }
            }

            if (neighbourTile.neighbours[StreetTile.D] == null) {
                open--;
                if (open < 0) {
                    if (currentTile.getWay() == StreetTile.U) {
                        passTrafficLight(neighbourTile);
                    }
                    return;
                }
            }

            if (neighbourTile.neighbours[StreetTile.L] == null) {
                open--;
                if (open < 0) {
                    if (currentTile.getWay() == StreetTile.R) {
                        passTrafficLight(neighbourTile);
                    }
                    return;
                }
            }

            if (neighbourTile.neighbours[StreetTile.R] == null) {
                open--;
                if (open < 0) {
                    if (currentTile.getWay() == StreetTile.L) {
                        passTrafficLight(neighbourTile);
                    }
                    return;
                }
            }
        }
    }

    private void passTrafficLight(TrafficLight neighbourTile) {
        StreetTile nextStreetTile;
        nextStreetTile = neighbourTile.getNext();
        if (nextStreetTile.availableSpace() > 0) {
            currentTile.getVehicles().remove(this);
            nextStreetTile.addVehicles(this);
            currentTile = nextStreetTile;
        }

    }
}
