package com.centura.findhim

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.centura.findhim.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.findLocationBtn.setOnClickListener {
            if (binding.phoneNumberEt.text.isNotEmpty()){
                if (binding.phoneNumberEt.text.toString().length > 5
                ){
                    startActivity(Intent(this,LocationActivity::class.java))
                }else{
                    binding.phoneNumberEt.error = getString(R.string.entervalidnumber)

                }
            }else{
                binding.phoneNumberEt.error = getString(R.string.enternumber)
            }
        }

        binding.pickFromContact.setOnClickListener {
            if (hasContactsPermission()) {
                val contactPickerIntent = Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                )
                startForResult.launch(contactPickerIntent)
            } else {
                requestContactsPermission()
            }
        }




        binding.phoneNumberEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.phoneNumberEt.backgroundTintList = getColorStateList(R.color.bg_screen1)
            } else {
                binding.phoneNumberEt.backgroundTintList = getColorStateList(R.color.black)
            }
        }

    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val phonePattern = Regex("^\\+?[1-9]\\d{1,14}\$")
        return phonePattern.matches(phoneNumber)
    }


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                var cursor: Cursor? = null
                try {
                    val phoneNo: String?
                    val uri: Uri? = result.data?.data
                    cursor = contentResolver?.query(uri!!, null, null, null, null)
                    cursor?.moveToFirst()
                    val phoneIndex: Int =
                        cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    phoneNo = cursor.getString(phoneIndex)
                    binding.phoneNumberEt.setText( phoneNo)  //setting phone number in textview
                    startActivity(Intent(this,LocationActivity::class.java))


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                cursor?.close()
            }
        }





    private fun hasContactsPermission(): Boolean {
        val permission = android.Manifest.permission.READ_CONTACTS
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission() {
        val permission = android.Manifest.permission.READ_CONTACTS
        ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSIONS_REQUEST_READ_CONTACTS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val contactPickerIntent = Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                )
                startForResult.launch(contactPickerIntent)
            }
        }
    }



    companion object {
        private const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }


}