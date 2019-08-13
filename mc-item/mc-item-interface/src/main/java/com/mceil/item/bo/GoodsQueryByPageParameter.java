package com.mceil.item.bo;

public class GoodsQueryByPageParameter {
     /*
    *   - page：当前页，int
        - rows：每页大小，int
        - sortBy：排序字段，String
        - desc：是否为降序，boolean
        - key：搜索关键词，String
    * */

    private Integer page;
    private Integer rows;

    private String key;
    private Integer checkStatus;
    private String goodsSn;
    private Long cid;
    private Long bid;
    private Boolean saleable;
    private static final Integer DEFAULT_SIZE = 5;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public Boolean getSaleable() {
        if(saleable == null){
            saleable = true;
        }
        return saleable;
    }

    public void setSaleable(Boolean saleable) {
        this.saleable = saleable;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        if(rows == null){
            return DEFAULT_SIZE;
        }
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public GoodsQueryByPageParameter() {
    }


    public GoodsQueryByPageParameter(Integer page, Integer rows, String key, Integer checkStatus, String goodsSn, Long cid, Long bid, Boolean saleable) {
        this.page = page;
        this.rows = rows;
        this.key = key;
        this.checkStatus = checkStatus;
        this.goodsSn = goodsSn;
        this.cid = cid;
        this.bid = bid;
        this.saleable = saleable;
    }

    @Override
    public String toString() {
        return "GoodsQueryByPageParameter{" +
                "page=" + page +
                ", rows=" + rows +
                ", key='" + key + '\'' +
                ", checkStatus=" + checkStatus +
                ", goodsSn='" + goodsSn + '\'' +
                ", cid=" + cid +
                ", bid=" + bid +
                ", saleable=" + saleable +
                '}';
    }
}
