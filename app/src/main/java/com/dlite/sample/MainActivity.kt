package com.dlite.sample

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cubox.cameramodule.core.CameraModule
import com.cubox.cameramodule.core.CameraResult
import com.cubox.cameramodule.util.option.FaceRecognitionOption
import com.cubox.cameramodule.util.option.LiveCaptureOption
import com.cubox.cameramodule.util.option.LivenessOption
import com.cubox.dlite.core.Dlite
import com.cubox.dlite.core.DliteErrorStatus
import com.cubox.dlite.core.DliteOption
import com.cubox.dlite.logger.LogLevel
import com.cubox.inferencemodule.core.mlkit.FaceAnalyzer.Companion.LOG_TAG

class MainActivity : ComponentActivity() {
    private var error: DliteErrorStatus? = null
    private lateinit var cameraModule: CameraModule

    // 이미지 상태 변수 추가
    private val resultImageState = mutableStateOf<Bitmap?>(null)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraModule = CameraModule(this)
        initializeLibrary()

        enableEdgeToEdge()
        setContent {
            Scaffold {
                Box(contentAlignment = Alignment.Center) {
                    AddViews(resultImageState.value)
                }
            }
        }
    }

    private fun initializeLibrary() {
        // option 생성 빌드
        val option = DliteOption.Builder()
            .setLogLevel(LogLevel.ALL)
            .setLicenseKey("YOUR LICENSE KEY")
            .build()

        Dlite.with(this)
            .initialize(option, object : Dlite.InitializeCompleteListener {
                override fun onInitializeComplete(
                    completed: Boolean,
                    errorStatus: DliteErrorStatus?
                ) {
                    if (completed) {
                        // 성공시
                        Log.d(LOG_TAG, "Initialization completed successfully")
                    } else {
                        error = errorStatus

                        // 오류 처리
                        when (errorStatus) {
                            DliteErrorStatus.LICENSE_EXPIRED -> {}
                            // ....
                            else -> {}
                        }
                        Log.e(LOG_TAG, "Initialization failed with error: $errorStatus")
                    }
                }
            })
    }

    @Composable
    fun AddViews(resultImage: Bitmap?) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 결과 이미지 뷰
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .weight(0.5f)
                    .fillMaxWidth()
                    .background(Color.LightGray),  // 임시로 영역을 보기 위한 배경색
                contentAlignment = Alignment.Center
            ) {
                // 결과 이미지가 있을 경우 표시
                resultImage?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Captured Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (Dlite.with(applicationContext).getCurrentError() != null) {
                            return@Button
                        }

                        val option = LiveCaptureOption.Builder()
                            .build()

                        cameraModule
                            .start(option) { result ->
                                Log.d("TAGTAGTAG", " Results ${result} ::")
                                when (result) {
                                    is CameraResult.Result -> {
                                        Log.d("TAGTAGTAG", " Result ${result.succeedStatus} :: ${result.faceId}")
                                        // 상태 변수 업데이트
                                        resultImageState.value = result.image
                                    }
                                    is CameraResult.Error -> {
                                        // 에러 처리
                                    }
                                }
                            }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Live Capture")
                }
                Button(
                    onClick = {
                        if (Dlite.with(applicationContext).getCurrentError() != null) {
                            return@Button
                        }

                        val option = FaceRecognitionOption.Builder()
                            .build()

                        cameraModule
                            .start(option) { result ->
                                Log.d("TAGTAGTAG", " Results ${result} ::")
                                when (result) {
                                    is CameraResult.Result -> {
                                        Log.d("TAGTAGTAG", " Result ${result.succeedStatus} :: ${result.faceId}")
                                        // 상태 변수 업데이트
                                        resultImageState.value = result.image
                                    }
                                    is CameraResult.Error -> {
                                        // 에러 처리
                                    }
                                }
                            }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "FRS")
                }
                Button(
                    onClick = {
                        if (Dlite.with(applicationContext).getCurrentError() != null) {
                            return@Button
                        }

                        val option = LivenessOption.Builder()
                            .build()

                        cameraModule
                            .start(option) { result ->
                                Log.d("TAGTAGTAG", " Results ${result} ::")
                                when (result) {
                                    is CameraResult.Result -> {
                                        Log.d("TAGTAGTAG", " Result ${result.succeedStatus} :: ${result.faceId}")
                                        // 상태 변수 업데이트
                                        resultImageState.value = result.image
                                    }
                                    is CameraResult.Error -> {
                                        // 에러 처리
                                    }
                                }
                            }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Liveness")
                }
            }
        }
    }
}