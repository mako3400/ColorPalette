package pl.eduweb.colorpalette

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

import java.util.Random

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick

class ColorActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.redSeekBar)
    internal var redSeekBar: SeekBar? = null
    @Bind(R.id.greenSeekBar)
    internal var greenSeekBar: SeekBar? = null
    @Bind(R.id.blueSeekBar)
    internal var blueSeekBar: SeekBar? = null
    @Bind(R.id.generateButton)
    internal var generateButton: Button? = null
    @Bind(R.id.saveButton)
    internal var saveButton: Button? = null
    @Bind(R.id.colorLinearLayout)
    internal var colorLinearLayout: LinearLayout? = null
    @Bind(R.id.redLabel)
    internal var redLabel: TextView? = null
    @Bind(R.id.greenLabel)
    internal var greenLabel: TextView? = null
    @Bind(R.id.blueLabel)
    internal var blueLabel: TextView? = null

    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0

    private var actionBar: ActionBar? = null

    private val random = Random()
    private var oldColor: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        ButterKnife.bind(this)

        actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        redSeekBar!!.setOnSeekBarChangeListener(this)
        greenSeekBar!!.setOnSeekBarChangeListener(this)
        blueSeekBar!!.setOnSeekBarChangeListener(this)

        val intent = intent
        oldColor = intent.getStringExtra(OLD_COLOR_KEY)
        if (oldColor != null) {
            val color = Color.parseColor(oldColor)
            red = Color.red(color)
            green = Color.green(color)
            blue = Color.blue(color)

            updateSeekBars()
            updateBackgroundColor()

            generateButton!!.visibility = View.GONE
            actionBar!!.setTitle(R.string.edit_color)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @OnClick(R.id.generateButton)
    fun generate() {

        red = random.nextInt(256)
        green = random.nextInt(256)
        blue = random.nextInt(256)

        updateSeekBars()
        updateBackgroundColor()

    }

    private fun updateSeekBars() {
        redSeekBar!!.progress = red
        greenSeekBar!!.progress = green
        blueSeekBar!!.progress = blue
    }

    private fun updateBackgroundColor() {
        val color = Color.rgb(red, green, blue)
        val textColor = PaletteActivity.getTextColorFromColor(color)

        redLabel!!.setTextColor(textColor)
        greenLabel!!.setTextColor(textColor)
        blueLabel!!.setTextColor(textColor)

        colorLinearLayout!!.setBackgroundColor(color)
    }

    @OnClick(R.id.saveButton)
    fun save() {

        val data = Intent()
        data.putExtra(COLOR_IN_HEX_KEY, String.format("#%02X%02X%02X", red, green, blue))
        if (oldColor != null) {
            data.putExtra(OLD_COLOR_KEY, oldColor)
        }
        setResult(Activity.RESULT_OK, data)
        finish()

    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.redSeekBar -> red = progress
            R.id.greenSeekBar -> green = progress
            R.id.blueSeekBar -> blue = progress
        }
        updateBackgroundColor()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(RED, red)
        outState.putInt(GREEN, green)
        outState.putInt(BLUE, blue)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        red = savedInstanceState.getInt(RED)
        green = savedInstanceState.getInt(GREEN)
        blue = savedInstanceState.getInt(BLUE)
    }

    companion object {

        private val LOG_TAG = ColorActivity::class.java.simpleName
        val RED = "red"
        val GREEN = "green"
        val BLUE = "blue"

        val OLD_COLOR_KEY = "old_color"
        val COLOR_IN_HEX_KEY = "color_in_hex"
    }
}
