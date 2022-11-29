package org

import androidx.navigation.Navigation.findNavController
//import androidx.navigation.NavController.navigate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var userName: EditText
    private lateinit var userPassword: EditText
    private lateinit var login_button: Button
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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userName = requireView().findViewById(R.id.editTextTextPersonName2)
        userPassword = requireView().findViewById(R.id.editTextTextPassword2)
        login_button = requireView().findViewById(R.id.button2)
        login_button.setOnClickListener {
            if(userName.text.isNullOrBlank() && userPassword.text.isNullOrBlank()) {
                Toast.makeText(context, "請輸入使用者名稱和密碼", Toast.LENGTH_LONG).show();
            }
            else if(userName.text.isNullOrBlank() && !(userPassword.text.isNullOrBlank())) {
                Toast.makeText(context, "請輸入使用者名稱", Toast.LENGTH_LONG).show();
            }
            else if(!(userName.text.isNullOrBlank()) && userPassword.text.isNullOrBlank()) {
                Toast.makeText(context, "請輸入使用者密碼", Toast.LENGTH_LONG).show();
            }
            else {
                val okHttpClient = OkHttpClient()
                val formbody = FormBody.Builder()
                    .add("username", userName.text.toString())
                    .add("password", userPassword.text.toString())
                    .build()
                val request = Request.Builder().url("https://misprojectserver.azurewebsites.net/login").post(formbody).build()
                okHttpClient.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        MainScope().launch {
                            withContext(Dispatchers.Default) {

                            }
                            Toast.makeText(context, "error from login page" , Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {

                        val text = response.body!!.string()

                        if(text == "此會員沒有資料" || text == "查無此會員") {
                            MainScope().launch {
                                withContext(Dispatchers.Default) {

                                }
                                Toast.makeText(context, text , Toast.LENGTH_LONG).show()
                            }
                        }
                        else {
                            MainScope().launch {
                                withContext(Dispatchers.Default) {

                                }
                                //登入後取得username存入username變數中，之後只需呼叫username即可知道登入者
                                val menulist = text.split(" ")
                                for (str in menulist){print("$str \t")}
                                var username = GlobalVariable.setName(menulist[1])
                                var userid = GlobalVariable.setId(menulist[0].toInt())
                                var usergender = GlobalVariable.setGender(menulist[4].toInt())

                                Toast.makeText(context, "userid:"+menulist[0]+"\nusername:"+menulist[1], Toast.LENGTH_LONG).show()
                                val controller = findNavController(it)
                                controller.navigate(R.id.action_loginFragment_to_boardFragment)
                            }
                        }
                    }
                })
            }
        }

        //導引到註冊頁面
        val button: Button = requireView().findViewById(R.id.button3)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_loginFragment_to_registerFragment)
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}