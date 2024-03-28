package com.example.testcloudstorage

import android.content.pm.PackageManager
import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storageMetadata
import java.io.File
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    val PERMISSION_REQUEST_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Dưới này thì ko cần tại vì cái này chỉ để cho phép đọc file từ external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }
    // Dưới này thì ko cần tại vì cái này chỉ để cho phép đọc file từ external storage
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with your operation
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Những hàm cần để demo
    fun readOne(){
        val storage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Points to "images"
        var imagesRef = storageRef.child("images")

        // Points to "images/chrome.png"
        val chromeRef = imagesRef.child("chrome.png")

        chromeRef.metadata
            .addOnSuccessListener {
                println("Path: ${chromeRef.path}")
                println("File: ${chromeRef.name}")
                println("Parent: ${chromeRef.parent}")
            }
            .addOnFailureListener{ exception ->
                println("Error Checking reference existence: $exception")
            }
    }
    fun readAll(){
        val storage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Points to "images"
        var imagesRef = storageRef.child("images")

        imagesRef.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items){
                    //this is a file reference
                    val fileReference = item

                    println("Path: ${fileReference.path}")
                    println("File: ${fileReference.name}")
                    println("Parent: ${fileReference.parent}")
                }
            }
            .addOnFailureListener { exception ->
                println("Error listing items: $exception")
            }
    }


    fun uploadFile(){
        val storage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Points to "images"
        var imagesRef = storageRef.child("images")

        // Change path to your file
        val pathToImage = Environment.getExternalStorageDirectory().path + "/Download/testcloudstorage/brave.png"

        // Create a reference to "brave.png"
        val file = Uri.fromFile(File(pathToImage))

        // get file_name.file_extenion
        val fileRef = imagesRef.child("${file.lastPathSegment}")

        val uploadTask = fileRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener{ exception ->
            println("Upload failed: $exception")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            println("Path: ${taskSnapshot.metadata?.path}")
            println("File: ${taskSnapshot.metadata?.name}")
        }

    }
    fun deleteFile(){
        val storage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Points to "images"
        var imagesRef = storageRef.child("images")

        // Create a reference to the file to delete
        val firefoxRef = imagesRef.child("firefox.png")

        // Delete the file
        firefoxRef.delete().addOnSuccessListener {
            println("Delete successfully")
        }.addOnFailureListener{ exception ->
            println("Delete failed $exception")
        }
    }
    fun updateFile(){
        val storage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Points to "images"
        var imagesRef = storageRef.child("images")

        // Create a reference to the file to change
        val edgeRef = imagesRef.child("edge.png")

        val metadata = storageMetadata {
            contentType = "image/jpg"
            setCustomMetadata("myCustomProperty", "myValue")
        }

        edgeRef.updateMetadata(metadata)
            .addOnSuccessListener {
                println("Update successfully")
            }
            .addOnFailureListener{ exception ->
                println("Update fail: $exception")
            }

    }
    fun downloadFile(){
        val imagePlaceholder = findViewById<ImageView>(R.id.imagePlaceholder)
        val storage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Points to "images"
        var imagesRef = storageRef.child("images")

        // Create a reference to the file to change
        val chromeRef = imagesRef.child("chrome.png")

        val localFile = File.createTempFile("chrome", "png")

        chromeRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            imagePlaceholder.setImageBitmap(bitmap)
        }.addOnFailureListener { exception ->
            println("Download failed: ${exception}")
        }
    }
}