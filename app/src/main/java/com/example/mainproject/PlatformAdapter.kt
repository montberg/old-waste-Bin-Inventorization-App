package com.example.mainproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.yandex.runtime.Runtime.getApplicationContext

class PlatformAdapter(platformList: MutableList<PlatformInfo>): RecyclerView.Adapter<PlatformAdapter.MyViewHolder>(), Filterable {
    var PlatformList = platformList
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var address: TextView = itemView.findViewById(R.id.address)
        var platform:RelativeLayout = itemView.findViewById(R.id.platform)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.address_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val platformInfo: PlatformInfo = PlatformList[position]
        holder.address.text = platformInfo.Address
        holder.platform.setOnClickListener {
            val redactor = Intent(getApplicationContext(), Redactor::class.java)
            redactor.putExtra("platformID", platformInfo.Id)
            redactor.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(getApplicationContext(), redactor, null)
        }
    }

    override fun getItemCount(): Int {
        return PlatformList.size
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}
