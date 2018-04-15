package hack.allowanceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static DecimalFormat currency = new DecimalFormat(".##");

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


    EditText spendingInput;
    Button incramentButton,decButton;
    TextView currencyTextView;

    private double count = 0.0;
    private double spending = 0.0;

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

        // Finds elements on screen
        incramentButton = (Button) findViewById(R.id.bt);
        decButton = (Button) findViewById(R.id.decButton);
        currencyTextView = (TextView) findViewById(R.id.tx);
        spendingInput = (EditText) findViewById(R.id.spendingInput);

        incramentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count+=6.0;
                currencyTextView.setText(currency.format(count));
                String x = currency.format(count);
            }
        });

        decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spendingInput.getText().toString() != null || spendingInput.getText().toString() != "") {

                    spending = Double.valueOf(spendingInput.getText().toString());
                    count = count - spending;
                    currencyTextView.setText(Double.toString(count));

                }

            }
        });
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
}