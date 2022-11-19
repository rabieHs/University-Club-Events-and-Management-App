package com.vfxf.fvxmob;

import java.util.Map;

public class EventSendNotification {
    public String to;
    public Map<String,String> data;

    public EventSendNotification(String to, Map<String, String> data) {
        this.to = to;
        this.data = data;
    }

    public EventSendNotification() {

    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
