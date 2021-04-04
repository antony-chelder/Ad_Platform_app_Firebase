package com.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapter.SelectCountryAdapter
import com.tony_fire.descorder.R

object DialogCountryHelper {
    fun showDialog(context: Context,list:ArrayList<String>,tvtext:TextView){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        val dialogView = LayoutInflater.from(context).inflate(R.layout.select_country_dialog,null)
        val rc_view = dialogView.findViewById<RecyclerView>(R.id.rc_view_country)
        rc_view.layoutManager = LinearLayoutManager(context)
        val adapter = SelectCountryAdapter(dialog,tvtext)
        val sw = dialogView.findViewById<SearchView>(R.id.searchCountry)
        setSearchView(sw,adapter,list)
        rc_view.adapter = adapter
        adapter.update(list)
        dialog.setView(dialogView)
        dialog.show()

    }

    private fun setSearchView(sw: SearchView?, adapter: SelectCountryAdapter, list: ArrayList<String>) {
        sw?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val templist = CountryManager.filterListData(list,p0)
                adapter.update(templist)
                return true
            }
        })

    }
}