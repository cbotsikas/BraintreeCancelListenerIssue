package com.example.braintreecancellistenerissue;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;

import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;

public class PaypalClient implements
        PaymentMethodNonceCreatedListener,
        BraintreeCancelListener,
        BraintreeErrorListener {
    private BraintreeFragment mBraintreeFragment;
    private MainActivity mActivity;
    private PayPalClientListener mListener;

    public PaypalClient(MainActivity activity, PayPalClientListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    public void requestOneTimePayment(String amount, String clientToken) {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(mActivity, clientToken);
            mBraintreeFragment.addListener(this);
            PayPal.requestOneTimePayment(mBraintreeFragment, getPayPalRequest(amount));
        } catch (InvalidArgumentException e) {
            mListener.onPayPalResponse(PayPalClientListener.Result.FAILURE, e.getLocalizedMessage());
        }

    }

    public void close() {
        if (mBraintreeFragment != null) {
            mBraintreeFragment.removeListener(this);
            // If we don't remove the fragment from the manager, no future listeners are executed
            mBraintreeFragment
                    .getParentFragmentManager()
                    .beginTransaction()
                    .remove(mBraintreeFragment)
                    .commit();
        }
    }

    PayPalRequest getPayPalRequest(String amount) {
        PayPalRequest request = new PayPalRequest(amount);

        request.currencyCode("EUR");
        request.userAction(PayPalRequest.USER_ACTION_COMMIT);
        request.intent(PayPalRequest.INTENT_SALE);
        return request;
    }

    @Override
    public void onCancel(int i) {
        mListener.onPayPalResponse(PayPalClientListener.Result.CANCELLED, "" + i);
    }

    @Override
    public void onError(Exception e) {
        mListener.onPayPalResponse(PayPalClientListener.Result.FAILURE, e.getLocalizedMessage());
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        mListener.onPayPalResponse(PayPalClientListener.Result.SUCCESS);
    }
}
