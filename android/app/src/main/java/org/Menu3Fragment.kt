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

import org.camera.CameraSource
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [Menu3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Menu3Fragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_menu3, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button: ImageButton
        val button2: ImageButton
        val button3: Button
        val button4: Button
        val button5: Button

        val loadingDialog =  loading_dialog(activity)
        loadingDialog.startLoadingDialog()

        val okHttpClient = OkHttpClient()
        val request1 = Request.Builder().url("https://misprojectserver.azurewebsites.net/printAllMenu").build()
        okHttpClient.newCall(request1).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "disconnect" , Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val msg = response.body!!.string()
                val menulist = msg.split("'")
                for (str in menulist){
                    print("$str \t")
                }

                val menuName1: TextView = requireView().findViewById(R.id.button12)
                menuName1.text = menulist[1]
                val menuName2: TextView = requireView().findViewById(R.id.button13)
                menuName2.text = menulist[3]
                val menuName3: TextView = requireView().findViewById(R.id.button14)
                menuName3.text = menulist[5]
                val menuName4: TextView = requireView().findViewById(R.id.button15)
                menuName4.text = menulist[7]
                val menuName5: TextView = requireView().findViewById(R.id.button16)
                menuName5.text = menulist[9]
                val menuName6: TextView = requireView().findViewById(R.id.button17)
                menuName6.text = menulist[11]
                val menuName7: TextView = requireView().findViewById(R.id.button18)
                menuName7.text = menulist[13]
                val menuName8: TextView = requireView().findViewById(R.id.button19)
                menuName8.text = menulist[15]
                val menuName9: TextView = requireView().findViewById(R.id.button20)
                menuName9.text = menulist[17]
                val menuName10: TextView = requireView().findViewById(R.id.button21)
                menuName10.text = menulist[19]
                val menuName11: TextView = requireView().findViewById(R.id.button22)
                menuName11.text = menulist[21]
                val menuName12: TextView = requireView().findViewById(R.id.button23)
                menuName12.text = menulist[23]
                val menuName13: TextView = requireView().findViewById(R.id.button24)
                menuName13.text = menulist[25]
                val menuName14: TextView = requireView().findViewById(R.id.button25)
                menuName14.text = menulist[27]
                val menuName15: TextView = requireView().findViewById(R.id.button26)
                menuName15.text = menulist[29]

                loadingDialog.dismissDialog()
            }
        })

        button = requireView().findViewById(R.id.imageButton1)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu3Fragment_to_menu2Fragment)
        }
        button2 = requireView().findViewById(R.id.imageButton2)
        button2.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu3Fragment_to_homePageFragment)
        }
        button3 = requireView().findViewById(R.id.button12)
        button3.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu3Fragment_to_menuChooseFragment)
        }
        button4 = requireView().findViewById(R.id.button13)
        button4.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu3Fragment_to_menuChoose2Fragment)
        }
        button5 = requireView().findViewById(R.id.button14)
        button5.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menu3Fragment_to_menuChoose3Fragment)
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
         * @return A new instance of fragment Menu3Fragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Menu3Fragment {
            val fragment = Menu3Fragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}