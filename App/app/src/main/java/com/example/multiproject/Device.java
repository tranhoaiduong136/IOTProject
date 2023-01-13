package com.example.multiproject;

public class Device {
    Integer humidity;
    Integer light;
    Integer moisture;
    Integer temperature;
    Integer moist_max;
    Integer moist_min;
    Integer temp_max;
    Integer temp_min;
    String status;
    Integer temp_level;
    Integer moist_level;
    String avg_humidity;
    String avg_light;
    String avg_moisture;
    String avg_temperature;
    public Device(){
        this.humidity = 30;
        this.light = 30;
        this.moisture = 30;
        this.temperature = 30;
        this.moist_max = 50;
        this.moist_min = 20;
        this.temp_max = 30;
        this.temp_min = 20;
        this.status = "Normal";
        this.temp_level = 25;
        this.moist_level = 30;
        this.avg_humidity = "25";
        this.avg_light = "25";
        this.avg_moisture = "25";
        this.avg_temperature = "25";
    }


    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {this.status = status;}

    public Integer getHumidity() {return this.humidity;}
    public void setHumidity(Integer humidity) {this.humidity = humidity;}

    public Integer getLight() {return this.light;}
    public void setLight(Integer light) {this.light = light;}

    public Integer getMoisture() {return this.moisture;}
    public void setMoisture(Integer moisture) {this.moisture = moisture;}

    public Integer getTemperature() {return this.temperature;}
    public void setTemperature(Integer temperature) {this.temperature = temperature;}

    public Integer getMoist_max() {return this.moist_max;}
    public void setMoist_max(Integer moist_max) {this.moist_max = moist_max;}

    public Integer getMoist_min() {return this.moist_min;}
    public void setMoist_min(Integer moist_min) {this.moist_min = moist_min;}

    public Integer getTemp_max() {return this.temp_max;}
    public void setTemp_max(Integer temp_max) {this.temp_max = temp_max;}

    public Integer getTemp_min() {return this.temp_min;}
    public void setTemp_min(Integer temp_min) {this.temp_min = temp_min;}

    public Integer getTemp_level() {return this.temp_level;}
    public void setTemp_level(Integer temp_level) {this.temp_level = temp_level;}

    public Integer getMoist_level() {return this.moist_level;}
    public void setMoist_level(Integer moist_level) {this.moist_level = moist_level;}

    public String getAvg_humidity() {return this.avg_humidity;}
    public void setAvg_humidity(String avg_humidity) {this.avg_humidity = avg_humidity;}

    public String getAvg_light() {return this.avg_light;}
    public void setAvg_light(String avg_light) {this.avg_light = avg_light;}

    public String getAvg_moisture() {return this.avg_moisture;}
    public void setAvg_moisture(String avg_moisture) {this.avg_moisture = avg_moisture;}

    public String getAvg_temperature() {return this.avg_temperature;}
    public void setAvg_temperature(String avg_temperature) {this.avg_temperature = avg_temperature;}
}
