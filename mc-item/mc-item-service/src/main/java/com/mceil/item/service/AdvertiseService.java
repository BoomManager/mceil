package com.mceil.item.service;

import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.Advertise;

import java.util.Date;
import java.util.List;

public interface AdvertiseService {
    PageResult<Advertise> queryAdvertisePage(Integer page, Integer rows, String name, Date endTime);

    void saveAdvertise(Advertise advertise);

    void updateAdvertise(Advertise advertise);

    void deleteAdvertise(List<Long> ids);

    Advertise queryAdvertiseById(Long id);

    PageResult<Advertise> queryAdvertisePagePortal(Integer page, Integer rows);
}
