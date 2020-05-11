package com.abhishekjagushte.engage.ui.main.fragments.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.utils.Constants

class SearchAdapter(val clickListener: SearchDataListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(
    SearchResultDiffUtilCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            Constants.RECYCLERVIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            Constants.RECYCLERVIEW_TYPE_SEARCH_RESULT -> SearchDataViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.Header -> Constants.RECYCLERVIEW_TYPE_HEADER
            is DataItem.SearchDataItem -> Constants.RECYCLERVIEW_TYPE_SEARCH_RESULT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is SearchDataViewHolder -> {
                val searchResult = getItem(position) as DataItem.SearchDataItem
                holder.bind(searchResult, clickListener, getItem(position))
            }
            is HeaderViewHolder -> {
                val header = getItem(position) as DataItem.Header
                holder.bind(header)
            }
        }
    }

}

class SearchDataViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    private var title: TextView = itemView.findViewById(R.id.title_line_text)
    private var subtitle: TextView = itemView.findViewById(R.id.subtitle_line_text)
    private lateinit var searchResultItem: SearchData

    fun bind(
        item: DataItem.SearchDataItem,
        clickListener: SearchDataListener,
        dataItem: DataItem
    ){
        val searchData = item.searchData

        title.text = searchData.title
        subtitle.text = searchData.subtitle

        searchResultItem = (dataItem as DataItem.SearchDataItem).searchData

        itemView.setOnClickListener{
            clickListener.onClick(searchResultItem)
        }
    }

    companion object{
        fun from(parent: ViewGroup): SearchDataViewHolder {
            return SearchDataViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_result_item, parent, false)
            )
        }
    }
}

class HeaderViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    private val headerTitle = itemView.findViewById<TextView>(R.id.header_title)

    fun bind(header: DataItem.Header){
        headerTitle.text = header.title
    }

    companion object{
        fun from(parent: ViewGroup): HeaderViewHolder {
            return HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_list_header, parent, false)
            )
        }
    }

}

class SearchResultDiffUtilCallback: DiffUtil.ItemCallback<DataItem>(){
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean{
        return oldItem == newItem
    }

}

sealed class DataItem{

    abstract val id: String

    data class SearchDataItem(val searchData: SearchData): DataItem(){
        override val id = searchData.subtitle
    }

    data class Header(val title: String): DataItem(){
        override val id = Constants.HEADER_ID_RECYCLERVIEW
    }
}

class SearchDataListener(val clickListener: (SearchData) -> Unit){
    fun onClick(searchData: SearchData) = clickListener(searchData)
}