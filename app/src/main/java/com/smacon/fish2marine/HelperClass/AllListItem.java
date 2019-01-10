package com.smacon.fish2marine.HelperClass;

/**
 * Created by smacon on 13/1/17.
 */

public final class AllListItem {

    String reward_id,comment,created,expiry,points;




    public final void setPoints(String points) {
        this.points = points;
    }

    public final void setComment(String comment) {
        this.comment = comment;
    }

    public final void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public final void setCreated(String created) {
        this.created = created;
    }

    public final void setReward_id(String reward_id) {
        this.reward_id = reward_id;
    }


    public String getReward_id() {
        return reward_id;
    }

    public String getCreated() {
        return created;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getComment() {
        return comment;
    }

    public String getPoints() {
        return points;
    }

}
