package org.動作判斷式

import android.graphics.PointF
import kotlin.math.sqrt

class Point_Dis {
    fun get_dis(point1: PointF, point2: PointF): Float {
        return sqrt((point2.x-point1.x)*(point2.x-point1.x)+(point2.y-point1.y)*(point2.y-point1.y))
    }
}