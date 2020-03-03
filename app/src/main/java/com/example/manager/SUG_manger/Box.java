package com.example.manager.SUG_manger;

import java.io.Serializable;

class Box implements Serializable {

    private int id;
    private int member;
    private String topic;
    private String purpose;
    private String info;
    private String date;
    private float satisfied;
    private String feed_back;
    private String reply;
    private boolean expanded;


    public Box(int id, int member, String topic, String purpose, String info, String date, float satisfied, String feed_back, String reply, boolean expanded) {
        this.id = id;
        this.member = member;
        this.topic = topic;
        this.purpose = purpose;
        this.info = info;
        this.date = date;
        this.satisfied = satisfied;
        this.feed_back = feed_back;
        this.reply = reply;
        this.expanded = expanded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getSatisfied() {
        return satisfied;
    }

    public void setSatisfied(float satisfied) {
        this.satisfied = satisfied;
    }

    public String getFeed_back() {
        return feed_back;
    }

    public void setFeed_back(String feed_back) {
        this.feed_back = feed_back;
    }

    public String getReply() { return reply; }

    public void setReply(String reply) { this.reply = reply; }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    @Override
    public String toString() {
        return "Box{" +
                "id=" + id +
                ", member=" + member +
                ", topic='" + topic + '\'' +
                ", purpose='" + purpose + '\'' +
                ", info='" + info + '\'' +
                ", date='" + date + '\'' +
                ", satisfied=" + satisfied +
                ", feed_back='" + feed_back + '\'' +
                ", reply='" + reply + '\'' +
                ", expanded=" + expanded +
                '}';
    }
}
