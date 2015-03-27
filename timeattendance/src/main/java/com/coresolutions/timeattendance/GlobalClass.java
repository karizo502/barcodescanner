package com.coresolutions.timeattendance;

/**
 * Created by Panupong on 26/3/2558.
 */
import android.app.Application;

public class GlobalClass extends Application{
    private String state;
    private String empid;
    private String name;

    public String getState() {

        return state;
    }

    public void setState(String cState) {

        state = cState;

    }

    public String getEmpID() {

        return empid;
    }

    public void setEmpID(String cEmpID) {

        empid = cEmpID;
    }
    public String getName() {

        return name;
    }

    public void setName(String cName) {

        name = cName;

    }

}
