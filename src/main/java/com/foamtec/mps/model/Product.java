package com.foamtec.mps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Product implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date createDate;
    private String partNumber;
    private String partName;
    private String codeSap;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private GroupForecast groupForecast;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getCodeSap() {
        return codeSap;
    }

    public void setCodeSap(String codeSap) {
        this.codeSap = codeSap;
    }

    public GroupForecast getGroupForecast() {
        return groupForecast;
    }

    public void setGroupForecast(GroupForecast groupForecast) {
        this.groupForecast = groupForecast;
    }
}
