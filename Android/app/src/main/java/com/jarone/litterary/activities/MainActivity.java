package com.jarone.litterary.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.jarone.litterary.DroneState;
import com.jarone.litterary.GroundStation;
import com.jarone.litterary.R;
import com.jarone.litterary.handlers.MessageHandler;

import dji.sdk.api.DJIDrone;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;


public class MainActivity extends DJIBaseActivity {

    public static final int POINTS_REQUEST_CODE = 130;
    public static final int POINTS_RESULT_CODE = 230;

    private DjiGLSurfaceView mDjiGLSurfaceView;

    private static final String TAG = MainActivity.class.toString();

    private Context mainActivity;

    //Activity is starting.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        setOnClickListeners();

        registerCamera();


    }


    public View.OnClickListener getDirectionalListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_up:
                        //TODO: Up Code
                        if (DroneState.getMode() == DroneState.DIRECT_MODE) {
                            GroundStation.setAngles(DroneState.getPitch() + 5, DroneState.getYaw(), DroneState.getRoll());
                        }
                        break;
                    case R.id.button_down:
                        //TODO: Down Code
                        if (DroneState.getMode() == DroneState.DIRECT_MODE) {
                            GroundStation.setAngles(DroneState.getPitch() - 5, DroneState.getYaw(), DroneState.getRoll());
                        }
                        break;
                    case R.id.button_left:
                        //TODO: Left Code
                        if (DroneState.getMode() == DroneState.DIRECT_MODE) {
                            GroundStation.setAngles(DroneState.getPitch(), DroneState.getYaw(), DroneState.getRoll() - 5);
                        }
                        break;
                    case R.id.button_right:
                        //TODO: Right Code
                        if (DroneState.getMode() == DroneState.DIRECT_MODE) {
                            GroundStation.setAngles(DroneState.getPitch() + 5, DroneState.getYaw(), DroneState.getRoll() + 5);
                        }
                        break;
                    case R.id.button_stop:
                        if (DroneState.getMode() == DroneState.DIRECT_MODE) {
                            GroundStation.setAngles(0, 0, 0);
                        } else {
                            GroundStation.stopTask();
                        }
                        break;
                }
            }
        };
    }

    public View.OnClickListener getHomeButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_go_home:
                        GroundStation.goHome();
                        break;
                    case R.id.button_set_home:
                        GroundStation.setHomePoint();
                        break;
                }
            }
        };
    }

    public View.OnClickListener setAltitudeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float altitude = getAltitudeValue();
                if (altitude == -1) {
                    MessageHandler.d("Please enter a valid number");
                } else if (altitude < 100) {
                    GroundStation.setAltitude(altitude);
                } else {
                    MessageHandler.d("Please choose valid altitude <100 m");
                }
            }
        };
    }

    public View.OnClickListener getStartSurveyListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroundStation.startSurveyRoute();
            }
        };
    }

    private void registerCamera() {
        mDjiGLSurfaceView = (DjiGLSurfaceView) findViewById(R.id.DjiSurfaceView_02);
        mDjiGLSurfaceView.start();

        DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = new DJIReceivedVideoDataCallBack() {
            @Override
            public void onResult(byte[] videoBuffer, int size) {
                mDjiGLSurfaceView.setDataToDecoder(videoBuffer, size);

            }
        };
        DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(mReceivedVideoDataCallBack);
    }


    private void setOnClickListeners() {
        findViewById(R.id.button_down).setOnClickListener(getDirectionalListener());
        findViewById(R.id.button_left).setOnClickListener(getDirectionalListener());
        findViewById(R.id.button_right).setOnClickListener(getDirectionalListener());
        findViewById(R.id.button_up).setOnClickListener(getDirectionalListener());
        findViewById(R.id.button_stop).setOnClickListener(getDirectionalListener());
        findViewById(R.id.button_go_home).setOnClickListener(getHomeButtonListener());
        findViewById(R.id.button_set_home).setOnClickListener(getHomeButtonListener());
        findViewById(R.id.button_set_altitude).setOnClickListener(setAltitudeListener());
        findViewById(R.id.button_set_region).setOnClickListener(setRegionClickListener());
        findViewById(R.id.button_start_survey).setOnClickListener(getStartSurveyListener());

    }


    private Bitmap viewToBitmap(DjiGLSurfaceView view) {
        Bitmap b = Bitmap.createBitmap(view.getLayoutParams().width, view.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(c);

        return b;
    }


    private View.OnClickListener setRegionClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mainActivity, MapActivity.class), POINTS_REQUEST_CODE);
            }
        };
    }

    public float getAltitudeValue() {
        EditText text = (EditText) findViewById(R.id.editText);
        try {
            return Float.parseFloat(text.getText().toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POINTS_REQUEST_CODE && resultCode == POINTS_RESULT_CODE) {
            LatLng[] parcel = (LatLng[]) data.getParcelableArrayExtra("points");

            GroundStation.initializeSurveyRoute(parcel, getAltitudeValue());
        }
    }
}
