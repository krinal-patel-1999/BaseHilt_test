package com.example.kotlinflowdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Created By Vishal Joshi on 14/07/2021 at 10:09AM
 * Generic Filterable Adapter with DiffUtils for set recycler view
 * @param T Response model
 * @param D Item Layout binding class
 * @property mArrayList ArrayList<T?> its main list of model type without filter(its private, managed by only predefined methods of this adapter)
 * @property mFilterList ArrayList<T?> its list of model type with filtered Data(its private, managed by only predefined methods of this adapter)
 * @property searchQuery String its SearchQuery received in getFilter -> performFiltering(see its usage in addItems method)
 * @property layoutItemResId Int layout name
 * @property addItems(ArrayList<T>()) Method to add items in adapter(it always append list in mArraylist and change mFilterList accordingly when searchQuery isEmpty or not)
 * @property getItem(position) Provide item from mFilterList at given position
 * @property removeItem(position) Remove item from both list at given Position
 * @property updateItem(model , position) Update the given model at given position in both lists
 * @property clearList() clear both lists
 * @property refreshWithNewList(ArrayList<T>) clear both list and add given new List
 * @property getCopyOfMainList() provide copy of mArraylist to perform filter operation in onFilterQueryReceived()
 * @property getFilteredListSize() returns size of mFilterList list
 * @property getFilteredListSize() returns size of mArrayList list
 * NOTE: always use these predefined methods of this adapter
 * @Class GenericDiffUtils --> thanks to DiffUtils now we don't have to notify Adapter every time
 */

abstract class GenericRecyclerViewAdapterWithFilterDiffUtils<T, D>() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    var isShowingProgress = false

    private var mArrayList: ArrayList<T> = arrayListOf()
    private var mFilterList: ArrayList<T> = arrayListOf()

    private var searchQuery: String = ""

    abstract val layoutItemResId: Int

    abstract fun onBindData(model: T, position: Int, dataBinding: D)

    abstract fun onItemClick(model: T, position: Int)

    abstract fun onFilterQueryReceived(nonEmptyQuery: CharSequence): ArrayList<T>

//    abstract fun onFilterComplete(query: CharSequence?, result: ArrayList<T>)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutItemResId,
            parent,
            false
        )
        return ItemViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(
            mFilterList[position], holder.adapterPosition,
            dataBinding = (holder as GenericRecyclerViewAdapterWithFilterDiffUtils<*, *>.ItemViewHolder).mDataBinding as D
        )

        (holder.mDataBinding as ViewDataBinding).root.setOnClickListener {
            onItemClick(
                mFilterList[position],
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return getFilteredListSize()
    }

    fun getFilteredListSize(): Int = mFilterList.size

    fun getOriginalListSize(): Int = mArrayList.size

    private fun setData(newList: ArrayList<T>, oldList: ArrayList<T>) {
        val differ = MyDiffUtils<T>(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(differ)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItems(item: ArrayList<T>) {
        if (searchQuery.isEmpty()) {
            var oldList = arrayListOf<T>()
            oldList.addAll(mArrayList)
            val newList = arrayListOf<T>()
            newList.addAll(mArrayList)
            newList.addAll(item)
            mArrayList = newList
            mFilterList = mArrayList
            setData(newList, oldList)
            return
        }

        var oldList = arrayListOf<T>()
        oldList.addAll(mArrayList)
        val newList = arrayListOf<T>()
        newList.addAll(mArrayList)
        newList.addAll(item)
        mArrayList = newList
        filter.filter(searchQuery.trim())
    }

    fun getItem(position: Int): T {
        return mFilterList[position]
    }

    fun removeItem(position: Int) {
        val oldList = arrayListOf<T>()
        oldList.addAll(mFilterList)
        mArrayList.remove(mFilterList.removeAt(position))
        setData(mFilterList, oldList)
    }

    fun updateItem(model: T, position: Int) {
        val oldList = arrayListOf<T>()
        oldList.addAll(mFilterList)
        mArrayList[mArrayList.indexOf(mFilterList[position])] = model
        mFilterList[position] = model
        setData(mFilterList, oldList)
    }

    fun clearList() {
        mArrayList.clear()
        val oldList = arrayListOf<T>()
        oldList.addAll(mFilterList)
        mFilterList.clear()
        setData(arrayListOf<T>(), oldList)
    }

    fun refreshWithNewList(list: ArrayList<T>) {
        clearList()
        addItems(list)
    }

    private fun updateFilterList(filterList: ArrayList<T>) {
        mFilterList = filterList
        notifyDataSetChanged()
    }

    fun getCopyOfMainList(): ArrayList<T> {
        val newList: ArrayList<T> = arrayListOf()
        newList.addAll(mArrayList)
        return newList
    }

    internal inner class ItemViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var mDataBinding: D = binding as D
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                searchQuery = constraint?.trim().toString() ?: ""
                val filterResults = FilterResults()
                if (searchQuery.trim().isEmpty()) {
                    filterResults.values = mArrayList
                    return filterResults
                }
                filterResults.values = onFilterQueryReceived(constraint ?: "")
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null)
                    updateFilterList(results.values as ArrayList<T>)
            }
        }
    }

    class MyDiffUtils<T>(
        private val oldList: ArrayList<T>,
        private val newList: ArrayList<T>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return false
        }
    }
}
