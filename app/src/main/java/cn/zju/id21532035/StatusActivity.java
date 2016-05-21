package cn.zju.id21532035;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.marakana.android.yamba.clientlib.SubmitProgram;

import org.w3c.dom.Text;

public class StatusActivity extends AppCompatActivity {

    private TextView textViewPkgName, textViewRemainder;
    private EditText editTextMessage;
    private Button btnClear, btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Find View
        textViewPkgName = (TextView)findViewById(R.id.textViewPkgName);
        textViewRemainder = (TextView)findViewById(R.id.textViewRemainder);
        editTextMessage = (EditText)findViewById(R.id.editTextMessage);
        btnClear = (Button)findViewById(R.id.buttonClear);
        btnPublish = (Button)findViewById(R.id.buttonPublish);

        // Set Package Name
        textViewPkgName.setText(this.getPackageName());

        // Button
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextMessage.setText("");
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // EditText
        textViewRemainder.setTextColor(Color.GREEN);
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                int Count = 140 - editTextMessage.length();
                textViewRemainder.setText(Integer.toString(Count));

                if(Count <= 0)
                    textViewRemainder.setTextColor(Color.RED);
                else if(Count < 10)
                    textViewRemainder.setTextColor(Color.YELLOW);
                else
                    textViewRemainder.setTextColor(Color.GREEN);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_menu_upload) {
            new SubmitProgram().doSubmit(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
