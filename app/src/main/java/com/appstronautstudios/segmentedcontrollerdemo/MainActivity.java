package com.appstronautstudios.segmentedcontrollerdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appstronautstudios.segmentedcontroller.SegmentedController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView output1 = findViewById(R.id.output1);
        final TextView output2 = findViewById(R.id.output2);
        final TextView output3 = findViewById(R.id.output3);
        SegmentedController segmentedController1 = findViewById(R.id.segmented1);
        SegmentedController segmentedController2 = findViewById(R.id.segmented2);
        SegmentedController segmentedController3 = findViewById(R.id.segmented3);
        Button withOnClick = findViewById(R.id.with_onclick);
        Button withoutOnClick = findViewById(R.id.without_onclick);

        segmentedController1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton selectedButton = radioGroup.findViewById(i);
                output1.setText("you selected option with id: " + i + " with text: " + selectedButton.getText());
            }
        });

        segmentedController2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton selectedButton = radioGroup.findViewById(i);
                output2.setText("you selected option with id: " + i + " with text: " + selectedButton.getText());
            }
        });

        segmentedController3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton selectedButton = radioGroup.findViewById(i);
                output3.setText("you selected option with id: " + i + " with text: " + selectedButton.getText());
            }
        });

        withoutOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segmentedController1.checkItem(R.id.one, false);
            }
        });

        withOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segmentedController1.checkItem(R.id.two, true);
            }
        });
    }
}
