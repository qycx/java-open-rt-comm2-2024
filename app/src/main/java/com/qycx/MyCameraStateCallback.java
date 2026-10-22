package com.qycx;


import android.hardware.camera2.CameraDevice;

public class MyCameraStateCallback extends CameraDevice.StateCallback {

    @Override
    public void onOpened(CameraDevice cameraDevice) {
        boolean bDbg = false;
        String str;

    }

    @Override
    public void onDisconnected(CameraDevice camera) {

    }

    @Override
    public void onError(CameraDevice camera, int error) {
        //throw new NotImplementedException();

    }
}
