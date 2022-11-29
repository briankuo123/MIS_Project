package org

//import androidx.navigation.NavController.navigate

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation.findNavController
import com.applandeo.materialcalendarview.CalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [PersonalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var record: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //取得登入者名稱
        val username: TextView = requireView().findViewById(R.id.textView35)
        username.text = GlobalVariable.getName()
        //取得使用者等級
        val userGrade: TextView = requireView().findViewById(R.id.textView37)
        userGrade.text = "  LV."+((GlobalVariable.getExp()/2000)+1).toString()

        record = requireView().findViewById(R.id.textView3)

        val loadingDialog =  loading_dialog(activity)
        loadingDialog.startLoadingDialog()

        val userid = GlobalVariable.getId()
        val okHttpClient = OkHttpClient()
        val formbody = FormBody.Builder()
            .add("userid",userid.toString())
            .build()
        val request = Request.Builder().url("https://misprojectserver.azurewebsites.net/printrecord").post(formbody).build()
        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "error from login page" , Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val msg = response.body!!.string()
                val recordlist1: TextView = requireView().findViewById(R.id.textView3)
                val recordlist2: TextView = requireView().findViewById(R.id.textView4)
                if( msg == "DB do not have data"){
                    recordlist1.text = "目前尚無紀錄"
                    recordlist2.text = " "
                }
                else{
                    val jsonarrayData = JSONArray(msg)
                    //顯示紀錄時間與菜單名稱
                    val data1 = StringBuilder()
                    for (i in 0 until jsonarrayData.length()) {
                        val recorddata = jsonarrayData.getJSONObject(i)
                        data1.append("${recorddata.get("finish_time")} ${recorddata.get("menuname")}\n")
                    }
                    recordlist1.text = data1

                    //顯示消耗熱量與得分
                    val data2 = StringBuilder()
                    for (i in 0 until jsonarrayData.length()) {
                        val recorddata = jsonarrayData.getJSONObject(i)
                        data2.append(" ${recorddata.get("calories")}  ${recorddata.get("score")}\n")
                    }
                    recordlist2.text = data2
                }
            }
        })

        val request2 = Request.Builder().url("https://misprojectserver.azurewebsites.net/getdate").post(formbody).build()
        okHttpClient.newCall(request2).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "error from login page" , Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                //val timerecord: TextView = requireView().findViewById(R.id.textView5)
                val calendarView : CalendarView = requireView().findViewById(R.id.calendarView)
                //var events : ArrayList<EventDay> = ArrayList()
                var selectedDatelist:ArrayList<Calendar> = ArrayList()
                val msg = response.body!!.string()
                if(msg == "get_date fail"){
                    //timerecord.text = "尚無紀錄"
                }
                else{
                    val data = StringBuilder()
                    val jsonarrayData = JSONArray(msg)
                    for (i in 0 until jsonarrayData.length()) {
                        val datelist = jsonarrayData.getJSONObject(i)
                        data.append("${datelist.get("record_time")} \n")

                        val calendar = Calendar.getInstance()
                        val items1 = datelist.get("record_time").toString().split("-")
                        val year = items1[0].toInt()
                        val month = items1[1].toInt()-1
                        val day = items1[2].toInt()
                        calendar.set(year, month, day)
                        selectedDatelist.add(calendar)
                        //events.add( EventDay(calendar,R.drawable.ic_fitness_24dp) )
                    }
                    //timerecord.text = data

                    //calendarView.setEvents(events)
                    //calendarView.setHighlightedDays(selectedDatelist)
                    MainScope().launch {
                        withContext(Dispatchers.Default) {

                        }
                        /*for(i in selectedDatelist) {
                            calendarView.setDate(i)
                        }*/
                        calendarView.setSelectedDates(selectedDatelist)
                    }



                }
                loadingDialog.dismissDialog()
            }
        })


        val button: ImageButton
        val button2: Button
        button = requireView().findViewById(R.id.imageButton20)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_personalFragment_to_homePageFragment)
        }
        button2 = requireView().findViewById(R.id.button28)
        button2.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_personalFragment_to_tour1Fragment)
        }
    }

        companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PersonalFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): PersonalFragment {
            val fragment = PersonalFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
