package model;

import java.time.LocalDate;

public class AidDistribution {

    private int distributionId;
    private int familyId;
    private int orgId;
    private int distributedBy;

    private String aidType;

    private LocalDate distributionDate;

    private String familyName;
    private String orgName;
    private String coordinatorName;

    public AidDistribution() {
    }

    public AidDistribution(int distributionId,
                           int familyId,
                           int orgId,
                           int distributedBy,
                           String aidType,
                           LocalDate distributionDate) {

        this.distributionId = distributionId;
        this.familyId = familyId;
        this.orgId = orgId;
        this.distributedBy = distributedBy;
        this.aidType = aidType;
        this.distributionDate = distributionDate;
    }

    public int getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(int distributionId) {
        this.distributionId = distributionId;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getDistributedBy() {
        return distributedBy;
    }

    public void setDistributedBy(int distributedBy) {
        this.distributedBy = distributedBy;
    }

    public String getAidType() {
        return aidType;
    }

    public void setAidType(String aidType) {
        this.aidType = aidType;
    }

    public LocalDate getDistributionDate() {
        return distributionDate;
    }

    public void setDistributionDate(LocalDate distributionDate) {
        this.distributionDate = distributionDate;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }
}