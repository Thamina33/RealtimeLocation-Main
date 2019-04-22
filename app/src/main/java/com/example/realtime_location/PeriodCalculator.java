package com.example.realtime_location;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class PeriodCalculator extends AppCompatActivity {
    TextView mTv ;
    Button mBtn;


    Calendar c;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_period_calculator);

        mTv = (TextView)findViewById(R.id.txtView);
        mBtn = (Button)findViewById(R.id.btnPick);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c= Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);




                dpd = new DatePickerDialog(PeriodCalculator.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        mMonth = mMonth+2;
                        if(mMonth>=13){
                            mMonth=Math.abs(mMonth-12);
                            mYear = mYear+1;
                        }
                        mTv.setText("Your Next Period Date: "+mDay+"/"+mMonth+"/"+mYear);

                    }
                },day,month,year);
                dpd.show();
            }

        });
    }
}
