package cn.zju.id21532035;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.*;

import org.w3c.dom.Text;

public class StatusActivity extends AppCompatActivity{
    CoordinatorLayout SnackbarContainer;
    private TextView textViewPkgName, textViewRemainder;
    private EditText editTextMessage;
    private Button btnClear, btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        // Return menu button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find View
        SnackbarContainer = (CoordinatorLayout)findViewById(R.id.SnackbarContainer);
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
                Snackbar.make(SnackbarContainer, "aaa", Snackbar.LENGTH_LONG).show();
                editTextMessage.setText("");
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "<" + getString(R.string.app_name) + ">控制系： " + editTextMessage.getText().toString();

                new PostTask().execute(status);
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
        getMenuInflater().inflate(R.menu.menu_statue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_submit:
                new SubmitProgram().doSubmit(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(StatusActivity.this);
            String username = prefs.getString("username","");
            String password = prefs.getString("password","");
            if(username == "" || password == "") {
                startActivity(new Intent(StatusActivity.this, SettingsActivity.class));
                return "Please update your username and password";
            }
            YambaClient YambaCloud = new YambaClient(username, password);
            try {
                YambaCloud.postStatus(params[0]);
                return "Successfully posted";
            }
            catch(YambaClientException e) {
                e.printStackTrace();
                return "Failed to post to Yamba server.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Snackbar.make(SnackbarContainer, result, Snackbar.LENGTH_LONG).show();
            if(result.startsWith("Successfully")) {
                editTextMessage.setText("");
                textViewPkgName.setText(StatusActivity.this.getPackageName());
            }

        }
    }
}
