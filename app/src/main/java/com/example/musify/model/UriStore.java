package com.example.musify.model;

public class UriStore {

    private String mname;
    private String urifile;

    public UriStore() {
    }

    public UriStore(String mname, String urifile) {
        this.mname = mname;
        this.urifile = urifile;
    }


    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getUrifile() {
        return urifile;
    }

    public void setUrifile(String urifile) {
        this.urifile = urifile;
    }
}
