package org.動作判斷式

import android.media.MediaPlayer
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.data.AudioCountdown
import org.data.Person
import kotlin.math.abs

class Leg_Raise {

    private var output_text = ""
    //靜態動作
    fun check(showing_text: TextView,
              persons: List<Person>,
              fpscounter: TextView,
              countdowntimer: TextView,
              correction_Counter: TextView,
              need_time: Int,
              mp_List: List<MediaPlayer>,
              audio_playing_check: Boolean,
              audioCountDown: List<AudioCountdown>): List<Pair<Int, Int>> {

        var answer: List<Pair<Int, Int>> = listOf(Pair(0, 0))

        if (!(persons.isNullOrEmpty())) {

            var counter = 1
            var playing_this_round = 0
            var correction_point = 0

            var target: Person = persons[0]
            var keyPoint = target.keyPoints

            var waist_dis = Point_Dis().get_dis(keyPoint[12].coordinate, keyPoint[11].coordinate)
            var body_dis_left = Point_Dis().get_dis(keyPoint[6].coordinate, keyPoint[12].coordinate)
            var body_dis_right = Point_Dis().get_dis(keyPoint[5].coordinate, keyPoint[11].coordinate)

            var angle1 = AngleCal().getAngle(
                keyPoint[6].coordinate,
                keyPoint[16].coordinate,
                keyPoint[12].coordinate
            )

            var angle2 = AngleCal().getAngle(
                keyPoint[5].coordinate,
                keyPoint[15].coordinate,
                keyPoint[11].coordinate
            )

            if(waist_dis > 70) {
                output_text += "請側著面對鏡頭\n"
                if(!audio_playing_check && playing_this_round == 0 && audioCountDown[6].countDown == 0) {
                    mp_List[6].start()
                    playing_this_round = 1
                }
            }
            if(body_dis_left < 100 && body_dis_right < 100) {
                output_text += "離太遠\n"
                if(!audio_playing_check && playing_this_round == 0 && audioCountDown[8].countDown == 0) {
                    mp_List[8].start()
                    playing_this_round = 1
                }
            }
            if(body_dis_left > 170 && body_dis_right < 170) {
                output_text += "離太近\n"
                if(!audio_playing_check && playing_this_round == 0 && audioCountDown[7].countDown == 0) {
                    mp_List[7].start()
                    playing_this_round = 1
                }
            }

            if(angle1 < angle2 && angle1 < 160) {
                if(angle1 > 110) {
                    output_text += "腿再抬高\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[9].countDown == 0) {
                        mp_List[9].start()
                        playing_this_round = 1
                    }
                    answer += Pair(6, 12)
                    answer += Pair(12, 14)
                    answer += Pair(14, 16)
                }
            }
            else if(angle1 > angle2 && angle2 < 160) {
                if(angle2 > 110) {
                    output_text += "腿再抬高\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[9].countDown == 0) {
                        mp_List[9].start()
                        playing_this_round = 1
                    }
                    answer += Pair(5, 11)
                    answer += Pair(11, 13)
                    answer += Pair(13, 15)
                }
            }
            else {
                output_text = "--"
            }

            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                showing_text.setText(output_text)
                correction_Counter.setText((correction_Counter.text.toString().toInt() + correction_point).toString())
            }
        }
        else {
            output_text = "無偵測到人"
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                showing_text.setText(output_text)
            }
        }
        return answer
    }
}