package de.lorenz.da_exam_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // enable toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void enableInsertButton() {
        Button insertButton = findViewById(R.id.insertButton);
        insertButton.setEnabled(true);
        insertButton.setText(R.string.insert_button_text);
    }

    public void retrieveName(View view) {
        Button retrieveButton = view.findViewById(R.id.retrieveButton);

        RequestQueue queue = Volley.newRequestQueue(this);
        retrieveButton.setEnabled(false);

        String backendUrl = getString(R.string.backend_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, backendUrl + "api/name", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                retrieveButton.setEnabled(true);
                try {
                    Toast.makeText(MainActivity.this, "Hello Dear " + response.getString("name"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                retrieveButton.setEnabled(true);
                Toast.makeText(MainActivity.this, R.string.retrieve_button_text_error, Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);

    }

    public void saveName(View view) {
        Button insertButton = view.findViewById(R.id.insertButton);
        TextView nameInput = findViewById(R.id.nameInput);

        insertButton.setEnabled(false);
        String name = nameInput.getText().toString();

        // handle error
        if (name.isEmpty()) {
            insertButton.setText(R.string.insert_button_text_error);

            // re-enable button after 2 seconds
            insertButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enableInsertButton();
                }
            }, 2000);
        }

        // handle backend request
        insertButton.setText(R.string.insert_button_text_loading);

        RequestQueue queue = Volley.newRequestQueue(this);
        String backendUrl = getString(R.string.backend_url);
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("name", name);
        } catch (Exception e) {
            System.out.println(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, backendUrl + "api/name", jsonData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                insertButton.setText(R.string.insert_button_text_success);

                // re-enable button after 2 seconds
                insertButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableInsertButton();
                    }
                }, 2000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, R.string.insert_button_text_error, Toast.LENGTH_LONG).show();
                enableInsertButton();
            }
        });

        queue.add(request);
    }
}