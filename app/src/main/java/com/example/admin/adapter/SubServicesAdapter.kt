package com.example.admin.adapter

import android.util.Log.e
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.databinding.ItemSubServicesBinding

class SubServicesAdapter  : RecyclerView.Adapter<SubServicesAdapter.SubServicesHolder>() {

    private var data: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubServicesHolder {
        return SubServicesHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_sub_services, parent, false
            )
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SubServicesHolder, position: Int) {
        holder.execute()
    }

    inner class SubServicesHolder(private val mBinding: ItemSubServicesBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun execute() {
            mBinding.any = data[adapterPosition]
            e("sdfsdfs", data[adapterPosition])

        }
    }

    fun addItem(data: ArrayList<String>) {
        this.data = data
    }
}