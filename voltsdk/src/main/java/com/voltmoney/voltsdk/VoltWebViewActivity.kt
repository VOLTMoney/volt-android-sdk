package com.voltmoney.voltsdk

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.voltmoney.voltlib.R
import com.voltmoney.voltsdk.models.SHOW_HEADER
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class VoltWebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val REQUEST_CODE_FILE_CHOOSER = 1
    private val REQUEST_CODE_CAMERA = 2
    private var fileCallback: ValueCallback<Array<Uri>>? = null
    private var capturePhotoPath: String? = null
    private var webUrl: String? = null
    private var webUri: Uri? = null
    private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
    private val urlOpenInCustomTab =
        arrayOf("alpha-", "bfin.in", "docapp.bajajfinserv.in", "bajajfinserv", "enach", "tatacapital")
    private val shouldNotReloadUrls = arrayOf(
        "otp_verify",
        "kyc_pan_verification",
        "otp_auth_cas",
        "mf_fetch_portfolio",
        "pledge_confirmation",
        "bank_account_verification",
        "bank_select",
        "bank_account_add",
        "checking_limit",
        "mf_pledge_portfolio"
    )
    private val shouldReloadUrls = arrayOf(
        "mf_fetch_portfolio",
        "mf_pledge_portfolio",
        "kyc_stepper",
        "bank_account_verification",
        "modify_pledged_amount",
        "portfolio",
        "pledge_confirmation",
        "update_phone_number",
        "update_email_id",
        "otp_auth_cas",
        "pledge_verify"
    )
    private var reloadUrlBankAccount: Boolean = true
    private var reloadUrlmfFetch: Boolean = true
    private var reloadUrlmfPledge: Boolean = true
    private var reloadUrlKycStepper: Boolean = true
    private var reloadDashboard: Boolean = true
    private var primaryColor: String? = null
    private var countWebViewLoad = 0
    private var webViewReloadCount = 0
    private lateinit var toolbar: Toolbar
    private var textColor: String? = ""
    private var mWebviewPop: WebView? = null
    private var builder: AlertDialog? = null
    private var target: String? = ""
    private var customerSSToken: String? = ""
    private var voltPlatformCode: String? = ""
    private var divId: String? = ""
    private var platformAuthToken: String? = ""
    private var showHeader: String? = "Yes"

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volt_main)
        verifyCameraPermissions(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        webView = findViewById(R.id.web_view)
        if (intent.getStringExtra("webViewUrl") != null) {
            webUrl = intent.getStringExtra("webViewUrl")!!
            primaryColor = intent.getStringExtra("primaryColor")
            textColor = intent.getStringExtra("textColor")
            target = intent.getStringExtra("target")
            customerSSToken = intent.getStringExtra("customerSSToken")
            voltPlatformCode = intent.getStringExtra("voltPlatformCode")
            platformAuthToken = intent.getStringExtra("platformAuthToken")
            showHeader = intent.getStringExtra("showHeader")
            webView.loadUrl(webUrl!!)
            toolbar.setBackgroundColor(Color.parseColor("#$primaryColor"))
            if (textColor!!.isNotEmpty()) {
                toolbar.setTitleTextColor(Color.parseColor("#$textColor"))
            }
            if (showHeader == "No") {
                toolbar.visibility = GONE
            } else {
                toolbar.visibility = VISIBLE
            }
            toolbar.setNavigationIcon(R.drawable.arrow_back)
            CookieManager.getInstance().setAcceptCookie(true);

            if (Build.VERSION.SDK_INT >= 21) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            } else {
                CookieManager.getInstance().setAcceptCookie(true);
            }
            webUri = Uri.parse(webUrl)
            Log.d("URL GOT IN WEBVIEW", webUrl.toString())
            webView.settings.apply {
                defaultTextEncodingName = "utf-8"
                javaScriptEnabled = true
                loadWithOverviewMode = true
                allowFileAccess = true
                domStorageEnabled = true
                setSupportMultipleWindows(true)
                allowContentAccess = true
                javaScriptCanOpenWindowsAutomatically
                useWideViewPort = true
                mediaPlaybackRequiresUserGesture= false
                loadsImagesAutomatically = true
                // mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            webView.webViewClient = VoltWebViewClient()
            webView.webChromeClient = VoltWebChromeClient()


        } else {
            Log.d("TAG", "onCreate of SDK 2")
            webUrl =
                "https://app.staging.voltmoney.in/?ref=4CCLRP&primaryColor=FF6E31&partnerPlatform=SDK_INVESTWELL"
            webView.loadUrl(webUrl!!)

            toolbar.setNavigationIcon(R.drawable.arrow_back)
            webUri = Uri.parse(webUrl)
            webView.settings.apply {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                //  javaScriptEnabled = true
                loadWithOverviewMode = true
                allowFileAccess = true
                domStorageEnabled = true
                setSupportMultipleWindows(true)
                useWideViewPort = true
                allowContentAccess = true
                // mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webView.webViewClient = VoltWebViewClient()
            webView.webChromeClient = VoltWebChromeClient()
        }
    }



    private class UriWebViewClient : WebViewClient() {
        /*
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            //Log.d("shouldOverrideUrlLoading", url);
            if (host.equals(target_url_prefix))
            {
                // This is my web site, so do not override; let my WebView load
                // the page
                if(mWebviewPop!=null)
                {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop=null;
                }
                return false;
            }

            if(host.equals("m.facebook.com"))
            {
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch
            // another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        */
        override fun onReceivedSslError(
            view: WebView, handler: SslErrorHandler,
            error: SslError
        ) {
            Log.d("onReceivedSslError", "onReceivedSslError")
            //super.onReceivedSslError(view, handler, error);
        }
    }

    inner class VoltWebChromeClient : WebChromeClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequest(request: PermissionRequest?) {
            Log.d("HERE IN THE REQUEST", request.toString())
            this@VoltWebViewActivity.runOnUiThread { request!!.grant(request.resources) }

        }

        override fun onPermissionRequestCanceled(request: PermissionRequest?) {

            super.onPermissionRequestCanceled(request)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            Log.d("WebView SGAR", consoleMessage!!.message());
            return true
        }

        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {
            mWebviewPop = WebView(this@VoltWebViewActivity)
            mWebviewPop?.setVerticalScrollBarEnabled(false)
            mWebviewPop?.setHorizontalScrollBarEnabled(false)
            mWebviewPop?.setWebViewClient(UriWebViewClient())
            mWebviewPop?.setWebChromeClient(VoltWebChromeClient())
            mWebviewPop?.getSettings()?.setJavaScriptEnabled(true)
            mWebviewPop?.getSettings()?.setSavePassword(true)
            mWebviewPop?.getSettings()?.setSaveFormData(true)
            mWebviewPop?.settings?.domStorageEnabled = true




            // AlertDialog.Builder builder;
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //     builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            // } else {
            //     builder = new AlertDialog.Builder(MainActivity.this);
            // }

            // set the WebView as the AlertDialog.Builder’s view

            // mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            // create an AlertDialog.Builder
            // the below did not give me .dismiss() method . See : https://stackoverflow.com/questions/14853325/how-to-dismiss-alertdialog-in-android

            // AlertDialog.Builder builder;
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //     builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            // } else {
            //     builder = new AlertDialog.Builder(MainActivity.this);
            // }

            // set the WebView as the AlertDialog.Builder’s view
            builder = AlertDialog.Builder(
                this@VoltWebViewActivity,
                android.R.style.Theme_Light_NoTitleBar_Fullscreen
            )
                .create()
            builder?.setTitle("")
            builder!!.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", {
                //do your own idea.
                    dialog, which ->
                mWebviewPop!!.destroy()
            })
            builder?.setView(mWebviewPop)




            builder?.show()
            builder?.getWindow()
                ?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(mWebviewPop, true)
            }

            val transport = resultMsg!!.obj as WebViewTransport
            transport.webView = mWebviewPop
            resultMsg!!.sendToTarget()
            return true
        }

        override fun onCloseWindow(window: WebView?) {
            Log.d("CLOSE CALLED", "CLOSE CALLED")
            try {
                try {
                    val myIntent = Intent(this@VoltWebViewActivity, VoltWebViewActivity::class.java)
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this@VoltWebViewActivity.startActivity(myIntent)

                    Log.d("CLOSE CALL RECEIVED", "CLOSE CALL RECEIVED" + mWebviewPop)
                } catch (e: Exception) {
                    // TODO: Write an exception handler to notify user
                }

            } catch (e: Exception) {
                Log.e("ERROR RECEIVED", e.toString())
                // TODO: Write an exception handler to notify user
            }
            try {
                mWebviewPop!!.destroy()
            } catch (e: Exception) {
                // TODO: Write an exception handler to notify user
            }

            try {
                builder!!.dismiss()
            } catch (e: Exception) {
                // TODO: Write an exception handler to notify user
            }
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            fileCallback = filePathCallback
            val items = arrayOf("Take a photo", "Choose from gallery")
            val builder = AlertDialog.Builder(this@VoltWebViewActivity)
            builder.setTitle("Choose an option")
            builder.setItems(items) { _, item ->
                when (item) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(
                                this@VoltWebViewActivity,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@VoltWebViewActivity,
                                arrayOf(Manifest.permission.CAMERA),
                                REQUEST_CODE_CAMERA
                            )
                        } else {
                            openCamera()
                        }
                    }
                    1 -> {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        val mimetype = arrayOf("application/pdf", "image/*")
                        intent.type = "*/*"
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetype)
                        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)
                    }
                }
            }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setOnCancelListener {
                fileCallback?.onReceiveValue(null)
            }
            dialog.show()
            return true
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
            super.getVisitedHistory(callback)
        }
    }

    inner class VoltWebViewClient : WebViewClient() {
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            return super.shouldInterceptRequest(view, url)
        }



        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }

        override fun onTooManyRedirects(
            view: WebView?,
            cancelMsg: Message?,
            continueMsg: Message?
        ) {
            super.onTooManyRedirects(view, cancelMsg, continueMsg)
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
            super.onFormResubmission(view, dontResend, resend)
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
            Log.d("VoltWebViewCount: ", countWebViewLoad.toString())
            Log.d("TAG", "onCreate of SDK doUpdateVisitedHistory entry")
            if (countWebViewLoad > 15) {
                //view?.removeAllViews()
                /*  webUrl?.let { view!!.loadUrl(it) }
                  Log.d("VoltWebViewReloadCount: ", (webViewReloadCount++).toString())
                  Log.d("UrlWherePageReloaded", url.toString())*/
                countWebViewLoad = 0
                reloadUrlBankAccount = true
                reloadUrlmfFetch = true
                reloadUrlmfPledge = true
                reloadUrlKycStepper = true
                reloadDashboard = true
            }
            if (url!!.contains("mf_fetch_portfolio") && reloadUrlmfFetch) {
                reloadUrlmfFetch = false
                webUrl?.let { view!!.loadUrl(it) }
                countWebViewLoad = 0
            }
            if (url!!.contains("mf_pledge_portfolio") && reloadUrlmfPledge) {
                reloadUrlmfPledge = false
                webUrl?.let { view!!.loadUrl(it) }
                countWebViewLoad = 0
            }
            if (url!!.contains("kyc_stepper") && reloadUrlKycStepper) {
                reloadUrlKycStepper = false
                // webUrl+="/pledge_confirmation"
                webUrl?.let { view!!.loadUrl(it) }
                // https://app.staging.voltmoney.in/?ref=4CCLRP&primaryColor=FF6E31&partnerPlatform=SDK_INVESTWELL&user=8939254696/pledge_confirmation
                countWebViewLoad = 0
            }
            if (url!!.contains("bank_account_verification") && reloadUrlBankAccount) {
                reloadUrlBankAccount = false
                // webUrl+="/pledge_confirmation"
                webUrl?.let { view!!.loadUrl(it) }
                // https://app.staging.voltmoney.in/?ref=4CCLRP&primaryColor=FF6E31&partnerPlatform=SDK_INVESTWELL&user=8939254696/pledge_confirmation
                countWebViewLoad = 0
            }
            if (url!!.contains("dashboard") && reloadDashboard) {
                reloadDashboard = false
                // webUrl+="/pledge_confirmation"
                webUrl?.let { view!!.loadUrl(it) }
                // https://app.staging.voltmoney.in/?ref=4CCLRP&primaryColor=FF6E31&partnerPlatform=SDK_INVESTWELL&user=8939254696/pledge_confirmation
                countWebViewLoad = 0
            }
            countWebViewLoad++
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
        }

        override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
            super.onReceivedClientCertRequest(view, request)
        }

        override fun onReceivedHttpAuthRequest(
            view: WebView?,
            handler: HttpAuthHandler?,
            host: String?,
            realm: String?
        ) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm)
        }

        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            if (fileCallback != null) {
                fileCallback!!.onReceiveValue(null)
                fileCallback = null
            }
            return super.shouldOverrideKeyEvent(view, event)
        }

        override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
            super.onUnhandledKeyEvent(view, event)
        }

        override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
            super.onScaleChanged(view, oldScale, newScale)
        }

        override fun onReceivedLoginRequest(
            view: WebView?,
            realm: String?,
            account: String?,
            args: String?
        ) {
            super.onReceivedLoginRequest(view, realm, account, args)
        }

        override fun onRenderProcessGone(
            view: WebView?,
            detail: RenderProcessGoneDetail?
        ): Boolean {
            return super.onRenderProcessGone(view, detail)
        }

        override fun onSafeBrowsingHit(
            view: WebView?,
            request: WebResourceRequest?,
            threatType: Int,
            callback: SafeBrowsingResponse?
        ) {
            super.onSafeBrowsingHit(view, request, threatType, callback)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.d("RECHED HERE", url)
            Log.d("RECHED HERE AS WELL", checkURLMatchesFromListArray(url, urlOpenInCustomTab).toString())


             if(url.contains(("closePop"))){
                 val myIntent = Intent(this@VoltWebViewActivity, VoltWebViewActivity::class.java)
                 myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                 myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                 this@VoltWebViewActivity.startActivity(myIntent)
                 return  true
             }
            if (url.contains(webUri!!.host!!)) {
                view.loadUrl(url)
                return true
            } else if (checkURLMatchesFromListArray(url, urlOpenInCustomTab)) {
                Log.d("REACHED THERE IN THE", "")
               var  customTabsIntent = CustomTabsIntent.Builder().setInitialActivityHeightPx(400,
                    CustomTabsIntent.ACTIVITY_HEIGHT_FIXED
                )
                customTabsIntent!!.setToolbarColor(ContextCompat.getColor(this@VoltWebViewActivity, androidx.appcompat.R.color.material_blue_grey_800));
                openCustomTab(this@VoltWebViewActivity, customTabsIntent!!.build(), Uri.parse(url));

            }
            // open camera/document picker
            else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                webView.context.startActivity(intent)
            }
            return true
        }

        private fun checkURLMatchesFromListArray(url: String, list: Array<String>): Boolean {
            list.forEach {
                if (url.contains(it)) {
                    return true
                }
            }
            return false
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            CookieSyncManager.getInstance().sync();

            super.onPageFinished(view, url)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FILE_CHOOSER) {
                //val imageUri = data?.data
                fileCallback?.onReceiveValue(arrayOf(Uri.parse(data?.dataString)))
                this.fileCallback = null
                //webView.loadUrl("javascript:document.getElementById('fileInput').value = '$imageUri';")
            }
            if (requestCode == REQUEST_CODE_CAMERA) {
                // this is important, call the callback with null parameter
                this.fileCallback?.onReceiveValue(arrayOf(Uri.parse(capturePhotoPath)))
                this.fileCallback = null
            }
        } else {
            fileCallback?.onReceiveValue(null)
            Toast.makeText(this, "Photo not uploaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile != null) {
                capturePhotoPath = "file:" + photoFile.absolutePath
                intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(
                        this,
                        this.getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile
                    )
                )
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri?) {
        // package name is the default package
        // for our custom chrome tab
        val packageName = "com.android.chrome"
        if (packageName != null) {


            // Create a CustomTabsCallback
            val customTabsCallback = object : CustomTabsCallback() {
                override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                    super.onNavigationEvent(navigationEvent, extras)
                    // Check if the tab has been closed
                    if (navigationEvent == TAB_HIDDEN) {
                        // Handle the tab being closed
                        // For example, notify the user, or perform any necessary actions
                        // Here you can put your callback code
                        // For simplicity, I'm just logging
                        Log.d("CustomTab", "Custom tab closed")
                    }
                }
            }
            // we are checking if the package name is not null
            // if package name is not null then we are calling
            // that custom chrome tab with intent by passing its
            // package name.
            customTabsIntent.intent.setPackage(packageName)


            // in that custom tab intent we are passing
            // our url which we have to browse.
            customTabsIntent.launchUrl(activity, uri!!)
        } else {
            // if the custom tabs fails to load then we are simply
            // redirecting our user to users device default browser.
            activity.startActivityForResult(Intent(Intent.ACTION_VIEW, uri), 1)
        }
    }


    private fun verifyCameraPermissions(activity: Activity) {
        val cameraPermission =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_CAMERA,
                REQUEST_CODE_CAMERA
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}