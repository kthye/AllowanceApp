package hack.allowanceapp;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String FILENAME = "dateData.txt";

    private static Context context;

    private double allowance = 0.0;
    private double spending = 0.0;

    private EditText _spendingInput;
    private Button _incramentButton, _decrementButton;
    private TextView _currentAllowanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        // TODO: Reads the current allowance from file
        try {
            DataInputStream inputDataFile = new DataInputStream(openFileInput(FILENAME));
            byte [] tempBytes = new byte[8];
            allowance = inputDataFile.readDouble();
            pastTime = inputDataFile.readLong();
            double secondsPassed = Long.valueOf(System.currentTimeMillis() - pastTime)
                    .doubleValue()/1000.0;
            allowance += secondsPassed/100.0;
            inputDataFile.close();
        } catch (FileNotFoundException e)
        {
            // TODO: CHECK IF NEW ACCOUNT
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        _incramentButton = (Button) findViewById(R.id.IncrementButton);
        _decrementButton = (Button) findViewById(R.id.DecrementButton);
        _currentAllowanceText = (TextView) findViewById(R.id.CurrentAllowanceText);
        _spendingInput = (EditText) findViewById(R.id.SpendingInputText);

        _incramentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowance +=20.0;
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
                _currentAllowanceText.setText(currencyFormatter.format(allowance));
            }
        });

        _decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = _spendingInput.getText().toString();
                if (amount.isEmpty()) {
                    return;
                }
                spending = Double.valueOf(_spendingInput.getText().toString());
                allowance = allowance - spending;
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
                _currentAllowanceText.setText(currencyFormatter.format(allowance));
            }
        });

        Button b = (Button) findViewById(R.id.StartButton);
        b.setText("stop");
        timerHandler.postDelayed(timerRunnable, 0);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");
                } else {
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("stop");
                }
            }
        });

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            FileOutputStream outputFileStream = openFileOutput(FILENAME, context.MODE_PRIVATE);
//            DataOutputStream dataOutputStream = new DataOutputStream(outputFileStream);
//            dataOutputStream.writeDouble(allowance);
//            dataOutputStream.writeLong(System.currentTimeMillis());
//            dataOutputStream.close();
//        } catch (FileNotFoundException e) {
//            // TODO file should always be found
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            FileOutputStream outputFileStream = openFileOutput(FILENAME, context.MODE_PRIVATE);
            DataOutputStream dataOutputStream = new DataOutputStream(outputFileStream);
            dataOutputStream.writeDouble(allowance);
            dataOutputStream.writeLong(System.currentTimeMillis());
            dataOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO: file should always be found
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // *******************************************************************************************
    /* Work in progress */

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
    public static double convertToDouble(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getDouble();
    }

    long pastTime = 0;
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            if (System.currentTimeMillis() > pastTime + 1000) {
                pastTime = System.currentTimeMillis();
                allowance += 0.01;
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
                _currentAllowanceText.setText(currencyFormatter.format(allowance));
            }

            timerHandler.postDelayed(this, 500);
        }
    };
}