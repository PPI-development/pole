// CreateReportActivity.kt

package com.example.povilika

import android.Manifest
import androidx.appcompat.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.util.*
import android.graphics.Color
import android.util.Xml
import org.xmlpull.v1.XmlSerializer
import android.net.Uri


class CreateReportActivity : AppCompatActivity() {

    private lateinit var activityTypeSpinner: Spinner
    private lateinit var workTypeSpinner: Spinner
    private lateinit var workPlaceSpinner: Spinner
    private lateinit var cultureSpinner: Spinner
    private lateinit var developmentStageSpinner: Spinner
    private lateinit var regionSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var workerSpinner: Spinner
    private lateinit var areaEditText: EditText
    private lateinit var farmEditText: EditText
    private lateinit var saveLocationButton: Button
    private lateinit var dynamicFieldsLayout: LinearLayout
    private lateinit var descriptionEditText: EditText
    private lateinit var takePhotoButton: Button
    private lateinit var dateButton: Button
    private lateinit var saveButton: Button
    private lateinit var locationTextView: TextView

    private var selectedLocation: Location? = null
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private val REQUEST_PERMISSIONS_CODE = 1001
    private val CAMERA_PERMISSION_CODE = 1002
    private val LOCATION_PERMISSION_CODE = 1003
    private val REQUEST_IMAGE_CAPTURE = 1001
    private val REQUEST_IMAGE_PICK = 1003



