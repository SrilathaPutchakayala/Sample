package com.tm.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

class ModelBindingAdapter<T : Any, V : ViewDataBinding>(
        private var items: MutableList<T>,
        private val layoutId: Int,
        private val bindData: (V, T) -> Unit
) : RecyclerView.Adapter<ModelBindingAdapter.ViewHolder<T, V>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T, V> {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view, bindData)
    }

    override fun onBindViewHolder(holder: ViewHolder<T, V>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    operator fun get(position: Int) = items[position]

    fun replaceItems(items: MutableList<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun getItems()=items

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAllItems(newItems: MutableList<T>) {
        clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun add(item: T) {
        items.add(item)
        notifyDataSetChanged()
    }

    fun remove(item: T) {
        items.remove(item)
        notifyDataSetChanged()
    }

    class ViewHolder<in T : Any, in V : ViewDataBinding>(view: View,
                                                         private val bindData: (V, T) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val binding: V? = DataBindingUtil.bind(view)

        fun bind(item: T) {
            binding?.let {
                bindData(it, item)
                it.executePendingBindings()
            }
        }

    }
}