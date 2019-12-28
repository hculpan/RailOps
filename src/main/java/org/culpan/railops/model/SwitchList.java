package org.culpan.railops.model;

import java.util.ArrayList;
import java.util.List;

public class SwitchList {
    private int id;
    private int routeId;
    private String status;

    private final List<Move> moves = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Move> getMoves() {
        return moves;
    }
}