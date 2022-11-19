package com.vfxf.fvxmob;

public class EVENTS {
    public String eventtitle, eventtime, eventdate, eventmonth, eventdescription, eventimage, eventday, eventlocal, eventurl ;


    public EVENTS(){

    }




    public EVENTS(String eventtitle, String eventlocal, String eventtime, String eventdate, String eventmonth, String eventdescription, String eventimage, String eventday, String eventurl) {
        this.eventtitle = eventtitle;
        this.eventlocal = eventlocal;
        this.eventtime = eventtime;
        this.eventdate = eventdate;
        this.eventmonth = eventmonth;
        this.eventdescription = eventdescription;
        this.eventimage = eventimage;
        this.eventday = eventday;
        this.eventurl = eventurl;

    }

    public String getEventurl() {
        return eventurl;
    }

    public void setEventurl(String eventurl) {
        this.eventurl = eventurl;
    }

    public String getEventlocal() {
        return eventlocal;
    }

    public void setEventlocal(String eventlocal) {
        this.eventlocal = eventlocal;
    }

    public String getEventtitle() {
        return eventtitle;
    }

    public void setEventtitle(String eventtitle) {
        this.eventtitle = eventtitle;
    }

    public String getEventtime() {
        return eventtime;
    }

    public void setEventtime(String eventtime) {
        this.eventtime = eventtime;
    }

    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }

    public String getEventmonth() {
        return eventmonth;
    }

    public void setEventmonth(String eventmonth) {
        this.eventmonth = eventmonth;
    }

    public String getEventdescription() {
        return eventdescription;
    }

    public void setEventdescription(String eventdescription) {
        this.eventdescription = eventdescription;
    }

    public String getEventimage() {
        return eventimage;
    }

    public void setEventimage(String eventimage) {
        this.eventimage = eventimage;
    }
    public String getEventday() {
        return eventday;
    }

    public void setEventday(String eventday) {
        this.eventday = eventday;
    }

}
