package com.example.converter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tv_c, tv_f, tv_k, tv_kzt, tv_usd, tv_astana, tv_moscow;
    SeekBar seekBar, seekBar1;
    double c, f, k, kzt, usd;
    BroadcastReceiver minuteUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_c = (TextView) findViewById(R.id.tv_c);
        tv_f = (TextView) findViewById(R.id.tv_f);
        tv_k = (TextView) findViewById(R.id.tv_k);
        tv_kzt = (TextView) findViewById(R.id.tv_kzt);
        tv_usd = (TextView) findViewById(R.id.tv_usd);
        tv_astana = (TextView) findViewById(R.id.tv_astana);
        tv_moscow = (TextView) findViewById(R.id.tv_moscow);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.setMax(546);
        seekBar.setProgress(273);
        seekBar1.setMax(1000000);
        seekBar1.setProgress(0);

        calculateTemperature(seekBar.getProgress());
        calculateCurrency(seekBar1.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calculateTemperature(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calculateCurrency(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void startMinuteUpdater() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tv_astana.setText("Time in Nur-sultan: " + getTimeInAstana());
                tv_moscow.setText("Time in Moscow: " + getTimeInMoscow());
            }
        };
        registerReceiver(minuteUpdateReceiver, intentFilter);
    }

    public void calculateTemperature(int progress) {
        c = progress - 273;
        f = c * 1.8 + 32;
        k = c + 273.15;

        tv_c.setText(String.format(Locale.getDefault(), "%.2f C", c));
        tv_f.setText(String.format(Locale.getDefault(), "%.2f F", f));
        tv_k.setText(String.format(Locale.getDefault(), "%.2f K", k));
    }

    public void calculateCurrency(int progress) {
        kzt = progress;
        usd = kzt * 0.0024;

        tv_kzt.setText(String.format(Locale.getDefault(), "%.1f KZT", kzt));
        tv_usd.setText(String.format(Locale.getDefault(), "%.1f USD", usd));
    }

    private String getTimeInAstana() {
        Instant nowUtc = Instant.now();
        ZoneId astana = ZoneId.of("UTC+06:00");
        ZonedDateTime nowAstana = ZonedDateTime.ofInstant(nowUtc, astana);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return nowAstana.format(formatter);
    }

    private String getTimeInMoscow() {
        Instant nowUtc = Instant.now();
        ZoneId moscow = ZoneId.of("GMT+3");
        ZonedDateTime nowMoscow = ZonedDateTime.ofInstant(nowUtc, moscow);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return nowMoscow.format(formatter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMinuteUpdater();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(minuteUpdateReceiver);
    }
}