package app.muhammed.com.androidnetworking;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String CONTACT_URL = "https://api.androidhive.info/contacts/";
    private static final String TAG = MainActivity.class.getName();

    private RecyclerView mContactRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactRecyclerView = findViewById(R.id.contactListView);

        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        // Network call

        new DownloadData().execute(CONTACT_URL);


    }


    /**
     * Network handler
     */
    private class DownloadData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(String... url) {

            try {
                URL mainUrl = new URL(url[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) mainUrl.openConnection();

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                return builder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading...!");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            //TODO continue
            ArrayList<ContactModel> contactModels = parse(s);


            progressDialog.dismiss();

            Log.d(TAG, "onPostExecute: " + s);
        }
    }

    private ArrayList<ContactModel> parse(String s) {
        ArrayList<ContactModel> models = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(s);

            JSONArray contacts = object.getJSONArray("contacts");


            for (int i = 0; i < contacts.length(); i++) {

                ContactModel model = new ContactModel();

                JSONObject contact = contacts.getJSONObject(i);

                model.setName(contact.getString("name"));
                model.setId(contact.getString("id"));
                model.setAddress(contact.getString("address"));
                model.setEmail(contact.getString("email"));
                model.setGender(contact.getString("gender"));


                // Phone parsing

                JSONObject phoneObject = contact.getJSONObject("phone");
                Phone phone = new Phone();

                phone.setHome(phoneObject.getString("home"));
                phone.setOffice(phoneObject.getString("office"));
                phone.setMobile(phoneObject.getString("mobile"));

                model.setPhone(phone);

                models.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return models;
    }
}
