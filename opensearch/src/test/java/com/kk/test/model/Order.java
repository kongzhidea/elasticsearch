package com.kk.test.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author zhihui.kzh
 * @create 3/9/1821:21
 */
public class Order {
    private Long id;

    private String creator;

    private String title;

    @JSONField(name = "gmt_created")
    private Long gmtCreated;

    private String phone;

    private String email;

    private String remark;

    @JSONField(name = "group_ids")
    private List<Long> groupIds;

    @JSONField(name = "all_deal_user")
    private List<String> allDealUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Long gmtCreated) {
        this.gmtCreated = gmtCreated;
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

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<String> getAllDealUser() {
        return allDealUser;
    }

    public void setAllDealUser(List<String> allDealUser) {
        this.allDealUser = allDealUser;
    }
}
