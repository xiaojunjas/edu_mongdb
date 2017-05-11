package com.gclasscn.mongo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by fengtp on 2017/5/2.
 */
@Document(collection = "ManageLog")
public class ManageLog {

        @Id
        private String id;

        private String operate;
        private String operatorNo;
        private String loginName;
        private String operatorName;
        private String description;
        private Date operationDate;
        private String operateType;
        private String operateSystem;
        private Long schoolId;
        private String operateIp;

    @Override
    public String toString() {
        return "ManageLog{" +
                "id='" + id + '\'' +
                ", operate='" + operate + '\'' +
                ", operatorNo='" + operatorNo + '\'' +
                ", loginName='" + loginName + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", description='" + description + '\'' +
                ", operationDate=" + operationDate +
                ", operateType='" + operateType + '\'' +
                ", operateSystem='" + operateSystem + '\'' +
                ", schoolId=" + schoolId +
                ", operateIp='" + operateIp + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateSystem() {
        return operateSystem;
    }

    public void setOperateSystem(String operateSystem) {
        this.operateSystem = operateSystem;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public String getOperateIp() {
        return operateIp;
    }

    public void setOperateIp(String operateIp) {
        this.operateIp = operateIp;
    }
}
