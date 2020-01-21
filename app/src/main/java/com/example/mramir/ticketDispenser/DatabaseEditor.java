package com.example.mramir.ticketDispenser;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.zj.usbdemo.R;

public class DatabaseEditor extends Activity {
    private TextView visaField;
    private TextView studentField;
    private TextView proxyField;
    private TextView passportField;
    private Button saveButton;
    private Turn todaysTurn;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_editor);
        visaField = findViewById(R.id.database_visa_ni);
        studentField = findViewById(R.id.database_student_ni);
        proxyField = findViewById(R.id.database_proxy_ni);
        passportField = findViewById(R.id.database_passport_ni);
        saveButton = findViewById(R.id.database_save_button);
        resetButton = findViewById(R.id.resetButt);
        final Repository repository = new Repository(getApplicationContext());
        todaysTurn = repository.findTodaysTurn();
        setFields();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int toSave = 1;
                try {
                    toSave = Integer.parseInt(visaField.getText().toString()) < 1 ? 1 : Integer.parseInt(visaField.getText().toString());
                    todaysTurn.setVisaCounter(toSave);
                }catch (NumberFormatException e){}
                try {
                    toSave = Integer.parseInt(studentField.getText().toString()) < 1 ? 1 : Integer.parseInt(studentField.getText().toString());
                    todaysTurn.setDaneshjooCounter(toSave);
                }catch (NumberFormatException e){}
                try {
                    toSave = Integer.parseInt(proxyField.getText().toString()) < 1 ? 1 : Integer.parseInt(proxyField.getText().toString());
                    todaysTurn.setPassportCounter(toSave);
                }catch (NumberFormatException e){}
                try {
                    toSave = Integer.parseInt(passportField.getText().toString()) < 1 ? 1 : Integer.parseInt(passportField.getText().toString());
                    todaysTurn.setProxyCounter(toSave);
                }catch (NumberFormatException e){}
                repository.updateTurn(todaysTurn);
                setFields();
                Toast.makeText(getApplicationContext() , todaysTurn.toString() , Toast.LENGTH_SHORT).show();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                todaysTurn.setProxyCounter(1);
                todaysTurn.setPassportCounter(1);
                todaysTurn.setDaneshjooCounter(1);
                todaysTurn.setVisaCounter(1);
                setFields();
            }
        });
    }
    private void setFields(){
        visaField.setText("" + todaysTurn.getVisaCounter());
        studentField.setText("" + todaysTurn.getDaneshjooCounter());
        proxyField.setText("" + todaysTurn.getProxyCounter());
        passportField.setText("" + todaysTurn.getPassportCounter());
    }

}
