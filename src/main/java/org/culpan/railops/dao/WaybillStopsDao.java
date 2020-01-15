package org.culpan.railops.dao;

import org.culpan.railops.model.Waybill;
import org.culpan.railops.model.WaybillStop;

public class WaybillStopsDao extends BaseDao<WaybillStop> {
    public boolean deleteAllForWaybill(Waybill waybill) {
        return delete(String.format("waybill_id = %d", waybill.getId()));
    }
}
