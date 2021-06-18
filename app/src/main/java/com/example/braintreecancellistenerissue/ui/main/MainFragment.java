package com.example.braintreecancellistenerissue.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.braintreecancellistenerissue.MainActivity;
import com.example.braintreecancellistenerissue.PayPalClientListener;
import com.example.braintreecancellistenerissue.PaypalClient;
import com.example.braintreecancellistenerissue.R;

public class MainFragment extends Fragment implements PayPalClientListener {

    private MainViewModel mViewModel;
    private MainActivity mainActivity;
    private PaypalClient mPaypalClient;
    private TextView status;
    private EditText status2;
    private Integer statusCounter = 0;
    private Integer tokenCounter = 0;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainActivity = (MainActivity) getActivity();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        status = (TextView) getView().findViewById(R.id.status);
        status2 = (EditText) getView().findViewById(R.id.editTextTextMultiLine);
        Button button = getView().findViewById(R.id.button);
        button.setOnClickListener(v -> onButtonClick(v));

    }

    public void onButtonClick(View view) {
        String amount = "1.00";
        String[] clientTokens = {
        };
        if (clientTokens.length == 0) {
            setStatus("You need to add clientTokens in MainFragment!");
            return;
        }
        int index = tokenCounter++ % clientTokens.length;
        String clientToken = clientTokens[index];
        setStatus("Waiting for paypal, token#" + index);
        mPaypalClient = new PaypalClient(mainActivity, this);
        mPaypalClient.requestOneTimePayment(amount, clientToken);
    }

    public void setStatus(String msg) {
        String text = String.format("%s: %s", statusCounter++, msg);
        status.setText(text);
        status2.append(text + "\n");
    }

    @Override
    public void onPayPalResponse(Result result) {
        onPayPalResponse(result, "");
    }

    @Override
    public void onPayPalResponse(Result result, String txt) {
        mPaypalClient.close();
        setStatus(String.format("%s %s", result.toString(), txt));
    }
}