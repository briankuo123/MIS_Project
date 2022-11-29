package org

import kotlin.math.E

class GlobalVariable : android.app.Application() {
    companion object {
        //當前使用者名稱
        private var username: String = ""
        fun setName(username: String){
            this.username = username
        }
        fun getName(): String{
            return username
        }

        //當前使用者ID
        private var userid: Int = 0
        fun setId(userid: Int){
            this.userid = userid
        }
        fun getId(): Int{
            return userid
        }

        //當前使用者性別
        private var usergender: Int = 0
        fun setGender(usergender: Int){
            this.usergender = usergender
        }
        fun getGender(): Int{
            return usergender
        }

        //得分
        private var temp_score: Int = 0
        fun setScore(temp_score: Int){
            this.temp_score = temp_score
        }
        fun getScore(): Int{
            return temp_score
        }

        //經驗值
        private var Exp: Int = 0
        fun setExp(Exp: Int){
            this.Exp = Exp
        }
        fun getExp(): Int{
            return Exp
        }

        //今日運動次數
        private var ExeTimes: Int =0
        fun setTimes(ExeTimes:Int){
            this.ExeTimes = ExeTimes
        }
        fun getTimes(): Int{
            return ExeTimes
        }
    }
}