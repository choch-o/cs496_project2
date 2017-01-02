package com.example.q.project2;

/**
 * Created by q on 2017-01-02.
 */

public class Tab2Item {
    private String name;
    private String url;
    private String last_update;
    private boolean on_server;

    public Tab2Item(String name, String url, String last_update, boolean on_server) {
        this.name = name;
        this.url = url;
        this.last_update = last_update;
        this.on_server = on_server;
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

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public boolean isOn_server() {
        return on_server;
    }

    public void setOn_server(boolean on_server) {
        this.on_server = on_server;
    }
}
