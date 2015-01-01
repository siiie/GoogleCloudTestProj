package com.google_cloud_app;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    EndpointsAsyncTask end;
    GcmRegistrationAsyncTask gcmRegTask;
    Button bSum;
    Button bGetRegId;
    EditText firstNum;
    EditText secondNum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bSum= (Button) findViewById(R.id.bSum);
        bGetRegId= (Button) findViewById(R.id.bGetRegId);
        bGetRegId.setOnClickListener(this);
        bSum.setOnClickListener(this);

        firstNum = (EditText) findViewById(R.id.eNum1);
        secondNum = (EditText) findViewById(R.id.eNum2);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bGetRegId:
                gcmRegTask=new GcmRegistrationAsyncTask(this);
                gcmRegTask.execute(this);

                 break;
            case R.id.bSum:
                end=new EndpointsAsyncTask();
                end.execute(new Pair<Context, Integer>(this,Integer.parseInt(firstNum.getText().toString())),
                        new Pair<Context, Integer>(null,Integer.parseInt(secondNum.getText().toString())));
                break;

        }




        //end.execute(new Pair<Context, Integer>(this,3));
    }
}
