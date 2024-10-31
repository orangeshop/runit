package com.zoku.login

import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.coroutineScope

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: LoginViewModel){
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    // 한번 만 호출되야함.
    LaunchedEffect(uiState.isLogin){
        if (uiState.isLogin) {
            onLoginSuccess()
            Log.d(TAG, "LoginScreen: 호츛ㄹ")
            Log.d(TAG, "LoginScreen: ${uiState.userId}"
                    + " ${uiState.nickName}"
                    + " ${uiState.image}"
                    + " ${uiState.isLogin}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(
                color = com.zoku.ui.BaseDarkBackground
            )
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.padding(top = 60.dp))
            Box(  modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .wrapContentWidth()
                .wrapContentHeight()
        ){
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)

                )
            }
        Image(
            painter = painterResource(id = R.drawable.main_logo),
            contentDescription = "Logo Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        KakaoLoginButton(
            onKakaoLoginClick = {
                Toast.makeText(context, "로그인 버튼 클릭됨", Toast.LENGTH_SHORT).show()
                viewModel.handleKaKaoLogin()

            }
        )
    }

}

@Composable
fun KakaoLoginButton(onKakaoLoginClick: () -> Unit) {
    Surface(
        onClick = { onKakaoLoginClick() },
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.kakao_login_medium_wide),  // 카카오 로그인 아이콘
            contentDescription = "Kakao Login",
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        )
    }
}



