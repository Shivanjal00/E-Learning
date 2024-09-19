package com.app.tensquare.ui.profile

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.app.tensquare.BuildConfig
import com.app.tensquare.R
import com.app.tensquare.base.AppBaseActivity
import com.app.tensquare.databinding.ActivityEditProfileBinding
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.chapter.ChaptersInSubjectActivity
import com.app.tensquare.ui.home.HomeActivity
import com.app.tensquare.ui.home.HomeViewModel
import com.app.tensquare.ui.initialsetup.ClassData
import com.app.tensquare.ui.initialsetup.InitialViewModel
import com.app.tensquare.ui.initialsetup.Language
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.android.synthetic.main.activity_edit_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import pl.aprilapps.easyphotopicker.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class EditProfileActivity : AppBaseActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val viewModel1: InitialViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var langList: List<Language>
    private lateinit var classList: ArrayList<ClassData>
    private var stateList: ArrayList<State> = ArrayList<State>()

    private var profileDetail: JSONObject? = null

    lateinit var currentPhotoPath: String
    private var file: File? = null
    private var mimeType: String? = null
    private var languageName: String = ""

    //private lateinit var easyImage: EasyImage

    companion object {
        const val REQUEST_CODE_TAKE_PICTURE: Int = 0x2
        const val REQUEST_CODE_GALLERY: Int = 1000
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1001
        val PERMISSIONS = arrayOf<String>(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        const val PERMISSION_ALL: Int = 1

        const val PERMISSION_REQUEST_CODE = 200
        lateinit var photoURI: Uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        //iniEasyImage()
        setListeners()
        initObservers()
    }

    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            // Permission is not granted
            false
        } else true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun init() {
        val data = intent.getStringExtra("profile_detail")
        if (data != null && !data.contains("null")) {
            profileDetail = JSONObject(data)
        }
        try {
            profileDetail?.let {
                languageName = it.getString("languageName")
                binding.edtName.setText(it.getString("name"))
                binding.edtMobile.setText(it.getString("mobile"))
                binding.txtClass.text = it.getString("className")
                binding.txtLanguage.text = it.getString("languageName")
                binding.edtEmail.setText(it.getString("email"))
                binding.edtSchoolName.setText(it.getString("schoolName"))
                binding.txtState.text = it.getString("stateName")
                binding.edtDistrict.setText(it.getString("district"))
                if (it.getString("profilePic").isNotEmpty()) {
                    Glide.with(this)
                        .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                        .load(it.getString("profilePic"))
                        .skipMemoryCache(true) //2
                        .diskCacheStrategy(DiskCacheStrategy.ALL) //3
                        .into(binding.imgProfile)
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        showProgressDialog()
        viewModel1.getLanguageList()
        //profileDetail?.let { viewModel1.getClassList(it.getString("languageId")) }
        profileDetail?.let { viewModel.getStateList(it.getString("languageId")) }
    }

    override fun setListsAndAdapters() {

    }

    override fun setListeners() {
        binding.imgBack.setOnClickListener { finish() }

        binding.flLanguage.setOnClickListener {
            binding.spnLang.performClick()
        }
        binding.flClass.setOnClickListener {
            binding.spnClass.performClick()
        }
        binding.flState.setOnClickListener {
            binding.spnState.performClick()
        }
        binding.imgChangeProfile.setOnClickListener {
            if (!checkPermission()) {
                requestPermission()
            }else {
                showAlertDialog()
            }
        }

        binding.txtSave.setOnClickListener {
            if (!isValid())
                return@setOnClickListener
            createMultiPartRequestBundle(/*it, mimeType*/)

        }

        binding.spnLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                //selectedCity = parent.adapter.getItem(position) as City
                binding.txtLanguage.text = parent.adapter.getItem(position) as String

                for (i in langList.indices) {
                    if (langList[i].name.uppercase() == parent.adapter.getItem(position).toString()
                            .uppercase()
                    ) {
                        viewModel1.getClassList(langList[i]._id)
                        break
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spnClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                //selectedCity = parent.adapter.getItem(position) as City
                binding.txtClass.text = parent.adapter.getItem(position) as String

                Log.e(
                    "Selected class3", "${binding.spnClass.selectedItem}"
                )

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spnState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
//                selectedCity = parent.adapter.getItem(position) as City
                binding.txtState.text = parent.adapter.getItem(position) as String

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun isValid(): Boolean {
        var isValid = true
        if (binding.spnLang.selectedItemPosition == 0) {
            isValid = false
            showToast(getString(com.app.tensquare.R.string.select_a_language))
        } else if (binding.spnClass.selectedItemPosition == 0) {
            isValid = false
            showToast(getString(com.app.tensquare.R.string.select_a_class))
        } /*else if (binding.spnState.selectedItemPosition == 0) {
            isValid = false
            showToast("Select a state")
        }*/
        return isValid
    }

    override fun initObservers() {
        homeViewModel.refreshTokenResponse.observe(this){
            when (it){
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    dismissProgressDialog()
                    it.data?.let {data ->

                        Log.e("token_Home = >" , data.accessToken.toString())
                        prefs.setUserToken(data.accessToken)
                        prefs.setRefreshToken(data.refreshToken)
                        showProgressDialog()
                        viewModel1.getLanguageList()
                        //profileDetail?.let { viewModel1.getClassList(it.getString("languageId")) }
                        profileDetail?.let { viewModel.getStateList(it.getString("languageId")) }
                    }
                }
                is NetworkResult.Error -> {
                    Log.e("PrintProfileResponse","edit profile success ${it.message}")
                    dismissProgressDialog()
                    if (it.message == REFRESH_TOKEN_EXPIRED ) {
//                            requireActivity().showToast("2")
                        Intent(this, LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        finish()
                    }else if (it.message == OTHER_DEVISE_LOGIN) {

                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    } else showToast(it.message)

//                            requireActivity().showToast("3")

                }
            }
        }

        viewModel1.languageListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    //if (response.data?.status == STATUS_SUCCESS) {
                    langList = response.data!!
                    val langNameList = ArrayList<String>()
                    langNameList.add("Select Language")
                    for (item in langList.map { it.name }) {
                        langNameList.add(item)
                    }

                    val arrayAdapter = ArrayAdapter(
                        this@EditProfileActivity,
                        android.R.layout.simple_list_item_1, langNameList
                    )
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spnLang.adapter = arrayAdapter


                    var langIndex = 0
                    for (i in langList.indices) {
                        if (langList[i].name == binding.txtLanguage.text.toString()) {
                            langIndex = i
                            break
                        }
                    }

                    binding.spnLang.setSelection(langIndex + 1)
                }
                is NetworkResult.Error -> {
                    when (response.message) {
                        ACCESS_TOKEN_EXPIRED -> {
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken(prefs.getRefreshToken().toString())
                            homeViewModel.getRefreshToken(prefs.getRefreshToken().toString())
//                            showMessage("1")
                        }
                        REFRESH_TOKEN_EXPIRED -> {
//                           Intent(requireActivity(), LoginActivity::class.java).also {
//                                startActivity(it)
//                            }
//                            requireActivity().finish()
//                            generateNewToken()    // JACK
                        }
                        OTHER_DEVISE_LOGIN ->{
                            val intent =  Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()
                        }
                        else->{
//                            requireActivity().showToast(response.message)
//                            prefs.setUserToken(prefs.getRefreshToken())
//                            viewModel.getRefreshToken()
//                            viewModel.getRefreshToken(prefs.getUserToken().toString())
//                            generateNewToken()
//                            showMessage("2")
                        }

                    }
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel1.classListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    //if (response.data?.status == STATUS_SUCCESS) {
                    classList = (response.data?.classList as ArrayList<ClassData>?)!!

                    //first release temporary
//                    val intermediate = classList.find { it.name.uppercase() == "12TH" }
//                    classList.remove(intermediate)

                    val classNameList = ArrayList<String>()
                    classNameList.add("Select Class")
                    for (item in classList.map { it.name }) {
                        classNameList.add(item)
                    }

                    Log.e(
                        "ClassId###",
                        profileDetail?.let { it1 -> it1.getString("classId") }.toString()
                    )

                    val arrayAdapter = ArrayAdapter(
                        this@EditProfileActivity,
                        android.R.layout.simple_list_item_1, classNameList
                    )
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spnClass.adapter = arrayAdapter

                    var classIndex = 0
                    for (i in 0 until classList.size) {
                        if (classList[i].name.trim() == binding.txtClass.text.toString().trim()) {
                            classIndex = i
                            break
                        }
                    }

                    binding.spnClass.setSelection(classIndex + 1)
                }
                is NetworkResult.Error -> {
                    if (response.message != OTHER_DEVISE_LOGIN) {
                        val intent =  Intent(this, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        sendBroadcast(broadcast)
                        startActivity(intent)
                        finish()
                    }
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }

        viewModel.stateListResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        val data = response.data
                        stateList.add(State(_id = "", name = getString(R.string.state_optional)))
                        stateList.addAll(data.data)

                        val stateNameList = ArrayList<String>()
                        //stateNameList.add("State (Optional)")
                        for (item in stateList.map { it.name }) {
                            stateNameList.add(item)
                        }

                        val arrayAdapter = ArrayAdapter(
                            this@EditProfileActivity,
                            android.R.layout.simple_list_item_1, stateNameList
                        )
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spnState.adapter = arrayAdapter

                        binding.spnState.setSelection(stateList.indexOf(stateList.find {
                            it._id == profileDetail?.let { it1 -> it1.getString("stateId") }
                        }))

                    } else {

                        if (response.data?.message == OTHER_DEVISE_LOGIN) {

                            val intent =  Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()

                        }else{
                            showToast(response.data?.message)
                        }

                    }
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
        viewModel.saveProfileDetailResponse.observe(this) { response ->
            dismissProgressDialog()
            when (response) {
                is NetworkResult.Success -> {
                    if (response.data?.status == STATUS_SUCCESS) {
                        val data = response.data

                        prefs.setSelectedLanguageId(langList.first {
                            it.name.uppercase() == binding.txtLanguage.text.toString().uppercase()
                        }._id)

                        prefs.setSelectedClassId(classList.first {
                            it.name.uppercase() == binding.txtClass.text.toString().uppercase()
                        }._id)

                        prefs.setSelectedClassName(classList.first {
                            it.name.uppercase() == binding.txtClass.text.toString().uppercase()
                        }.name)

                        showToast(data.message)

                        if (languageName != binding.spnLang.selectedItem.toString()) {
                            if (binding.txtLanguage.text.toString().uppercase() == "HINDI") {
                                prefs.setUserLanguage("hi")
                                prefs.setSelectedLanguagenName("Hindi")
                            } else {
                                prefs.setUserLanguage("en")
                                prefs.setSelectedLanguagenName("English")
                            }

                            //                            prefs.setSelectedClassName(getString(R.string.tenth))
                            //                            showToast(prefs.getSelectedLanguageName() + "\n" + getString(R.string.tenth))

                            Intent(this@EditProfileActivity, HomeActivity::class.java).also {
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(it)
                            }
                            //                            onBackPressed()
                        } else {
                            Intent().also {
                                it.putExtra(
                                    "isSomethingChanged",
                                    profileDetail!!.getString("name") != binding.edtName.text.toString()
                                )
                                setResult(RESULT_OK, it)
                            }
                            finish()
                        }
                    } else {
                        if (response.data?.message == OTHER_DEVISE_LOGIN) {

                            val intent =  Intent(this, LoginActivity::class.java)
                            intent.putExtra("Expired", "Expired")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val broadcast = Intent(LOG_OUT)
                            sendBroadcast(broadcast)
                            startActivity(intent)
                            finish()

                        }else{
                            showToast(response.data?.message)
                        }

                    }
                }
                is NetworkResult.Error -> {
                    showToast(response.message)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////
    private fun showAlertDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(com.app.tensquare.R.string.choose_action))
        val items = arrayOf<CharSequence>(getString(com.app.tensquare.R.string.take_photo), getString(com.app.tensquare.R.string.choose_from_library), getString(com.app.tensquare.R.string.cancel))
        builder.setItems(items, DialogInterface.OnClickListener { dialog, which ->
            if (items[which] == getString(com.app.tensquare.R.string.take_photo)) {
                dialog.dismiss()
                //easyImage.openCameraForImage(this@EditProfileActivity)
                dispatchTakePictureIntent()
            } else if (items[which] == getString(com.app.tensquare.R.string.choose_from_library)) {
                dialog.dismiss()
                pickImageFromGallery()
            } else
                dialog.dismiss()
        })

        val alertDialog = builder.create()
        alertDialog?.show()


    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // The image was saved into the given Uri -> do something with it

                try {
                    mimeType = contentResolver.getType(photoURI)
                    file = mimeType?.let { getFile(it, photoURI) }
                    Glide.with(binding.imgProfile.context)
                        .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                        .load(file)
                        .error(com.google.android.material.R.drawable.mtrl_ic_error)
                        .skipMemoryCache(true) //2
                        .diskCacheStrategy(DiskCacheStrategy.ALL) //3
                        .into(binding.imgProfile)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    private fun dispatchTakePictureIntent() {
        val photoFile: File = createImageFile()
        photoFile.also {
            // You must set up file provider to expose the url to Camera app
            photoURI = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }

    //create image file from camera
    @Throws(IOException::class)
    private fun createImageFile(): File {

        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        return File.createTempFile(
            "tensquare_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }

    }

    private fun pickImageFromGallery() {

        //CropImage.activity().start(this@EditProfileActivity)

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        galleryIntent.launch(pickIntent)

    }

/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        */
/*if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                val resultUri = result.uri
                Picasso.with(this).load(resultUri).into(binding.imgProfile)
            }
        }*//*


        easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            (context as Activity?)!!,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    file = imageFiles[0].file
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    //Some error handling
                    error.printStackTrace()
                }

                override fun onCanceled(source: MediaSource) {
                    //Not necessary to remove any files manually anymore
                }
            })
    }
*/

    private val galleryIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {

                mimeType = contentResolver.getType(result.data?.data!!)
                file = mimeType?.let { getFile(it, result.data?.data!!) }

                Glide.with(this)
                    .setDefaultRequestOptions(RequestOptions().priority(Priority.HIGH))
                    .load(file)
                    .skipMemoryCache(true) //2
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //3
                    .into(binding.imgProfile)
                /* val icon = BitmapFactory.decodeResource(
                     context.resources,
                     R.drawable.ic_document1
                 )

                 binding.imgProfilePic.setImageBitmap(icon)*/

            }
        }

    private fun createMultiPartRequestBundle(/*file: File, mimeType: String*/) {
        /* file?.let {*/
        val multiPartbuilder = MultipartBody.Builder()
        try {
            multiPartbuilder.setType(MultipartBody.FORM)
            multiPartbuilder.addFormDataPart("name", binding.edtName.text.toString())
            multiPartbuilder.addFormDataPart("email", binding.edtEmail.text.toString())

            for (i in classList.indices) {
                if (classList[i].name == binding.txtClass.text.toString()) {
                    multiPartbuilder.addFormDataPart(
                        "classId",
                        classList[i]._id
                    )
                    break
                }
            }


            for (i in langList.indices) {
                if (langList[i].name == binding.txtLanguage.text.toString())
                    multiPartbuilder.addFormDataPart(
                        "languageId",
                        langList[i]._id
                    )
            }

            file?.let {
                multiPartbuilder.addFormDataPart(
                    "userProfile",
                    file!!.name,
                    file!!.asRequestBody(mimeType?.toMediaTypeOrNull())
                )
            }
            stateList.find { it.name == binding.txtState.text.toString() }?.let { it1 ->
                multiPartbuilder.addFormDataPart(
                    "stateId",
                    it1._id
                )
            }
            multiPartbuilder.addFormDataPart(
                "stateName",
                if (binding.spnState.selectedItemPosition != 0) binding.spnState.selectedItem.toString() else ""
            )
            multiPartbuilder.addFormDataPart("district", binding.edtDistrict.text.toString())
            multiPartbuilder.addFormDataPart(
                "schoolName",
                binding.edtSchoolName.text.toString()
            )
            multiPartbuilder.addFormDataPart(
                "languageName", binding.spnLang.selectedItem.toString()
            )
            multiPartbuilder.addFormDataPart(
                "className", binding.spnClass.selectedItem.toString()
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }

        val multipart: MultipartBody? = multiPartbuilder.build()
        if (multipart != null) {
            showProgressDialog()
            viewModel.updateProfileDetails(multipart)
            //}
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                applicationContext.showToast(getString(R.string.permission_granted))

                // main logic
            } else {
                applicationContext.showToast(getString(R.string.permission_denied))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel(
                            "You need to allow access permissions"
                        ) { _, _ ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this@EditProfileActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

   /* private fun iniEasyImage() {
        easyImage = EasyImage.Builder(this@EditProfileActivity)
            .setChooserTitle("Pick media")
            .setCopyImagesToPublicGalleryFolder(false) //
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .setFolderName("Elearning")
            .allowMultiple(false)
            .build()

    }*/

}