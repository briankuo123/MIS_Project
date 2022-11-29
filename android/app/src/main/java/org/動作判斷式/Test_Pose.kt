package org.動作判斷式

import android.media.AudioManager
import android.media.MediaPlayer
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.data.Person
import kotlin.math.abs

class Test_Pose {

    private var output_text = ""

    fun check(textView: TextView, persons: List<Person>, textView3: TextView, mp_List: List<MediaPlayer>, textView5: TextView) {

        if(!(persons.isNullOrEmpty())) {
            var target: Person = persons[0]
            var keyPoint = target.keyPoints

            var waist_dis = abs(keyPoint[12].coordinate.x - keyPoint[11].coordinate.x)

            var body_dis_left = Point_Dis().get_dis(keyPoint[6].coordinate, keyPoint[12].coordinate)
            var body_dis_right = Point_Dis().get_dis(keyPoint[5].coordinate, keyPoint[11].coordinate)

            var angle1 = AngleCal().getAngle(
                keyPoint[12].coordinate,
                keyPoint[16].coordinate,
                keyPoint[14].coordinate
            )
            var angle2 = AngleCal().getAngle(
                keyPoint[11].coordinate,
                keyPoint[15].coordinate,
                keyPoint[13].coordinate
            )

            if(waist_dis > 70) {
                output_text += "請側著面對鏡頭\n"
                if(!mp_List[7].isPlaying) {
                    mp_List[7].start()
                }
            }
            if(body_dis_left < 100 && body_dis_right < 100) {
                output_text += "離太遠\n"
            }
            if(body_dis_left > 170 && body_dis_right < 170) {
                output_text += "離太近\n"
            }

            //先分左右
            if (angle1 > angle2) {
                //再做判斷
                //檢查前腳角度
                if (angle2 > 110) {
                    output_text += "前腳再蹲下去\n"
                }
                //檢查後腳角度
                if (angle1 < 150) {
                    output_text += "後腳伸直\n"
                }
            }
            else {
                //檢查前腳角度
                if (angle1 > 110) {
                    output_text += "前腳再蹲下去\n"
                }
                //檢查後腳角度
                if (angle2 < 150) {
                    output_text += "後腳伸直\n"
                }
            }

            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                textView.setText(output_text)
            }
        }
        else {
            output_text = "--"
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                textView.setText(output_text)
            }
        }

    }
}