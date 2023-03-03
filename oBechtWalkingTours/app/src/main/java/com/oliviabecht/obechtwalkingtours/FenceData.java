package com.oliviabecht.obechtwalkingtours;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

public class FenceData {

    private final String id;
    private final LatLng latLng;
    private final double lat;
    private final double lng;
    private final double radius;
    private final String buildingAddress;
    private final String buildingDescription;
    private final String buildingFenceColor;
    private final String buildingImage;
    private final int type = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;

    FenceData(String id, LatLng latLng, double lat, double lng, double radius, String buildingAddress, String buildingDescription, String buildingFenceColor, String buildingImage) {
        this.id = id;
        this.latLng = latLng;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.buildingAddress = buildingAddress;
        this.buildingDescription = buildingDescription;
        this.buildingFenceColor = buildingFenceColor;
        this.buildingImage = buildingImage;
    }

    String getId() {
        return id;
    }

    LatLng getLatLng() {
        return latLng;
    }
    Double getLat() {return lat;}
    Double getLng() {return lng;}

    float getRadius() {
        return (float) radius;
    }
    String getBuildingAddress() {
        return buildingAddress;
    }
    String getBuildingDescription() {
        return buildingDescription;
    }
    String getBuildingFenceColor() {
        return buildingFenceColor;
    }
    String getBuildingImage() {
        return buildingImage;
    }

    int getType() {
        return type;
    }


    @NonNull
    @Override
    public String toString() {
        return "Reminder{" +
                "id='" + id + '\'' +
                ", latLng=" + latLng +
                ", radius=" + radius +
                ", type=" + type +
                ", buildingAddress=" + buildingAddress +
                ", buildingDescription=" + buildingDescription +
                ", buildingFenceColor=" + buildingFenceColor +
                ", buildingImage=" + buildingImage +
                '}';
    }
}
