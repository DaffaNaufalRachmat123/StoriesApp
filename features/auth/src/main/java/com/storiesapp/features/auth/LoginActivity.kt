package com.storiesapp.features.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.core.app.ActivityOptionsCompat
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.d
import com.google.gson.Gson
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.*
import com.storiesapp.common.view.ViewState
import com.storiesapp.common.widget.CustomPasswordEditText
import com.storiesapp.core.local.LanguageHelper
import com.storiesapp.core.local.UserSession
import com.storiesapp.features.auth.databinding.ActivityLoginBinding
import com.storiesapp.features.auth.databinding.DialogBottomSheetLanguageBinding
import com.storiesapp.navigation.Activities
import com.storiesapp.navigation.startFeature
import com.storiesapp.navigation.startFeatureAnimation
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : BaseActivity<AuthViewModel>(R.layout.activity_login) {
    private val binding by binding<ActivityLoginBinding>()
    private var isShowPassword = false
    override fun getViewModel() = AuthViewModel::class
    override fun observerViewModel() {
        viewModel.loginResponse.onResult { state ->
            when (state) {
                is ViewState.Loading -> {
                    showLoading()
                }
                is ViewState.Success -> {
                    hideLoading()
                    if(!state.data.error){
                        UserSession.login(
                            "Bearer ${state.data.loginResult.token}",
                            Gson().toJson(state.data.loginResult).toString()
                        )
                        startFeatureAnimation(this , Activities.MainActivity , binding.btnLogin){
                            clearTask()
                            newTask()
                        }
                        finish()
                    } else {
                        snackBar(resources.getString(R.string.login_failed))
                    }
                }
                is ViewState.Failed -> {
                    hideLoading()
                    try {
                        val objs = JSONObject(state.message)
                        snackBar(objs.getString("message"))
                    } catch ( e : JSONException ) {
                        snackBar(resources.getString(R.string.login_failed))
                    }
                }
            }
        }
    }

    private fun showLoading(){
        binding.progressBar.show()
        binding.btnLogin.hide()
    }

    private fun hideLoading(){
        binding.progressBar.hide()
        binding.btnLogin.show()
    }

    override fun onStart() {
        super.onStart()
        if(UserSession.isLoggedIn){
            startFeature(Activities.MainActivity){
                clearTask()
                newTask()
            }
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            100 -> {
                if(resultCode == RESULT_OK){
                    snackBar(resources.getString(R.string.login_register_succeed))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        binding.icTogglePassword.setOnClickListener {
            if(isShowPassword){
                isShowPassword = false
                binding.icTogglePassword.setImageResource(R.drawable.ic_password_unvisible)
                binding.passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordText.text?.let {
                    binding.passwordText.setSelection(it.length)
                }
            } else {
                isShowPassword = true
                binding.icTogglePassword.setImageResource(R.drawable.ic_password_visible)
                binding.passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.passwordText.text?.let {
                    binding.passwordText.setSelection(it.length)
                }
            }
        }
        binding.bgLocale.click {
            val customDialog = MaterialDialog(this , BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                customView(R.layout.dialog_bottom_sheet_language , noVerticalPadding = true)
                cornerRadius(16f)
            }
            val itemBinding = DialogBottomSheetLanguageBinding.bind(customDialog.getCustomView())
            itemBinding.indonesiaSection.click {
                LanguageHelper.changeLocale(this@LoginActivity , true)
                changeLanguage()
                customDialog.dismiss()
            }
            itemBinding.usaSection.click {
                LanguageHelper.changeLocale(this@LoginActivity , false)
                changeLanguage()
                customDialog.dismiss()
            }
        }
        binding.localeText.text = LanguageHelper.getLanguage(this)
        changeLanguage()
        (binding.passwordText as CustomPasswordEditText).setValidateListener(object : CustomPasswordEditText.OnValidateListener {
            override fun onValidate(isError: Boolean) {
                if(!isError){
                    binding.constraintPassword.changeBackground(applicationContext , R.drawable.bg_invalid_red)
                } else {
                    binding.constraintPassword.changeBackground(applicationContext , R.drawable.bg_textfield)
                }
            }
        })
        binding.btnLogin.setOnClickListener {
            if(binding.emailText.text?.toString() != null &&
                    binding.emailText.text?.toString() != "" &&
                    binding.passwordText.text?.toString() != null &&
                    binding.passwordText.text?.toString() != ""){
                if(binding.passwordText.text?.toString()!!.length >= 6){
                    viewModel.loginUser(
                        binding.emailText.text.toString(),
                        binding.passwordText.text.toString()
                    )
                } else {
                    snackBar(resources.getString(R.string.login_warning_chars))
                }
            } else {
                snackBar(resources.getString(R.string.login_warning_credentials))
            }
        }
        binding.passwordText.clearFocus()
        binding.registerText.setOnClickListener { view ->
            val activityOptionsCompat : ActivityOptionsCompat =
                ActivityOptionsCompat.makeCustomAnimation(this , android.R.anim.fade_in , android.R.anim.fade_out)
            val registerIntent = Intent(
                this,
                RegisterActivity::class.java
            )
            startActivityForResult(registerIntent , 100 , activityOptionsCompat.toBundle())
        }
    }

    override fun onResume() {
        super.onResume()
        changeLanguage()
    }
    private fun changeLanguage(){
        binding.titleText.text = resources.getString(R.string.login_title_text)
        binding.emailText.setHint(resources.getString(R.string.login_email_hint_text))
        binding.passwordText.setHint(resources.getString(R.string.login_password_hint_text))
        binding.btnLogin.text = resources.getString(R.string.login_btn_login)
        binding.label1.text = resources.getString(R.string.login_label_1)
        binding.registerText.text = resources.getString(R.string.login_label_register)
        binding.localeText.text = if(UserSession.isIndonesia) "ID" else "EN"
        changeLanguageIcon()
    }
    private fun changeLanguageIcon(){
        binding.icLanguage.setImageResource(if(UserSession.isIndonesia) R.drawable.ic_indonesia else R.drawable.ic_usa)
    }
}