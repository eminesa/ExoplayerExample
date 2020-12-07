package com.eminesa.exoplayerexample;

public class VideoUrl {

    private String  mStringUrl;
    private String  mStringSubtitleUrl;

    public VideoUrl(String mStringUrl, String mStringSubtitleUrl) {
        this.mStringUrl = mStringUrl;
        this.mStringSubtitleUrl = mStringSubtitleUrl;
    }

    public String getStringUrl() {
        return mStringUrl;
    }

    public String getStringSubtitleUrl() {
        return mStringSubtitleUrl;
    }
}