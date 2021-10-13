package com.example.portfolian.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.portfolian.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LogInActivity : AppCompatActivity() {
    private final val RC_SIGN_IN = 1
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        initGoogle()
        initKakao()
        initGit()

    }

    private fun initGoogle() {
        val btn_Google = findViewById<ImageButton>(R.id.btn_Google)

        btn_Google.setOnClickListener {
            //TODO 구글 로그인 구현
            googleLogIn()
        }
    }

    private fun initKakao() {
        val btn_Kakao = findViewById<ImageButton>(R.id.btn_Kakao)

        btn_Kakao.setOnClickListener {
            //TODO 카카오 로그인 구현
        }
    }

    private fun initGit() {
        val btn_Git = findViewById<ImageButton>(R.id.btn_Git)

        btn_Git.setOnClickListener {
            //TODO 깃 로그인 구현
        }
    }

    //닉네임 페이지로 이동하기
    private fun nickname() {
        val intent = Intent(this, NicknameActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun googleLogIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        var signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //signInIntent 사용 시, 결과값을 받아 사용할 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

            nickname()
        }else {
            Log.e("onActivityResult:: ", "NOT EQUAL REQUEST_CODE")
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            val givenName = account?.givenName.toString()
            val displayName = account?.displayName.toString()

            Log.d("account: Email:: ", email)
            Log.d("account: FamilyName:: ", familyName)
            Log.d("account: givenName:: ", givenName)
            Log.d("account: displayName:: ", displayName)
        } catch (e: ApiException) {
            Log.e("signInResult: Failed:: ", "${e.statusCode}")
        }
    }

}