package com.example.myapplication;

import android.os.Bundle;
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
import com.example.myapplication.databinding.FragmentFirstBinding;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.continueToEnterOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = binding.countryCode.getText().toString();
                String phoneNumber = binding.phone.getText().toString();
                Pattern countryCodePattern = Pattern.compile("[0-9]{1,3}");
                Matcher countryMatcher = countryCodePattern.matcher(countryCode);
                Pattern phoneNumberPattern = Pattern.compile("[0-9]{10}");
                Matcher phoneNumberMatcher = phoneNumberPattern.matcher(phoneNumber);
                if(!countryMatcher.matches()) {
                    binding.countryCode.setError("Invalid Country Code");
                }
                else if(!phoneNumberMatcher.matches()){
                    binding.phone.setError("Invalid Phone Number");
                }
                else {
                    try {
                        binding.continueToEnterOtpButton.setEnabled(false);
                        RequestOTP();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void RequestOTP() throws JSONException {
        String countryCode = binding.countryCode.getText().toString();
        String phoneNumber = binding.phone.getText().toString();
        String parsedPhone = "+"+countryCode+phoneNumber;
        JSONObject input = new JSONObject();
        input.put("number", parsedPhone);
        String url = "https://testa2.aisle.co/V1/users/phone_number_login";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            actOnRequestOTPResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.loginfailed, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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

    private void actOnRequestOTPResponse(JSONObject response) throws JSONException {
        if((boolean) response.get("status")){
            SharedPreferencesDefault.setDefaults(getResources().getString(R.string.countryCode), binding.countryCode.getText().toString(), getContext());
            SharedPreferencesDefault.setDefaults(getResources().getString(R.string.PHONE), binding.phone.getText().toString(), getContext());
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        }
        else{
            binding.continueToEnterOtpButton.setEnabled(true);
            Toast.makeText(getContext(), R.string.loginfailed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}