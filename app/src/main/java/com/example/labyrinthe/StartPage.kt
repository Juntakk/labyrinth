package com.example.labyrinthe;

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View


public class StartPage : View {
    private var startPageContext : Context

    private var screenW : Int = 0
    private var screenH : Int = 0
    private var startBtnDefault : Bitmap
    private var startBtnPressed : Bitmap
    private var playBtnState : Boolean = false

    constructor(ctx : Context):super(ctx){
        startPageContext = ctx
        startBtnDefault = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.playbutton), 250,250,false)
        startBtnPressed = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.playbutton2), 250,250,false)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var p0 = Paint()
        var bgPaint = Paint()
        bgPaint.color = Color.LTGRAY
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), bgPaint)

        if(playBtnState){
            canvas.drawBitmap(startBtnPressed, 50f,height-300f,p0)
        }else{
            canvas.drawBitmap(startBtnDefault,50f,height - 300f,p0)
        }

        var titlePaint = Paint()
        titlePaint.color = Color.BLACK
        titlePaint.textSize = 120f
        canvas.drawText("Weird Ass Labyrinth",40f,800f,titlePaint)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenH = h
        screenW = w
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action : Int = event.action
        val touchX : Int = event.x.toInt()
        val touchY : Int = event.y.toInt()

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if ((touchX > 50f &&
                            touchX < 50f + startBtnDefault.width) && (touchY > height - 300 &&
                            (touchY < height-300 + startBtnDefault.height))) {
                    playBtnState = true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (playBtnState) {
                    val gameIntent = Intent(startPageContext, GameOn::class.java)
                    startPageContext.startActivity(gameIntent)
                }
                playBtnState = false
            }
            MotionEvent.ACTION_MOVE -> {
            }
        }
        invalidate()
        return true
    }

}
