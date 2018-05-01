package com.plbear.iweight.model.main.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

import com.plbear.iweight.data.DataManager
import com.plbear.iweight.R
import com.plbear.iweight.base.BaseActivity
import com.plbear.iweight.data.Data
import com.plbear.iweight.model.details.DetailsActivity
import com.plbear.iweight.model.main.fragment.MainDataFragment
import com.plbear.iweight.model.main.view.KeyboardBuilder
import com.plbear.iweight.model.other.AboutJActivity
import com.plbear.iweight.utils.Utils
import com.plbear.iweight.model.settings.SettingsActivity
import com.plbear.iweight.storage.XMLHelper
import com.plbear.iweight.utils.MySPUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_1.*
import kotlinx.android.synthetic.main.title_for_all.*

/**
 * Created by yanyongjun on 16/11/normal_5.
 */

class MainActivity : BaseActivity() {
    private var mXmlListener: XMLHelper.OnXMLListener? = null
    var mFragList = ArrayList<Fragment>()
    var mPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return mFragList[position]
        }

        override fun getCount(): Int {
            return mFragList.size
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun afterLayout() {
        init()
        btn_record.setOnClickListener(View.OnClickListener {
            val onceEveryDay = MySPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true)
            if (onceEveryDay) {
                val lastTime = Utils.formatTime(DataManager.getInstance().queryLastDataTime())
                if (lastTime == Utils.formatTime(System.currentTimeMillis())) {
                    Toast.makeText(this@MainActivity, R.string.main_toast_notify_only_once, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
            }
            recordWeight()
        })
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        btn_title_more.setOnClickListener { drawer_layout_main.openDrawer(nav_main_view) }
    }

    /**
     * init all the view
     */
    private fun init() {
        initNav()

        mXmlListener = object : XMLHelper.OnXMLListener {
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
    private fun initNav() {
        val btnAbout = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_about)
        btnAbout.setOnClickListener {
            val intent = Intent(this@MainActivity, AboutJActivity::class.java)
            startActivity(intent)
        }

        val exitButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_exit)
        exitButton.setOnClickListener { exitAll() }


        val detailsButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_detail)
        detailsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, DetailsActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initTabLayout() {
        try {
            tab_main.setupWithViewPager(view_pager_main)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tab_main.tabMode = TabLayout.MODE_FIXED
        tab_main.getTabAt(0)?.setText("本周")
        tab_main.getTabAt(1)?.setText("当月")
        tab_main.getTabAt(2)?.setText("全部")
    }

    /**
     * 初始化折线图
     */
    private fun initCharView() {
        //init fragment
        val weekFrag = MainDataFragment()
        weekFrag.tag = "weekFrag"
        weekFrag.setShowDateNums(7)
        mFragList.add(weekFrag)

        val monthFrag = MainDataFragment()
        monthFrag.tag = "monthFrag"
        monthFrag.setShowDateNums(30)
        mFragList.add(monthFrag)

        val allFrag = MainDataFragment()
        allFrag.tag = "allFrag"
        allFrag.setShowAllData(true)
        mFragList.add(allFrag)

        view_pager_main.adapter = mPagerAdapter
        initTabLayout()
    }


    override fun onResume() {
        super.onResume()

        val isExOn = MySPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_EXPORT_IMPORT, false)
        val importButton = nav_main_view.getHeaderView(0).findViewById<Button>(R.id.btn_main_nav_import)
        if (isExOn) {
            importButton.visibility = View.VISIBLE
            importButton.setOnClickListener(View.OnClickListener {
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
    private fun recordWeight() {
        btn_record.visibility = View.GONE
        edit_num.visibility = View.VISIBLE
        edit_num.setText("")
        var keyboardBuilder = KeyboardBuilder(this@MainActivity, keyboard_main, R.xml.main_keyboard, edit_num, object : KeyboardBuilder.OnStatusChanged {
            override fun onChanged(status: Int) {
                when (status) {
                    KeyboardBuilder.STATUS_CANCEL -> {
                        btn_record.visibility = View.VISIBLE
                        edit_num.visibility = View.GONE
                    }
                    KeyboardBuilder.STATUS_SUBMIT -> {
                        btn_record.visibility = View.VISIBLE
                        edit_num.visibility = View.GONE
                        //TODO
                        val time = System.currentTimeMillis()
                        val data = Data(-1, time, edit_num.text.toString().toFloat())
                        val isOnlyOneTime = MySPUtils.getSP().getBoolean(SettingsActivity.PREFERENCE_KEY_ONLY_ONCE_EVERYDAY, true)
                        DataManager.getInstance(this@MainActivity)!!.add(data)
                    }
                }
            }
        })
        keyboardBuilder.showKeyboard(edit_num)

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
