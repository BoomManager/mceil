package com.mceil.user.service;

import java.util.List;

public interface CollectionsService {

    int addProduct(List<Long> ids);

    int deleteProduct(List<Long> productIds);

}
