package me.jiyun233.nya.event.events.client;

import me.jiyun233.nya.event.events.EventStage;

public class AspectRatioEvent extends EventStage {
    private float aspectRatio;

    public AspectRatioEvent(int stage, float aspectRatio) {
        super(stage);
        this.aspectRatio = aspectRatio;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

}