    private val photosList = mutableListOf<String>()
    private lateinit var departmentSpinner: Spinner
    private val executorList = mapOf(
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
    private val regions = listOf(
        "Астана",
        "Алматы",
        "Шымкент",
        "Абайская область",
        "Актюбинская область",
        "Жетысуская область",
        "Карагандинская область",
        "Кызылординская область",
        "Туркестанская область",
        "Улытауская область",
        "Алматинская область",
        "Костанайская область",
        "Акмолинская область",
        "Атырауская область",
        "Восточно-Казахстанская область",
        "Жамбылская область",
        "Западно-Казахстанская область",
        "Павлодарская область",
        "Северо-Казахстанская область"
    )
    private val districtsMap = mapOf(
        "Астана" to listOf(),
        "Алматы" to listOf(),
        "Шымкент" to listOf(),
        "Абайская область" to listOf("Семей", "Курчатов","Абайский", "Аксуатский", "Аягозский", "Бескарагайский", "Бородулихинский", "Жарминский", "Кокпектинский", "Урджарский", "Маканчинский", "Жанасемейский"),
        "Актюбинская область" to listOf("Актобе", "Алгинский", "Айтекебийский", "Байганинский", "Иргизкий", "Карагалинский", "Мартукский", "Мугалжарский", "Темирский", "Уилский", "Хобдинский", "Хромтауский", "Шалкарский"),
        "Жетысуская область" to listOf("Талдыкорган", "Текели", "Аксуский", "Алакольский", "Ескельдинский", "Каратальский", "Кербулакский", "Коксуский", "Панфиловский", "Саркандский"),
        "Карагандинская область" to listOf("Караганда", "Балхаш", "Приозёрск", "Сарань", "Темиртау", "Шахтинск", "Абайский", "Актогайский", "Бухау-Жырауский", "Каркаралинский", "Нуринский", "Осакаровский", "Шетский"),
        "Кызылординская область" to listOf("Кызылорда", "Байконур", "Аральский", "Жалагашский", "Жанакорганский", "Казалинский", "Кармакшинский", "Сырдарьинский", "Шиелийский"),
        "Туркестанская область" to listOf("Туркестан", "Кентау", "Арыс", "Байдибекский", "Казыгурский", "Мактааральский", "Ордабасинский", "Отырарский", "Сайрамский", "Сарыагашский", "Сауранский", "Сузакский", "Толебийский", "Тюлькубаксский", "Шардаринский", "Жетысайский", "Келесский"),
        "Улытауская область" to listOf("Жесказган", "Сатпаев", "Каражал", "Улытауский", "Жанааркинский"),
        "Алматинская область" to listOf("Конаев", "Алатау", "Балхашский", "Енбекшиказахский", "Жамбылский", "Илийский", "Карасайский", "Кегенский", "Райымбекский", "Талгарский", "Уйгурский"),
        "Костанайская область" to listOf("Костанай", "Рудный", "Лисаковск", "Аркалык", "Алтынсаринский", "Амангельдинский", "Аулиекольский", "Денисовский", "Джангельдинский", "Житикаринский", "Камыстинский", "Карабалыкский", "Карасуский", "Костанайский", "Мендыкаринский", "Наурзумский", "Сарыкольский", "Беимбета Майлина", "Узункольский", "Фёдоровский"),
        "Акмолинская область" to listOf("Косшы", "Степногорск", "Кокшетау", "Аккольский", "Аршалыкский", "Астраханский", "Атбасарский", "Буландынский", "Бурабайский", "Егиндыкольский", "Биржан-сал", "Ерейментауский", "Есильский", "Жаксынский", "Жаркаинский", "Зерендинский", "Коргалжинский", "Сандыктауский", "Целиноградский", "Шортандинский"),
        "Атырауская область" to listOf("Атырау", "Жылыойский", "Индерский", "Исатайский", "Кзылкогинский", "Курмангазинский", "Макатайский", "Махамбетский"),
        "Восточно-Казахстанская область" to listOf("Усть-Каменогорск", "Ридер", "Алтайский", "Глубоковский", "Зайсанский", "Катон-Карагайский", "Курчумский", "Маркакольский", "Самарский", "Тарбагайский", "Уланский", "Улькен Нарынский", "Шемонаихинский"),
        "Жамбылская область" to listOf("Тараз", "Байзакский", "Жамбылский", "Жуалынский", "Кордайский", "Меркенский", "Мойынкумский", "Т. Рыскулова", "Сарысуский", "Таласский", "Шуский"),
        "Западно-Казахстанская область" to listOf("Уральск", "Акжаикский", "Бокейординский", "Бурлинский", "Жангалинский", "Жанибекский", "Байтерекский", "Казталовский", "Каратойбинский", "Сырымский", "Таскалинский", "Теректинский", "Чингирлауский"),
        "Павлодарская область" to listOf("Павлодар", "Аксу", "Экибастуз", "Актогайский", "Баянаульский", "Железинский", "Иртышский", "Теренкольский", "Аккулинский", "Майский", "Павлодарский", "Успенский", "Щербактинский"),
        "Северо-Казахстанская область" to listOf("Петропавловск", "Айыртауский", "Акжарский", "Аккайынский", "Есильский", "Жамбылский", "Магжана Жумабаева", "Кызылжарский", "Малютский", "Габита Мусрепова", "Тайыншинский", "Тимирязевский", "Уалихановский", "Шал Акына"),
    )

    private val activityTypes = listOf("Энтомолог", "Фитопатолог", "Герболог", "Пилот БПЛА")
    private val workTypes = listOf("Первичный мониторинг", "Проведение осмотра Вредных Организмов", "Проведение обработки", "Саранча")
    private val workPlaces = listOf("Поле", "Сад", "Теплица", "Лесопосадка")
    private val culturesField = listOf(
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
    private val culturesGarden = listOf(
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
    private val culturesGreenhouse = listOf(
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
        "Лук"
    )
    private val developmentStages = listOf(
        "Прорастание",
        "Всходы",
        "Кущение/Розетка",
        "Интенсивный рост",
        "Бутонизация",
        "Цветение",
        "Завязь/Плодообразование",
        "Созревание",
        "Полная спелость",
        "Отмирание"
    )

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
        photosList.add(file.absolutePath)
        Toast.makeText(this, "Фото добавлено с камеры", Toast.LENGTH_SHORT).show()
    }

    private fun saveImageUriToStorage(imageUri: Uri) {
        val appDir = File(getExternalFilesDir(null), "ReportsApp")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val filename = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(appDir, filename)

        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            photosList.add(file.absolutePath)
            Toast.makeText(this, "Фото добавлено из галереи", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при добавлении фото", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf<CharSequence>("Сделать фото", "Выбрать из галереи", "Отмена")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Добавить фото")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Сделать фото" -> {
                    openCamera()
                }
                "Выбрать из галереи" -> {
                    openGallery()
                }
                "Отмена" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_report)

        // Инициализация UI компонентов
        activityTypeSpinner = findViewById(R.id.activityTypeSpinner)
        workTypeSpinner = findViewById(R.id.workTypeSpinner)
        workPlaceSpinner = findViewById(R.id.workPlaceSpinner)
        cultureSpinner = findViewById(R.id.cultureSpinner)
        developmentStageSpinner = findViewById(R.id.developmentStageSpinner)
        regionSpinner = findViewById(R.id.regionSpinner)
        districtSpinner = findViewById(R.id.districtSpinner)

        areaEditText = findViewById(R.id.areaEditText)
        farmEditText = findViewById(R.id.farmEditText)
        saveLocationButton = findViewById(R.id.saveLocationButton)
        dynamicFieldsLayout = findViewById(R.id.dynamicFieldsLayout)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        takePhotoButton = findViewById(R.id.takePhotoButton)
        dateButton = findViewById(R.id.dateButton)
        saveButton = findViewById(R.id.saveButton)
        locationTextView = findViewById(R.id.locationTextView)
        departmentSpinner = findViewById(R.id.departmentSpinner)


        setupSpinners()
        initializeLocationManager()
        setupListeners()
        startLocationUpdates()

        // Устанавливаем текущую дату по умолчанию
        val calendar = Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateButton.text = dateFormat.format(calendar.time)

        workerSpinner = findViewById(R.id.workerSpinner)
        val departmentSpinner: Spinner = findViewById(R.id.departmentSpinner)

    }

