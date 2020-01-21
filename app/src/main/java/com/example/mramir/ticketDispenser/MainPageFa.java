package com.example.mramir.ticketDispenser;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.zj.command.sdk.PrintPicture;
import com.zj.command.sdk.PrinterCommand;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.com.zj.usbdemo.R;
import zj.com.customize.sdk.Other;


public class MainPageFa extends AppCompatActivity {
    private ImageView backFa;
    private Button passportButton;
    private Button proxyButton;
    private Button daneshjooButton;
    private String header;
    private String body;
    private String footer;

    public enum TurnType {PASSPORT, VISA, PROXY, DANESHJOO}
    private static final String LINE = "\n- - - - - - - - - - - - - - - -\n";
    private static final String BigNumber = "aaTURNbb" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LanguageSelect.dev == null && !LanguageSelect.DEBUGMODE) {
            Toast.makeText(getApplicationContext(), "The printer is not connected!!!", Toast.LENGTH_LONG);
            finish();
        }
        // if the intent is not made for Persian -> directly print the ticket and go back to the main screen
        int language = getIntent().getExtras().getInt("language");
        switch (language) {
            case 0://Persian
                this.header = "خوش آمدید٫";
                this.body = "شما نفر " + "aaTURNbb" + " در صف " + "aaTYPEbb" + " می باشید.";
                this.footer = "لطفا به باجه " + "aaDESKbb" + " مراجعه بفرمایید.";
                break;
            case 1://English
                this.header = "Welcome,";
                this.body = "Your  number is " + "aaTURNbb" + " in the queue of " + "aaTYPEbb";
                this.footer = "Please proceed to desk " + "aaDESKbb" + " when it is your turn.";
                print("Visa", TurnType.VISA);
                finish();
                break;
            case 2://Dutch
                this.header = "Welkom,";
                this.body = "Je nummer is " + "aaTURNbb" + " in de rij van " + "aaTYPEbb";
                this.footer = "Ga alstublieft naar de balie " + "aaDESKbb" + " wanneer het jouw beurt is.";
                print("Visa", TurnType.VISA);
                finish();
                break;
            case 3: //French
                this.header = "Bienvenue,";
                this.body = "Votre numéro  est " + "aaTURNbb" + " dans la queue de " + "aaTYPEbb";
                this.footer = "Veuillez vous rendre au guichet " + "aaDESKbb" + " quand c'est votre tour.";
                print("Visa", TurnType.VISA);
                finish();
                break;

            default:
                this.header = "خوش آمدید٫";
                this.body = "شما نفر " + "aaTURNbb" + " در صف " + "aaTYPEbb" + " می باشید.";
                this.footer = "لطفا به باجه " + "aaDESKbb" + " مراجعه بفرمایید.";
                break;
        }
        setContentView(R.layout.activity_main_page_fa);
        passportButton = (Button) findViewById(R.id.passportButton);
        passportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print("وکالت نامه و گذرنامه", TurnType.PASSPORT);
            }
        });
        proxyButton = (Button) findViewById(R.id.proxyButton);
        proxyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print("امور سجلی", TurnType.PROXY);
            }
        });
        daneshjooButton = (Button) findViewById(R.id.daneshjooButton);
        daneshjooButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print("امور دانشجویی/ثبت ازدواج و طلاق", TurnType.DANESHJOO);
            }
        });
        backFa = (ImageView) findViewById(R.id.backFa);
        backFa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void print(String typeString, TurnType type) {
        int turnNumber = LanguageSelect.repository.getNewTurn(type);
        int deskNumber = type == TurnType.PASSPORT ? 1 : 2;
        String modifiedBody = this.body.replaceAll("aaTURNbb", turnNumber + "").replaceAll("aaTYPEbb", typeString);
        String modifiedFooter = this.footer.replaceAll("aaDESKbb", deskNumber + "");
        String toPrint = this.header + "\n" + modifiedBody + "\n" + modifiedFooter;
        if(LanguageSelect.DEBUGMODE){
            Toast.makeText(getApplicationContext(), toPrint, Toast.LENGTH_SHORT).show();;
        }else {
            try {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            } catch (Exception e) {
            }
            String bigNumber = "               " + turnNumber + "";
            Bitmap bm1 = getImageFromAssetsFile("demo.png");
            Bitmap bmp = Other.createAppIconText(null, toPrint, 35, true, 2000);
            Bitmap bignumber = Other.createAppIconText(null, bigNumber, 50, true, 100);
            byte[] buffer = PrinterCommand.POS_Set_PrtInit();
            byte[] sp = PrinterCommand.POS_Set_LineSpace(0);
            int nMode = 0;
            int nPaperWidth = 384;

            if (bmp != null & bignumber != null) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
                printText(LINE);
                // printText("\n\n\n");
                // printText(BigNumber);
                // printText("\n\n\n");
                // printText(turnNumber + "");
                //printText("\n\n\n");
                byte[] bigNumberP = PrintPicture.POS_PrintBMP(bignumber, nPaperWidth, nMode);
                LanguageSelect.usbCtrl.sendByte(bigNumberP, LanguageSelect.dev);
                LanguageSelect.usbCtrl.sendByte(buffer, LanguageSelect.dev);
                LanguageSelect.usbCtrl.sendByte(sp, LanguageSelect.dev);
                byte[] data = PrintPicture.POS_PrintBMP(bmp, nPaperWidth, nMode);
                LanguageSelect.usbCtrl.sendByte(data, LanguageSelect.dev);
                LanguageSelect.usbCtrl.sendByte(new byte[]{0x1b, 0x4a, 0x30, 0x1d, 0x56, 0x42, 0x01}, LanguageSelect.dev);
                printText("\n\n\n\n\n");
                printText(ft.format(new Date()));
                printText(LINE);
                printText("\n\n\n");
                try {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                } catch (Exception e) {
                }
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "The text is null", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    private void printText(final String string) {
        LanguageSelect.usbCtrl.sendByte(new byte[]{0x1c, 0x26, 0x1b, 0x74, (byte) 0x00}, LanguageSelect.dev);
        LanguageSelect.usbCtrl.sendMsg(string, "GBK", LanguageSelect.dev);
    }
}
