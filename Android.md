
## Android SDK
Last modified: 2025.04.22 

</br>


## SDK Requirements
- Android 23+ (Marshmallow)
- This SDK is designed for Java version_1_8.
  
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

## SDK Setup

1.	Add the following three **.aar** library files to **app/libs**:
   
    ```kotlin
    secernai-dlite-{version}.aar // core module
    secernai-inference-{version}.aar // inference module 
    secernai-camera-{version}.aar // camera module
    ```

<br/> <br/>

2.	Add the following to **plugins / buildFeatures**:
   
    ```kotlin
    plugins {
       ...
       id("kotlin-kapt")
       id("kotlin-parcelize")
    }
    ```

<br/> <br/>

3.	Add the following to **gradle.properties** for AndroidX compatibility:
   
    ```kotlin
    android.enableJetifier=true
    ```

<br/> <br/>

4.	Add the following modules to **app/build.gradle** (external dependencies):
   
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

5.	Add the following to **AndroidManifest.xml**:
   
    ```kotlin
    <!--  network and camera permission  -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-feature android:name="android.hardware.camera.any" />
    <uses-permission android:name="android.permission.CAMERA" />
    ```



<br/> <br/>

## SDK Initialization
1. <span style="color:red; font-weight:bold;">[!] You must provide a license key in setLicenseKey during initialization.</span>

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

## Activity Usage
   1. The activity runs once, returns the result, and then terminates.
   2. You can modify the UI using the provided functions.
   3. Result handling: Success - `CameraResult.Result`, Failure - `CameraResult.Error`
   4. Create one of the following options: `LiveCapture`, `FaceRecognition`, or `ActiveLiveness`, then execute with start(option).

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

## Fragment Usage

   1. The fragment remains active until the user explicitly exits it.
   2. You can freely add and customize views on top of the fragment.
   3. Result handling: Success - `CameraResult.Result`, Failure - `CameraResult.Error`
   4. Create a `LiveCaptureFragment` and register a result callback.

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