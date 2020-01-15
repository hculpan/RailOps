package org.culpan.railops.dao;

import org.culpan.railops.dao.interfaces.PostInitialize;
import org.culpan.railops.model.Move;

public class MoveDao extends BaseDao<Move> implements PostInitialize<Move> {
    private LocationsDao locationsDao = new LocationsDao();
    private CarsDao carsDao = new CarsDao();

    @Override
    public void initializeComplete(Move item) {
        item.setCar(carsDao.findById(item.getCarId()));
        item.setLocation(locationsDao.findById(item.getLocationId()));
    }
}
