////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.tuner;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Locale;

// NumberPickerPreference
public class NumberPickerPreference extends DialogPreference
{
    private final int maxValue;
    private final int minValue;

    private float value;

    private NumberPicker units;
    private NumberPicker tenths;
    private LinearLayout layout;

    // Constructor
    public NumberPickerPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a =
            context.obtainStyledAttributes(attrs,
                                           R.styleable.NumberPickerPreference);
        maxValue =
            a.getInt(R.styleable.NumberPickerPreference_maxValue, 0);
        minValue =
            a.getInt(R.styleable.NumberPickerPreference_minValue, 0);
        a.recycle();
    }

    // On create dialog view
    @Override
    protected View onCreateDialogView()
    {
        units = new NumberPicker(getContext());
        tenths = new NumberPicker(getContext());
        layout = new LinearLayout(getContext());

        units.setMaxValue(maxValue);
        units.setMinValue(minValue);
        units.setValue(Float.valueOf(value).intValue());

        units.setWrapSelectorWheel(false);
        units.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        tenths.setMaxValue(9);
        tenths.setMinValue(0);
        tenths.setValue(Float.valueOf(value * 10).intValue() % 10);

        tenths.setWrapSelectorWheel(false);
        tenths.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        TextView dot = new TextView(getContext());
        //noinspection SetTextI18n
        dot.setText(".");

        TextView hz = new TextView(getContext());
        //noinspection SetTextI18n
        hz.setText("Hz");

        layout.setGravity(Gravity.CENTER);
        layout.addView(units);
        layout.addView(dot);
        layout.addView(tenths);
        layout.addView(hz);
        return layout;
    }

    // On get default value

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getInteger(index, (int) value);
    }

    // On set initial value
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue)
    {
        if (restorePersistedValue)
        {
            // Restore existing state
            try
            {
                value = getPersistedFloat(0);
            }

            catch (Exception e)
            {
                value = getPersistedInt(0);
            }
        }

        else
        {
            // Set default state from the XML attribute
            value = ((Integer) defaultValue).floatValue();
            persistFloat(value);
        }
    }

    // On dialog closed
    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        // When the user selects "OK", persist the new value
        if (positiveResult)
        {
            try
            {
                value = units.getValue() + tenths.getValue() / 10.0f;
                persistFloat(value);
            }

            catch (Exception e)
            {
                persistInt(units.getValue());
            }
        }
    }

    // Get value
    protected float getValue()
    {
        return value;
    }
}
