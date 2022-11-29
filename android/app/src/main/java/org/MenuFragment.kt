package org

import androidx.navigation.Navigation.findNavController
//import androidx.navigation.NavController.navigate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
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
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button1: ImageButton
        val button2: ImageButton
        val button3: Button
        val button4: Button
        val button5: ImageButton
        val button6: Button
        val button7: Button
        val button8: Button
        val button9: Button
        val button10: Button
        val userid = GlobalVariable.getId()

        val loadingDialog =  loading_dialog(activity)
        loadingDialog.startLoadingDialog()

        //取得最常做菜單
        val okHttpClient = OkHttpClient()
        val formbody = FormBody.Builder()
            .add("userid",userid.toString())
            .build()
        val request1 = Request.Builder().url("https://misprojectserver.azurewebsites.net/printmost").post(formbody).build()
        okHttpClient.newCall(request1).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "error from Home page" , Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val msg = response.body!!.string()
                val menulist = msg.split(" ")
                val menuName1: TextView = requireView().findViewById(R.id.button1)
                menuName1.text = "No1.   "+ menulist[0]
                val menuName2: TextView = requireView().findViewById(R.id.button3)
                menuName2.text = "No2.   "+ menulist[1]
                val menuName3: TextView = requireView().findViewById(R.id.button4)
                menuName3.text = "No3.   "+ menulist[2]
                loadingDialog.dismissDialog()
            }
        })
        //取得熱門菜單
        val request2 = Request.Builder().url("https://misprojectserver.azurewebsites.net/printhot").build()
        okHttpClient.newCall(request2).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "error from Home page" , Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val msg = response.body!!.string()
                val menulist = msg.split(" ")
                val menuName1: TextView = requireView().findViewById(R.id.button7)
                menuName1.text = "No1.   "+ menulist[0]
                val menuName2: TextView = requireView().findViewById(R.id.button8)
                menuName2.text = "No2.   "+ menulist[1]
                val menuName3: TextView = requireView().findViewById(R.id.button9)
                menuName3.text = "No3.   "+ menulist[2]
            }
        })

        //回上一頁
        button1 = requireView().findViewById(R.id.imageButton3)
        button1.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuFragment_to_homePageFragment)
        }

        //回首頁
        button2 = requireView().findViewById(R.id.imageButton5)
        button2.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuFragment_to_homePageFragment)
        }

        //導引到問券推薦頁面
        button4 = requireView().findViewById(R.id.button11)
        button4.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuFragment_to_adviceFragment)
        }

        //導引到菜單類別選擇
        button5 = requireView().findViewById(R.id.imageButton28)
        button5.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuFragment_to_menu2Fragment)
        }

        //點擊最常做No1.導引到相關菜單
        button3 = requireView().findViewById(R.id.button1)
        button3.setOnClickListener { view ->
            val menuNameNo1: TextView = requireView().findViewById(R.id.button1)
            when( menuNameNo1.text.toString() ){
                "No1.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChooseFragment)
                }
                "No1.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose2Fragment)
                }
                "No1.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose3Fragment)
                }
            }
        }

        //點擊最常做No2.導引到相關菜單
        button6 = requireView().findViewById(R.id.button3)
        button6.setOnClickListener { view ->
            val menuNameNo2: TextView = requireView().findViewById(R.id.button3)
            when( menuNameNo2.text.toString() ){
                "No2.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChooseFragment)
                }
                "No2.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose2Fragment)
                }
                "No2.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose3Fragment)
                }
            }
        }

        //點擊最常做No3.導引到相關菜單
        button7 = requireView().findViewById(R.id.button4)
        button7.setOnClickListener { view ->
            val menuNameNo3: TextView = requireView().findViewById(R.id.button4)
            when( menuNameNo3.text.toString() ){
                "No3.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChooseFragment)
                }
                "No3.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose2Fragment)
                }
                "No3.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose3Fragment)
                }
            }
        }

        //點擊熱門No1.導引到相關菜單
        button8 = requireView().findViewById(R.id.button7)
        button8.setOnClickListener { view ->
            val menuNameNo1: TextView = requireView().findViewById(R.id.button7)
            when( menuNameNo1.text.toString() ){
                "No1.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChooseFragment)
                }
                "No1.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose2Fragment)
                }
                "No1.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose3Fragment)
                }
            }
        }

        //點擊熱門做No2.導引到相關菜單
        button9 = requireView().findViewById(R.id.button8)
        button9.setOnClickListener { view ->
            val menuNameNo2: TextView = requireView().findViewById(R.id.button8)
            when( menuNameNo2.text.toString() ){
                "No2.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChooseFragment)
                }
                "No2.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose2Fragment)
                }
                "No2.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose3Fragment)
                }
            }
        }

        //點擊熱門No3.導引到相關菜單
        button10 = requireView().findViewById(R.id.button9)
        button10.setOnClickListener { view ->
            val menuNameNo3: TextView = requireView().findViewById(R.id.button9)
            when( menuNameNo3.text.toString() ){
                "No3.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChooseFragment)
                }
                "No3.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose2Fragment)
                }
                "No3.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menuFragment_to_menuChoose3Fragment)
                }
            }
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
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): MenuFragment {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}