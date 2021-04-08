package com.example.beeponcetimer;

        import androidx.appcompat.app.AppCompatActivity;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

public class EditActivity extends AppCompatActivity {
    private final String TAG = "Main";
    Button sbtn;
    Button erbbtn;
    EditText editTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        sbtn = findViewById(R.id.sbtn);
        erbbtn = findViewById(R.id.erbbtn);
        editTime = findViewById(R.id.editTime);



        sbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (editTime.getText().toString().length() > 6 || editTime.getText().toString().length() < 6) {
                        Toast.makeText(EditActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();
                    }
                    if (Integer.parseInt(editTime.getText().toString().substring(0, 2)) >= 0 && Integer.parseInt(editTime.getText().toString().substring(0, 2)) <= 23 &&
                            Integer.parseInt(editTime.getText().toString().substring(2, 4)) >= 0 && Integer.parseInt(editTime.getText().toString().substring(2, 4)) <= 59 &&
                            Integer.parseInt(editTime.getText().toString().substring(4, 6)) >= 1 && Integer.parseInt(editTime.getText().toString().substring(4, 6)) <= 59) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("changedTime", editTime.getText().toString());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(EditActivity.this, "Please select a value between 000001 and 235959", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e){
                    Toast.makeText(EditActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();
                }
            }
        });
        erbbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("changedTime", "nochange");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


    }

}