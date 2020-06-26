package com.example.admin.activity

import android.content.Intent
import android.view.View
import com.example.admin.R
import com.example.admin.base.BaseActivity
import com.example.admin.databinding.ActivityLoginBinding


class LoginActivity : BaseActivity<ActivityLoginBinding>(){

    private lateinit var mBinding: ActivityLoginBinding

    override fun onPermissionsGranted(requestCode: Int) {
    }

    override fun contentView() = R.layout.activity_login

    override fun initUI(binding: ActivityLoginBinding) {
        mBinding = binding

        if(mAuth.currentUser!=null){
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        mBinding.toolBar.imgBack.setOnClickListener {
            finish()
        }
        mBinding.toolBar.txtSignOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        mBinding.txtLogin.setOnClickListener {
            mBinding.progressBar.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword("abhi000@gmail.com", "qwerty")
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        showToast("Done")
                        mBinding.progressBar.visibility = View.GONE
                        startActivity(Intent(this, DashboardActivity::class.java))
                    } else {
                        showToast("failed")
                        mBinding.progressBar.visibility = View.GONE
                    }
                    // ...
                }.addOnFailureListener {
                    showToast(it.message.toString())
                    mBinding.progressBar.visibility = View.GONE
                }
        }
    }
}
