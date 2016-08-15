package tomdale.androidnotemaker;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NoteActivity extends AppCompatActivity {

    ArrayList<Note> arrayofWebData = new ArrayList<Note>();

    class Note {
        public String title;
        public String context;
        public String author;
        public String key;
    }

    FancyAdapter aa=null;

    static ArrayList<String> resultRow;

    private View currentSelectedView;
    String selected = null;
    String selectedTitle;
    String selectedContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Created Note View:", "success!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        try {
            String result = "";
            //create the fields into form data
            Globals g = (Globals)getApplication();
            String login_token;
            login_token = g.getData();
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("token", login_token);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            Log.d("postDataForNote: ", postData.toString());

            //convert to byte to send in post
            byte[] postDataBytes = postData.toString().getBytes();

            //set up post and perform post request
            URL url2 = new URL("http://androidnotemaker-1333.appspot.com/note");
            HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
            conn2.setRequestMethod("POST");
            conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn2.setDoOutput(true);
            conn2.getOutputStream().write(postDataBytes);

            //print out response and save token
            StringBuilder sb2 = new StringBuilder();
            String line;
            InputStreamReader isr2 = new InputStreamReader(conn2.getInputStream());
            BufferedReader br2 = new BufferedReader(isr2);
            while ((line = br2.readLine()) != null) {
                System.out.println(line);
                sb2.append(line + "\n");
            }
            isr2.close();
            result=sb2.toString();
            Log.d("Check1:", "check1");
            Log.d("Result", result);

            try{
                Log.d("Check2:", "check2");
                JSONArray jArray = new JSONArray(result);
                Log.d("jArray", jArray.toString());
                for (int i = 0; i<jArray.length(); i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    Log.d("json_data", json_data.toString());
                    Note resultRow = new Note();
                    resultRow.author = json_data.getString("user");
                    Log.d("resultRow author", resultRow.author);
                    resultRow.context = json_data.getString("comment");
                    Log.d("resultRow context", resultRow.context);
                    resultRow.key = json_data.getString("key");
                    Log.d("resultRow key", resultRow.key);
                    resultRow.title = json_data.getString("title");
                    Log.d("resultRow title", resultRow.title);
                    arrayofWebData.add(resultRow);
                }

            } catch (JSONException e){
                e.printStackTrace();
            }

            ListView myListView = (ListView)findViewById(R.id.myListView);
            aa = new FancyAdapter();

            myListView.setAdapter(aa);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        final ListView lv = (ListView) findViewById(R.id.myListView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedFromList = (Note) (lv.getItemAtPosition(position));
                selected = selectedFromList.key;
                selectedTitle = selectedFromList.title;
                selectedContext = selectedFromList.context;
                Log.d("selected: ", selected);
            }
        });

        final Button create_note_button = (Button)findViewById(R.id.newNoteButton);
        final Button edit_note_button = (Button)findViewById(R.id.editNoteButton);
        final Button delete_note_button = (Button)findViewById(R.id.deleteNoteButton);
        create_note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddNoteActivity.class);
                startActivity(i);
            }
        });
        edit_note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != null) {
                    Globals g = (Globals) getApplication();
                    g.setSelected(selected);
                    //setting here instead of pulling with another get request on edit activity
                    g.setSelectedContext(selectedContext);
                    g.setSelectedTitle(selectedTitle);
                    Intent i = new Intent(getApplicationContext(), EditNoteActicity.class);
                    startActivity(i);
                }
            }
        });
        delete_note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != null) {

                    try {
                        Globals g = (Globals) getApplication();
                        g.setSelected(selected);

                        String result = "";

                        //set up DELETE request
                        URL url3 = new URL("http://androidnotemaker-1333.appspot.com/note?id=" + g.getSelected() + "&amp;token=" + g.getData());
                        HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
                        conn3.setRequestMethod("DELETE");
                        conn3.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn3.connect();

                        //print out response and save token
                        StringBuilder sb3 = new StringBuilder();
                        String line;
                        InputStreamReader isr3 = new InputStreamReader(conn3.getInputStream());
                        BufferedReader br3 = new BufferedReader(isr3);
                        while ((line = br3.readLine()) != null) {
                            System.out.println(line);
                            sb3.append(line + "\n");
                        }
                        isr3.close();
                        result = sb3.toString();
                        conn3.disconnect();
                        Log.d("Check1:", "check1");
                        Log.d("Result", result);
                        //setContentView(R.layout.activity_note);
                        //RESET ACTIVITY to get new info
                        finish();
                        startActivity(getIntent());

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class FancyAdapter extends ArrayAdapter<Note> {
        FancyAdapter() {
            super(NoteActivity.this, android.R.layout.simple_list_item_1, arrayofWebData);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView==null){
                LayoutInflater inflater=getLayoutInflater();
                convertView=inflater.inflate(R.layout.row, null);

                holder=new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.populateFrom(arrayofWebData.get(position));

            return(convertView);
        }
    }

    class ViewHolder {
        public TextView title=null;
        public TextView context=null;
        public TextView key=null;


        ViewHolder(View row) {
            title=(TextView)row.findViewById(R.id.NoteTitle);
            context=(TextView)row.findViewById(R.id.NoteContext);
            key=(TextView)row.findViewById(R.id.NoteKey);
        }

        void populateFrom(Note r) {
            title.setText(r.title + ":  ");
            context.setText(r.context);
            key.setText(r.key);
        }
    }
}
