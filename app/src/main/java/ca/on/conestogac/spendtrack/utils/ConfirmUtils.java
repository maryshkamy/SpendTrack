package ca.on.conestogac.spendtrack.utils;

import android.content.Context;
import android.content.Intent;

import androidx.wear.activity.ConfirmationActivity;

public class ConfirmUtils {
    public static void showSavedMessage(String message, Context context) {
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        context.startActivity(intent);
    }
}
