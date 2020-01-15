package org.culpan.railops.dao;

import org.culpan.railops.dao.interfaces.PostInitialize;
import org.culpan.railops.model.SwitchList;

public class SwitchListDao extends BaseDao<SwitchList> implements PostInitialize<SwitchList> {
    private final static RoutesDao routesDao = new RoutesDao();
    private final static MoveDao moveDao = new MoveDao();

    @Override
    public void initializeComplete(SwitchList item) {
        item.setRoute(routesDao.findById(item.getRouteId()));
        item.getMoves().addAll(moveDao.find(String.format("switch_list_id = %d", item.getId())));
    }
}
