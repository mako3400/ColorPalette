package pl.eduweb.colorpalette

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.json.JSONArray
import org.json.JSONException

import java.util.ArrayList

class ColorAdapter(private val layoutInflater: LayoutInflater, private val sharedPreferences: SharedPreferences) : RecyclerView.Adapter<ColorViewHolder>() {

    private val colors = ArrayList<String>()

    private var colorClickedListener: ColorClickedListener? = null

    val itemCount: Int
        get() = colors.size

    init {

        val colorsJson = sharedPreferences.getString(COLORS_KEY, "[]")
        try {
            val jsonArray = JSONArray(colorsJson)
            for (i in 0 until jsonArray.length()) {
                colors.add(i, jsonArray.getString(i))
            }
            notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = layoutInflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        return ColorViewHolder(view, this)
    }

    fun onBindViewHolder(holder: ColorViewHolder, position: Int) {

        val colorInHex = colors[position]

        holder.color = colorInHex
    }

    fun add(color: String): Int {
        colors.add(color)
        val position = colors.size - 1
        notifyItemInserted(position)

        storeInPreferences()
        return position
    }

    private fun storeInPreferences() {
        try {
            val jsonArray = JSONArray()
            for (i in colors.indices) {
                jsonArray.put(i, colors[i])
            }

            val editor = sharedPreferences.edit()
            editor.putString(COLORS_KEY, jsonArray.toString())
            editor.apply()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun remove(position: Int) {
        colors.removeAt(position)
        storeInPreferences()
    }

    fun setColorClickedListener(colorClickedListener: ColorClickedListener) {
        this.colorClickedListener = colorClickedListener
    }

    fun clicked(position: Int) {
        if (colorClickedListener != null) {
            colorClickedListener!!.onColorClicked(colors[position])
        }
    }

    fun replace(oldColor: String, colorInHex: String) {
        val indexOf = colors.indexOf(oldColor)
        colors[indexOf] = colorInHex
        notifyItemChanged(indexOf)
        storeInPreferences()
    }

    fun clear() {
        colors.clear()
        notifyDataSetChanged()

        sharedPreferences.edit()
                .clear()
                .apply()
    }

    interface ColorClickedListener {
        fun onColorClicked(colorInHex: String)
    }

    companion object {

        val COLORS_KEY = "colors"
    }
}

internal class ColorViewHolder(itemView: View, private val colorAdapter: ColorAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var color: String? = null
        set(color) {
            field = color
            textView.text = color
            val backgroundColor = Color.parseColor(color)
            textView.setBackgroundColor(backgroundColor)
            textView.setTextColor(PaletteActivity.getTextColorFromColor(backgroundColor))
        }
    private val textView: TextView

    init {
        textView = itemView as TextView
        textView.setOnClickListener(this)

    }

    override fun onClick(v: View) {

        colorAdapter.clicked(getAdapterPosition())

    }

}