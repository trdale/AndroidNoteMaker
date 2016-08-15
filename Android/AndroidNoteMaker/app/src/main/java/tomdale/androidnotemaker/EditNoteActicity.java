package tomdale.androidnotemaker;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditNoteActicity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals g = (Globals) getApplication();
        setContentView(R.layout.activity_edit_note_acticity);
        EditText title = (EditText)findViewById(R.id.editNoteTitle);
        title.setText(g.getSelectedTitle());

        EditText context = (EditText)findViewById(R.id.editNoteContext);
        context.setText(g.getSelectedContext());

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        //set long and lat into forms
        final EditText notelong = (EditText)findViewById(R.id.editNoteLong);
        notelong.setText(Double.toString(longitude));

        final EditText notelat = (EditText)findViewById(R.id.editNoteLat);
        notelat.setText(Double.toString(latitude));

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final Button editNoteButton = (Button) findViewById(R.id.EditNoteSubmit);
        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String login_token = null;
                Globals g = (Globals) getApplication();
                login_token = g.getData();

                //get the data from the forms
                EditText newNoteTitle = (EditText) findViewById(R.id.editNoteTitle);
                String title = newNoteTitle.getText().toString();

                EditText newNoteContext = (EditText) findViewById(R.id.editNoteContext);
                String context = newNoteContext.getText().toString();

                try {

                    //create the fields into form data
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("title", title);
                    params.put("context", context);
                    params.put("user", login_token);
                    params.put("id", g.getSelected());
                    params.put("long", notelong);
                    params.put("lat", notelat);

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    Log.d("postDataAddNote: ", postData.toString());

                    //convert to byte to send in post
                    byte[] postDataBytes = postData.toString().getBytes();

                    //set up post and perform post request
                    URL url = new URL("http://androidnotemaker-1333.appspot.com/editNote");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
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


                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(i);

            }
        });
    }
}
