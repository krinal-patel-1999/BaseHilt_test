package com.example.kotlinflowdemo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Created By Vishal Joshi on 14/07/2021 at 10:09AM
 * Generic Adapter with loader for set recycler view
 * @param T Response model
 * @param D Item Layout binding class
 * @param P Loader Item Layout binding class
 * @property mArrayList ArrayList<T?> model type of list(its private, managed by only predefined methods of this adapter)
 * @property layoutItemResId Int layout name
 * @property layoutProgressResId Int layout name
 * @property addItems(ArrayList<T>()) Method to add items in adapter(it always append list in existing list, and it hide loader if its showing)
 * @property getItem(position) Provide item from list at given position
 * @property removeItem(position) Remove item from list at given position
 * @property updateItem(model , position) Update the given model at given position in list
 * @property clearList() clear existing list
 * @property isLastItemIsLoader() returns true if Last Item is Loader else returns false
 * @property showLoader(Boolean) shows or remove loader according to given Boolean value
 * @property refreshWithNewList(ArrayList<T>) clear existing list and add given new List
 * NOTE: always use these predefined methods of this adapter
 * @Class GenericDiffUtils --> thanks to DiffUtils now we don't have to notify Adapter every time
 */

abstract class GenericRecyclerViewAdapterWithLoaderDiffUtils<T, D, P>() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val PROGRESS_TYPE = 0
    private val ITEM_TYPE = 1
    var isShowingProgress = false

    private var mArrayList: ArrayList<T?> = arrayListOf()

    abstract val layoutItemResId: Int
    abstract val layoutProgressResId: Int

    abstract fun onBindItemData(model: T?, position: Int, dataBinding: D)

    abstract fun onBindProgressData(model: T?, position: Int, dataBinding: P)

    abstract fun onItemClick(model: T?, position: Int)

    abstract fun onProgressItemClick(model: T?, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == PROGRESS_TYPE) {
            val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                layoutProgressResId,
                parent,
                false
            )
            return ProgressViewHolder(dataBinding)
        } else {
            val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                layoutItemResId,
                parent,
                false
            )
            return ItemViewHolder(dataBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GenericRecyclerViewAdapterWithLoaderDiffUtils<*, *, *>.ItemViewHolder -> {
                onBindItemData(
                    mArrayList[position], holder.adapterPosition,
                    dataBinding = holder.mDataBinding as D
                )

                (holder.mDataBinding as ViewDataBinding).root.setOnClickListener {
                    onItemClick(
                        mArrayList[position],
                        position
                    )
                }
            }
            is GenericRecyclerViewAdapterWithLoaderDiffUtils<*, *, *>.ProgressViewHolder -> {
                onBindProgressData(
                    mArrayList[position], holder.adapterPosition,
                    dataBinding = holder.mDataBinding as P
                )

                (holder.mDataBinding as ViewDataBinding).root.setOnClickListener {
                    onProgressItemClick(
                        mArrayList[position],
                        position
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mArrayList.isNotEmpty()) {
            if (position == mArrayList.size.minus(1) && mArrayList.last() == null) {
                return PROGRESS_TYPE
            }
            return ITEM_TYPE
        }
        return ITEM_TYPE
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun listSize(): Int {
        return mArrayList.size
    }

    private fun setData(newList: ArrayList<T?>, oldList: ArrayList<T?>) {
        val differ = GenericDiffUtils<T>(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(differ)
        mArrayList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItems(item: ArrayList<T>) {
        if (mArrayList.isNotEmpty()) {
            if (isLastItemIsLoader())
                showLoader(false)
        }
        var oldList = arrayListOf<T?>()
        oldList.addAll(mArrayList)
        val newList = arrayListOf<T?>()
        newList.addAll(mArrayList)
        newList.addAll(item)
        setData(newList, oldList)
    }

    fun getItem(position: Int): T? {
        return mArrayList[position]
    }

    fun removeItem(position: Int) {
        val oldList = arrayListOf<T?>()
        oldList.addAll(mArrayList)
        val newList = arrayListOf<T?>()
        newList.addAll(mArrayList)
        newList.removeAt(position)
        setData(newList, oldList)
    }

    fun updateItem(model: T, position: Int) {
        val oldList = arrayListOf<T?>()
        oldList.addAll(mArrayList)
        val newList = arrayListOf<T?>()
        newList.addAll(mArrayList)
        newList[position] = model
        setData(newList, oldList)
    }

    fun clearList() {
        setData(arrayListOf<T?>(), mArrayList)
    }

    fun refreshWithNewList(list: ArrayList<T>) {
        clearList()
        addItems(list)
    }

    fun isLastItemIsLoader(): Boolean {
        if (isShowingProgress && mArrayList[listSize().minus(1)] == null)
            return true
        return false
    }

    fun showLoader(flag: Boolean) {
        isShowingProgress = flag
        val oldList = arrayListOf<T?>()
        oldList.addAll(mArrayList)
        val newList = arrayListOf<T?>()
        newList.addAll(mArrayList)
        if (flag) {
            if (newList.last() != null)
                newList.add(null)

        } else {
            if (newList.last() == null)
                newList.removeLast()
        }
        setData(newList, oldList)
    }

    internal inner class ItemViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var mDataBinding: D = binding as D
    }

    internal inner class ProgressViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var mDataBinding: P = binding as P
    }

    class GenericDiffUtils<T>(
        private val oldList: ArrayList<T?>,
        private val newList: ArrayList<T?>
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
