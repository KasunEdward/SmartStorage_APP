package com.smartstorage.mobile.machine_learning;

/**
 * Created by anuradha on 8/20/17.
 */

public interface Regressor {

    String name();

    float recognize(final float[] pixels);
}
