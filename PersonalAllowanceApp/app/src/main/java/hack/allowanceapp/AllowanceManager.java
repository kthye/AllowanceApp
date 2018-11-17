package hack.allowanceapp;

import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class AllowanceManager {
    String FILENAME = "dateData.txt";

    private static AllowanceManager m_allowanceManager;

    private double _allowance = 0.0;
    private long _pastTime = 0;

    public static AllowanceManager getInstance(Context context) {
        if (m_allowanceManager == null) {
            m_allowanceManager = new AllowanceManager(context);
        }

        return m_allowanceManager;
    }

    private AllowanceManager(Context context) {
        m_allowanceManager = this;
        updateAllowanceFromFile(context);
    }

    public void incrementAllowance(double amount) {
        _allowance += amount;
    }

    public void decrementAllowance(double amount) {
        _allowance -= amount;
    }

    public double getAllowance() {
        return _allowance;
    }


    public void updateAllowanceToFile(Context context) {
        try {
            FileOutputStream outputFileStream = context.openFileOutput(FILENAME,
                    context.MODE_PRIVATE);
            DataOutputStream dataOutputStream = new DataOutputStream(outputFileStream);
            dataOutputStream.writeDouble(_allowance);
            dataOutputStream.writeLong(System.currentTimeMillis());
            dataOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO: file should always be found
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAllowanceFromFile(Context context) {
        // TODO: Reads the current allowance from file
        try {
            DataInputStream inputDataFile = new DataInputStream(context.openFileInput(FILENAME));
            _allowance = inputDataFile.readDouble();
            _pastTime = inputDataFile.readLong();
            double secondsPassed = Long.valueOf(System.currentTimeMillis() - _pastTime)
                    .doubleValue()/1000.0;
            _allowance += secondsPassed/100.0;
            inputDataFile.close();
        } catch (FileNotFoundException e)
        {
            // TODO: CHECK IF NEW ACCOUNT
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
