package com.example.povilika

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.EditText
import android.widget.LinearLayout
import android.view.ViewGroup


class EditReportActivity : AppCompatActivity() {

    private lateinit var editActivityTypeSpinner: Spinner
    private lateinit var editWorkTypeSpinner: Spinner
    private lateinit var editWorkPlaceSpinner: Spinner
    private lateinit var editCultureSpinner: Spinner
    private lateinit var editDevelopmentStageSpinner: Spinner
    private lateinit var editRegionSpinner: Spinner
    private lateinit var editDistrictSpinner: Spinner
    private lateinit var editDepartmentSpinner: Spinner
    private lateinit var editExecutorSpinner: Spinner
    private lateinit var editAreaEditText: EditText
    private lateinit var editFarmNameEditText: EditText
    private lateinit var editLatitudeEditText: EditText
    private lateinit var editLongitudeEditText: EditText
    private lateinit var editDescriptionEditText: EditText
    private lateinit var dynamicFieldsLayout: LinearLayout
    private lateinit var photosLayout: LinearLayout
    private lateinit var buttonAddPhoto: Button
    private lateinit var buttonSaveChanges: Button

    private var report: Report? = null
    private var xmlFilePath: String = ""
    private val REQUEST_IMAGE_CAPTURE = 1001
    private val REQUEST_IMAGE_PICK = 1003
    private val REQUEST_PERMISSIONS_CODE = 1004

