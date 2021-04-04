package com.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.EditActivity
import com.tony_fire.descorder.R
import org.w3c.dom.Text

class SelectCountryAdapter(private val dialog:AlertDialog, val tvText:TextView): RecyclerView.Adapter<SelectCountryAdapter.ViewHolder>() {
    private val mainArray = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.select_country_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(mainArray[position])
        holder.itemView.setOnClickListener{
           tvText.text = mainArray[position]
            dialog.dismiss()

        }

    }

    override fun getItemCount(): Int {
        return mainArray.size

    }
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        lateinit var country_name: TextView
        fun setData(name: String) {
            country_name = itemView.findViewById(R.id.country_names)
            country_name.text = name

        }
    }
    fun update(newList:ArrayList<String>){
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()

    }

}
