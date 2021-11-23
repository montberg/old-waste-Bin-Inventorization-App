package com.example.mainproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContainerAdapter(containerList: MutableList<Container>) : RecyclerView.Adapter<ContainerAdapter.MyViewHolder>() {
    var ContainerList = containerList
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var volume: TextView = itemView.findViewById(R.id.containerVolume)
        var type: TextView = itemView.findViewById(R.id.containerRubbishType)
        var deleteContainer:ImageView = itemView.findViewById(R.id.deleteContainer)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.container, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var container: Container = ContainerList[position]
        holder.volume.text = container.Volume.toString()
        holder.type.text = container.RubbishType
        holder.deleteContainer.setOnClickListener {
            ContainerList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return ContainerList.size
    }
}