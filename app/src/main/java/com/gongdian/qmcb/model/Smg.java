package com.gongdian.qmcb.model;

import java.util.List;

/**
 * Created by qian-pc on 2/26/16.
 */
public class Smg {
    private String nr;
    private List<PhoneNumber> phoneNumbers;

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