    private val newPhotos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_report)

        photosLayout = findViewById(R.id.photosLayout)

        xmlFilePath = intent.getStringExtra("xmlFilePath") ?: ""
        if (xmlFilePath.isNotEmpty()) {
            report = ReportUtils.parseXmlReport(xmlFilePath)
            report?.xmlFilePath = xmlFilePath
            setupSpinners()
            report?.let { populateFields(it) }
        } else {
            Toast.makeText(this, "Путь к файлу отчёта не передан.", Toast.LENGTH_SHORT).show()
            finish()
        }

        buttonSaveChanges = findViewById(R.id.buttonSaveChanges)
        buttonSaveChanges.setOnClickListener {
            report?.let { saveReportToXml(it) }
        }

        buttonAddPhoto = findViewById(R.id.buttonAddPhoto)
        buttonAddPhoto.setOnClickListener {
            showImageSourceDialog()
        }
    }



    private fun setupSpinners() {
        editActivityTypeSpinner = findViewById(R.id.editActivityTypeSpinner)
        editWorkTypeSpinner = findViewById(R.id.editWorkTypeSpinner)
        editWorkPlaceSpinner = findViewById(R.id.editWorkPlaceSpinner)
        editCultureSpinner = findViewById(R.id.editCultureSpinner)
        editDevelopmentStageSpinner = findViewById(R.id.editDevelopmentStageSpinner)
        editRegionSpinner = findViewById(R.id.editRegionSpinner)
        editDistrictSpinner = findViewById(R.id.editDistrictSpinner)
        editExecutorSpinner = findViewById(R.id.editExecutorSpinner)
        editCultureSpinner = findViewById(R.id.editCultureSpinner)
        editDepartmentSpinner = findViewById(R.id.editDepartmentSpinner)


        val activityTypes = listOf("Энтомолог", "Фитопатолог", "Герболог", "Пилот БПЛА")
        val workTypes = listOf("Первичный мониторинг", "Проведение осмотра Вредных Организмов", "Проведение обработки", "Саранча")
        val workPlaces = listOf("Поле", "Сад", "Теплица", "Лесопосадка")
        val culturesField = listOf(
            "Пшеница",
            "Ячмень",
            "Кукуруза",
            "Подсолнечник",
            "Соя",
            "Рапс",
            "Лён масличный",
            "Гречиха",
            "Просо",
            "Нут",
            "Горох",
            "Картофель",
            "Сахарная свёкла",
            "Хлопчатник",
            "Рис",
            "Люцерна",
            "Клевер",
            "Капуста",
            "Лук",
            "Свекла",
            "Томат"
        )
        val culturesGarden = listOf(
            "Яблоня",
            "Груша",
            "Абрикос",
            "Вишня",
            "Черешня",
            "Слива",
            "Персик",
            "Айва",
            "Орех (грецкий)",
            "Виноград",
            "Малина",
            "Смородина",
            "Жимолость",
            "Шелковица"
        )
        val culturesGreenhouse = listOf(
            "Томат",
            "Огурец",
            "Перец сладкий",
            "Баклажан",
            "Салат",
            "Редис",
            "Зелень (укроп, петрушка, кинза)",
            "Базилик",
            "Клубника",
            "Шпинат",
            "Руккола",
            "Микрозелень",
            "Лук")
        val developmentStages = listOf(
            "Прорастание",
            "Всходы",
            "Кущение/Розетка",
            "Интенсивный рост",
            "Бутонизация",
            "Цветение",
            "Завязь/Плодообразование",
            "Созревание",
            "Полная спелость",
            "Отмирание")
        val regions = ReportUtils.getRegions()
        val executors = mapOf(
            "Руководство" to listOf(
                "Дуйсембеков Б.А.",
                "Успанов А.М.",
                "Султанова Н.Ж."
            ),
            "Научно-аналитическая группа" to listOf(
                "Мухаметкаримов К.",
                "Ажбенов В.К.",
                "Арыстангулов С.С.",
                "Джаймурзина А.А.",
                "Шанимов Х.И.",
                "Макаров Е.М.",
                "Копжасаров Б.К."
            ),
            "Отдел Биологического Метода Защиты Растений" to listOf(
                "Мухамадиев Н.С.",
                "Әділханқызы А.",
                "Баймагамбетов Е.Ж.",
                "Балабек А.Н.",
                "Тлеубергенов Х.М.",
                "Әлішер Б.Б.",
                "Қасымов А.А.",
                "Шакирова Ә.Е.",
                "Шисенбаева Н.Ж.",
                "Алпысбаева К.А.",
                "Шарипова Д.С.",
                "Найманова Б.Ж.",
                "Джубатова Э.А.",
                "Нурбаева Э.А.",
                "Нурманов Б.Б.",
                "Сейтжан Ә.М.",
                "Кенжегалиева Ж.З.",
                "Ағабек А.Б.",
                "Әуелбек Б.М.",
                "Тұрысбек А.Т.",
                "Меңдібаева Г.Ж.",
                "Курмангалиева Н.Д.",
                "Дәулеткелді Е.",
                "Кеңес Н.Т.",
                "Шакеров А.С."
            ),
            "Отдел Карантина Растений" to listOf(
                "Исина Ж.М.",
                "Динасилов А.С.",
                "Темрешев И.И.",
                "Жумагалиев А.К."
            ),
            "Отдел Интегрированной Защиты Растений" to listOf(
                "Ниязбеков Ж.Б.",
                "Бекназарова З.Б.",
                "Абдиева К.М.",
                "Сарбасова А.М.",
                "Калдыбекқызы Г.",
                "Калдыбек Д.Е.",
                "Кошмагамбетова М.Ж.",
                "Бактиярова Н.",
                "Мәтен Т.Е.",
                "Дуйсембеков О.А.",
                "Койгельдина А.Е.",
                "Болтаева Л.А.",
                "Есжанов Т.К.",
                "Ертаева Б.А.",
                "Айтбаева Б.У.",
                "Кожабаева Г.Е.",
                "Копирова Г.И.",
                "Тусупбаев К.Б.",
                "Джумахан Д.М.",
                "Мусина Қ.М.",
                "Усембаева Ж.С.",
                "Касембаева Н.К.",
                "Ермекбаев Б.У.",
                "Болтаев М.Д.",
                "Бейсекина Б.М.",
                "Басымбеков Н.Ш."
            ),
            "Отдел Регистрации Пестицидов" to listOf(
                "Есимов У.О.",
                "Бекежанова М.М.",
                "Башкараев Н.А.",
                "Нұрманов Ж.Ғ.",
                "Нысанбаев С.Н.",
                "Еспембетов Б.С."
            ),
            "ИЦФЛА" to listOf(
                "Исенова Г.Ж.",
                "Отжагарова Г.С.",
                "Умиралиева Ж.З.",
                "Сардар А.А.",
                "Еркін А.Н.",
                "Расулбекқызы Х.",
                "Жамалбекова А.А.",
                "Әшірбекова А.А.",
                "Мәулен А.Т."
            ),
            "Отдел Внедрения и Коммерциализации Технологий" to listOf(
                "Рысбекова А.М.",
                "Фазылбеков Р.Р.",
                "Никоноров А.П.",
                "Тайшиков М.А.",
                "Усманов У.Т.",
                "Жорахан Б.Б."
            ),
            "ЦРЗ" to listOf("Чадинова А.М."),
            "Костанайский филиал" to listOf(
                "Алиев О.Т.",
                "Ковалёва Е.В.",
                "Кабделов Н.Н.",
                "Контрабаева Ж.Д.",
                "Ааб Е.М.",
                "Шиллер А.В.",
                "Колбаев Д.Т.",
                "Хасенов Д.К.",
                "Карсакбаева С.М.",
                "Перегудов Ю.П."
            ),
            "Южно-Казахстанский филиал" to listOf(
                "Туреханов Б.Т.",
                "Жүзбаев М.Ж.",
                "Алпысбаев А.С.",
                "Мақсұмова Д.Б.",
                "Утегенов А.Т.",
                "Оспанова Ұ.О.",
                "Елшібек А.",
                "Райымбекова Ж.Н.",
                "Налибаева С.К.",
                "Шылмырзаев Б.А.",
                "Сатыбалды С.",
                "Батанов Т.У.",
                "Маралбаев Б.А.",
                "Зулпыхаров С.А."
            )
        )
        val departments = executors.keys.toList()
        setupSpinner(editDepartmentSpinner, departments, report?.department ?: "")


        setupSpinner(editActivityTypeSpinner, activityTypes, report?.activityType ?: "")
        setupSpinner(editWorkTypeSpinner, workTypes, report?.workType ?: "")
        setupSpinner(editWorkPlaceSpinner, workPlaces, report?.workPlace ?: "")
        setupSpinner(editDevelopmentStageSpinner, developmentStages, report?.developmentStage ?: "")
        setupSpinner(editRegionSpinner, regions, report?.region ?: "")
        val allExecutors = executors.values.flatten()
        setupSpinner(editExecutorSpinner, allExecutors, report?.executor ?: "")


        updateCultureSpinner(report?.workPlace ?: "")
        updateDistrictSpinner(report?.region ?: "")

        editWorkPlaceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCultureSpinner(editWorkPlaceSpinner.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        editRegionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateDistrictSpinner(editRegionSpinner.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun saveReportToXml(report: Report) {
        val file = File(report.xmlFilePath)
        if (file.exists()) {
            report.activityType = editActivityTypeSpinner.selectedItem?.toString() ?: ""
            report.workType = editWorkTypeSpinner.selectedItem?.toString() ?: ""
            report.workPlace = editWorkPlaceSpinner.selectedItem?.toString() ?: ""
            report.culture = editCultureSpinner.selectedItem?.toString() ?: ""
            report.developmentStage = editDevelopmentStageSpinner.selectedItem?.toString() ?: ""
            report.region = editRegionSpinner.selectedItem?.toString() ?: ""
            report.district = editDistrictSpinner.selectedItem?.toString() ?: ""
            report.department = editDepartmentSpinner.selectedItem?.toString() ?: ""
            report.executor = editExecutorSpinner.selectedItem?.toString() ?: ""
            report.area = editAreaEditText.text.toString().toDoubleOrNull() ?: 0.0
            report.farmName = editFarmNameEditText.text.toString()
            report.latitude = editLatitudeEditText.text.toString().toDoubleOrNull() ?: 0.0
            report.longitude = editLongitudeEditText.text.toString().toDoubleOrNull() ?: 0.0
            report.description = editDescriptionEditText.text.toString()
            report.dynamicFieldsData.clear()
            for (i in 0 until dynamicFieldsLayout.childCount step 2) {
                val keyView = dynamicFieldsLayout.getChildAt(i) as? TextView
                val valueView = dynamicFieldsLayout.getChildAt(i + 1) as? EditText
                if (keyView != null && valueView != null) {
                    report.dynamicFieldsData[keyView.text.toString()] = valueView.text.toString()
                }
            }

            val updatedPhotos = (report.photos + newPhotos).toMutableList()
            report.photos = updatedPhotos

            ReportUtils.saveReport(report, file)
            Toast.makeText(this, "Отчёт сохранён", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Файл отчёта не найден", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf<CharSequence>("Сделать фото", "Выбрать из галереи", "Отмена")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Добавить фото")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Сделать фото" -> {
                    openCamera()
                }
                options[item] == "Выбрать из галереи" -> {
                    openGallery()
                }
                options[item] == "Отмена" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        saveImageToStorage(it)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        saveImageUriToStorage(it)
                    }
                }
            }
        }
    }

    private fun saveImageToStorage(bitmap: Bitmap) {
        val appDir = File(getExternalFilesDir(null), "ReportsApp")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val filename = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(appDir, filename)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        newPhotos.add(file.absolutePath)
        displayPhotos(report?.photos.orEmpty() + newPhotos)
    }

    private fun saveImageUriToStorage(imageUri: Uri) {
        val appDir = File(getExternalFilesDir(null), "ReportsApp")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val filename = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(appDir, filename)

        try {
            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            newPhotos.add(file.absolutePath)
            displayPhotos(report?.photos.orEmpty() + newPhotos)
            Toast.makeText(this, "Фото добавлено из галереи", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при добавлении фото", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayPhotos(photos: List<String>) {
        photosLayout.removeAllViews()
        for (photoPath in photos) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            Glide.with(this)
                .load(photoPath)
                .apply(RequestOptions().placeholder(R.drawable.placeholder_image).centerCrop())
                .into(imageView)

            photosLayout.addView(imageView)
        }
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>, selectedValue: String) {
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as? TextView)?.setTextColor(Color.BLACK)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as? TextView)?.apply {
                    setTextColor(Color.BLACK)
                    setBackgroundColor(Color.WHITE)
                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(items.indexOf(selectedValue))
    }


    private fun updateCultureSpinner(workPlace: String) {
        val cultures = when (workPlace) {
            "Поле" -> listOf("Пшеница",
                "Ячмень",
                "Кукуруза",
                "Подсолнечник",
                "Соя",
                "Рапс",
                "Лён масличный",
                "Гречиха",
                "Просо",
                "Нут",
                "Горох",
                "Картофель",
                "Сахарная свёкла",
                "Хлопчатник",
                "Рис",
                "Люцерна",
                "Клевер",
                "Капуста",
                "Лук",
                "Свекла",
                "Томат")
            "Сад" -> listOf("Яблоня",
                "Груша",
                "Абрикос",
                "Вишня",
                "Черешня",
                "Слива",
                "Персик",
                "Айва",
                "Орех (грецкий)",
                "Виноград",
                "Малина",
                "Смородина",
                "Жимолость",
                "Шелковица")
            "Теплица" -> listOf("Томат",
                "Огурец",
                "Перец сладкий",
                "Баклажан",
                "Салат",
                "Редис",
                "Зелень (укроп, петрушка, кинза)",
                "Базилик",
                "Клубника",
                "Шпинат",
                "Руккола",
                "Микрозелень",
                "Лук")
            else -> listOf()
        }
        setupSpinner(editCultureSpinner, cultures, report?.culture ?: "")
    }

    private fun updateDistrictSpinner(region: String) {
        val districts = ReportUtils.getDistrictsForRegion(region)
        setupSpinner(editDistrictSpinner, districts, report?.district ?: "")
    }

    private fun populateFields(report: Report) {
        editAreaEditText = findViewById(R.id.editAreaEditText)
        editFarmNameEditText = findViewById(R.id.editFarmNameEditText)
        editLatitudeEditText = findViewById(R.id.editLatitudeEditText)
        editLongitudeEditText = findViewById(R.id.editLongitudeEditText)
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText)
        dynamicFieldsLayout = findViewById(R.id.dynamicFieldsLayout)
        photosLayout = findViewById(R.id.photosLayout)

        editAreaEditText.setText(report.area.toString())
        editFarmNameEditText.setText(report.farmName)
        editLatitudeEditText.setText(report.latitude.toString())
        editLongitudeEditText.setText(report.longitude.toString())
        editDescriptionEditText.setText(report.description)

        // Отображаем динамические поля
        populateDynamicFields(report)
        // Отображаем фотографии
        displayPhotos(report.photos)
    }

    private fun populateDynamicFields(report: Report) {
        dynamicFieldsLayout.removeAllViews()
        for ((key, value) in report.dynamicFieldsData) {
            val textView = TextView(this).apply {
                text = key
                setTextColor(Color.BLACK)
            }
            val editText = EditText(this).apply {
                setText(value)
                setTextColor(Color.BLACK)
                setHintTextColor(Color.GRAY)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            dynamicFieldsLayout.addView(textView)
            dynamicFieldsLayout.addView(editText)
        }
    }
}
