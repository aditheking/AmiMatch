package com.mini.amimatch

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import com.mini.amimatch.R
import com.mini.amimatch.SquareImageView
class EditProfileActivity : AppCompatActivity() {
    private lateinit var man: Button
    private lateinit var woman: Button
    private lateinit var back: ImageButton
    private lateinit var man_text: TextView
    private lateinit var women_text: TextView
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView
    private lateinit var imageView5: ImageView
    private lateinit var imageView6: ImageView
    private lateinit var imageView: ImageView
    private var myBitmap: Bitmap? = null
    private var picUri: Uri? = null
    private var permissionsRequired = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val mContext: Context = this@EditProfileActivity
    private var permissionStatus: SharedPreferences? = null
    private var sentToSettings = false
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    private val userChoosenTask: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE)
        requestMultiplePermissions()
        initializeViews()
    }

    private fun initializeViews() {
        imageView1 = findViewById(R.id.image_view_1)
        imageView2 = findViewById(R.id.image_view_2)
        imageView3 = findViewById(R.id.image_view_3)
        imageView4 = findViewById(R.id.image_view_4)
        imageView5 = findViewById(R.id.image_view_5)
        imageView6 = findViewById(R.id.image_view_6)
        man = findViewById(R.id.man_button)
        woman = findViewById(R.id.woman_button)
        man_text = findViewById(R.id.man_text)
        women_text = findViewById(R.id.woman_text)
        back = findViewById(R.id.back)
        back.setOnClickListener { onBackPressed() }
        woman.setOnClickListener {
            women_text.setTextColor(R.color.colorAccent)
            woman.setBackgroundResource(R.drawable.ic_check_select)
            man_text.setTextColor(R.color.black)
            man.setBackgroundResource(R.drawable.ic_check_unselect)
        }
        man.setOnClickListener {
            man_text.setTextColor(R.color.colorAccent)
            man.setBackgroundResource(R.drawable.ic_check_select)
            women_text.setTextColor(R.color.black)
            woman.setBackgroundResource(R.drawable.ic_check_unselect)
        }
        imageView1.setOnClickListener {
            imageView = imageView1
            proceedAfterPermission()
        }
        imageView2.setOnClickListener {
            imageView = imageView2
            proceedAfterPermission()
        }
        imageView3.setOnClickListener {
            imageView = imageView3
            proceedAfterPermission()
        }
        imageView4.setOnClickListener {
            imageView = imageView4
            proceedAfterPermission()
        }
        imageView5.setOnClickListener {
            imageView = imageView5
            proceedAfterPermission()
        }
        imageView6.setOnClickListener {
            imageView = imageView6
            proceedAfterPermission()
        }
    }

    private fun requestMultiplePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this@EditProfileActivity,
                permissionsRequired[0]
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this@EditProfileActivity,
                permissionsRequired[1]
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this@EditProfileActivity,
                permissionsRequired[2]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@EditProfileActivity,
                    permissionsRequired[0]
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this@EditProfileActivity,
                    permissionsRequired[1]
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this@EditProfileActivity,
                    permissionsRequired[2]
                )
            ) {
                //Show Information about why you need the permission
                val builder = AlertDialog.Builder(this@EditProfileActivity)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs Camera and Location permissions.")
                builder.setPositiveButton(
                    "Grant"
                ) { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(
                        this@EditProfileActivity,
                        permissionsRequired,
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, which -> dialog.cancel() }
                builder.show()
            } else if (permissionStatus!!.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(this@EditProfileActivity)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs Camera and Location permissions.")
                builder.setPositiveButton(
                    "Grant"
                ) { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(
                        "package",
                        packageName, null
                    )
                    intent.setData(uri)
                    startActivityForResult(
                        intent,
                        REQUEST_PERMISSION_SETTING
                    )
                    Toast.makeText(
                        baseContext,
                        "Go to Permissions to Grant  Camera and Location",
                        Toast.LENGTH_LONG
                    ).show()
                }
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(
                    this@EditProfileActivity,
                    permissionsRequired,
                    PERMISSION_CALLBACK_CONSTANT
                )
            }

            // txtPermissions.setText("Permissions Required");
            val editor = permissionStatus!!.edit()
            editor.putBoolean(permissionsRequired[0], true)
            editor.commit()
        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();
        }
    }

    private fun proceedAfterPermission() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@EditProfileActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                cameraIntent()
            } else if (options[item] == "Choose from Gallery") {
                galleryIntent()
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT) //
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(
                    this@EditProfileActivity,
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
                proceedAfterPermission()
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) onSelectFromGalleryResult(data) else if (requestCode == REQUEST_CAMERA) onCaptureImageResult(
                data
            )
        }
    }

    private fun onCaptureImageResult(data: Intent?) {
        val thumbnail = data!!.extras!!["data"] as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val destination = File(
            Environment.getExternalStorageDirectory(),
            System.currentTimeMillis().toString() + ".jpg"
        )
        val fo: FileOutputStream
        try {
            destination.createNewFile()
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        imageView!!.setImageBitmap(thumbnail)
    }

    @Suppress("deprecation")
    private fun onSelectFromGalleryResult(data: Intent?) {
        var bm: Bitmap? = null
        if (data != null) {
            try {
                bm =
                    MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        imageView!!.setImageBitmap(bm)
    }

    override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(
                    this@EditProfileActivity,
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
                proceedAfterPermission()
            }
        }
    }

    companion object {
        private const val TAG = "EditProfileActivity"
        private const val PERMISSION_CALLBACK_CONSTANT = 100

        //firebase
        private const val REQUEST_PERMISSION_SETTING = 101
    }
}
