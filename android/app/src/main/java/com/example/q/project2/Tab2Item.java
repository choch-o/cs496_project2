package com.example.q.project2;

/**
 * Created by q on 2017-01-02.
 */

public class Tab2Item {
    private String name;
    private String url;
    private String fbid;
    private String fbname;
    private boolean on_server;

    public Tab2Item(String name, String url, String fbid, String fbname, boolean on_server) {
        this.name = name;
        this.url = url;
        this.fbid = fbid;
        this.fbname = fbname;
        this.on_server = on_server;
    }

    public String getFbname() {
        return fbname;
    }

    public void setFbname(String fbname) {
        this.fbname = fbname;
    }

    public String getFbid() {
        return fbid;
    }

    public void setFbid(String fbid) {
        this.fbid = fbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOn_server() {
        return on_server;
    }

    public void setOn_server(boolean on_server) {
        this.on_server = on_server;
    }
}
