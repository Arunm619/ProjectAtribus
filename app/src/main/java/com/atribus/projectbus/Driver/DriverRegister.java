package com.atribus.projectbus.Driver;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.projectbus.Model.BusRoutes;
import com.atribus.projectbus.Model.Driver;
import com.atribus.projectbus.Model.User;
import com.atribus.projectbus.R;
import com.atribus.projectbus.User.UserHomeActivity;
import com.atribus.projectbus.User.UserRegister;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DriverRegister extends AppCompatActivity {
    MaterialEditText et_name, et_license, et_mobilenumber, et_dob;
    MaterialAutoCompleteTextView et_busroute;
    Button btn_register;
    View v;
    FirebaseDatabase database;
    DatabaseReference driversNode;
    FirebaseUser currentUser;

    BusRoutes busdb;
    String name, license, busroute, mobilenumber, dob;
    private Date mybirthday;

    //shared pref
    private static final String MY_PREFS_NAME = "Mydatabase";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(" Driver Register ");

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        setupviews();
        busdb = new BusRoutes();
        database = FirebaseDatabase.getInstance();
        driversNode = database.getReference("Drivers");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        busroutesetter();
        licensesetter();
        dobsetter();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = nullcheck();
                if (a != -1)
                    doregister();
            }
        });

    }

    private void busroutesetter() {
        //bus route - working

        /* *************************************/
        String[] busroutenumbers =
                busdb.Areasroutes.keySet().toArray(new String[busdb.Areasroutes.size()]);
        ArrayAdapter <String> adapter =
                new ArrayAdapter <>(this, android.R.layout.simple_list_item_1, busroutenumbers);
        et_busroute.setThreshold(1);
        et_busroute.setAdapter(adapter);

    }

    private void licensesetter() {
        et_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final List <String> licenses = new ArrayList <>();
                for (String s : busdb.licenses.keySet()) {
                    if (s != null)
                        licenses.add(s);

                }

                new MaterialDialog.Builder(DriverRegister.this)
                        .title(R.string.chooseLicense)
                        .items(licenses)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_license.setText(text);
                            }
                        })
                        .show();
            }
        });
    }

    private void doregister() {

        Driver driver = new Driver();
        driver.setName(name);
        driver.setPhonenumber(mobilenumber);
        driver.setBusroute(busroute);
        driver.setLicense(license);
        driver.setDateOfBirth(parseDatetostring(mybirthday));

        //generating USER ID
        String UUID = currentUser.getUid();

        driver.setUuid(UUID);


        //pushing child driver to driversNode
        driversNode.child(UUID).setValue(driver);


        //storing offline copy of UserObject in sharedpred under MYDB
        Gson gson = new Gson();
        String userobjectasstring = gson.toJson(driver);
        editor.putString("DriverObj", userobjectasstring);
        editor.apply();
        Toast.makeText(this, "Successfully Stored", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(DriverRegister.this, DriverHomeActivity.class));
        finish();
    }

    private String parseDatetostring(Date date) {
        SimpleDateFormat simpleDate;
        simpleDate = new SimpleDateFormat("dd/MM/yyyy");

        return simpleDate.format(date);
    }


    private int nullcheck() {
        name = et_name.getText().toString().trim();
        license = et_license.getText().toString().trim();
        busroute = et_busroute.getText().toString().trim();
        mobilenumber = et_mobilenumber.getText().toString().trim();
        dob = et_dob.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Snackbar.make(v, "Enter name", Snackbar.LENGTH_SHORT).show();
            return -1;
        }
        if (TextUtils.isEmpty(license)) {
            Snackbar.make(v, "Choose license", Snackbar.LENGTH_SHORT).show();
            return -1;
        }
        if (TextUtils.isEmpty(busroute)) {
            Snackbar.make(v, "Choose busroute", Snackbar.LENGTH_SHORT).show();
            return -1;
        }
        if (TextUtils.isEmpty(mobilenumber)) {
            Snackbar.make(v, "Enter mobile number", Snackbar.LENGTH_SHORT).show();
            return -1;
        }
        if ((mobilenumber.length() != 10)) {
            Snackbar.make(v, "Check mobile number", Snackbar.LENGTH_SHORT).show();
            return -1;
        }

        if (TextUtils.isEmpty(dob)) {
            Snackbar.make(v, "Choose dob", Snackbar.LENGTH_SHORT).show();
            return -1;
        }


        return 0;
    }

    private void setupviews() {
        v = findViewById(R.id.ll);
        btn_register = findViewById(R.id.btn_register);
        et_busroute = findViewById(R.id.et_busroute);
        et_dob = findViewById(R.id.et_dob);
        et_name = findViewById(R.id.et_name);
        et_license = findViewById(R.id.et_license);
        et_mobilenumber = findViewById(R.id.et_mobilenumber);

    }

    private void dobsetter() {
        et_dob.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(final View v) {

                //To show current date in the datepicker
                @SuppressLint({"NewApi", "LocalSuppress"}) final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(DriverRegister.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        mybirthday = new GregorianCalendar(selectedyear, selectedmonth, selectedday).getTime();

                        String birthday = "" + selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear + "";
                        // String    dob = birthday;
                        et_dob.setText(birthday);


                        if (validatedate(mybirthday)) {
                            Snackbar.make(v, birthday + " is not possible", Snackbar.LENGTH_LONG).show();
                            et_dob.setText("");
                            return;
                        }
                        if (validateage(mybirthday)) {
                            Snackbar.make(v, "You have to be above 15 years old", Snackbar.LENGTH_LONG)
                                    .setAction("Help", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //visitwebsite();
                                        }
                                    })
                                    .show();
                            et_dob.setText("");
                        }

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();


            }
        });
    }


    public static int getAge(Date date) {

        int age;
        //DateFormat dateFormat = null;
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime((date));
        if (dob.after(now)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }
        return age;
    }

    private boolean validateage(Date date) {

        return getAge(date) <= 15;
    }


    private boolean validatedate(Date date) {

        assert date != null;
        return !date.before(new Date());


    }

}
