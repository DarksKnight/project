package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by SEELE on 2016/6/27.
 */
public class Bike implements Serializable {

    public String ble_serial = "";

    public String ble_name = "";

    public int bike_status = 0;

    public int bikesite_id = 0;

    public int battery = 0;

    public String bikesite_name = "";

    public Bike(){

    }

    public Bike(String ble_serial, String ble_name, int bike_status, int bikesite_id, int battery, String bikesite_name) {
        this.ble_serial = ble_serial;
        this.ble_name = ble_name;
        this.bike_status = bike_status;
        this.bikesite_id = bikesite_id;
        this.battery = battery;
        this.bikesite_name = bikesite_name;

    }

}
