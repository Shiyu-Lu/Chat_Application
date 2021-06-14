package model;

import DAL.RawAccountRecordDAO;

public  class RawAccountRecord {
    private String name;
    private String password;

    public RawAccountRecord(String name,String password){
        this.name = name;
        this.password = password;
    }

    public RawAccountRecord(){}

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
