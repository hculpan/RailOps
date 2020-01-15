package org.culpan.railops.dao.interfaces;

import org.culpan.railops.model.BaseModel;

public interface PostAddOrUpdate<T extends BaseModel> {
    void addOrUpdateComplete(T item, boolean autocommit);
}
