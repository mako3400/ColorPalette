package pl.eduweb.colorpalette

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.Palette
import androidx.appcompat.widget.LinearLayoutManager
import androidx.appcompat.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View

import butterknife.Bind
import butterknife.ButterKnife

class PaletteActivity : AppCompatActivity(), ColorAdapter.ColorClickedListener {

    @Bind(R.id.colorsRecyclerView)
    internal var colorsRecyclerView: RecyclerView? = null

    private var fab: FloatingActionButton? = null

    private var colorAdapter: ColorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_palette)
        ButterKnife.bind(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab!!.setOnClickListener(View.OnClickListener { addColor() })
        colorAdapter = ColorAdapter(layoutInflater,
                PreferenceManager.getDefaultSharedPreferences(this))
        colorAdapter!!.setColorClickedListener(this)
        colorsRecyclerView!!.setLayoutManager(LinearLayoutManager(this))
        colorsRecyclerView!!.setAdapter(colorAdapter)
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.getAdapterPosition()
                colorAdapter!!.remove(position)

            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(colorsRecyclerView)

    }

    private fun addColor() {
        val intent = Intent(this@PaletteActivity, ColorActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_CREATE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_palette, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_add) {
            addColor()
            return true
        } else if (id == R.id.action_clear) {
            colorAdapter!!.clear()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CREATE) {
                val colorInHex = data!!.getStringExtra(ColorActivity.COLOR_IN_HEX_KEY)
                val position = colorAdapter!!.add(colorInHex)
                Snackbar.make(fab, getString(R.string.new_color_created, colorInHex), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, View.OnClickListener {
                            colorAdapter!!.remove(position)
                            colorAdapter!!.notifyItemRemoved(position)
                        })
                        .show()

            } else if (requestCode == REQUEST_CODE_EDIT) {
                val colorInHex = data!!.getStringExtra(ColorActivity.COLOR_IN_HEX_KEY)
                val oldColor = data.getStringExtra(ColorActivity.OLD_COLOR_KEY)

                colorAdapter!!.replace(oldColor, colorInHex)
            }
        }

    }

    override fun onColorClicked(colorInHex: String) {
        val intent = Intent(this, ColorActivity::class.java)
        intent.putExtra(ColorActivity.OLD_COLOR_KEY, colorInHex)
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    companion object {

        val LOG_TAG = PaletteActivity::class.java.simpleName
        val REQUEST_CODE_CREATE = 1
        val REQUEST_CODE_EDIT = 2

        fun getTextColorFromColor(color: Int): Int {
            return Palette.Swatch(color, 1).getTitleTextColor()
        }
    }
}
