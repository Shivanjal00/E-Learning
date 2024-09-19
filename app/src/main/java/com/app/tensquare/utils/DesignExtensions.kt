package com.app.tensquare.utils

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

fun Context.setTileData(
    intent: Bundle,
    txtSubjectModuleName: TextView,
    imgSubject: ImageView,
    llSubjectTile: LinearLayout
) {
    when (intent.getInt("SUBJECT")) {
        SUBJECT_MATHS -> {
            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_maths)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_maths))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_maths)

        /*    val paddingDp = 30
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel
            )   */
        }
        SUBJECT_SCIENCE -> {
            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_latest_updates)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_science))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_latest_update)

//            val paddingDp = 30
            val paddingDp = 25
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel
            )
        }
        SUBJECT_PHYSICS -> {
            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_physics)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_physics))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_physics)
        }
        SUBJECT_CHEMISTRY -> {
            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_chemistry)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_chemistry))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_chemistry)
        }
        SUBJECT_BIOLOGY -> {
            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_biology)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_biology))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_biology)
        }
        MODEL_PAPER -> {
            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_model_paper)

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.img_model_paper))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_tile_model)

            val paddingDp = 30
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel)
        }

        SUBJECT_SANSKRIT -> {

            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_science)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.san_img_2))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_science)

        /*    val paddingDp = 30
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel
            )   */

        }
        SUBJECT_HINDI -> {

            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_biology)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.hindi_img_2))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_biology)

        /*    val paddingDp = 30
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel
            )   */

        }
        SUBJECT_SOCIAL_STUDIES -> {

            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_prev_paper)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.app.tensquare.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.ss_img_2))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_previous_year_papers)

            val paddingDp = 25
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel
            )

        }
        SUBJECT_ENGLISH -> {

            txtSubjectModuleName.background =
                getDrawable(com.app.tensquare.R.drawable.bg_lower_tile_maths)
//            txtSubjectModuleName.setTextColor(
//                ContextCompat.getColor(
//                    this,
//                    com.app.tensquare.R.color.white
//                )
//            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.tensquare.R.drawable.eng_img_2))
            llSubjectTile.background =
                resources.getDrawable(com.app.tensquare.R.drawable.bg_maths)

            val paddingDp = 25
            val density: Float = this.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(
                paddingPixel,
                paddingPixel,
                paddingPixel,
                paddingPixel
            )

        }

        /*PREVIOUS_YEAR_PAPER -> {
            txtSubjectModuleName.background =
                getDrawable(com.app.elearning.R.drawable.bg_lower_tile_prev_paper)
            txtSubjectModuleName.setTextColor(
                ContextCompat.getColor(
                    this@PreviousPaperInSubjectActivity,
                    com.app.elearning.R.color.white
                )
            )

            imgSubject.setImageDrawable(resources.getDrawable(com.app.elearning.R.drawable.img_previous_paper))
            llSubjectTile.background =
                resources.getDrawable(com.app.elearning.R.drawable.bg_previous_year_papers)


            val paddingDp = 30
            val density: Float = context.resources.displayMetrics.density
            val paddingPixel: Int = (paddingDp.toInt() * density).toInt()
            imgSubject.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel)
        }
        else -> ""*/
    }
}
