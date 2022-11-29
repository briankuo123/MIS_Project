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
 * Use the [Menu2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Menu2Fragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_menu2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button1: ImageButton
        val button2: ImageButton
        val button3: ImageButton
        val button4: Button
        val button5: Button
        val button6: Button
        val button7: Button
        val button8: Button
        val button9: Button
        val button10: Button
        val button11: Button
        val button12: Button
        val button13: Button

        val loadingDialog =  loading_dialog(activity)
        loadingDialog.startLoadingDialog()

        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url("https://misprojectserver.azurewebsites.net/printhot").build()
        okHttpClient.newCall(request).enqueue(object: Callback {
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
                loadingDialog.dismissDialog()
            }
        })

        button1 = requireView().findViewById(R.id.imageButton3)
        button1.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_homePageFragment)
        }
        button2 = requireView().findViewById(R.id.imageButton5)
        button2.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_homePageFragment)
        }
        button3 = requireView().findViewById(R.id.imageButton28)
        button3.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_menuFragment)
        }
        button4 = requireView().findViewById(R.id.button11)
        button4.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_adviceFragment)
        }

        button5 = requireView().findViewById(R.id.button10)
        button5.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_menu4Fragment)
        }

        button6 = requireView().findViewById(R.id.button12)
        button6.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_menu5Fragment)
        }
        button7 = requireView().findViewById(R.id.button13)
        button7.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_menu6Fragment)
        }
        button8 = requireView().findViewById(R.id.button14)
        button8.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_menu7Fragment)
        }
        button9 = requireView().findViewById(R.id.button15)
        button9.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_menu8Fragment)
        }
        button10 = requireView().findViewById(R.id.button16)
        button10.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu2Fragment_to_menu3Fragment)
        }

        //點擊熱門No1.導引到相關菜單
        button11 = requireView().findViewById(R.id.button7)
        button11.setOnClickListener { view ->
            val menuNameNo1: TextView = requireView().findViewById(R.id.button7)
            when( menuNameNo1.text.toString() ){
                "No1.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChooseFragment)
                }
                "No1.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChoose2Fragment)
                }
                "No1.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChoose3Fragment)
                }
            }
        }

        //點擊熱門做No2.導引到相關菜單
        button12 = requireView().findViewById(R.id.button8)
        button12.setOnClickListener { view ->
            val menuNameNo2: TextView = requireView().findViewById(R.id.button8)
            when( menuNameNo2.text.toString() ){
                "No2.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChooseFragment)
                }
                "No2.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChoose2Fragment)
                }
                "No2.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChoose3Fragment)
                }
            }
        }

        //點擊熱門No3.導引到相關菜單
        button13 = requireView().findViewById(R.id.button9)
        button13.setOnClickListener { view ->
            val menuNameNo3: TextView = requireView().findViewById(R.id.button9)
            when( menuNameNo3.text.toString() ){
                "No3.   促進血液循環" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChooseFragment)
                }
                "No3.   全身放鬆" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChoose2Fragment)
                }
                "No3.   核心訓練" ->
                {
                    val controller = findNavController(view)
                    controller.navigate(R.id.action_menu2Fragment_to_menuChoose3Fragment)
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
         * @return A new instance of fragment Menu2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Menu2Fragment {
            val fragment = Menu2Fragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}