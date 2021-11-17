package com.example.portfolian.view.login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.portfolian.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException

class LogInFragment  : Fragment(R.layout.fragment_login) {
    private lateinit var navController: NavController
    private lateinit var btn_Google: ImageButton
    private lateinit var btn_Kakao: ImageButton
    private lateinit var btn_Git: ImageButton

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123

    private lateinit var LogInActivity: LogInActivity
    private lateinit var callback: SessionCallback



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        LogInActivity = context as LogInActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    private fun init() {
        initClient()
        initView()
    }

    private fun initClient() {
        //Google
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    private fun initView() {
        initGoogle()
        initKakao()
        initGit()
    }

    private fun initGoogle() {
        btn_Google = requireView().findViewById(R.id.btn_Google)
        btn_Google.setOnClickListener {
            //TODO google 로그인 실행
            Log.d("LogInFragment:: ", "btn_Google: Clicked")
            googleSignIn()

            //nickname()
        }
    }

    private fun initKakao() {
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()

        btn_Kakao = requireView().findViewById(R.id.btn_Kakao)
        btn_Kakao.setOnClickListener {
            //TODO Kakao 로그인 실행
            Log.d("LogInFragment:: ", "btn_Kakao: Clicked")

            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LogInActivity)

        }
    }

    private fun initGit() {
        btn_Git= requireView().findViewById(R.id.btn_Git)
        btn_Git.setOnClickListener {
            //TODO Git 로그인 실행
            Log.d("LogInFragment:: ", "btn_Git: Clicked")
        }
    }

    private fun nickname() {
        navController.navigate(R.id.action_logInFragment_to_nicknameFragment)
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acct = completedTask.getResult(ApiException::class.java)

            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto = acct.photoUrl
                val idToken = acct.idToken

                Log.d(TAG, "handleSignInResult:personName $personName")
                Log.d(TAG, "handleSignInResult:personGivenName $personGivenName")
                Log.d(TAG, "handleSignInResult:personEmail $personEmail")
                Log.d(TAG, "handleSignInResult:personId $personId")
                Log.d(TAG, "handleSignInResult:personFamilyName $personFamilyName")
                Log.d(TAG, "handleSignInResult:personPhoto $personPhoto")
                Log.d(TAG, "handleSignInResult:idToken $idToken")

            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }

    private fun tokenToServer() {
        //TODO 서버로 토큰 보내기
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            nickname()
        }
    }

    private inner class SessionCallback: ISessionCallback {
        override fun onSessionOpened() {
            UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    nickname()
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.e("Kakao:: ", "세션이 닫혔습니다.")
                }
            })
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            if(exception != null) {
                com.kakao.util.helper.log.Logger.e(exception)
                Log.e("Kakao:: ", "로그인 도중 오류가 발생했습니다.")
            }
        }
    }


}