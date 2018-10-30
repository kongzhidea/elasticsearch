package com.kk.es.test.param;


import com.kk.es.model.search.BaseSearchParam;
import com.kk.es.model.search.annotations.SearchKey;
import com.kk.es.model.search.enums.OperatorType;

import java.util.List;

/**
 * @author zhihui.kzh
 * @create 11/9/1816:57
 */
public class OrderParam extends BaseSearchParam {

    @SearchKey(name = "id")
    private Long id;

    @SearchKey(name = "id", operatorType = OperatorType.in)
    private List<Long> idList;

    @SearchKey(name = "email", operatorType = OperatorType.in)
    private List<String> emailList;

    @SearchKey(name = "creator", operatorType = OperatorType.equal)
    private String creator;

    @SearchKey(name = "title", operatorType = OperatorType.match_phrase)
    private String title;

    // 时间筛选
    @SearchKey(name = "gmt_created", operatorType = OperatorType.gte)
    private Long startTimeLong;
    @SearchKey(name = "gmt_created", operatorType = OperatorType.lte)
    private Long endTimeLong;


    @SearchKey(name = "phone")
    private String phone;

    @SearchKey(name = "email")
    private String email;

    @SearchKey(name = "remark", operatorType = OperatorType.match)
    private String remark;

    // column为数组
    @SearchKey(name = "group_ids")
    private Long groupIds;

    // column为数组
    @SearchKey(name = "all_deal_user")
    private String dealUser;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStartTimeLong() {
        return startTimeLong;
    }

    public void setStartTimeLong(Long startTimeLong) {
        this.startTimeLong = startTimeLong;
    }

    public Long getEndTimeLong() {
        return endTimeLong;
    }

    public void setEndTimeLong(Long endTimeLong) {
        this.endTimeLong = endTimeLong;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Long groupIds) {
        this.groupIds = groupIds;
    }

    public String getDealUser() {
        return dealUser;
    }

    public void setDealUser(String dealUser) {
        this.dealUser = dealUser;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }
}
