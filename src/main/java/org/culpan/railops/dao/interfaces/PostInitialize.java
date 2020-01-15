package org.culpan.railops.dao.interfaces;

import org.culpan.railops.model.BaseModel;

public interface PostInitialize<T extends BaseModel> {
    void initializeComplete(T item);
}
