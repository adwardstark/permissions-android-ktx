package com.adwardstark.permissions_ktx

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Created by Aditya Awasthi on 24/12/20.
 * @author github.com/adwardstark
 */

fun Context.hasPermission(permission: String): Boolean {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun Context.permissionSafeScope(permission: String, run: () -> Unit) {
    if(hasPermission(permission)) {
        run()
    } else {
        return
    }
}

fun Context.permissionSafeScope(permission: String, onGrant: () -> Unit, onDeny: () -> Unit) {
    if(hasPermission(permission)) {
        onGrant()
    } else {
        onDeny()
    }
}

fun ComponentActivity.permissionSafeScope(permission: String, onGrant: () -> Unit, onDeny: () -> Unit, onNeeded: () -> Unit) {
    if(hasPermission(permission)) {
        onGrant()
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(shouldShowRequestPermissionRationale(permission)){
                onNeeded()
            } else {
                onDeny()
            }
        } else {
            onDeny()
        }
    }
}

fun ComponentActivity.singlePermissionResult(onResult: (Boolean) -> Unit)
        : ActivityResultLauncher<String> {
    return registerForActivityResult(RequestPermission()) { onResult(it) }
}

fun ComponentActivity.multiPermissionResult(onResult: (Set<Map.Entry<String, Boolean>>) -> Unit)
        : ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(RequestMultiplePermissions()) { onResult(it.entries) }
}

private suspend fun ComponentActivity.requestForSinglePermissionResult(permission: String, onResult: (Boolean) -> Unit) {
    return suspendCancellableCoroutine { continuation ->
        if(!hasPermission(permission)) {
            val singleRequest = singlePermissionResult {
                onResult(it)
                continuation.resume(Unit)
            }
            continuation.invokeOnCancellation {
                singleRequest.unregister()
            }
            singleRequest.launch(permission)
        } else {
            onResult(true)
            continuation.resume(Unit)
        }
    }
}

private suspend fun ComponentActivity.requestForMultiPermissionResult(permissions: Array<String>, onResult: (Set<Map.Entry<String, Boolean>>) -> Unit) {
    return suspendCancellableCoroutine { continuation ->
        val multiRequest = multiPermissionResult {
            onResult(it)
            continuation.resume(Unit)
        }
        continuation.invokeOnCancellation {
            multiRequest.unregister()
        }
        multiRequest.launch(permissions)
    }
}

fun ComponentActivity.requestSinglePermissionOnStart(permission: String, onResult: (Boolean) -> Unit) {
    lifecycleScope.launchWhenCreated {
        requestForSinglePermissionResult(permission) { onResult(it) }
    }
}

fun ComponentActivity.requestMultiPermissionOnStart(permissions: Array<String>, onResult: (Set<Map.Entry<String, Boolean>>) -> Unit) {
    lifecycleScope.launchWhenCreated {
        requestForMultiPermissionResult(permissions) { onResult(it) }
    }
}