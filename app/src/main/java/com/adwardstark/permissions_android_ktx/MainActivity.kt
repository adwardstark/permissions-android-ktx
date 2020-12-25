package com.adwardstark.permissions_android_ktx

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.adwardstark.permissions_android_ktx.databinding.ActivityMainBinding
import com.adwardstark.permissions_ktx.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var viewBinder: ActivityMainBinding

    private val listOfPermissions: Array<String>
            = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE
            )

    private val singlePermissionRequest
            = singlePermissionResult { isGranted ->
                if(isGranted) {
                    Log.i(TAG, "->singlePermissionResult() Permission-granted")
                    showToast("Permission-granted")
                } else {
                    Log.i(TAG,"->singlePermissionResult() Permission-denied")
                    showToast("Permission-denied")
                }
            }

    private val multiPermissionRequest
            = multiPermissionResult { permissions ->
                permissions.forEach { permission ->
                    Log.i(TAG, "${permission.key} is ${permission.value}")
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinder.root)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        init()
    }

    override fun onStart() {
        super.onStart()
        requestSinglePermissionOnStart(listOfPermissions[0]) { isGranted ->
            if(isGranted) {
                Log.i(TAG, "->requestSinglePermissionOnStart() Permission-granted")
            } else {
                Log.i(TAG,"->requestSinglePermissionOnStart() Permission-denied")
            }
        }
    }

    private fun init() {
        runWithScopes()

        viewBinder.locationButton.setOnClickListener {
            singlePermissionRequest.launch(listOfPermissions[0])
        }

        viewBinder.cameraButton.setOnClickListener {
            singlePermissionRequest.launch(listOfPermissions[1])
        }

        viewBinder.phoneButton.setOnClickListener {
            singlePermissionRequest.launch(listOfPermissions[2])
        }

        viewBinder.allButton.setOnClickListener {
            multiPermissionRequest.launch(listOfPermissions)
        }
    }

    private fun runWithScopes() {
        permissionSafeScope(listOfPermissions[0]) {
            // This block will only run if the given permission is already granted
            Log.i(TAG, "->permissionSafeScope() Executed-successfully")
        }

        permissionSafeScope(listOfPermissions[1],
            onGrant = {
                // This block will only run if the given permission is already granted
                Log.i(TAG, "->permissionSafeScope() onGrant() Executed-successfully")
            },
            onDeny = {
                // This block will only run if the given permission is already denied
                Log.i(TAG, "->permissionSafeScope() onDeny() Executed-successfully")
            })

        permissionSafeScope(listOfPermissions[2],
            onGrant = {
                // This block will only run if the given permission is already granted
                Log.i(TAG, "->permissionSafeScope() onGrant() Executed-successfully")
            },
            onDeny = {
                // This block will only run if the given permission is already denied
                Log.i(TAG, "->permissionSafeScope() onDeny() Executed-successfully")
            },
            onNeeded = {
                // This block will only run if the given permission need
                // an additional rational to be shown to the user
                Log.i(TAG, "->permissionSafeScope() onNeeded() Executed-successfully")
            })
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}