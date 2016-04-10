package com.angkorteam.mbaas.server.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 4/10/16.
 */
public class MessageDTORequest {

    @Expose
    @SerializedName("message")
    private final Message message = new Message();

    @Expose
    @SerializedName("criteria")
    private final Criteria criteria = new Criteria();

    @Expose
    @SerializedName("config")
    private final Config config = new Config();

    public Message getMessage() {
        return message;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public Config getConfig() {
        return config;
    }

    public static class Criteria {

        @Expose
        @SerializedName("categories")
        private String[] categories;

        @Expose
        @SerializedName("variants")
        private String[] variants;

        @Expose
        @SerializedName("alias")
        private String[] alias;

        @Expose
        @SerializedName("deviceType")
        private String[] deviceType;

        public String[] getCategories() {
            return categories;
        }

        public void setCategories(String... categories) {
            this.categories = categories;
        }

        public String[] getVariants() {
            return variants;
        }

        public void setVariants(String... variants) {
            this.variants = variants;
        }

        public String[] getAlias() {
            return alias;
        }

        public void setAlias(String... alias) {
            this.alias = alias;
        }

        public String[] getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String... deviceType) {
            this.deviceType = deviceType;
        }
    }

    public static class Config {

        @Expose
        @SerializedName("ttl")
        private int timeToLive = -1;

        public int getTimeToLive() {
            return timeToLive;
        }

        public void setTimeToLive(int timeToLive) {
            this.timeToLive = timeToLive;
        }
    }

    public static class Message {

        @Expose
        @SerializedName("alert")
        private String alert;

        @Expose
        @SerializedName("sound")
        private String sound;

        @Expose
        @SerializedName("badge")
        private int badge = -1;

        @Expose
        @SerializedName("user-data")
        private Map<String, Object> userData = new HashMap();

        @Expose
        @SerializedName("simple-push")
        private String simplePush;

        @Expose
        @SerializedName("consolidationKey")
        private String consolidationKey;

        @Expose
        @SerializedName("windows")
        private final Windows windows = new Windows();

        @Expose
        @SerializedName("apns")
        private final APNs apns = new APNs();

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public int getBadge() {
            return badge;
        }

        public void setBadge(int badge) {
            this.badge = badge;
        }

        public Map<String, Object> getUserData() {
            return userData;
        }

        public void setUserData(Map<String, Object> userData) {
            this.userData = userData;
        }

        public String getSimplePush() {
            return simplePush;
        }

        public void setSimplePush(String simplePush) {
            this.simplePush = simplePush;
        }

        public String getConsolidationKey() {
            return consolidationKey;
        }

        public void setConsolidationKey(String consolidationKey) {
            this.consolidationKey = consolidationKey;
        }

        public Windows getWindows() {
            return windows;
        }

        public APNs getApns() {
            return apns;
        }
    }

    public static class Windows {

        @Expose
        @SerializedName("type")
        private String type;

        @Expose
        @SerializedName("duration")
        private String duration;

        @Expose
        @SerializedName("badge")
        private String badge;

        @Expose
        @SerializedName("titleType")
        private String titleType;

        @Expose
        @SerializedName("toastType")
        private String toastType;

        @Expose
        @SerializedName("page")
        private String page;

        @Expose
        @SerializedName("images")
        private final List<String> images = new ArrayList<>();

        @Expose
        @SerializedName("textFields")
        private final List<String> textFields = new ArrayList<>();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getBadge() {
            return badge;
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public String getTitleType() {
            return titleType;
        }

        public void setTitleType(String titleType) {
            this.titleType = titleType;
        }

        public String getToastType() {
            return toastType;
        }

        public void setToastType(String toastType) {
            this.toastType = toastType;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public List<String> getImages() {
            return images;
        }

        public List<String> getTextFields() {
            return textFields;
        }
    }

    public static class APNs {

        @Expose
        @SerializedName("title")
        private String title;

        @Expose
        @SerializedName("action")
        private String action;

        @Expose
        @SerializedName("action-category")
        private String actionCategory;

        @Expose
        @SerializedName("localized-title-key")
        private String localizedTitleKey;

        @Expose
        @SerializedName("localized-title-arguments")
        private final List<String> localizedTitleArguments = new ArrayList<>();

        @Expose
        @SerializedName("url-args")
        private final List<String> urlArgs = new ArrayList<>();

        @Expose
        @SerializedName("content-available")
        private boolean contentAvailable = false;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getActionCategory() {
            return actionCategory;
        }

        public void setActionCategory(String actionCategory) {
            this.actionCategory = actionCategory;
        }

        public String getLocalizedTitleKey() {
            return localizedTitleKey;
        }

        public void setLocalizedTitleKey(String localizedTitleKey) {
            this.localizedTitleKey = localizedTitleKey;
        }

        public List<String> getLocalizedTitleArguments() {
            return localizedTitleArguments;
        }

        public List<String> getUrlArgs() {
            return urlArgs;
        }

        public boolean isContentAvailable() {
            return contentAvailable;
        }

        public void setContentAvailable(boolean contentAvailable) {
            this.contentAvailable = contentAvailable;
        }
    }

}
