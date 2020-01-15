package org.culpan.railops.dao;

import org.culpan.railops.dao.interfaces.PostInitialize;
import org.culpan.railops.model.Route;

public class RoutesDao extends BaseDao<Route> implements PostInitialize<Route> {
    private final static RailroadsDao railroadDao = new RailroadsDao();
    private final static SwitchListDao switchListDao = new SwitchListDao();

    @Override
    public void initializeComplete(Route item) {
        item.setRailroad(railroadDao.findById(item.getRailroadId()));
        item.setSwitchList(switchListDao.findById(item.getSwitchListId()));
    }
}
