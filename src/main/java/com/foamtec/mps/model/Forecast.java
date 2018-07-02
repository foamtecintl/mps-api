package com.foamtec.mps.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Forecast implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date createDate;
    private String forecastNumber;
    private Long forecastFile;
    private String forecastGroup;
    private Integer totalPart;
    @OrderBy("createDate")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<SubForecast> subForecasts = new HashSet<>();

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

    public String getForecastNumber() {
        return forecastNumber;
    }

    public void setForecastNumber(String forecastNumber) {
        this.forecastNumber = forecastNumber;
    }

    public Long getForecastFile() {
        return forecastFile;
    }

    public void setForecastFile(Long forecastFile) {
        this.forecastFile = forecastFile;
    }

    public String getForecastGroup() {
        return forecastGroup;
    }

    public void setForecastGroup(String forecastGroup) {
        this.forecastGroup = forecastGroup;
    }

    public Integer getTotalPart() {
        return totalPart;
    }

    public void setTotalPart(Integer totalPart) {
        this.totalPart = totalPart;
    }

    public Set<SubForecast> getSubForecasts() {
        return subForecasts;
    }

    public void setSubForecasts(Set<SubForecast> subForecasts) {
        this.subForecasts = subForecasts;
    }
}
