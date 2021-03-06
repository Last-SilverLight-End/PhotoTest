package com.example.phototest

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.addPhotoButton)
    }

    val startPhotoFrameModeButton: Button by lazy {
        findViewById<Button>(R.id.startPhotoFrameModeButton)
    }

    val imageViewList : List<ImageView> by lazy{
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.image1_1))
            add(findViewById(R.id.image1_2))
            add(findViewById(R.id.image1_3))
            add(findViewById(R.id.image2_1))
            add(findViewById(R.id.image2_2))
            add(findViewById(R.id.image2_3))

        }
    }

    private  val imageUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }

    private fun initAddPhotoButton(){
        addPhotoButton.setOnClickListener {
            when{
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED ->{
                    //TODO 권한이 잘 부여되었을때 갤러리에서 사진을 선택하는
                    navigatePhotos()
                }
                //activitycompat를 붙여야 오류가 안난다.
                ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //todo 권한 팝업 확인 후 띄우는 기능
                    showContextPermissionPopup()
                }
                else->{
                    ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
                }
            }
        }
    }



    private fun initStartPhotoFrameModeButton(){
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this, PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed{ index, uri ->
                intent.putExtra("photo$index",uri.toString())

            }
            intent.putExtra("photoListSize", imageUriList.size)
            startActivity(intent)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){

            1000 -> {
                // 요청 코드 승인 됫을시
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //todo 권한 부여 됫을시 쓴다.
                    navigatePhotos()
                }
                else{
                    Toast.makeText(this,"권한 요청을 거부하여 되지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // 굳이?
            }
        }

    }



    private val resultLauncher: ActivityResultLauncher<Intent>? = null




    private val filterActivityLauncher: ActivityResultLauncher<Intent> =

     registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {

        }

    private fun navigatePhotos(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        // val resultLauncher: ActivityResultLauncher<Intent>? = null
        // resultLauncher = registerForActivityResult(
        //    ActivityResultContracts.StartActivityForResult(),
        //
        // )

        //startActivityForResult 가 deprecated 가 되어 다른 걸 써야 한다.
        filterActivityLauncher.launch(intent)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?)
    {
        super.onActivityResult(requestCode,resultCode,data)
        if(resultCode != Activity.RESULT_OK){
            return
        }

        when(requestCode){
            2000 -> {
                val selectedImageUri : Uri? = data?.data

                if(selectedImageUri != null){

                    if(imageUriList.size > 6) {
                        Toast.makeText(this, " 이미 사진이 6장을 넘겼습니다", Toast.LENGTH_SHORT).show()
                        return
                    }

                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size-1].setImageURI(selectedImageUri)
                }
                else {
                    Toast.makeText(this, "자신을 가져오지 못햇습니다d", Toast.LENGTH_SHORT).show()

                }
            }
            else -> {
                Toast.makeText(this, "자신을 가져오지 못햇습니다d", Toast.LENGTH_SHORT).show()
            }

        }



    }

    private fun showContextPermissionPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 요구됩니다")
            .setMessage("사진을 불러오기 위해 권한이 필요합니다. 그래야 전자액자에 전시 가능합니다")
            .setPositiveButton("동의하기")
            { _, _ ->
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("거절하기")
            { _, _ -> }
            .create()
            .show()

    }





}