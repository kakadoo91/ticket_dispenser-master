package com.example.mramir.ticketDispenser;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.usbsdk.UsbController;

import java.util.List;

import cn.com.zj.usbdemo.R;


public class LanguageSelect extends AppCompatActivity {

    public enum LangugeEnum {PERSIAN, ENGLISH, DUTCH, FRENCH}
    private Button infoButton;
    private ImageView persianImage;
    private ImageView englishImage;
    private ImageView dutchImage;
    private ImageView frenchImage;
    private TextView welcomingTX;
    private Button connectButton;
    private int counter;
    public static UsbController usbCtrl = null;
    public static UsbDevice dev = null;
    //toggle to switch between debug and release
    public static final boolean DEBUGMODE = false;
    public static Repository repository = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);
        persianImage = findViewById(R.id.persianImage);
        englishImage = findViewById(R.id.englishImage);
        dutchImage = findViewById(R.id.dutchImage);
        frenchImage = findViewById(R.id.frenchImage);
        welcomingTX = findViewById(R.id.welcomimgTX);
        connectButton = findViewById(R.id.connectButton);
        infoButton = findViewById(R.id.Information);
        //build the database
        if(repository == null) repository = new Repository(getApplicationContext());
        //delete old turn from previous days
        repository.cleanup();
        counter = 0;
        if (usbCtrl == null && !DEBUGMODE) {
            connectUSB();
        }
        persianImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeActivity(LangugeEnum.PERSIAN);
            }
        });
        englishImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeActivity(LangugeEnum.ENGLISH);
            }
        });
        dutchImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeActivity(LangugeEnum.DUTCH);
            }
        });
        frenchImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeActivity(LangugeEnum.FRENCH);
            }
        });
        changeText();
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButton.setVisibility(View.VISIBLE);
                connectUSB();
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Turn> allTurns = repository.getAllAsyc();
                String toDisplay = "";
                for( Turn t : allTurns){
                    if(!toDisplay.equals("")) toDisplay += "\n";
                    toDisplay += t.toString();
                }
                Toast.makeText(getApplicationContext(),toDisplay, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void connectUSB() {
        int[][] u_infor = new int[8][2];
        u_infor[0][0] = 0x1CBE;
        u_infor[0][1] = 0x0003;
        u_infor[1][0] = 0x1CB0;
        u_infor[1][1] = 0x0003;
        u_infor[2][0] = 0x0483;
        u_infor[2][1] = 0x5740;
        u_infor[3][0] = 0x0493;
        u_infor[3][1] = 0x8760;
        u_infor[4][0] = 0x0416;
        u_infor[4][1] = 0x5011;
        u_infor[5][0] = 0x0416;
        u_infor[5][1] = 0xAABB;
        u_infor[6][0] = 0x1659;
        u_infor[6][1] = 0x8965;
        u_infor[7][0] = 0x0483;
        u_infor[7][1] = 0x5741;
        usbCtrl = new UsbController(this, mHandler);
        usbCtrl.close();
        int i = 0;
        for (i = 0; i < 8; i++) {
            dev = usbCtrl.getDev(u_infor[i][0], u_infor[i][1]);
            if (dev != null) break;
        }
        if (dev != null && !(usbCtrl.isHasPermission(dev))) {
            Toast.makeText(getApplicationContext(), "The printer is connected", Toast.LENGTH_LONG);
            usbCtrl.getPermission(dev);
            persianImage.setVisibility(View.VISIBLE);
            englishImage.setVisibility(View.VISIBLE);
            dutchImage.setVisibility(View.VISIBLE);
            frenchImage.setVisibility(View.VISIBLE);
            welcomingTX.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.GONE);
        } else {
            Toast.makeText(getApplicationContext(), "The printer is not connected", Toast.LENGTH_LONG);
            persianImage.setVisibility(View.GONE);
            englishImage.setVisibility(View.GONE);
            dutchImage.setVisibility(View.GONE);
            frenchImage.setVisibility(View.GONE);
            welcomingTX.setVisibility(View.GONE);
            connectButton.setVisibility(View.VISIBLE);
        }
    }

    private void changeActivity(LangugeEnum language) {
        Intent i = new Intent(this, MainPageFa.class);
        i.putExtra("language" , language.ordinal());
        startActivity(i);
    }

    private void changeText() {
        new CountDownTimer(5000, 5000) {

            public void onTick(long millisUntilFinished) {
                String text = "";
                if (counter == 0)
                    text = "خوش آمدید \nبرای امور ایرانیان٫ لطفا پرچم ایران را انتخاب کنید.";
                else if (counter == 1)
                    text = "Welcome\nFor visa and legalisation, please select the British flag.";
                else if (counter == 2) text = "Bienvenue\nPour le visa et la légalisation, veuillez sélectionner le drapeau belge";
                else if (counter == 3) text = "Welkom\nSelecteer de Belgische vlag voor visa en legalisatie.";
                welcomingTX.setText(text);
                if (counter >= 3) counter = 0;
                else counter++;
            }

            public void onFinish() {
                changeText();
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbController.USB_CONNECTED:
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_getpermission),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!DEBUGMODE) {
            usbCtrl.close();
        }
        usbCtrl = null;
        dev = null;
        repository = null;
        Toast.makeText(getApplicationContext(), "The resources were released.", Toast.LENGTH_SHORT).show();
    }
}
