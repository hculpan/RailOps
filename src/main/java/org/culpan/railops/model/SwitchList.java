package org.culpan.railops.model;

import java.util.ArrayList;
import java.util.List;

public class SwitchList extends BaseModel {
    private int routeId;
    private String status;

    private final List<Move> moves = new ArrayList<>();

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
