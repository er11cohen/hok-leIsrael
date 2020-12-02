package com.eran.hokleisrael;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;

public class NubmerPickerPref extends DialogPreference { 

    private int Minute = 0;
    private NumberPicker np= null;
    //private NubmerPickerPref nubmerPickerPref = null;
    public static int getMinute(String time) {
       // String[] pieces = time.split(":");
      //  int minute = Integer.parseInt(pieces[1]);
    	int minute = Integer.parseInt(time);
        return (minute);
    }

    public NubmerPickerPref(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("אישור");
        setNegativeButtonText("ביטול");
        //nubmerPickerPref = this;
       
    }

    @SuppressLint("NewApi")
	@Override
    protected View onCreateDialogView() {
        np = new NumberPicker(getContext());
        return (np);
    }

    @SuppressLint("NewApi")
	@Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        np.setMaxValue(180);
        np.setValue(Minute);
        /*np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                Toast.makeText(getContext(), "Value was: " + Integer.toString(oldValue) + " is now: " + Integer.toString(newValue), Toast.LENGTH_SHORT).show();              
            }
        });*/
    }

 
    
    @SuppressLint("NewApi")
	@Override
    protected void onDialogClosed(boolean positiveResult) {                                                             
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            Minute = np.getValue();

           // String time = 0 + ":" + String.valueOf(Minute);
            String time = String.valueOf(Minute);
            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time = null;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("0");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        Minute = getMinute(time);
    }

}