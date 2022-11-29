package org.動作判斷式

import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class AngleCal() {
    fun getAngle(point1: PointF, point2: PointF, point_mid: PointF): Float {
        var angle1 = atan2(((point1.y)-(point_mid.y)),((point1.x)-(point_mid.x)))
        var angle2 = atan2(((point2.y)-(point_mid.y)),((point2.x)-(point_mid.x)))
        var output: Float?


        if(angle1 * angle2 >= 0) {
            output = abs(angle1-angle2) *180/ PI.toFloat()
        }
        else {
            output = (abs(angle1) + abs(angle2))*180/ PI.toFloat()
            if(output > 180) {
                output = 360 - output
            }
        }
        return output
    }
}