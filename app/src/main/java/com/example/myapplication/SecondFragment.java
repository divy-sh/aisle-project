package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.databinding.FragmentSecondBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    CountDownTimer cd;
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setPhoneNumberAtTheTop();
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = binding.otpBox.getText().toString();
                Pattern pattern = Pattern.compile("[0-9]{4}");
                Matcher matcher = pattern.matcher(otp);
                if(!matcher.matches()){
                    binding.otpBox.setError("Invalid OTP");
                }
                else {
                    binding.buttonSecond.setEnabled(false);
                    try {
                        login();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        cd = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished / 1000>=10)
                    binding.countdown.setText("00:" + millisUntilFinished / 1000);
                else
                    binding.countdown.setText("00:0" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                Toast.makeText(getContext(), R.string.otpTimeOut, Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        };
        cd.start();
    }

    private void login() throws JSONException {
        String otp = binding.otpBox.getText().toString();
        Pattern pattern = Pattern.compile("[0-9]{4}");
        Matcher matcher = pattern.matcher(otp);
        String parsedPhone = "+"
                            + SharedPreferencesDefault.getDefaults(getResources().getString(R.string.countryCode), getContext())
                            + SharedPreferencesDefault.getDefaults(getResources().getString(R.string.PHONE), getContext());
        JSONObject input = new JSONObject();
        input.put("number", parsedPhone);
        input.put("otp",binding.otpBox.getText().toString());
        String url = "https://testa2.aisle.co/V1/users/verify_otp";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            actOnVerifyOTPResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.incorrect_otp, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        binding.buttonSecond.setEnabled(true);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Cookie", "__cfduid=df9b865983bd04a5de2cf5017994bbbc71618565720");
                return params;
            }
        };
        requestQueue.add(objectRequest);
    }

    private void actOnVerifyOTPResponse(JSONObject response) throws JSONException {
        String token = response.get("token").toString();
        if(token!="null"){
            SharedPreferencesDefault.setDefaults(getResources().getString(R.string.token), response.get("token").toString(), getContext());
            openHomeActivity();
        }
        else{
            binding.buttonSecond.setEnabled(true);
            Toast.makeText(getContext(), R.string.incorrect_otp, Toast.LENGTH_LONG).show();
        }
    }

    private void openHomeActivity() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
        getActivity().finish();
    }

    private void setPhoneNumberAtTheTop() {
        String phone = SharedPreferencesDefault.getDefaults(getResources().getString(R.string.PHONE), getContext());
        String countryCode = SharedPreferencesDefault.getDefaults(getResources().getString(R.string.countryCode), getContext());
        binding.getOTP.setText("+"+countryCode+" "+phone);
    }

    @Override
    public void onDestroyView() {
        cd.cancel();
        super.onDestroyView();
        binding = null;
    }
}