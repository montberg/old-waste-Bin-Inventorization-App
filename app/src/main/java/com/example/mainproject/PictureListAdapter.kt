package com.example.mainproject

import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PictureListAdapter(imageList: MutableList<Bitmap>, base64list: MutableList<String>?) : RecyclerView.Adapter<PictureListAdapter.MyViewHolder>() {
    var ImageList = imageList
    var Base64list = base64list

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
        var deleteButton:ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView:View = LayoutInflater.from(parent.context).inflate(R.layout.image_list, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image: Bitmap = ImageList[position]
        val matrix = Matrix()
        matrix.postRotate(90F)
        val scaledBitmap = Bitmap.createScaledBitmap(image, image.width, image.height, true)
        val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        holder.image.setImageBitmap(rotatedBitmap)
        holder.deleteButton.setOnClickListener {
            Base64list?.removeAt(position)
            ImageList.removeAt(position)
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int {
        return ImageList.size
    }
}

