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
import com.storiesapp.common.base.BaseActivity
import com.storiesapp.common.extension.*
import com.storiesapp.common.view.ViewState
import com.storiesapp.common.widget.CustomPasswordEditText
import com.storiesapp.core.local.LanguageHelper
import com.storiesapp.core.local.UserSession
import com.storiesapp.features.auth.databinding.ActivityRegisterBinding
import com.storiesapp.features.auth.databinding.DialogBottomSheetLanguageBinding
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : BaseActivity<AuthViewModel>(R.layout.activity_register) {
    private val binding by binding<ActivityRegisterBinding>()
    private var isShowPassword = false
    private var isLanguageChanged = false
    override fun getViewModel() = AuthViewModel::class
    override fun observerViewModel() {
        viewModel.registerResponse.onResult { state ->
            when (state) {
                is ViewState.Loading -> {
                    showLoading()
                }
                is ViewState.Success -> {
                    hideLoading()
                    if(!state.data.error){
                        val intent = Intent()
                        setResult(RESULT_OK , intent)
                        finish()
                    } else {
                        snackBar(resources.getString(R.string.register_failed))
                    }
                }
                is ViewState.Failed -> {
                    hideLoading()
                    try {
                        val objs = JSONObject(state.message)
                        snackBar(objs.getString("message"))
                    } catch ( e : JSONException ) {
                        snackBar(resources.getString(R.string.register_failed))
                    }
                }
            }
        }
    }

    private fun showLoading(){
        binding.progressBar.show()
        binding.btnRegister.hide()
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }

    private fun hideLoading(){
        binding.progressBar.hide()
        binding.btnRegister.show()
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
                LanguageHelper.changeLocale(this@RegisterActivity , true)
                changeLanguage()
                isLanguageChanged = true
                customDialog.dismiss()
            }
            itemBinding.usaSection.click {
                LanguageHelper.changeLocale(this@RegisterActivity , false)
                changeLanguage()
                isLanguageChanged = true
                customDialog.dismiss()
            }
        }
        changeLanguageIcon()
        binding.localeText.text = LanguageHelper.getLanguage(this)
        (binding.passwordText as CustomPasswordEditText).setValidateListener(object : CustomPasswordEditText.OnValidateListener {
            override fun onValidate(isError: Boolean) {
                if(!isError){
                    binding.constraintPassword.changeBackground(applicationContext , R.drawable.bg_invalid_red)
                } else {
                    binding.constraintPassword.changeBackground(applicationContext , R.drawable.bg_textfield)
                }
            }
        })
        binding.btnRegister.setOnClickListener {
            if(binding.nameText.text.toString() != null &&
                binding.nameText.text.toString() != "" &&
                binding.emailText.text.toString() != null &&
                    binding.emailText.text.toString() != "" &&
                    binding.passwordText.text.toString() != null &&
                    binding.passwordText.text.toString() != ""){
                if(binding.passwordText.text?.toString()!!.length >= 6){
                    viewModel.registerUser(
                        binding.nameText.text.toString(),
                        binding.emailText.text.toString(),
                        binding.passwordText.text.toString()
                    )
                } else {
                    snackBar(resources.getString(R.string.register_warning_chars))
                }
            } else {
                snackBar(resources.getString(R.string.register_warning_credentials))
            }
        }
        binding.loginText.setOnClickListener {
            onBackPressed()
        }
    }

    private fun changeLanguage(){
        binding.titleText.text = resources.getString(R.string.register_title_text)
        binding.nameText.setHint(resources.getString(R.string.register_name_hint_text))
        binding.emailText.setHint(resources.getString(R.string.register_email_hint_text))
        binding.passwordText.setHint(resources.getString(R.string.register_password_hint_text))
        binding.btnRegister.text = resources.getString(R.string.register_btn_register)
        binding.label1.text = resources.getString(R.string.register_label_1)
        binding.loginText.text = resources.getString(R.string.register_label_login)
        binding.localeText.text = if(UserSession.isIndonesia) "ID" else "EN"
        changeLanguageIcon()
    }

    private fun changeLanguageIcon(){
        binding.icLanguage.setImageResource(if(UserSession.isIndonesia) R.drawable.ic_indonesia else R.drawable.ic_usa)
    }
}