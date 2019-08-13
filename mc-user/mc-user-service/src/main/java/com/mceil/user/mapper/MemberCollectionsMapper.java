package com.mceil.user.mapper;

import com.mceil.user.pojo.Collections;
import org.apache.ibatis.annotations.Delete;
import tk.mybatis.mapper.common.Mapper;

public interface MemberCollectionsMapper extends Mapper<Collections> {
    @Delete("delete from ums_member_collections where product_id = #{productId}")
    int deleteCollectionsByProductId(Long productId);
}
