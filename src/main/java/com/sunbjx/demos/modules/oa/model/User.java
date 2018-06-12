package com.sunbjx.demos.modules.oa.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.sunbjx.demos.framework.core.mvc.model.BaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class User extends BaseEntity<User> {

    private static final long serialVersionUID = -4962422412798107814L;
    private String name;

    private Boolean sex;

    @JSONField(format = "yyyy-MM-dd")
    private Date birthday;

    private String address;

    @TableField("head_src")
    private String headSrc;

    private String nickname;

    private String autograph;

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return sex
     */
    public Boolean getSex() {
        return sex;
    }

    /**
     * @param sex
     */
    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    /**
     * @return birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * @param birthday
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return head_src
     */
    public String getHeadSrc() {
        return headSrc;
    }

    /**
     * @param headSrc
     */
    public void setHeadSrc(String headSrc) {
        this.headSrc = headSrc;
    }

    /**
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return autograph
     */
    public String getAutograph() {
        return autograph;
    }

    /**
     * @param autograph
     */
    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("sex", getSex())
                .append("birthday", getBirthday())
                .append("address", getAddress())
                .append("headSrc", getHeadSrc())
                .append("nickname", getNickname())
                .append("autograph", getAutograph())
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;
        if (this == obj) return true;
        User other = (User) obj;
        return new EqualsBuilder()
                .append(getId(), other.getId())
                .isEquals();
    }
}