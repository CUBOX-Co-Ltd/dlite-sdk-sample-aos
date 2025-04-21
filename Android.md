---
description : "Android 세팅 방법 문서"
last_modified : "2025.04.16"
---

## SDK Requirements
- Android 23+ ( Marshmallow )
- 해당 SDK는 Java version 1_8 버전에서 설계되었습니다.
  
```kotlin
android {
   compileOptions {
       sourceCompatibility JavaVersion.VERSION_1_8
       targetCompatibility JavaVersion.VERSION_1_8
   }

   kotlinOptions {
       jvmTarget = "1.8"
   }

   // add buildFeatures
   buildFeatures {
       dataBinding = true
       viewBinding = true
   }
}
```

<br/> 

## SDK 설정

1. 세 가지의 라이브러리 .aar 파일 **app/libs에** 추가해 주세요. 
   
    ```kotlin
    secernai-dlite-{version}.aar // core module
    secernai-inference-{version}.aar // inference module 
    secernai-camera-{version}.aar // camera module
    ```

<br/> <br/>

2. **plugins / buildFeatures에** 추가
   
    ```kotlin
    plugins {
       ...
       id("kotlin-kapt")
       id("kotlin-parcelize")
    }
    ```

<br/> <br/>

3. AndroidX 호환을 위해 **gradle.properties에** 추가해주세요.
   
    ```kotlin
    android.enableJetifier=true
    ```

<br/> <br/>

4. **app/build.gradle에** 아래 모듈들을 추가해주세요. ( 외부 라이브러리 의존성 )
   
    ```kotlin
    dependencies {

        implementation(files("libs/secernai-dlite-1.0.0.aar"))
        implementation(files("libs/secernai-inference-1.0.0.aar"))
        implementation(files("libs/secernai-camera-1.0.0.aar"))


        // network
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:okhttp:4.11.0")


        // inference
        implementation("org.tensorflow:tensorflow-lite:2.16.1")
        implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
        implementation("com.google.mlkit:face-detection:16.1.5")
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")


        // util, other
        implementation("commons-io:commons-io:2.11.0")


        // camera module
        implementation("androidx.camera:camera-camera2:1.1.0")
        implementation("androidx.camera:camera-core:1.1.0")
        implementation("androidx.camera:camera-lifecycle:1.1.0")
        implementation("androidx.camera:camera-view:1.1.0")
        implementation("androidx.fragment:fragment-ktx:1.5.5")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
        ...
    }
    ```

<br/> <br/>

5. **AndroidManifest.xml에** 아래 조건을 추가해주세요.
   
    ```kotlin
    <!--  network and camera permission  -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-feature android:name="android.hardware.camera.any" />
    <uses-permission android:name="android.permission.CAMERA" />
    ```



<br/> <br/>

## SDK 초기화
1. <span style="color:red; font-weight:bold;">[!] 초기화 시 setLicenseKey에 라이센스 키를 반드시 넣어주세요.</span>

    ```kotlin
    // option build
    val option = DliteOption.Builder()
    .setLogLevel(LogLevel.ALL)
    .setLicenseKey("your license key")
    .build()


    // init sdk
    Dlite.with(this)
    .initialize(option, object : Dlite.InitializeCompleteListener {
        override fun onInitializeComplete(completed: Boolean, errorStatus: DliteErrorStatus?) {
            if (completed) {
                LogManager.d("success")
            } else {
                LogManager.e("fail")
            }
        }
    })



    // set log level
    Dlite.with(this).logLevel = LogLevel.ALL


    // check for errors.
    Dlite.with(this).getCurrentError()
    ```

</br>

## Activity 사용법
   1. Activity는 일회성 실행으로 실행 후 결과를 반환하고 종료됩니다.
   2. 제공되는 기능을 통한 UI 수정이 가능합니다.
   3. 결과 처리 방식, 성공 - `CameraResult.Result`, 실패 - `CameraResult.Error`
   4. `LiveCapture`, `FaceRecognition`, `ActiveLiveness` 중 하나의 옵션을 생성하고 `start(option)`로 실행합니다.

        ```kotlin
        // val option = LivenessOption.Builder().build()
        // val option = FaceRecognitionOption.Builder().build()
        val option = LiveCaptureOption.Builder().build()

        cameraModule.start(option) { result ->
            when (result) {
                is CameraResult.Result -> {
                    LogManager.d("Activity Results - ${result}")
                    binding.imageView.setImageBitmap(result.image)
                }

                is CameraResult.Error -> {
                    LogManager.e("Results - ${result}")
                }
            }
        }
        ```

</br> </br>

## Fragment 사용법

   1. Fragment 는 사용자가 종료하기 전까지 계속 사용됩니다.
   2. Fragment 위에 사용자가 자유롭게 View 를 추가하여 Custom 하여 사용할 수 있습니다.
   3. 결과 처리 방식, 성공 - `CameraResult.Result`, 실패 - `CameraResult.Error`
   4. `LiveCaptureFragment`를 생성하고 결과 콜백을 등록하여 사용합니다.

        ```kotlin
        val cameraFragment = LiveCaptureFragment.newInstance(
            LiveCaptureOption.Builder().build(),
            startRightAway = true,
            showFaceFrame = true)

        cameraFragment.captureResultCallback = { result ->
            when (result) {
                is CameraResult.Result -> {
                    if (result.image == null) {
                        // fail
                    } else {
                    // success
                    }
                }
                is CameraResult.Error -> {
                // error
                }
            }
        }

        // observe frame state
        cameraFragment.passFrameStateCallback = { state ->
        ...
        }

        // commit fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, cameraFragment)
            .commit()

        ```

</br>