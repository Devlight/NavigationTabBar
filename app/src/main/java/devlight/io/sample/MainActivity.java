package devlight.io.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        final View btnHorizontalNtb = findViewById(R.id.btn_horizontal_ntb);
        btnHorizontalNtb.setOnClickListener(this);
        final View btnHorizontalCoordinatorNtb = findViewById(R.id.btn_horizontal_coordinator_ntb);
        btnHorizontalCoordinatorNtb.setOnClickListener(this);
        final View btnTopHorizontalNtb = findViewById(R.id.btn_horizontal_top_ntb);
        btnTopHorizontalNtb.setOnClickListener(this);
        final View btnVerticalNtb = findViewById(R.id.btn_vertical_ntb);
        btnVerticalNtb.setOnClickListener(this);
        final View btnSamplesNtb = findViewById(R.id.btn_samples_ntb);
        btnSamplesNtb.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        ViewCompat.animate(v)
                .setDuration(200)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setInterpolator(new CycleInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {

                    }

                    @Override
                    public void onAnimationEnd(final View view) {
                        switch (v.getId()) {
                            case R.id.btn_horizontal_ntb:
                                startActivity(
                                        new Intent(MainActivity.this, HorizontalNtbActivity.class)
                                );
                                break;
                            case R.id.btn_horizontal_top_ntb:
                                startActivity(
                                        new Intent(MainActivity.this, TopHorizontalNtbActivity.class)
                                );
                                break;
                            case R.id.btn_horizontal_coordinator_ntb:
                                startActivity(
                                        new Intent(MainActivity.this, HorizontalCoordinatorNtbActivity.class)
                                );
                                break;
                            case R.id.btn_vertical_ntb:
                                startActivity(
                                        new Intent(MainActivity.this, VerticalNtbActivity.class)
                                );
                                break;
                            case R.id.btn_samples_ntb:
                                startActivity(
                                        new Intent(MainActivity.this, SamplesNtbActivity.class)
                                );
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onAnimationCancel(final View view) {

                    }
                })
                .withLayer()
                .start();
    }

    private class CycleInterpolator implements android.view.animation.Interpolator {

        private final float mCycles = 0.5f;

        @Override
        public float getInterpolation(final float input) {
            return (float) Math.sin(2.0f * mCycles * Math.PI * input);
        }
    }
}
