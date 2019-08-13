package com.mceil.item.service;

import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.SpecBo;
import com.mceil.item.bo.SpecValueBo;
import com.mceil.item.pojo.Spec;

import java.util.List;

public interface SpecService {
    /**
     * 根据cid分类查询规格模板
     * @param cid
     * @return
     */
    Spec querySpecByCid(Long cid);

    /**
     * 根据id查询规格模板
     * @param id
     * @return
     */
    Spec querySpecById(Long id);
    /**
     * 添加分类模板
     */
    void saveSpec(Spec spec);

    void updateSpec(Spec spec);

    /**
     * 删除分类模板
     * @param ids
     */
    void deleteSpec(List<Long> ids);

    /**
     * 分页获取分类模板
     * @param page
     * @param rows
     * @return
     */
    PageResult<Spec> queryAllSpec(Integer page, Integer rows);


    List<SpecValueBo> querySpecByValue(Long cid, String values);
}
