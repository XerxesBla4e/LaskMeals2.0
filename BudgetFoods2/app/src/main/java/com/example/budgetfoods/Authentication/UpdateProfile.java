package com.example.budgetfoods.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetfoods.R;
import com.example.budgetfoods.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    ActivityUpdateProfileBinding activityUpdateProfileBinding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    String uid1;
    Button updateData;
    private Calendar bodcalendar = Calendar.getInstance();
    private EditText bodedittext;
    Button pickDOB, pickLocation, submit;
    public static final int PERMISSION_REQUEST_CODE = 444;
    String date, name, email, phoneNumber, university, location, gender;
    String district, city, state, country, address;

    TextView login;
    Spinner spinner;
    Double latitude, longitude;

    ProgressBar progressBar;

    LinearLayout linearLayout;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    LocationManager locationManager;
    LocationListener locationListener;
    private static int UPDATE_INTERVAL = 5000;
    String userId;
    FirebaseUser firebaseUser;
    private static int FASTEST_INTERVAL = 3000;

    private static final String TAG = "UpdateProfile";
    String token;

    private DatePickerDialog.OnDateSetListener bodDate = (view, year, month, dayOfMonth) -> {
        bodcalendar.set(Calendar.YEAR, year);
        bodcalendar.set(Calendar.MONTH, month);
        bodcalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        date = sdf.format(bodcalendar.getTime());
        //bodedittext.setText(new SimpleDateFormat("yyyy-MM-dd").format(bodcalendar.getTime()));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityUpdateProfileBinding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(activityUpdateProfileBinding.getRoot());

        initViews(activityUpdateProfileBinding);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid1 = user.getUid();
            retrieveUserDetails(uid1);
        }

        updateData.setOnClickListener(view -> {
            if (!validateFields()) {
                return;
            } else {
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (progressBar.getParent() != null) {
                    ((ViewGroup) progressBar.getParent()).removeView(progressBar);
                }

                linearLayout.addView(progressBar, layoutParams);
                updateData(uid1);
            }
        });
    }

    private void updateData(String userId) {
        name = activityUpdateProfileBinding.fnameEdit.getText().toString();
        university = activityUpdateProfileBinding.versityEdit.getText().toString();
        email = activityUpdateProfileBinding.mailEdit.getText().toString();
        phoneNumber = activityUpdateProfileBinding.phoneEdit.getText().toString();
        location = activityUpdateProfileBinding.locationEdit.getText().toString();
        date = activityUpdateProfileBinding.dobEdit.getText().toString();

        Map<String, Object> user = new HashMap<>();
        final String timestamp = String.valueOf(System.currentTimeMillis());

        user.put("uid", uid1);
        user.put("name", name);
        user.put("university", university);
        user.put("email", email);
        user.put("phonenumber", phoneNumber);
        user.put("location", location);
        user.put("city", city);
        user.put("state", state);
        user.put("country", country);
        user.put("district", district);
        user.put("gender", gender);
        user.put("DOB", date);
        user.put("timestamp", timestamp);
        user.put("latitude", longitude);
        user.put("longitude", latitude);
        user.put("accounttype", "Student");
        user.put("online", "true");
        user.put("token", token);

        firestore.collection("users")
                .document(userId)
                .update(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent x6 = new Intent(getApplicationContext(), UpdateProfile.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure scenario if necessary
                });
    }

    private void retrieveUserDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        name = documentSnapshot.getString("name");
                        university = documentSnapshot.getString("university");
                        email = documentSnapshot.getString("email");
                        phoneNumber = documentSnapshot.getString("phonenumber");
                        location = documentSnapshot.getString("location");
                        date = documentSnapshot.getString("DOB");

                        // Set the retrieved details to the appropriate EditText fields
                        activityUpdateProfileBinding.fnameEdit.setText(name);
                        activityUpdateProfileBinding.dobEdit.setText(date);
                        activityUpdateProfileBinding.versityEdit.setText(university);
                        activityUpdateProfileBinding.mailEdit.setText(email);
                        activityUpdateProfileBinding.phoneEdit.setText(phoneNumber);
                        activityUpdateProfileBinding.locationEdit.setText(location);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure scenario if necessary
                });
    }

    private void initViews(ActivityUpdateProfileBinding activityUpdateProfileBinding) {
        updateData = activityUpdateProfileBinding.signupButton;
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
        spinner = activityUpdateProfileBinding.spinnerGender;
        pickDOB = activityUpdateProfileBinding.btnDatePicker;
        bodedittext = activityUpdateProfileBinding.dobEdit;
        submit = activityUpdateProfileBinding.signupButton;
        linearLayout = activityUpdateProfileBinding.updateproflinlayout;
        // studentSignup = activityUpdateProfileBinding.studentsignup;
        login = activityUpdateProfileBinding.textViewLogin1;
        pickLocation = activityUpdateProfileBinding.btnPickLocation;

        pickDOB.setOnClickListener(view -> {
            new DatePickerDialog(UpdateProfile.this, bodDate, bodcalendar.get(Calendar.YEAR),
                    bodcalendar.get(Calendar.MONTH), bodcalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        pickLocation.setOnClickListener(view -> {
            displayLocation();
        });
    }

    private boolean validateFields() {
        String noSpecialChars = "\\A[A-Za-z]{4,20}\\z";
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        String phoneRegex = "^[+]?[0-9]{10,15}$";

        name = activityUpdateProfileBinding.fnameEdit.getText().toString();
        email = activityUpdateProfileBinding.mailEdit.getText().toString();
        phoneNumber = activityUpdateProfileBinding.phoneEdit.getText().toString();
        university = activityUpdateProfileBinding.versityEdit.getText().toString();
        location = activityUpdateProfileBinding.locationEdit.getText().toString();

        if (TextUtils.isEmpty(name)) {
            activityUpdateProfileBinding.fnameEdit.setError("Username field can't be empty");
            return false;
        } else if (!name.matches(noSpecialChars)) {
            activityUpdateProfileBinding.fnameEdit.setError("Only letters are allowed!");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            activityUpdateProfileBinding.mailEdit.setError("Email field can't be empty");
            return false;
        } else if (!email.matches(emailRegex)) {
            activityUpdateProfileBinding.mailEdit.setError("Invalid email format");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            activityUpdateProfileBinding.phoneEdit.setError("Phone number field can't be empty");
            return false;
        } else if (!phoneNumber.matches(phoneRegex)) {
            activityUpdateProfileBinding.phoneEdit.setError("Invalid phone number format");
            return false;
        }

        if (TextUtils.isEmpty(university)) {
            activityUpdateProfileBinding.versityEdit.setError("Please fill in university");
            return false;
        }

        if (TextUtils.isEmpty(location)) {
            activityUpdateProfileBinding.locationEdit.setError("Location field can't be empty");
            return false;
        }

        if (TextUtils.isEmpty(date)) {
            activityUpdateProfileBinding.dobEdit.setError("Please select a date");
            return false;
        }

        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(getApplicationContext(), "Select Appropriate Gender Please", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startLocationUpdates();
        } else {
            startLegacyLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();

                    getLocationAddress(longitude, latitude);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void startLegacyLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    getLocationAddress(longitude, latitude);
                }
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_CODE);
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Location Services Are Disabled\nPlease enable GPS or network provider", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLegacyLocationUpdates();
            } else {
                Toast.makeText(getApplicationContext(), "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getLocationAddress(Double longitude, Double latitude) {
        try {
            Geocoder geocoder = new Geocoder(UpdateProfile.this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            address = addressList.get(0).getAddressLine(0);

            activityUpdateProfileBinding.locationEdit.setText(address);
            city = addressList.get(0).getLocality();
            state = addressList.get(0).getAdminArea();
            country = addressList.get(0).getCountryName();
            district = addressList.get(0).getSubAdminArea();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        // fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayLocation();
    }
}
