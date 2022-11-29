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

class Sit_Ups {
    private var output_text = ""

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

            var slope_left = Slope().getSlope(keyPoint[12].coordinate, keyPoint[6].coordinate)
            var slope_right = Slope().getSlope(keyPoint[11].coordinate, keyPoint[5].coordinate)

            var angle1 = AngleCal().getAngle(
                keyPoint[6].coordinate,
                keyPoint[14].coordinate,
                keyPoint[12].coordinate
            )

            var angle2 = AngleCal().getAngle(
                keyPoint[5].coordinate,
                keyPoint[13].coordinate,
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


            //偵測身體上來與否
            if(angle1 < 60 && angle2 < 60) {
                counter = 0
            }
            else {
                if(fpscounter.text.toString().toInt() > 150) {
                    output_text = "動作不夠確實，無法偵測"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[9].countDown == 0) {
                        mp_List[9].start()
                        playing_this_round = 1
                    }
                    answer += Pair(6, 12)
                    answer += Pair(12, 14)
                    answer += Pair(5, 11)
                    answer += Pair(11, 13)
                }
                else {
                    output_text = "--"
                }
                counter = 1
            }

            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                showing_text.setText(output_text)
                correction_Counter.setText((correction_Counter.text.toString().toInt() + correction_point).toString())
                if(counter == 0) {
                    fpscounter.setText("0")
                }
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