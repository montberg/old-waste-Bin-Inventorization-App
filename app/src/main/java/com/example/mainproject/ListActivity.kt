package com.example.mainproject


import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class ListActivity : OptionsMenu(), DataBase, SwipeRefreshLayout.OnRefreshListener {
    lateinit var ArrayOfPlatforms:MutableList<PlatformInfo>
    lateinit var adapter:PlatformAdapter
    lateinit var platformlistview:RecyclerView
    lateinit var txtFind:EditText
    lateinit var mSwipeRefresh:SwipeRefreshLayout
    lateinit var thislayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var filteredList:MutableList<PlatformInfo>
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)
        ArrayOfPlatforms = mutableListOf()
        platformlistview = findViewById(R.id.platformList)
        thislayout = findViewById(R.id.thislayout)
        progressBar = findViewById(R.id.progressBar)
        adapter = PlatformAdapter(ArrayOfPlatforms)
        txtFind = findViewById(R.id.txtFind)
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        platformlistview.layoutManager = layoutManager
        platformlistview.itemAnimator = DefaultItemAnimator()
        platformlistview.adapter = adapter
        MainScope().launch {
            ArrayOfPlatforms = getPlatform().await()
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            thislayout.visibility = View.VISIBLE
        }

        mSwipeRefresh =  findViewById(R.id.swipe)
        mSwipeRefresh.setOnRefreshListener(this)

        txtFind.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    filteredList = ArrayList()
                    for (item in ArrayOfPlatforms) {
                        if (item.Address.toLowerCase(Locale.ROOT).replace(",", "").replace(
                                " улица",
                                ""
                            ).contains(s.toString().toLowerCase(Locale.ROOT).replace(",", ""))
                        )
                            filteredList.add(item)
                        setupRecyclerView(filteredList)
                    }
                } else {
                    setupRecyclerView(ArrayOfPlatforms)
                }
            }

            private fun setupRecyclerView(list: MutableList<PlatformInfo>) {
                adapter = PlatformAdapter(list)
                platformlistview.layoutManager = LinearLayoutManager(applicationContext)
                platformlistview.setHasFixedSize(true)
                platformlistview.adapter = adapter
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
    override fun onResume() {
        refreshList()
        super.onResume()
        txtFind.setText("")
    }

    override fun onRefresh() {
        txtFind.setText("")
        refreshList()
    }
    private fun refreshList() {
        Handler().postDelayed({ //Останавливаем обновление:
            MainScope().launch {
                val arrayOfPlatforms = getPlatform().await()
                ArrayOfPlatforms = arrayOfPlatforms
                val Adapter = PlatformAdapter(arrayOfPlatforms)
                platformlistview.adapter = Adapter
                Adapter.notifyDataSetChanged()
                mSwipeRefresh.isRefreshing = false
            }
        }, 100)
    }
}