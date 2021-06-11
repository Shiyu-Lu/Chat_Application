package model;

import java.io.Serializable;

public class LoginRequestMessage implements Serializable {
    public String account;
    public String password;

    public LoginRequestMessage(String account,String password) {
        this.account = account;
        this.password = password;
    }
}
