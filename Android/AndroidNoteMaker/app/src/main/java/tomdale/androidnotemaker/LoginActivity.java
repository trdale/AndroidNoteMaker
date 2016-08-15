package tomdale.androidnotemaker;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private String login_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final String[] LOCATION_PERMS= {"android.permission.ACCESS_FINE_LOCATION"};
        final int LOCATION_REQUEST = 200;

        requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);

        final Button login_button = (Button) findViewById(R.id.submit_Login);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login_token = null; //reset the login token

                //get the login ID from phone field
                EditText loginID  = (EditText)findViewById(R.id.userID);
                String uname = loginID.getText().toString();
                Log.d("uname: ", uname);

                //get the login pass from phone field
                EditText loginPass = (EditText)findViewById(R.id.loginpass);
                String pw = loginPass.getText().toString();
                Log.d("pw", pw);

                try {

                    //create the fields into form data
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("uname", uname);
                    params.put("pw", pw);

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    Log.d("postData: ", postData.toString());

                    //convert to byte to send in post
                    byte[] postDataBytes = postData.toString().getBytes();

                    //set up post and perform post request
                    URL url = new URL("http://androidnotemaker-1333.appspot.com/login");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);

                    //print out response and save token
                    StringBuilder sb = new StringBuilder();
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        sb.append(line);
                    }
                    br.close();
                    conn.disconnect();
                    try {
                        JSONObject json = new JSONObject(sb.toString());
                        String token = json.getString("token");
                        login_token = token;
                        Log.d("login_token", login_token);
                        Globals g = (Globals)getApplication();
                        g.setData(login_token);
                        if (g.getData() != null) {
                            Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                            startActivity(i);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
                catch (IOException e){
                    e.printStackTrace();
                }

            }
        });
    }
}
