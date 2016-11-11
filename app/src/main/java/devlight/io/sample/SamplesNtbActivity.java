package devlight.io.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.widget.Toast;

import devlight.io.library.ntb.NavigationTabBar;

import java.util.ArrayList;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class SamplesNtbActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples_ntb);
        initUI();
    }

    private void initUI() {
        final NavigationTabBar ntbSample1 = (NavigationTabBar) findViewById(R.id.ntb_sample_1);
        final ArrayList<NavigationTabBar.Model> models1 = new ArrayList<>();
        models1.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first), Color.WHITE
                ).build()
        );
        models1.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second), Color.LTGRAY
                ).build()
        );
        models1.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third), Color.GRAY
                ).build()
        );
        models1.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fourth), Color.DKGRAY
                ).build()
        );
        ntbSample1.setModels(models1);

        final NavigationTabBar ntbSample2 = (NavigationTabBar) findViewById(R.id.ntb_sample_2);
        final ArrayList<NavigationTabBar.Model> models2 = new ArrayList<>();
        models2.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_seventh), Color.YELLOW
                ).build()
        );
        models2.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_sixth), Color.YELLOW
                ).build()
        );
        models2.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth), Color.YELLOW
                ).build()
        );
        models2.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_eighth), Color.YELLOW
                ).build()
        );
        models2.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second), Color.YELLOW
                ).build()
        );
        ntbSample2.setModels(models2);
        ntbSample2.setModelIndex(3, true);

        final NavigationTabBar ntbSample3 = (NavigationTabBar) findViewById(R.id.ntb_sample_3);
        final ArrayList<NavigationTabBar.Model> models3 = new ArrayList<>();
        models3.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_seventh), Color.RED
                ).build()
        );
        models3.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_seventh), Color.RED
                ).build()
        );
        models3.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_seventh), Color.RED
                ).build()
        );
        ntbSample3.setModels(models3);
        ntbSample3.setModelIndex(1, true);

        final NavigationTabBar ntbSample4 = (NavigationTabBar) findViewById(R.id.ntb_sample_4);
        final int bgColor = Color.parseColor("#423752");
        final ArrayList<NavigationTabBar.Model> models4 = new ArrayList<>();
        models4.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth), bgColor
                ).build()
        );
        models4.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first), bgColor
                ).build()
        );
        models4.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fourth), bgColor
                ).build()
        );
        models4.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_sixth), bgColor
                ).build()
        );
        models4.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third), bgColor
                ).build()
        );
        ntbSample4.setModels(models4);
        ntbSample4.setModelIndex(2, true);

        final NavigationTabBar ntbSample5 = (NavigationTabBar) findViewById(R.id.ntb_sample_5);
        final ArrayList<NavigationTabBar.Model> models5 = new ArrayList<>();
        models5.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth), Color.WHITE
                ).build()
        );
        models5.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first), Color.WHITE
                ).build()
        );
        models5.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fourth), Color.WHITE
                ).build()
        );
        models5.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_sixth), Color.WHITE
                ).build()
        );
        ntbSample5.setModels(models5);
        ntbSample5.setModelIndex(2, true);
        ntbSample5.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                Toast.makeText(SamplesNtbActivity.this, String.format("onEndTabSelected #%d", index), Toast.LENGTH_SHORT).show();
            }
        });

        final NavigationTabBar ntbSample6 = (NavigationTabBar) findViewById(R.id.ntb_sample_6);
        final ArrayList<NavigationTabBar.Model> models6 = new ArrayList<>();
        models6.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth), randomColor()
                ).build()
        );
        models6.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first), randomColor()
                ).build()
        );
        models6.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fourth), randomColor()
                ).build()
        );
        models6.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_sixth), randomColor()
                ).build()
        );
        ntbSample6.setModels(models6);
    }

    private int randomColor() {
        float[] TEMP_HSL = new float[]{0, 0, 0};
        float[] hsl = TEMP_HSL;
        hsl[0] = (float) (Math.random() * 360);
        hsl[1] = (float) (40 + (Math.random() * 60));
        hsl[2] = (float) (40 + (Math.random() * 60));
        return ColorUtils.HSLToColor(hsl);
    }
}
