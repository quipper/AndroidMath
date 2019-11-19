package com.agog.latexmathsample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.agog.mathdisplay.MTFontManager
import com.agog.mathdisplay.MTMathView
import com.agog.mathdisplay.MTMathView.MTMathViewMode
import com.agog.mathdisplay.parse.MTLineStyle
import com.agog.mathdisplay.parse.MTMathListBuilder
import com.agog.mathdisplay.render.MTTypesetter
import com.agog.mathdisplay.render.trim
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    var menufont: MenuItem? = null
    var menumode: MenuItem? = null
    var menusize: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createEquations()
    }

    private fun createBitmap(): Bitmap? {
        val font = MTFontManager.latinModernFontWithSize(40f)

        val latexString = """\vec \bf V_1 \times \vec \bf V_2 =  \begin{vmatrix}
    \hat \imath &\hat \jmath &\hat k \\
    \frac{\partial X}{\partial u} &  \frac{\partial Y}{\partial u} & 0 \\
    \frac{\partial X}{\partial v} &  \frac{\partial Y}{\partial v} & 0
    \end{vmatrix}"""

//        val latexString = "e = mc^2"

        val mathList = MTMathListBuilder.buildFromString(latexString)

        if (mathList != null && font != null) {
            val BITMAPWIDTH = 640
            val BITMAPHEIGHT = 480
            val bitmap = Bitmap.createBitmap(BITMAPWIDTH, BITMAPHEIGHT, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.translate(0.0f, BITMAPHEIGHT.toFloat())
            canvas.scale(1.0f, -1.0f)
            canvas.translate(100.0f, 100.0f) // We shift this to catch any coordinate system errors

            val display = MTTypesetter.createLineForMathList(mathList, font, MTLineStyle.KMTLineStyleText)
            display.draw(canvas)

//            saveBitmap("testRandom.png", bitmap)
            return bitmap.trim(margin = 20)
        }

        return null
    }

//    private fun saveBitmap(filename: String, bitmap: Bitmap) {
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + filename
//
//        FileOutputStream(path).use {
//            try {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) // bmp is your Bitmap instance
//                // PNG is a lossless format, the compression factor (100) is ignored
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                try {
//                    it.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menufont = menu.findItem(R.id.menufont)
        menumode = menu.findItem(R.id.menumode)
        menusize = menu.findItem(R.id.menusize)
        return true
    }


    var sampleEquations: MutableList<MTMathView> = mutableListOf()
    var defaultFontSize = 20.0f  // Starting fontsize dp pixels

    fun createEquations() {
        val layoutPadHoriz = 24
        val layoutPadVert = 42

        // Some padding around the equations. Cosmetic
        val layoutParams = LinearLayout.LayoutParams(0, 0)
        layoutParams.setMargins(layoutPadHoriz, layoutPadVert, layoutPadHoriz, layoutPadVert)

        /*
             We read a plain text file with LaTeX strings and comments
             Each LaTeX string is placed in a separate MTMathViews.
             Comments are placed in TextViews.
             All are placed into a view to scroll vertically and horizontally.
         */
        val inputStream = getResources().openRawResource(R.raw.samples)
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

        lineList.forEach {
            if (it.isNotBlank()) {
                if (it[0] == '#') {
                    val tv = TextView(this)
                    tv.text = it.trim()
                    tv.setTextColor(Color.DKGRAY)
                    println("textSize ${tv.textSize}")
                    mainLayout.addView(tv)
                } else {
                    val mathView = MTMathView(this)
                    mathView.fontSize = MTMathView.convertDpToPixel(defaultFontSize)
                    mathView.latex = it
                    sampleEquations.add(mathView)
                    mainLayout.addView(mathView, layoutParams)
                }
            }
        }


    }


    fun applyfont(fontname: String) {
        for (eq in sampleEquations) {
            eq.font = MTFontManager.fontWithName(fontname, eq.fontSize)
        }
    }

    fun applyfontsize(fontsize: Float) {
        val pixelfontsize = MTMathView.convertDpToPixel(fontsize)
        for (eq in sampleEquations) {
            eq.font = MTFontManager.fontWithName(eq.font!!.name, pixelfontsize)
        }
    }

    fun createBitmapView() {
        val latexBitmap = createBitmap()

        val iv = ImageView(this)
        iv.setBackgroundColor(Color.parseColor("#d3d3d3"))
        iv.setImageBitmap(latexBitmap)
        mainLayout.addView(iv)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            // Font menu
            R.id.fontdefault -> {
                applyfont("latinmodern-math")
                fontmenucheck(R.id.fontdefault)
                return true
            }
            R.id.fonttermes -> {
                applyfont("texgyretermes-math")
                fontmenucheck(R.id.fonttermes)
                return true
            }
            R.id.fontxits -> {
                applyfont("xits-math")
                fontmenucheck(R.id.fontxits)
                return true
            }
            // Size Menu
            R.id.f12 -> {
                applyfontsize(12.0f)
                sizemenucheck(R.id.f12)
                return true
            }
            R.id.f16 -> {
                applyfontsize(16.0f)
                sizemenucheck(R.id.f16)
                return true
            }
            R.id.f20 -> {
                applyfontsize(20.0f)
                sizemenucheck(R.id.f20)
                return true
            }
            R.id.f40 -> {
                applyfontsize(40.0f)
                sizemenucheck(R.id.f40)
                return true
            }
            // Mode Menu
            R.id.modedisplay -> {
                for (eq in sampleEquations) {
                    eq.labelMode = MTMathViewMode.KMTMathViewModeDisplay
                }
                modemenucheck(R.id.modedisplay)
                return true
            }
            R.id.modetext -> {
                for (eq in sampleEquations) {
                    eq.labelMode = MTMathViewMode.KMTMathViewModeText
                }
                modemenucheck(R.id.modetext)
                return true
            }
            R.id.modebitmap -> {
                // clear the main layout
                mainLayout.removeAllViewsInLayout()
                sampleEquations.clear()
                mainLayout.invalidate()

                // load a bitmap
                createBitmapView()

                modemenucheck(R.id.modebitmap)
                return true
            }
            // Good for profiling
            R.id.modereload -> {
                mainLayout.removeAllViewsInLayout()
                sampleEquations.clear()
                mainLayout.invalidate()
                // Nice to see the screen show blank before adding back equations
                mainLayout.postDelayed({ createEquations() }, 10)
                sizemenucheck(R.id.f20)
                modemenucheck(R.id.modedisplay)
                fontmenucheck(R.id.fontdefault)
                return true
            }


            // Color Menu
            R.id.colorblack -> {
                for (eq in sampleEquations) {
                    eq.textColor = Color.BLACK
                }
                return true
            }
            R.id.colorpurple -> {
                for (eq in sampleEquations) {
                    eq.textColor = Color.MAGENTA
                }
                return true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    // Utility functions to make sure menu checked state is correct
    fun menucheck(menu: MenuItem, thisone: Int) {
        val m = menu.subMenu
        for (i in 0 until m.size()) {
            val mi = m.getItem(i)
            if (mi.itemId == thisone) {
                mi.setChecked(true)
            } else {
                mi.setChecked(false)
            }
        }
    }

    fun fontmenucheck(thisone: Int) {
        if (menufont != null) {
            menucheck(menufont!!, thisone)
        }
    }

    fun modemenucheck(thisone: Int) {
        if (menumode != null) {
            menucheck(menumode!!, thisone)
        }
    }

    fun sizemenucheck(thisone: Int) {
        if (menusize != null) {
            menucheck(menusize!!, thisone)
        }
    }


}
