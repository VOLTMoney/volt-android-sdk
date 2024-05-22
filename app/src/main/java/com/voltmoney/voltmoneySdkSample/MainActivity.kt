package com.voltmoney.voltmoneySdkSample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.voltmoney.voltmoneySdkSample.databinding.ActivityMainBinding
import com.voltmoney.voltsdk.VoltAPIResponse
import com.voltmoney.voltsdk.VoltSDKContainer
import com.voltmoney.voltsdk.models.PreCreateAppResponse


class MainActivity : AppCompatActivity(), VoltAPIResponse {
    private var voltSDKContainer: VoltSDKContainer? = null
    private lateinit var requestQueue: RequestQueue
    var selectedEnvironment: String = "staging"
    var showHeader: String = "Yes"


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestQueue = Volley.newRequestQueue(this)

        binding.stagingRB.setOnCheckedChangeListener { button, b ->
            if (binding.stagingRB.isChecked) {
                selectedEnvironment = "staging"
                binding.productionRB.isChecked = false
            }
        }

        binding.productionRB.setOnCheckedChangeListener { button, b ->
            if (binding.productionRB.isChecked) {
                selectedEnvironment = "production"
                binding.stagingRB.isChecked = false
            }
        }

        binding.yesHeaderRB.setOnCheckedChangeListener { button, b ->
            if (binding.yesHeaderRB.isChecked) {
                showHeader = "Yes"
                binding.noHeaderRB.isChecked = false
            }
        }

        binding.noHeaderRB.setOnCheckedChangeListener { button, b ->
            if (binding.noHeaderRB.isChecked) {
                showHeader = "No"
                binding.yesHeaderRB.isChecked = false
            }
        }

        fun onExitSDK() {
            Log.d("TAG", "Volt SDK has been exited")
        }

        binding.btVolt.setOnClickListener {
            voltSDKContainer = binding.etPrimaryColor.text.toString().let { it ->
                if (it.length == 6) {
                    val target = binding.etTarget.text.toString()
                    val customerSSOToken = binding.etSsoToken.text.toString()
                    val platformAuthToken = binding.etPlatformAuthToken.text.toString()
                    val customerCode = binding.etCustomerCode.text.toString()

                    if (platformAuthToken == null) {
                        Log.e("TAG", "Please enter Platform Auth Token")
                        Toast.makeText(this, "Please enter Platform Auth Token", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (customerSSOToken == null) {

                            binding.etPlatform.text.toString().let { iter ->
                                {
                                    if (iter.length > 2) {
                                        VoltSDKContainer(
                                            this,
                                            iter,
                                            platformAuthToken,
                                            selectedEnvironment,
                                            it,
                                            "FFFFFF",
                                            target,
                                            customerSSOToken,
                                            customerCode,
                                            showHeader,
                                            ::onExitSDK
                                        )
                                    }
                                }
                            }

                        } else {
                            if (customerCode !== null) {
                                binding.etPlatform.text.toString().let { iter ->
                                    {
                                        if (iter.length > 2) {
                                            VoltSDKContainer(
                                                this,
                                                iter,
                                                platformAuthToken,
                                                selectedEnvironment,
                                                it,
                                                "FFFFFF",
                                                target,
                                                customerSSOToken,
                                                customerCode,
                                                showHeader,
                                                ::onExitSDK
                                            )
                                        }
                                    }
                                }
                            } else {
                                Log.e("TAG", "Please enter Customer Code")
                                Toast.makeText(
                                    this, "Please enter Customer Code", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.etPlatform.text.toString().let { it1 ->
                        if (it1.length > 2) {
                            VoltSDKContainer(
                                this,
                                it1,
                                platformAuthToken,
                                selectedEnvironment,
                                it,
                                "FFFFFF",
                                target,
                                customerSSOToken,
                                customerCode,
                                showHeader,
                                ::onExitSDK
                            )

                        } else {
                            Log.e("TAG", "Please enter correct platform")
                            Toast.makeText(
                                this, "Please enter correct Platform", Toast.LENGTH_SHORT
                            ).show()
                            null
                        }

                    }
                } else {
                    Log.e("TAG", "Please enter primary color")
                    Toast.makeText(this, "Please enter primary color", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        }
    }

    override fun preCreateAppAPIResponse(
        preCreateAppResponse: PreCreateAppResponse?,
        errorMsg: String?
    ) {
        TODO("Not yet implemented")
    }

}