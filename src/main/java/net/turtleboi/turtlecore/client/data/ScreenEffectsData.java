package net.turtleboi.turtlecore.client.data;

public class ScreenEffectsData {
    private static float cameraShakeIntensity = 0;
    private static int cameraShakeDuration = 0;

    public static Float getCameraShakeIntensity(){
        return cameraShakeIntensity;
    }

    public static void setCameraShakeIntensity(float intensity) {
        cameraShakeIntensity = intensity;
    }

    public static Integer getCameraShakeDuration(){
        return cameraShakeDuration;
    }

    public static void setCameraShakeDuration(int duration) {
        cameraShakeDuration = duration;
    }
}
