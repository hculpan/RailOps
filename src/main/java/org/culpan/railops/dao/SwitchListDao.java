package org.culpan.railops.dao;

import org.culpan.railops.dao.interfaces.PostAddOrUpdate;
import org.culpan.railops.dao.interfaces.PostInitialize;
import org.culpan.railops.model.Move;
import org.culpan.railops.model.SwitchList;

public class SwitchListDao extends BaseDao<SwitchList> implements PostInitialize<SwitchList>, PostAddOrUpdate<SwitchList> {
    private final static MoveDao moveDao = new MoveDao();

    @Override
    public void initializeComplete(SwitchList item) {
        item.getMoves().addAll(moveDao.find(String.format("switch_list_id = %d", item.getId())));
    }

    @Override
    public void addOrUpdateComplete(SwitchList item, boolean autocommit) {
        if (moveDao.delete(String.format("switch_list_id = %d", item.getId()))) {
            for (Move m : item.getMoves()) {
                m.setSwitchListId(item.getId());
                moveDao.addOrUpdate(m, false);
            }
            moveDao.commit();
        }
    }
}