    private fun setupSpinners() {
        // Вид деятельности
        val activityAdapter = createBlackTextAdapter(activityTypes)
        activityTypeSpinner.adapter = activityAdapter

        // Вид работы
        val workTypeAdapter = createBlackTextAdapter(workTypes)
        workTypeSpinner.adapter = workTypeAdapter

        // Место проведения работ
        val workPlaceAdapter = createBlackTextAdapter(workPlaces)
        workPlaceSpinner.adapter = workPlaceAdapter

        // Исполнитель
        val departments = executorList.keys.toList()
        val departmentAdapter = createBlackTextAdapter(departments)
        departmentSpinner.adapter = departmentAdapter

        departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDept = departments[position]
                val employees = executorList[selectedDept] ?: listOf()
                val workerAdapter = createBlackTextAdapter(employees)
                workerSpinner.adapter = workerAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



        // Области Казахстана
        val regionAdapter = createBlackTextAdapter(regions)
        regionSpinner.adapter = regionAdapter

        // Фаза развития
        val developmentStageAdapter = createBlackTextAdapter(developmentStages)
        developmentStageSpinner.adapter = developmentStageAdapter
    }

    private fun createBlackTextAdapter(data: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(this, R.layout.spinner_item, data) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getDropDownView(position, convertView, parent).apply {
                    (this as TextView).setTextColor(Color.BLACK)
                    setBackgroundColor(Color.parseColor("#FFFFFF")) // фон выпадающего списка
                }
            }
        }.apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
    }



    private fun initializeLocationManager() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                selectedLocation = location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, locationListener)
        }
    }

    private fun setupListeners() {
        workPlaceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCultureSpinner(workPlaceSpinner.selectedItem.toString())
            }
        }

        workTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedWorkType = workTypeSpinner.selectedItem.toString()
                updateWorkTypeFields(selectedWorkType)

            }
        }




        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateDistrictSpinner(regionSpinner.selectedItem.toString())
            }
        }

        activityTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedWorkType = workTypeSpinner.selectedItem.toString()
                if (selectedWorkType != "Проведение обработки" && selectedWorkType != "Саранча") {
                    updateDynamicFields(activityTypeSpinner.selectedItem.toString())
                }
            }
        }


        saveLocationButton.setOnClickListener {
            selectedLocation?.let {
                val coordinates = "Координаты: Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                locationTextView.text = coordinates
                Toast.makeText(this, coordinates, Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "Местоположение не выбрано", Toast.LENGTH_SHORT).show()
            }
        }


        dateButton.setOnClickListener {
            showDatePicker()
        }

        takePhotoButton.setOnClickListener {
            if (checkAndRequestPermissions()) {
                showImageSourceDialog()
            }
        }

        saveButton.setOnClickListener {
            val report = collectReportData()
            saveReport(report)
        }
    }

    private fun updateWorkTypeFields(workType: String) {
        dynamicFieldsLayout.removeAllViews()

        // Сброс доступности
        cultureSpinner.isEnabled = true
        workPlaceSpinner.isEnabled = true

        fun addLinedEditText(hint: String, inputType: Int = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL) {
            dynamicFieldsLayout.addView(createEditText(hint, inputType))
            dynamicFieldsLayout.addView(createDivider())
        }


        when (workType) {
            "Проведение обработки" -> {
                // Разблокируем выбор
                cultureSpinner.isEnabled = true
                workPlaceSpinner.isEnabled = true
                developmentStageSpinner.isEnabled = true

                addLinedEditText("Действующее вещество", android.text.InputType.TYPE_CLASS_TEXT)
                addLinedEditText("Площадь обработки") // оставить числовым
                addLinedEditText("Метод обработки", android.text.InputType.TYPE_CLASS_TEXT)
                addLinedEditText("Объём раствора") // оставить числовым

            }

            "Саранча" -> {
                // Принудительно очищаем значения
                cultureSpinner.adapter = createBlackTextAdapter(listOf(""))
                cultureSpinner.setSelection(0)
                cultureSpinner.isEnabled = false

                workPlaceSpinner.adapter = createBlackTextAdapter(listOf(""))
                workPlaceSpinner.setSelection(0)
                workPlaceSpinner.isEnabled = false

                developmentStageSpinner.adapter = createBlackTextAdapter(listOf(""))
                developmentStageSpinner.setSelection(0)
                developmentStageSpinner.isEnabled = false

                addLinedEditText("Площадь распространения")
                addLinedEditText("Количество взрослых особей")
                addLinedEditText("Количество кубышек")
            }


            else -> {
                // Разблокировка по умолчанию
                cultureSpinner.isEnabled = true
                workPlaceSpinner.isEnabled = true
                developmentStageSpinner.isEnabled = true

                updateDynamicFields(activityTypeSpinner.selectedItem.toString())
            }
        }

    }


    private fun updateCultureSpinner(workPlace: String) {
        val cultures = when (workPlace) {
            "Поле" -> culturesField
            "Сад" -> culturesGarden
            "Теплица" -> culturesGreenhouse
            "Лесопосадка" -> listOf()
            else -> listOf()
        }

        val cultureAdapter = createBlackTextAdapter(cultures)
        cultureSpinner.adapter = cultureAdapter
        cultureSpinner.isEnabled = cultures.isNotEmpty()
    }

    private fun updateDistrictSpinner(region: String) {
        val districts = districtsMap[region] ?: listOf()

        val districtAdapter = createBlackTextAdapter(districts)
        districtSpinner.adapter = districtAdapter
    }

    private fun updateDynamicFields(activityType: String) {
        dynamicFieldsLayout.removeAllViews()
        val workType = workTypeSpinner.selectedItem?.toString() ?: ""


        fun addLinedEditText(hint: String, inputType: Int = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL) {
            val editText = createEditText(hint, inputType)
            dynamicFieldsLayout.addView(editText)
            dynamicFieldsLayout.addView(createDivider())
        }

        // Если выбрано "Проведение обработки"
        if (workType == "Проведение обработки") {
            addLinedEditText("Действующее вещество")
            addLinedEditText("Площадь обработки")
            addLinedEditText("Метод обработки")
            addLinedEditText("Объём раствора")
            return
        }

        // Если выбрано "Саранча"
        if (workType == "Саранча") {
            addLinedEditText("Площадь распространения")
            addLinedEditText("Количество взрослых особей")
            addLinedEditText("Количество кубышек")
            return
        }

        when (activityType) {
            "Герболог" -> {
                addLinedEditText("Площадь обследуемого участка")
                addLinedEditText("Количество мест учета")
                addLinedEditText("Количество сорняков")
                dynamicFieldsLayout.addView(TextView(this).apply { text = "Сорняк" })
                dynamicFieldsLayout.addView(
                    createSpinnerWithItems(
                        listOf("Повелика полевая", "Горчак ползучий", "Амброзия полыннолистная", "Щирица запрокинутая", "Щетинник зелёный", "Марь белая", "Осот розовый", "Осот огородный", "Осот полевой", "Пырей ползучий", "Молочай лозный", "Герань полевая", "Подмаренник цепкий", "Просо куриное", "Лебеда раскидистая", "Ромашка непахучая", "Вьюнок полевой", "Чертополох курчавый", "Горец птичий", "Сурепка обыкновенная", "Резуха повислая", "Гусиная лапка", "Пикульник красивый", "Вероника плющелистная", "Ярутка полевая", "Пастушья сумка обыкновенная", "Желтушник левкойный", "Редька дикая", "Горчица сарептская", "Рапс дикий", "Миндалина полевая", "Крестовник обыкновенный", "Клоповник мусорный", "Пикульник стройный", "Курай", "Солянка Рихтера", "Галинсога мелкоцветковая", "Щавель конский", "Будра плющевидная", "Костер ржаной", "Мятлик однолетний", "Рогоз широколистный"),
                        "Сорняк"
                    )
                )
            }

            "Энтомолог" -> {
                addLinedEditText("Площадь обследуемого участка")
                addLinedEditText("Количество вредителей")
                dynamicFieldsLayout.addView(TextView(this).apply { text = "Вредитель" })
                dynamicFieldsLayout.addView(
                    createSpinnerWithItems(
                        listOf("Азиатская саранча", "Мароккская саранча", "Итальянский прус", "Клоп вредная черепашка", "Пшеничный трипс", "Гессенская муха", "Шведская муха", "Минирующая муха", "Хлебная полосатая блошка", "Стеблевая блошка", "Хлебный пилильщик", "Колорадский жук", "Капустная белянка", "Капустная моль", "Рапсовый цветоед", "Рапсовый пилильщик", "Крестоцветная блошка", "Льняной трипс", "Тля бахчевая (хлопковая)", "Тля зелёная персиковая", "Медведка обыкновенная", "Майский хрущ", "Жук-усач", "Короед типограф", "Златка дымчатая", "Древоточец пахучий", "Непарный шелкопряд", "Кольчатый коконопряд", "Яблонная моль", "Смородинная стеклянница", "Серая зерновая совка", "Хлопковая совка", "Совка зерновая обыкновенная", "Медляк степной", "Клоп рапсовый", "Пикульник красивый", "Вероника плющелистная", "Ярутка полевая", "Пастушья сумка обыкновенная", "Желтушник левкойный", "Редька дикая", "Горчица сарептская", "Рапс дикий", "Миндалина полевая", "Крестовник обыкновенный", "Клоповник мусорный", "Пикульник стройный", "Курай", "Солянка Рихтера", "Галинсога мелкоцветковая", "Щавель конский", "Будра плющевидная", "Костер ржаной", "Мятлик однолетний", "Рогоз широколистный"),
                        "Вредитель"
                    )
                )
            }

            "Фитопатолог" -> {
                addLinedEditText("Количество обследуемых растений")
                addLinedEditText("Количество пораженных растений")
                addLinedEditText("Степень поражения")
                dynamicFieldsLayout.addView(TextView(this).apply { text = "Болезнь" })
                dynamicFieldsLayout.addView(
                    createSpinnerWithItems(
                        listOf("Пыльная головня пшеницы", "Твёрдая головня пшеницы", "Пыльная головня ячменя", "Каменная головня ячменя", "Стеблевая ржавчина", "Бурая листовая ржавчина", "Септориоз листьев пшеницы", "Септориоз колоса", "Фузариозная корневая гниль", "Гельминтоспориозная корневая гниль", "Мучнистая роса", "Альтернариоз", "Антракноз", "Пероноспороз", "Кила капусты", "Фитофтороз", "Ризоктониоз", "Кладоспориоз", "Серая гниль", "Белая гниль", "Снежная плесень", "Полосатая пятнистость ячменя", "Сетчатая пятнистость ячменя", "Ринхоспориоз ржи", "Красно-бурая пятнистость овса", "Корончатая ржавчина овса", "Пузырчатая головня кукурузы", "Пыльная головня кукурузы", "Фузариоз початков кукурузы", "Стеблевые гнили кукурузы","Бактериозы пшеницы", "Бактериозы капусты", "Бактериозы томатов", "Бактериозы огурцов", "Обыкновенная мозаика огурца", "Зелёная крапчатая мозаика", "Вирус крапчатости листьев томата", "Ризомания свёклы", "Физиологические нарушения из-за дефицита элементов питания", "Повреждения от высоких или низких температур", "Засоление почвы", "Загрязнение воздуха"),
                        "Болезнь"
                    )
                )
            }
        }
    }

    private fun createDivider(): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1 // толщина линии
            ).apply {
                topMargin = 0
                bottomMargin = 8
            }
            setBackgroundColor(Color.BLACK) // Всегда чёрная линия
        }
    }



    private fun createEditText(hint: String, inputType: Int = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL): EditText {
        return EditText(this).apply {
            this.hint = hint
            this.inputType = inputType
            this.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            this.setTextColor(Color.BLACK)
            this.setHintTextColor(Color.GRAY)
            this.tag = hint
        }
    }


    private fun createSpinnerWithItems(items: List<String>, tag: String): Spinner {
        val spinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            this.tag = tag
        }
        val adapter = createBlackTextAdapter(items)
        spinner.adapter = adapter
        return spinner
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            dateButton.text = "$dayOfMonth/${month + 1}/$year"
        }
        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_PERMISSIONS_CODE)
            false
        } else {
            true
        }
    }

    private lateinit var currentPhotoPath: String

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                    }
                } else {
                    Toast.makeText(this, "Разрешение на доступ к местоположению отклонено", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Необходимы разрешения для камеры и хранения", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_PERMISSIONS_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                    // Все разрешения даны
                } else {
                    Toast.makeText(this, "Необходимы все разрешения для корректной работы приложения", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun collectReportData(): Report {
        val dynamicFieldsData = collectDynamicFieldsData()
        val workType = workTypeSpinner.selectedItem?.toString() ?: ""
        val culture = if (workType == "Саранча") "" else cultureSpinner.selectedItem?.toString() ?: ""
        val workPlace = if (workType == "Саранча") "" else workPlaceSpinner.selectedItem?.toString() ?: ""
        val developmentStage = if (workType == "Саранча") "" else developmentStageSpinner.selectedItem?.toString() ?: ""
        return Report(
            id = System.currentTimeMillis(),
            activityType = activityTypeSpinner.selectedItem?.toString() ?: "",
            workType = workTypeSpinner.selectedItem?.toString() ?: "",
            workPlace = workPlaceSpinner.selectedItem?.toString() ?: "",
            culture = cultureSpinner.selectedItem?.toString() ?: "",
            developmentStage = developmentStageSpinner.selectedItem?.toString() ?: "",
            region = regionSpinner.selectedItem?.toString() ?: "",
            district = districtSpinner.selectedItem?.toString() ?: "",
            department = departmentSpinner.selectedItem?.toString() ?:"",
            executor = workerSpinner.selectedItem?.toString() ?: "",
            area = areaEditText.text.toString().toDoubleOrNull() ?: 0.0,
            farmName = farmEditText.text.toString(),
            latitude = selectedLocation?.latitude ?: 0.0,
            longitude = selectedLocation?.longitude ?: 0.0,
            description = descriptionEditText.text.toString(),
            photos = photosList,
            results = calculateResults(),
            date = dateButton.text.toString(),
            dynamicFieldsData = dynamicFieldsData
        )
    }

    private fun collectDynamicFieldsData(): MutableMap<String, String> {
        val data = mutableMapOf<String, String>()
        val workType = workTypeSpinner.selectedItem.toString()
        if (workType != "Саранча" && workType != "Проведение обработки") {
            when (activityTypeSpinner.selectedItem.toString()) {
                "Герболог" -> {
                    data["Площадь обследуемого участка"] = dynamicFieldsLayout.findViewWithTag<EditText>("Площадь обследуемого участка")?.text.toString()
                    data["Количество мест учета"] = dynamicFieldsLayout.findViewWithTag<EditText>("Количество мест учета")?.text.toString()
                    data["Количество сорняков"] = dynamicFieldsLayout.findViewWithTag<EditText>("Количество сорняков")?.text.toString()
                    val weedSpinner = dynamicFieldsLayout.findViewWithTag<Spinner>("Сорняк")
                    data["Сорняк"] = weedSpinner?.selectedItem?.toString() ?: ""
                }
                "Энтомолог" -> {
                    data["Площадь обследуемого участка"] = dynamicFieldsLayout.findViewWithTag<EditText>("Площадь обследуемого участка")?.text.toString()
                    data["Количество вредителей"] = dynamicFieldsLayout.findViewWithTag<EditText>("Количество вредителей")?.text.toString()
                    val pestSpinner = dynamicFieldsLayout.findViewWithTag<Spinner>("Вредитель")
                    data["Вредитель"] = pestSpinner?.selectedItem?.toString() ?: ""
                }
                "Фитопатолог" -> {
                    data["Количество обследуемых растений"] = dynamicFieldsLayout.findViewWithTag<EditText>("Количество обследуемых растений")?.text.toString()
                    data["Количество пораженных растений"] = dynamicFieldsLayout.findViewWithTag<EditText>("Количество пораженных растений")?.text.toString()
                    data["Степень поражения"] = dynamicFieldsLayout.findViewWithTag<EditText>("Степень поражения")?.text.toString()
                    val diseaseSpinner = dynamicFieldsLayout.findViewWithTag<Spinner>("Болезнь")
                    data["Болезнь"] = diseaseSpinner?.selectedItem?.toString() ?: ""
                }
            }
        }
        when (workType) {
            "Проведение обработки" -> {
                data["Действующее вещество"] =
                    dynamicFieldsLayout.findViewWithTag<EditText>("Действующее вещество")?.text.toString()
                data["Площадь обработки"] =
                    dynamicFieldsLayout.findViewWithTag<EditText>("Площадь обработки")?.text.toString()
                data["Метод обработки"] =
                    dynamicFieldsLayout.findViewWithTag<EditText>("Метод обработки")?.text.toString()
                data["Объём раствора"] =
                    dynamicFieldsLayout.findViewWithTag<EditText>("Объём раствора")?.text.toString()
            }

            "Саранча" -> {
                data["Площадь распространения"] =
                    dynamicFieldsLayout.findViewWithTag<EditText>("Площадь распространения")?.text.toString()
                data["Количество взрослых особей"] =
                    dynamicFieldsLayout.findViewWithTag<EditText>("Количество взрослых особей")?.text.toString()
                data["Количество кубышек"] =
                    dynamicFieldsLayout.findViewWithTag<EditText>("Количество кубышек")?.text.toString()
            }
        }
        return data
    }


    private fun calculateResults(): String {
        val workType = workTypeSpinner.selectedItem.toString()
        val activityType = activityTypeSpinner.selectedItem.toString()
        if (workType == "Саранча" || workType == "Проведение обработки") return ""
        return when (activityType) {
            "Герболог" -> {
                val area = dynamicFieldsLayout.findViewWithTag<EditText>("Площадь обследуемого участка")?.text.toString().toDoubleOrNull() ?: 0.0
                val locations = dynamicFieldsLayout.findViewWithTag<EditText>("Количество мест учета")?.text.toString().toIntOrNull() ?: 0
                val weeds = dynamicFieldsLayout.findViewWithTag<EditText>("Количество сорняков")?.text.toString().toIntOrNull() ?: 0
                val score = when {
                    weeds <= 1 -> 1
                    weeds <= 3 -> 2
                    weeds <= 5 -> 3
                    else -> 4
                }
                "Баллы: $score"
            }
            "Энтомолог" -> {
                val area = dynamicFieldsLayout.findViewWithTag<EditText>("Площадь обследуемого участка")?.text.toString().toDoubleOrNull() ?: 0.0
                val pests = dynamicFieldsLayout.findViewWithTag<EditText>("Количество вредителей")?.text.toString().toIntOrNull() ?: 0
                "Количество вредителей: $pests"
            }
            "Фитопатолог" -> {
                val total = dynamicFieldsLayout.findViewWithTag<EditText>("Количество обследуемых растений")?.text.toString().toIntOrNull() ?: 0
                val affected = dynamicFieldsLayout.findViewWithTag<EditText>("Количество пораженных растений")?.text.toString().toIntOrNull() ?: 0
                val severity = dynamicFieldsLayout.findViewWithTag<EditText>("Степень поражения")?.text.toString().toDoubleOrNull() ?: 0.0
                val percentage = if (total > 0) (affected / total.toDouble()) * 100 else 0.0
                "Процент пораженных растений: %.2f%%".format(percentage)
            }
            else -> "Нет результатов"
        }
    }

    private fun saveReport(report: Report) {
        val appDir = File(getExternalFilesDir(null), "ReportsApp")
        if (!appDir.exists()) appDir.mkdirs()
        val xmlFile = File(appDir, "report_${report.id}.xml")

        try {
            val serializer: XmlSerializer = Xml.newSerializer()
            val fos = FileOutputStream(xmlFile)
            serializer.setOutput(fos, "UTF-8")
            serializer.startDocument("UTF-8", true)
            serializer.startTag("", "Report")

            fun tagIfNotEmpty(name: String, value: String) {
                if (value.isNotBlank()) {
                    serializer.startTag("", name)
                    serializer.text(value)
                    serializer.endTag("", name)
                }
            }

            serializer.startTag("", "ID")
            serializer.text(report.id.toString())
            serializer.endTag("", "ID")

            tagIfNotEmpty("ActivityType", report.activityType)
            tagIfNotEmpty("WorkType", report.workType)
            tagIfNotEmpty("WorkPlace", report.workPlace)
            tagIfNotEmpty("Culture", report.culture)
            tagIfNotEmpty("DevelopmentStage", report.developmentStage)
            tagIfNotEmpty("Region", report.region)
            tagIfNotEmpty("District", report.district)
            tagIfNotEmpty("Department", report.department)
            tagIfNotEmpty("Executor", report.executor)
            tagIfNotEmpty("FarmName", report.farmName)
            tagIfNotEmpty("Description", report.description)
            tagIfNotEmpty("Results", report.results)
            tagIfNotEmpty("Date", report.date)

            // Числовые поля всегда пишем
            serializer.startTag("", "Area")
            serializer.text(report.area.toString())
            serializer.endTag("", "Area")

            serializer.startTag("", "Latitude")
            serializer.text(report.latitude.toString())
            serializer.endTag("", "Latitude")

            serializer.startTag("", "Longitude")
            serializer.text(report.longitude.toString())
            serializer.endTag("", "Longitude")

            // Фото
            if (report.photos.isNotEmpty()) {
                serializer.startTag("", "Photos")
                for (photoPath in report.photos) {
                    tagIfNotEmpty("Photo", photoPath)
                }
                serializer.endTag("", "Photos")
            }

            // Dynamic fields
            if (report.dynamicFieldsData.isNotEmpty()) {
                serializer.startTag("", "DynamicFieldsData")
                for ((key, value) in report.dynamicFieldsData) {
                    if (value.isNotBlank()) {
                        serializer.startTag("", "Field")
                        serializer.attribute("", "name", key)
                        serializer.text(value)
                        serializer.endTag("", "Field")
                    }
                }
                serializer.endTag("", "DynamicFieldsData")
            }

            serializer.endTag("", "Report")
            serializer.endDocument()
            fos.close()

            Toast.makeText(this, "Отчет сохранен: ${xmlFile.absolutePath}", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при сохранении отчета: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
