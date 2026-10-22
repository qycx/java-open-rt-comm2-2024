package com.qycx;


import android.util.Size;

import java.util.Comparator;

public class CompareSizesByArea implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        var lhsSize = (Size) lhs;
        var rhsSize = (Size) rhs;
        return Long.signum( lhsSize.getWidth() * lhsSize.getHeight() -  rhsSize.getWidth() * rhsSize.getHeight());
    }
}
