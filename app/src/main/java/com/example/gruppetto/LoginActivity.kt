package com.example.gruppetto

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        val loginText : EditText = findViewById(R.id.loginText)
        val pwText : EditText = findViewById(R.id.pwText)
        val loginButton : Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            if (validateForm()){
                login(loginText.text.toString(), pwText.text.toString())
            }
        }


    }

    override fun onStart() {
        super.onStart()
        //val currentUser = auth.currentUser
    }

    private fun login(email: String, pw: String) {

        Log.d(TAG, "signIn:matt@gmail.com")

        auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }


    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = loginText.text.toString()
        if (TextUtils.isEmpty(email)) {
            loginText.error = "Required."
            valid = false
        } else {
            loginText.error = null
        }

        val password = pwText.text.toString()
        if (TextUtils.isEmpty(password)) {
            pwText.error = "Required."
            valid = false
        } else {
            pwText.error = null
        }

        return valid
    }


    companion object {
        private const val TAG = "EmailPassword"
    }
}