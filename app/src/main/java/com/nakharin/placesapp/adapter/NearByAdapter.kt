package com.nakharin.placesapp.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nakharin.placesapp.R
import com.nakharin.placesapp.extension.load
import com.nakharin.placesapp.model.NearByItem
import kotlinx.android.synthetic.main.view_adapter_nearby.view.*

class NearByAdapter(private var mNearByItemList: ArrayList<NearByItem>) : RecyclerView.Adapter<NearByAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    private var mOnFavoriteListener: OnFavoriteListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearByAdapter.ViewHolder {
        mContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.view_adapter_nearby, parent, false))
    }

    override fun getItemCount(): Int {
        return mNearByItemList.size
    }

    override fun onBindViewHolder(holder: NearByAdapter.ViewHolder, position: Int) {

        holder.imgIcon.load(mNearByItemList[position].icon)
        holder.txtName.text = mNearByItemList[position].name
        holder.txtUrlLink.text = mNearByItemList[position].url

        if (mNearByItemList[position].isFavorite) {
            holder.imgFavorite.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_favorite_active))
        } else {
            holder.imgFavorite.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_favorite))
        }

        holder.imgFavorite.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            val isFavorite = mNearByItemList[adapterPosition].isFavorite
            mOnFavoriteListener?.onFavorite(adapterPosition, !isFavorite)
        }
    }

    fun addAllItem(nearByItemList: ArrayList<NearByItem>) {
        mNearByItemList = nearByItemList
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mNearByItemList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateFavoriteItem(position: Int, isFavorite: Boolean) {
        mNearByItemList[position].isFavorite = isFavorite
        notifyItemChanged(position)
    }

    fun setOnFavoriteListener(onFavoriteListener: OnFavoriteListener) {
        mOnFavoriteListener = onFavoriteListener
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imgIcon = v.imgIcon!!
        val txtName = v.txtName!!
        val txtUrlLink = v.txtUrlLink!!
        val imgFavorite = v.imgFavorite!!
    }

    interface OnFavoriteListener {
        fun onFavorite(position: Int, isFavorite: Boolean)
    }
}