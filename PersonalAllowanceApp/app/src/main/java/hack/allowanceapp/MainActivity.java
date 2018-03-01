package hack.allowanceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private long currentAllowance;

    String FILENAME = "DataSinceLastLogout.txt";
    FileInputStream fis;
    FileOutputStream fos;


    @Override
    protected void onStop() {
        try {

            // Write current time and current allowance into file
            fis.close();
            fos = new FileOutputStream(FILENAME);
            fos.write(longToBytes(currentAllowance));
            fos.write(longToBytes(Calendar.getInstance().getTimeInMillis()), 8, 8);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onStop();
        getDelegate().onStop();
    }

    /*
    // When clicking Send button
    public void SendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
*/
    private void incramentAllowanceSinceLastLogin()
    {
        try {

            byte[] b = new byte[8];
            fis.read(b, 8, 8);
            long timeSinceLastLogin = bytesToLong(b);
            long increments = Calendar.getInstance().getTimeInMillis() - timeSinceLastLogin;
            currentAllowance += (increments / 1000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    private static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }



    private double count = 0.0;
    private double spending = 0.0;


    EditText spendingInput;
    Button btn,decButton;
    TextView txv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Reads the current allowance from file
        try {
            fis = openFileInput(FILENAME);
            byte [] b = new byte[8];
            fis.read(b);
            currentAllowance = bytesToLong(b);
            incramentAllowanceSinceLastLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn = (Button) findViewById(R.id.bt);
        decButton = (Button) findViewById(R.id.decButton);
        txv = (TextView) findViewById(R.id.tx);
        spendingInput = (EditText) findViewById(R.id.spendingInput);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count+=20.0;
                txv.setText(Double.toString(count));
            }
        });

        decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spendingInput.getText().toString() != null || spendingInput.getText().toString() != "") {

                    spending = Double.valueOf(spendingInput.getText().toString());
                    count = count - spending;
                    txv.setText(Double.toString(count));

                }

            }
        });




    }




}