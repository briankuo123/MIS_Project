package org

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.unity3d.player.UnityPlayerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var roleView: ImageView
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
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val loadingDialog =  loading_dialog(activity)
        loadingDialog.startLoadingDialog()

        //取得登入者名稱
        val username: TextView = requireView().findViewById(R.id.textView2)
        username.text = GlobalVariable.getName()

        //設置經驗值
        val userGrade: TextView = requireView().findViewById(R.id.textView3)

        val postname = GlobalVariable.getName()
        val okHttpClient = OkHttpClient()
        val formbody = FormBody.Builder().add("username",postname).build()
        val request = Request.Builder().url("https://misprojectserver.azurewebsites.net/getExp").post(formbody).build()
        okHttpClient.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "error from login page" , Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val msg = response.body!!.string()
                if(msg =="LV.0"){
                    MainScope().launch {
                        withContext(Dispatchers.Default) {

                        }
                        userGrade.text = "LV.1"
                        //Toast.makeText(context, msg , Toast.LENGTH_LONG).show()

                        val gender = GlobalVariable.getGender()
                        roleView = requireView().findViewById(R.id.imageView17)
                        if (gender == 1){
                            val res = R.drawable.male_1
                            roleView.setImageResource(res)
                        }
                        else if(gender == 2){
                            val res = R.drawable.female_1
                            roleView.setImageResource(res)
                        }
                    }
                }
                else{
                    MainScope().launch {
                        withContext(Dispatchers.Default) {

                        }
                        //把取到的經驗值存進Global Variable Exp中 再放入TextView裡
                        GlobalVariable.setExp(msg.toInt())
                        val grade = (GlobalVariable.getExp()/2000)+1
                        userGrade.text = "LV."+grade.toString()

                        var expbar: ProgressBar = requireView().findViewById(R.id.progressBar)
                        expbar.setProgress( GlobalVariable.getExp()%2000 )

                        //取得性別與等級->根據條件呈現角色圖像
                        val gender = GlobalVariable.getGender()
                        roleView = requireView().findViewById(R.id.imageView17)
                        if (gender == 1){
                            when(grade){
                                in 1..7 -> {
                                    val res = R.drawable.male_1
                                    roleView.setImageResource(res)
                                }
                                in 8..15 -> {
                                    val res = R.drawable.male_2
                                    roleView.setImageResource(res)
                                }
                                in 16..99 ->{
                                    val res = R.drawable.male_3
                                    roleView.setImageResource(res)
                                }
                            }
                        }
                        else if(gender == 2){
                            when(grade){
                                in 1..7 -> {
                                    val res = R.drawable.female_1
                                    roleView.setImageResource(res)
                                }
                                in 8..15 -> {
                                    val res = R.drawable.female_2
                                    roleView.setImageResource(res)
                                }
                                in 16..99 ->{
                                    val res = R.drawable.female_3
                                    roleView.setImageResource(res)
                                }
                            }
                        }
                    }
                }
                loadingDialog.dismissDialog()
            }
        })


        val button: ImageButton = requireView().findViewById(R.id.imageButton1)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_homePageFragment_to_boardFragment)
        }
        val button2: Button = requireView().findViewById(R.id.button5)
        button2.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_homePageFragment_to_menuFragment)
        }
        val button3: ImageButton = requireView().findViewById(R.id.imageButton21)
        button3.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_homePageFragment_to_personalFragment)
        }
        val button4: Button = requireView().findViewById(R.id.button6)
        button4.setOnClickListener { view ->
            val intent = Intent(
                super@HomePageFragment.getActivity(),
                UnityPlayerActivity::class.java
            )
            startActivity(intent)
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
         * @return A new instance of fragment HomePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): HomePageFragment {
            val fragment = HomePageFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}