package org.動作判斷式

import android.media.MediaPlayer
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.data.AudioCountdown
import org.data.Person

class LookUp_Stretch {

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
                keyPoint[10].coordinate,
                keyPoint[12].coordinate,
                keyPoint[6].coordinate
            )
            var angle4 = AngleCal().getAngle(
                keyPoint[9].coordinate,
                keyPoint[11].coordinate,
                keyPoint[5].coordinate
            )
            var angle5 = AngleCal().getAngle(
                keyPoint[6].coordinate,
                keyPoint[16].coordinate,
                keyPoint[12].coordinate
            )
            var angle6 = AngleCal().getAngle(
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


            //頭有沒有抬起來
            if(!(keyPoint[0].coordinate.y < keyPoint[3].coordinate.y || keyPoint[0].coordinate.y < keyPoint[4].coordinate.y)) {
                output_text += "頭抬起來\n"
                correction_point++
                if(!audio_playing_check && playing_this_round == 0 && audioCountDown[12].countDown == 0) {
                    mp_List[12].start()
                    playing_this_round = 1
                }
                answer += Pair(0, 1)
                answer += Pair(1, 3)
                answer += Pair(0, 2)
                answer += Pair(2, 4)
            }
            //弓箭步判斷
            if (angle1 > angle2) {
                //再做判斷
                //檢查前腳角度
                if (angle2 > 110) {
                    output_text += "前腳再蹲下去\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[10].countDown == 0) {
                        mp_List[10].start()
                        playing_this_round = 1
                    }
                    answer += Pair(11, 13)
                    answer += Pair(13, 15)
                }
                //檢查後腳角度
                if (angle1 < 150) {
                    output_text += "後腳伸直\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[9].countDown == 0) {
                        mp_List[9].start()
                        playing_this_round = 1
                    }
                    answer += Pair(12, 14)
                    answer += Pair(14, 16)
                }
            }
            else {
                //檢查前腳角度
                if (angle1 > 110) {
                    output_text += "前腳再蹲下去\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[10].countDown == 0) {
                        mp_List[10].start()
                        playing_this_round = 1
                    }
                    answer += Pair(12, 14)
                    answer += Pair(14, 16)
                }
                //檢查後腳角度
                if (angle2 < 150) {
                    output_text += "後腳伸直\n"
                    correction_point++
                    if(!audio_playing_check && playing_this_round == 0 && audioCountDown[9].countDown == 0) {
                        mp_List[9].start()
                        playing_this_round = 1
                    }
                    answer += Pair(11, 13)
                    answer += Pair(13, 15)
                }
            }
            //檢查手臂與身體
            if(angle3 < 160 || angle4 < 160) {
                output_text += "手與身體要呈現直線\n"
                correction_point++
                if(!audio_playing_check && playing_this_round == 0 && audioCountDown[11].countDown == 0) {
                    mp_List[11].start()
                    playing_this_round = 1
                }
                answer += Pair(6, 8)
                answer += Pair(8, 10)
                answer += Pair(6, 12)
                answer += Pair(5, 7)
                answer += Pair(7, 9)
                answer += Pair(5, 11)
            }
            //檢查身體與後腳
            /*if(angle5 > 160 || angle6 > 160) {
                output_text += "後腳與身體不要成一直線\n"
            }*/

            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                showing_text.setText(output_text)
                correction_Counter.setText((correction_Counter.text.toString().toInt() + correction_point).toString())
            }
        }
        else {
            output_text = "--"
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                showing_text.setText(output_text)
            }
        }
        return answer
    }
}