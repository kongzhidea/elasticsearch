package com.kk.test.param;

import com.kk.opensearch.model.search.BaseSearchParam;
import com.kk.opensearch.model.search.annotations.SearchKey;
import com.kk.opensearch.model.search.enums.OperatorType;

import java.util.List;

/**
 * @author zhihui.kzh
 * @create 11/9/1816:57
 */
public class OrderParam extends BaseSearchParam {

    @SearchKey(index = "id")
    private Long id;

    @SearchKey(index = "id", name = "id", operatorType = OperatorType.in)
    private List<Long> idList;

    @SearchKey(index = "idx_email", name = "email", operatorType = OperatorType.in)
    private List<String> emailList;

    @SearchKey(index = "idx_creator", operatorType = OperatorType.equal)
    private String creator;

    @SearchKey(index = "idx_title", operatorType = OperatorType.analysis)
    private String title;

    // 时间筛选
    @SearchKey(index = "idx_gmt_created", name = "gmt_created", operatorType = OperatorType.gte)
    private Long startTimeLong;
    @SearchKey(index = "idx_gmt_created", name = "gmt_created", operatorType = OperatorType.lte)
    private Long endTimeLong;


    @SearchKey(index = "idx_phone", name = "phone", or = true)
    private String phone;

    @SearchKey(index = "idx_phone", name = "phone", or = true)
    private String phone2;

    @SearchKey(index = "idx_email")
    private String email;

    @SearchKey(index = "idx_keyword", operatorType = OperatorType.analysis)
    private String remark;

    // column为数组
    @SearchKey(index = "idx_group_ids", name = "group_ids")
    private Long groupIds;

    // column为数组
    @SearchKey(index = "idx_all_deal_user", name = "all_deal_user")
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

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
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
