package com.plbear.iweight.model.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import com.plbear.iweight.data.DataManager
import com.plbear.iweight.R
import com.plbear.iweight.model.other.AboutActivity
import com.plbear.iweight.data.Data
import com.plbear.iweight.model.details.DetailsActivity
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.SPUtils
import com.plbear.iweight.utils.Utils
import com.plbear.iweight.model.settings.SettingsActivity
import com.plbear.iweight.storage.XMLHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.title.*

import java.util.Timer
import java.util.TimerTask

/**
 * Created by yanyongjun on 16/11/5.
 */

class MainActivity : FragmentActivity() {
    private val mSwitchLab = SparseArray<TextView>()
    private var mXmlListener: XMLHelper.OnXMLListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        btn_record.setOnClickListener(View.OnClickListener {
            MyLog.d(TAG, "input weight")
            val onceEveryDay = SPUtils.getSp().getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true)
            if (onceEveryDay) {
                val lastTime = Utils.formatTime(DataManager.getInstance().queryLastDataTime())
                MyLog.d(TAG, "lastTime:" + lastTime)
                if (lastTime == Utils.formatTime(System.currentTimeMillis())) {
                    Toast.makeText(this@MainActivity, R.string.toast_notify_only_once_everyday, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
            }
            showRecordDialog()
        })

        btn_title_more.setOnClickListener { drawer_layout_main.openDrawer(nav_main_view) }
    }

    /**
     * init all the view
     */
    private fun init() {
        MyLog.i(TAG,"init enter")
        initNav()

        mXmlListener = object:XMLHelper.OnXMLListener{
            override fun onReadSuccess() {
                Toast.makeText(this@MainActivity, "文件读取成功", Toast.LENGTH_SHORT).show()
            }

            override fun onSaveSuccess() {
                Toast.makeText(this@MainActivity, "恭喜您，数据成功导出", Toast.LENGTH_SHORT).show()
            }

            override fun onReadFail() {
                Toast.makeText(this@MainActivity, "读取失败，请检查文件是否存在", Toast.LENGTH_SHORT).show()
            }

            override fun onSaveFail() {
                Toast.makeText(this@MainActivity, "数据导出失败", Toast.LENGTH_SHORT).show()
            }
        }

        //init week\month\year\all TextView
        initCharView()




    }

