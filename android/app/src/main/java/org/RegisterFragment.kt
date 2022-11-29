package org

//import androidx.navigation.NavController.navigate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [MovementKneeHoldingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private lateinit var userName: EditText
    private lateinit var userPassword: EditText
    private lateinit var userEmail: EditText
    private lateinit var userSex: EditText
    private lateinit var userBirthdate: EditText

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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userName = requireView().findViewById(R.id.editTextTextPersonName2)
        userEmail = requireView().findViewById(R.id.editTextTextPersonName3)
        userPassword = requireView().findViewById(R.id.editTextTextPersonName4)
        userSex = requireView().findViewById(R.id.editTextTextPersonName5)
        userBirthdate = requireView().findViewById(R.id.editTextTextPersonName6)


        val button: Button = requireView().findViewById(R.id.button2)
        MainScope().launch {
            withContext(Dispatchers.Default) {

            }
            button.setOnClickListener { view ->
                //檢查mail格式
                val mailRegex = Regex(
                    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{1,}))@"
                            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
                )
                val mailcheck :Boolean = mailRegex.matches(userEmail.text.toString())

                //檢查密碼格式
                val passwordRegex = Regex("[A-Za-z0-9]{6,12}")
                val passwordcheck :Boolean = passwordRegex.matches(userPassword.text.toString())

                //檢查日期格式
                val dateRegex = Regex("[1-2]{1}+[0-9]{3}+[0-1]{1}+[0-9]{1}+[0-3]{1}+[0-9]{1}")
                val datecheck :Boolean = dateRegex.matches(userBirthdate.text.toString())

                if(userSex.text.toString() != "1" && userSex.text.toString() != "2") {
                    Toast.makeText(context, "性別:男性請輸入1，女性請輸入2", Toast.LENGTH_LONG).show();
                }
                else if(passwordcheck == false){
                    Toast.makeText(context, "密碼輸入至少6碼，最多12碼", Toast.LENGTH_LONG).show()
                }
                else if(datecheck == false){
                    Toast.makeText(context, "日期請以YYYYMMDD格式輸入", Toast.LENGTH_LONG).show()
                }
                else if(mailcheck == false) {
                    Toast.makeText(context, "Email輸入格式錯誤", Toast.LENGTH_LONG).show()
                }
                else {
                    val okHttpClient = OkHttpClient()
                    val formbody = FormBody.Builder()
                        .add("username", userName.text.toString())
                        .add("email", userEmail.text.toString())
                        .add("password", userPassword.text.toString())
                        .add("sex", userSex.text.toString())
                        .add("birthdate", userBirthdate.text.toString())
                        .build()
                    val request =
                        Request.Builder().url("https://misprojectserver.azurewebsites.net/register")
                            .post(formbody).build()
                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            MainScope().launch {
                                withContext(Dispatchers.Default) {

                                }
                                Toast.makeText(
                                    context,
                                    "error form register page",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val text = response.body!!.string()
                            MainScope().launch {
                                withContext(Dispatchers.Default) {

                                }
                                Toast.makeText(context, text, Toast.LENGTH_LONG).show()
                                //註冊成功跳到登入頁面
                                if (text == "註冊成功") {
                                    val controller = findNavController(view)
                                    controller.navigate(R.id.action_registerFragment_to_loginFragment)
                                }
                            }
                        }
                    })
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
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): RegisterFragment {
            val fragment = RegisterFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}