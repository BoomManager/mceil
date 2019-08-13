//package com.mceil.user.service.impl;
//
//import com.mceil.user.mapper.MemberCollectionsMapper;
//import com.mceil.user.pojo.Collections;
//import com.mceil.user.service.CollectionsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//@Service
//public class CollectionsServiceImpl implements CollectionsService {
//    @Autowired
//    private MemberService memberService;
//    @Autowired
//    private MemberCollectionsMapper memberCollectionsMapper;
//    @Override
//    public int addProduct(List<Long> ids) {
//        int count = 0;
//        UmsMember member = getMember();
//        Long userId = member.getId();
//        //获取当前会员所有的收藏产品
//        List<Collections> umsCollections = getUmsMemberCollections(userId);
//        List<Long> idList = new ArrayList<>();
//        //当前会员有收藏
//        if(!StringUtils.isEmpty(umsCollections) && umsCollections.size() > 0){
//            for (Collections umsMemberCollection : umsCollections) {
//                Long productId = umsMemberCollection.getProductId();
//                idList.add(productId);
//            }
//            for (Long id : ids) {
//                if (!idList.contains(id)) {
//                    count = saveCollections(userId, id);
//                }
//            }
//
//        }else {
//            //当前会员没有收藏
//            for (Long id : ids) {
//                count = saveCollections(userId, id);
//            }
//        }
//        return count;
//    }
//    @Override
//    public int deleteProduct(List<Long> productIds) {
//        int count = 0;
//        UmsMember member = getMember();
//        Long userId = member.getId();
//        List<Collections> umsCollections = getUmsMemberCollections(userId);
//        if(!StringUtils.isEmpty(umsCollections) && umsCollections.size() > 0){
//            for (Collections umsMemberCollection : umsCollections) {
//                Long Id = umsMemberCollection.getProductId();
//                for (Long productId : productIds) {
//                    if(productId.equals(Id)){
//                        count = memberCollectionsMapper.deleteCollectionsByProductId(productId);
//                    }
//                }
//            }
//        }
//        return count;
//    }
//    private UmsMember getMember(){
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        //String username = "root";
//        return memberService.getByUsername(username);
//    }
//    //获取当前会员收藏的商品
//    private List<Collections> getUmsMemberCollections(Long userId){
//        Collections memberCollections = new Collections();
//        memberCollections.setMemeberId(userId);
//        List<Collections> collections = memberCollectionsMapper.select(memberCollections);
//        return collections;
//    }
//    //保存当前会员收藏的产品
//    private int saveCollections(Long userId,Long id){
//
//            Collections newUmsCollections = new Collections();
//            newUmsCollections.setProductId(id);
//            newUmsCollections.setMemeberId(userId);
//            return memberCollectionsMapper.insert(newUmsCollections);
//        }
//
//
//}
