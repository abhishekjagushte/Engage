package com.abhishekjagushte.engage.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.abhishekjagushte.engage.utils.Constants

class WriteExternalStoragePermissionHelper(
    private val activity: Activity,
    private val context: Context,
    private val requestCode:Int
){

    fun permissionsForSave(): Boolean {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(context ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                Toast.makeText(activity,"ask", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),Constants.WRITE_PERMISSION_REQUEST_CODE)
            }
            else{
                return true
            }
        }
        else{
            return true
        }

        return false
    }
}