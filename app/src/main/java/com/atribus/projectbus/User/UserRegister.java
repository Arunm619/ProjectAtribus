package com.atribus.projectbus.User;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.projectbus.Model.BusRoutes;
import com.atribus.projectbus.Model.User;
import com.atribus.projectbus.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class UserRegister extends AppCompatActivity {


    String name, phonenumber, areaname, pickuppoint, dateofbirth, busroutenumber, gender;
    MaterialEditText et_name, et_phone, et_dob, et_gender;
    MaterialAutoCompleteTextView et_busroute, et_area;
    MaterialAutoCompleteTextView et_pickuppoint;
    Button btn_register;
    View v;
    Date mybirthday;
    public BusRoutes busdb;


    FirebaseDatabase database;
    DatabaseReference userNode;
    FirebaseUser currentUser;

    //shared pref
    private static final String MY_PREFS_NAME = "Mydatabase";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Register");


        busdb = new BusRoutes();

        database = FirebaseDatabase.getInstance();
        userNode = database.getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        setupViews();

        settingbusroutes();
        dobsetter();
        gendersetter();


        //if user already exists fetch data and display
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User u = dataSnapshot.getValue(User.class);
                            if (u != null) {
                                et_name.setText(u.getName());
                                et_phone.setText(String.valueOf(u.getPhonenumber()));
                                //et_dob.setText(u.getDateofbirth());
                                et_gender.setText(u.getGender());

                                et_busroute.setText(u.getBusroute());
                                et_area.setText(u.getAreaname());
                                et_pickuppoint.setText(u.getPickuppoint());

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int a = nullcheck();
                if (a != -1)
                    doregister();

            }
        });
    }

    private void settingbusroutes() {
        //bus route - working

        /* *************************************/
        String[] busroutenumbers =
                busdb.Areasroutes.keySet().toArray(new String[busdb.Areasroutes.size()]);
        ArrayAdapter <String> adapter =
                new ArrayAdapter <>(this, android.R.layout.simple_list_item_1, busroutenumbers);
        et_busroute.setThreshold(1);
        et_busroute.setAdapter(adapter);
/* *************************************/


/* *************************************/

        final List <String> areas = new ArrayList <>();
        for (String s : busdb.Areasroutes.values()) {
            if (s != null)
                areas.add(s);

        }
        et_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(UserRegister.this)
                        .title(R.string.chooseArea)
                        .items(areas)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_area.setText(text);
                            }
                        })
                        .show();
            }
        });
/* *************************************/


        //pick up points

        final ArrayList <String> pickuppoints = new ArrayList <>();

        for (String key : busdb.busroute.keySet()) {
            Vector <Pair <String, String>> pairsV = busdb.busroute.get(key);

            for (int i = 0; i < pairsV.size(); i++) {

                if (pairsV.get(i).first != null)
                    pickuppoints.add(pairsV.get(i).first);

            }
        }

        et_pickuppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(UserRegister.this)
                        .title(R.string.choosepickup)
                        .items(pickuppoints)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_pickuppoint.setText(text);
                            }
                        })
                        .show();

            }
        });
/* *************************************/


    }

    private int nullcheck() {
        name = et_name.getText().toString().trim();
        phonenumber = et_phone.getText().toString().trim();
        areaname = et_area.getText().toString().trim();
        pickuppoint = et_pickuppoint.getText().toString().trim();
        dateofbirth = et_dob.getText().toString().trim();
        busroutenumber = et_busroute.getText().toString().trim();
        gender = et_gender.getText().toString().trim();

        //name check
        if (TextUtils.isEmpty(name)) {
            Snackbar.make(v, "Enter Name", Snackbar.LENGTH_SHORT).show();
            return -1;
        }

        //phonenumber
        if (TextUtils.isEmpty(phonenumber)) {
            Snackbar.make(v, "Enter phone number", Snackbar.LENGTH_SHORT).show();
            return -1;
        }

        //phonenumber
        if (phonenumber.length() != 10) {
            Snackbar.make(v, "Check Phone number", Snackbar.LENGTH_SHORT).show();
            return -1;
        }

        //dob
        if (TextUtils.isEmpty(dateofbirth)) {
            Snackbar.make(v, "Choose Date Of Birth", Snackbar.LENGTH_SHORT).show();
            return -1;
        }

        //Gender
        if (TextUtils.isEmpty(gender)) {
            Snackbar.make(v, "Choose Gender", Snackbar.LENGTH_SHORT).show();
            return -1;
        }


        //bus route
        if (TextUtils.isEmpty(busroutenumber)) {
            Snackbar.make(v, "Enter Bus route number", Snackbar.LENGTH_SHORT).show();
            return -1;
        }

        //Area name
        if (TextUtils.isEmpty(areaname)) {
            Snackbar.make(v, "Enter Area name", Snackbar.LENGTH_SHORT).show();
            return -1;
        }

        //Pick Up point
        if (TextUtils.isEmpty(pickuppoint)) {
            Snackbar.make(v, "Enter Pick Up Point", Snackbar.LENGTH_SHORT).show();
            return -1;
        }


        return 0;
    }

    private void doregister() {
//null check done
        User user = new User();
        user.setName(name);
        user.setPhonenumber(phonenumber);
        user.setDateOfBirth(parseDatetostring(mybirthday));
        user.setGender(gender);
        user.setBusroute(busroutenumber);
        user.setAreaname(areaname);
        user.setPickuppoint(pickuppoint);

        //generating USER ID
        String UUID = currentUser.getUid();

        user.setUuid(UUID);


        //pushing child User to UserNode
        userNode.child(UUID).setValue(user);


        //storing offline copy of UserObject in sharedpred under MYDB
        Gson gson = new Gson();
        String userobjectasstring = gson.toJson(user);
        editor.putString("UserObj", userobjectasstring);
        editor.apply();
        Toast.makeText(this, "Successfully Stored", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(UserRegister.this, UserHomeActivity.class));
        finish();
    }

    private String parseDatetostring(Date date) {
        SimpleDateFormat simpleDate;
        simpleDate = new SimpleDateFormat("dd/MM/yyyy");

        return simpleDate.format(date);
    }

    private void setupViews() {

        //snackbar view
        v = findViewById(R.id.ll);
        //edittexts views
        et_area = findViewById(R.id.et_address);
        et_busroute = findViewById(R.id.et_busrote);
        et_dob = findViewById(R.id.et_dob);
        et_gender = findViewById(R.id.et_gender);
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_mobilenumber);
        et_pickuppoint = findViewById(R.id.et_pickuppoint);

        //button
        btn_register = findViewById(R.id.btn_register);

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

                DatePickerDialog mDatePicker = new DatePickerDialog(UserRegister.this, new DatePickerDialog.OnDateSetListener() {

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

    private void gendersetter() {
        et_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //material dialog to show all the blood groups

                new MaterialDialog.Builder(UserRegister.this)
                        .title(R.string.choosegender)
                        .items(R.array.gender)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_gender.setText(text);
                            }
                        })
                        .show();


            }
        });

    }
}
