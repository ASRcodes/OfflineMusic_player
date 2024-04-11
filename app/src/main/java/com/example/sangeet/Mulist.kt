package com.example.sangeet

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sangeet.databinding.ActivityMulistBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File

class Mulist : AppCompatActivity() {
    private lateinit var binding: ActivityMulistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMulistBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        runTimePermission()
        binding.dowload.setOnClickListener {
            startActivity(Intent(this@Mulist,dowload::class.java))
        }
    }
    fun runTimePermission()
     {
         Dexter.withActivity(this)
             .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
             .withListener(object : PermissionListener {
                 override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                     displaySongs()
                 }

                 override fun onPermissionDenied(permission: PermissionDeniedResponse?) {
                     Toast.makeText(this@Mulist, "Permission Denied!!", Toast.LENGTH_SHORT).show()
                 }

                 override fun onPermissionRationaleShouldBeShown(
                     permission : PermissionRequest?,
                     token: PermissionToken?
                 ) {
                     token?.continuePermissionRequest()
                 }
             }).check()
    }

    fun findSongs(file: File):ArrayList<File>{
        val fileList = ArrayList<File>()
        val files = file.listFiles()
        if(files!=null) {
            for (singleFiles in files) {
                if (singleFiles.isDirectory && !singleFiles.isHidden) {
                    fileList.addAll(findSongs(singleFiles))
                } else {
                    if (singleFiles.name.endsWith(".mp3") || singleFiles.name.endsWith(".wav")) {
                        fileList.add(singleFiles)
                    }
                }
            }
        }
        return fileList
    }
    fun displaySongs(){
        val mySongs:ArrayList<File> = findSongs(Environment.getExternalStorageDirectory())
        val items = arrayOfNulls<String>(mySongs.size)

        binding.musicRv.layoutManager = LinearLayoutManager(this)
        binding.musicRv.adapter = Adapter(mySongs)

//        for (i in 0 until mySongs.size){
//            items[i] = mySongs.get(i).name.toString().replace(".mp3","").replace(".wav","")
//        }
//        val arrayAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items)
//        binding.llMusic.adapter = arrayAdapter
    }
}