package org.culpan.railops.model;

import org.culpan.railops.dao.annotations.Column;

import java.util.ArrayList;
import java.util.List;

public class SwitchList extends BaseModel {
    @Column(name = "route_id")
    private int routeId;

    private Route route;

    @Column
    private String status;

    private final List<Move> moves = new ArrayList<>();

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
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
