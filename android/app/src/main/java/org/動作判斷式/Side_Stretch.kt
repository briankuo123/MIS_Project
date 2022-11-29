package org.動作判斷式

import android.media.MediaPlayer
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.data.AudioCountdown
import org.data.Person

class Side_Stretch {

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

            var angle3 = AngleCal().getAngle(
                keyPoint[6].coordinate,
                keyPoint[10].coordinate,
                keyPoint[8].coordinate
            )

            var angle4 = AngleCal().getAngle(
                keyPoint[5].coordinate,
                keyPoint[9].coordinate,
                keyPoint[7].coordinate
            )

            var slope_left = Slope().getSlope(keyPoint[10].coordinate, keyPoint[6].coordinate)
            var slope_right = Slope().getSlope(keyPoint[9].coordinate, keyPoint[5].coordinate)

            if(countdowntimer.text.toString().toInt() == need_time/2 && !mp_List[9].isPlaying) {
                for(i in mp_List) {
                    if(i.isPlaying){
                        i.stop()
                    }
                }
                mp_List[9].start()
                playing_this_round = 1
            }


            if(waist_dis < 70) {
                output_text += "請正面面對鏡頭\n"
                if(!audio_playing_check && playing_this_round == 0 && audioCountDown[2].countDown == 0) {
                    mp_List[2].start()
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

            if(keyPoint[10].coordinate.y < keyPoint[0].coordinate.y && keyPoint[9].coordinate.y < keyPoint[0].coordinate.y) {
                if(angle1 < 160 || angle2 < 160) {
                    output_text += "腳請站直\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[11].countDown == 0) {
                        mp_List[11].start()
                        playing_this_round = 1
                    }
                    answer += Pair(12, 14)
                    answer += Pair(14, 16)
                    answer += Pair(11, 13)
                    answer += Pair(13, 15)
                }
                /*if(angle3 < 150 || angle4 < 150) {
                    output_text += "手請打直\n"
                }*/
                if(slope_left > 2 || slope_right > 2) {
                    output_text += "伸展角度可以再大一點\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[10].countDown == 0) {
                        mp_List[10].start()
                        playing_this_round = 1
                    }
                    answer += Pair(6, 8)
                    answer += Pair(8, 10)
                    answer += Pair(5, 7)
                    answer += Pair(7, 9)
                }
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