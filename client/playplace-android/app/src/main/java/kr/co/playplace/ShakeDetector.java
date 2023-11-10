package kr.co.playplace;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
    private static final float SHAKE_THRESHOLD_GRAVITY = 5F; // 흔들림 감지 기준힘
    private static final int SHAKE_COUNT_RESET_TIME_MS = 1500; // 흔들림 카운트 초기화 밀리초
    private OhShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount; // 흔들림 카운트

    public void setOnShakeListener(OhShakeListener listener) {
        this.mListener = listener;
    }

    public interface OhShakeListener {
        void onShake(int count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ); // 중력가속도를 포함하는 물체가 받는 힘

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                long now = System.currentTimeMillis();

                // 진동 간격이 너무 짧을 때는 무시
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS > now) {
                    return;
                }

                if (mShakeCount >= 2) {
                    mListener.onShake(mShakeCount);
                    mShakeCount = 0; // 초기화
                }
                mShakeCount++;
                mShakeTimestamp = now;
            }
        }
    }
}
