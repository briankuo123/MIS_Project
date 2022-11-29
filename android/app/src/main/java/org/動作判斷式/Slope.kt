package org.動作判斷式

import android.graphics.PointF
import kotlin.math.abs

class Slope {
    fun getSlope(point1: PointF, point2: PointF): Float {
        return abs((point2.y-point1.y)/(point2.x-point1.x))
    }
}