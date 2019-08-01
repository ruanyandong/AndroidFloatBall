package com.example.myapplication.floatball.widget.textcounter.formatters;


import com.qukandian.video.qkdbase.floatball.widget.textcounter.Formatter;

/**
 * Created by prem on 10/28/14.
 *
 * Performs no formatting
 */
public class NoFormatter implements Formatter {

    @Override
    public String format(String prefix, String suffix, float value) {
        return prefix + value + suffix;
    }
}