    /**
     * 初始化侧边栏
     */
    private fun initNav(){
        val btnAbout = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_about)
        btnAbout.setOnClickListener {
            val intent = Intent(this@MainActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        val exitButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_exit)
        exitButton.setOnClickListener { System.exit(0) }


        val detailsButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_detail)
        detailsButton.setOnClickListener {
            MyLog.d(TAG, "detailsButton click")
            val intent = Intent(this@MainActivity, DetailsActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_settings)
        settingsButton.setOnClickListener {
            MyLog.d(TAG, "settingsButton click")
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 初始化折线图
     */
    private fun initCharView(){
        mSwitchLab.put(0, lab_week)
        mSwitchLab.put(1, lab_month)
        mSwitchLab.put(2, lab_all_data)

        lab_week.setOnClickListener {
            chooseFrag(0)
            view_pager_main.currentItem = 0
        }

        lab_month.setOnClickListener {
            chooseFrag(1)
            view_pager_main.currentItem = 1
        }


        lab_all_data.setOnClickListener {
            chooseFrag(3)
            view_pager_main.currentItem = 3
        }

        //init fragment
        val weekFrag = MainDataFragment()
        weekFrag.tag = "weekFrag"
        weekFrag.setShowDateNums(7)
        val monthFrag = MainDataFragment()
        monthFrag.tag = "monthFrag"
        monthFrag.setShowDateNums(30)
        val allFrag = MainDataFragment()
        allFrag.tag = "allFrag"
        allFrag.setShowAllData(true)
        val fragList = ArrayList<Fragment>()
        fragList.add(weekFrag)
        fragList.add(monthFrag)
        fragList.add(allFrag)
        val fragmentAdapter = MainDataFragmentAdapter(this@MainActivity.supportFragmentManager, fragList)
        view_pager_main.adapter = fragmentAdapter
        lab_week.setTextColor(resources.getColor(R.color.background))
        view_pager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                chooseFrag(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun chooseFrag(item: Int) {
        val drawable = resources.getDrawable(R.drawable.labselect)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        for (i in 0 until mSwitchLab.size()) {
            if (i == item) {
                mSwitchLab.get(i).setTextColor(resources.getColor(R.color.background))
                mSwitchLab.get(i).setCompoundDrawables(null, null, null, drawable)
            } else {
                mSwitchLab.get(i).setTextColor(resources.getColor(R.color.main_dlg_btn_gray))
                mSwitchLab.get(i).setCompoundDrawables(null, null, null, null)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        view_pager_main.currentItem = 0
        chooseFrag(0)

        val isExOn = SPUtils.getSp().getBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, false)
        val importButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_import)
        if (isExOn) {
            importButton.visibility = View.VISIBLE
            importButton.setOnClickListener(View.OnClickListener {
                MyLog.d(TAG, "importButton click")
                val xmlHelper = XMLHelper(this@MainActivity)
                if (Build.VERSION.SDK_INT >= 23) {
                    val checkPermission = ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS), REQUSET_IMPORT_CODE_PERMISSION)
                        return@OnClickListener
                    }
                }
                xmlHelper.readXML(mXmlListener!!)
            })
        } else {
            importButton.visibility = View.GONE
        }


        /**
         * 导出
         */
        val exportButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_export)
        if (isExOn) {
            exportButton.visibility = View.VISIBLE
            exportButton.setOnClickListener(View.OnClickListener {
                MyLog.d(TAG, "exportButton click")
                val helper = XMLHelper(this@MainActivity)
                if (Build.VERSION.SDK_INT >= 23) {
                    val checkPermission = ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS), REQUSET_EXPORT_CODE_PERMISSION)
                        return@OnClickListener
                    }
                }
                helper.saveXML(mXmlListener!!)
            })
        } else {
            exportButton.visibility = View.GONE
        }
    }

    /**
     * 弹出记录体重的弹框
     */
    private fun showRecordDialog() {
        //builder.setTitle(R.string.)
        val builder = AlertDialog.Builder(this)
        val layout = layoutInflater.inflate(R.layout.dialog_main_input_weight,null)
        builder.setView(layout)
        val dialog = builder.create()
        val btnSubmit = layout.findViewById<View>(R.id.dialog_submit) as Button
        val editText = layout.findViewById<View>(R.id.dialog_input_weight) as EditText
        btnSubmit.setOnClickListener(View.OnClickListener {
            val time = System.currentTimeMillis()
            try {
                val weight = java.lang.Float.parseFloat(editText.text.toString())
                if (!Utils.checkWeightValue(weight)) {
                    Toast.makeText(this@MainActivity, "您输入的值太不合理了，在逗我玩吧~", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                val data = Data(-1, time, weight)
                val isOnlyOneTime = SPUtils.getSp().getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true)
                MyLog.d(TAG, "isOnlyOnceEveryday" + isOnlyOneTime)
                DataManager.getInstance(this@MainActivity)!!.add(data)

                dialog.dismiss()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "输入值不合法，请重新输入~", Toast.LENGTH_SHORT).show()
            }
        })
        val btnCancel = layout.findViewById<View>(R.id.dialog_cacnel) as Button
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
        editText.requestFocus()
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (editText != null) {
                    val imm = this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED)
                }
            }
        }, 200)

    }

    /**
     * 申请存储权限完的回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var helper: XMLHelper? = null
        when (requestCode) {
            REQUSET_EXPORT_CODE_PERMISSION -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在“设置->应用->权限”中赋予权限后重新执行", Toast.LENGTH_SHORT).show()
                    return
                }

                helper = XMLHelper(this)
                helper!!.saveXML(mXmlListener!!)
            }
            REQUSET_IMPORT_CODE_PERMISSION -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在“设置->应用->权限”赋予权限后重新执行", Toast.LENGTH_SHORT).show()
                    return
                }

                helper = XMLHelper(this)
                helper!!.readXML(mXmlListener!!)
            }
            else -> {
            }
        }
        return
    }

    companion object {
        private val TAG = "MainActivity"
        val REQUSET_IMPORT_CODE_PERMISSION = 1
        val REQUSET_EXPORT_CODE_PERMISSION = 2
    }
}
