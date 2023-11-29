package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class SaleItemAdapter(private var saleItems: List<SaleItem>,private val onItemClick: (SaleItem) -> Unit) :
    RecyclerView.Adapter<SaleItemAdapter.SaleItemViewHolder>() {
    private var originalSaleItems: List<SaleItem> = saleItems.toList()
    class SaleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
        val sellerTextView:TextView=itemView.findViewById(R.id.textViewSeller)
        val sellingTextView:TextView=itemView.findViewById(R.id.textViewSelling)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return SaleItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: SaleItemViewHolder, position: Int) {
        val saleItem = saleItems[position]

        // 데이터를 뷰에 바인딩
        holder.titleTextView.text = saleItem.title
        holder.descriptionTextView.text = saleItem.description
        holder.priceTextView.text = saleItem.price
        holder.sellerTextView.text=saleItem.userid
        if (saleItem.selling) {
            holder.sellingTextView.text = "판매중"
        } else {
            holder.sellingTextView.text = "판매완료"
        }
        holder.itemView.setOnClickListener {
            // 아이템 클릭 시 onItemClick 콜백 호출
            onItemClick(saleItem)
        }
    }



    override fun getItemCount(): Int {
        return saleItems.size
    }
    fun updateData(newList: List<SaleItem>) {
        saleItems = newList
        notifyDataSetChanged()
    }
    fun filterBySelling() {
        originalSaleItems=saleItems
        saleItems = saleItems.filter { it.selling == true }
        notifyDataSetChanged()
    }
    fun showAllItems() {
        // 기존 SaleItem 목록으로 복원
        saleItems = originalSaleItems.toList()
        notifyDataSetChanged()
    }



}
