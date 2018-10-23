package com.xee.sdk.fleet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Behavior implements Parcelable {

    public static final String SPEED_LIMIT = "SPEED_LIMIT";
    public static final String HARD_ACCELERATION = "HARD_ACCELERATION";
    public static final String HARD_BRAKING = "HARD_BRAKING";
    public static final String RPM_LIMIT = "RPM_LIMIT";

    @SerializedName("behaviorId")
    private long behaviorId;
    @SerializedName("type")
    private String type;
    @SerializedName("startDate")
    private Date startDate;
    @SerializedName("endDate")
    private Date endDate;
    @SerializedName("value")
    private double value;
    @SerializedName("defaultValue")
    private double defaultValue;
    @SerializedName("reasons")
    private List<String> reasons;
    @SerializedName("count")
    private Integer count;

    public long getBehaviorId() {
        return behaviorId;
    }

    public void setBehaviorId(long behaviorId) {
        this.behaviorId = behaviorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reason) {
        this.reasons = reason;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Behavior behavior = (Behavior) o;

        if (behaviorId != behavior.behaviorId) return false;
        if (Double.compare(behavior.value, value) != 0) return false;
        if (Double.compare(behavior.defaultValue, defaultValue) != 0) return false;
        if (!type.equals(behavior.type)) return false;
        if (!startDate.equals(behavior.startDate)) return false;
        if (!endDate.equals(behavior.endDate)) return false;
        if (!reasons.equals(behavior.reasons)) return false;
        return count.equals(behavior.count);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (behaviorId ^ (behaviorId >>> 32));
        result = 31 * result + type.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(defaultValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + reasons.hashCode();
        result = 31 * result + count.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Behavior{" +
                "behaviorId=" + behaviorId +
                ", type='" + type + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", value=" + value +
                ", defaultValue=" + defaultValue +
                ", reasons=" + reasons +
                ", count=" + count +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.behaviorId);
        dest.writeString(this.type);
        dest.writeLong(this.startDate != null ? this.startDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeDouble(this.value);
        dest.writeDouble(this.defaultValue);
        dest.writeStringList(this.reasons);
        dest.writeValue(this.count);
    }

    public Behavior() {
    }

    protected Behavior(Parcel in) {
        this.behaviorId = in.readLong();
        this.type = in.readString();
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.value = in.readDouble();
        this.defaultValue = in.readDouble();
        this.reasons = in.createStringArrayList();
        this.count = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Behavior> CREATOR = new Creator<Behavior>() {
        @Override
        public Behavior createFromParcel(Parcel source) {
            return new Behavior(source);
        }

        @Override
        public Behavior[] newArray(int size) {
            return new Behavior[size];
        }
    };
}