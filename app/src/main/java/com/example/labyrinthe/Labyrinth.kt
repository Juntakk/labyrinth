package com.example.labyrinthe
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import java.lang.Math.pow
import java.util.Random
import kotlin.math.pow
import kotlin.math.sqrt


class Labyrinth : View, SensorEventListener {
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    private var prevX:Float = 0f
    private var prevY:Float = 0f

    private lateinit var ball : Ball
    private lateinit var hole : Hole

    private var walls : ArrayList<Wall> = ArrayList()
    private var holes : ArrayList<Hole> = ArrayList()

    private var finishHoleRadius : Int = 100
    private var finishHoleX:Float = 0f
    private var finishHoleY:Float = 0f

    private var gameOver : Boolean = false
    private var random : Random =  Random()
    private var labCTX: Context = this.context

    private val sensorManager: SensorManager = labCTX.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor

    init{
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        viewportWidth = resources.displayMetrics.widthPixels
        viewportHeight = resources.displayMetrics.heightPixels
    }
    constructor(ctx : Context) : super(ctx) {
        createBall()

        createFinishHole()

        for (i in 1 .. 10){
            createWalls()
        }
        for(i in 1 .. 4){
            createHoles();
        }
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }
    private fun lineCircle(x1: Float, y1: Float, x2: Float, y2: Float, cx: Float, cy: Float, r: Float): Boolean {

        // get length of the line
        var distX = x1 - x2
        var distY = y1 - y2
        val len = sqrt(distX * distX + distY * distY)

        // get dot product of the line and circle
        val dot: Float = ((cx - x1) * (x2 - x1) + (cy - y1) * (y2 - y1)) / len.pow(2)

        // find the closest point on the line
        val closestX = x1 + dot * (x2 - x1)
        val closestY = y1 + dot * (y2 - y1)

        // get distance to closest point
        distX = closestX - cx
        distY = closestY - cy
        val distance = sqrt(distX * distX + distY * distY)
        return distance <= r
    }
    private fun isCollidingWithWalls(ballX: Float, ballY: Float, ballRadius:Float): Boolean {
        for (wall in walls) {
            if((lineCircle(wall.posXStart.toFloat(), wall.posYStart.toFloat(), wall.posXEnd.toFloat(), wall.posYEnd.toFloat(), ballX,ballY, ballRadius) ||
                        lineCircle(wall.posXStart.toFloat(), wall.posYStart.toFloat(), wall.posXEnd.toFloat(), wall.posYEnd.toFloat(), ballX,ballY, ballRadius))){
                return true
            }
        }
        return false
    }
    private fun collisionWithHoles() : Boolean{
        for (hole in holes) {
            val distance = calculateDistance(ball.posX, ball.posY, hole.posX, hole.posY)
            if (distance <= ball.radius) {
                return true
            }
        }
        return false
    }
    private fun createWalls() {
        val isVertical = random.nextBoolean()
        val wallLength = 300
        val minDistance = 200 // Minimum distance between walls
        var wallXStart: Int
        var wallYStart: Int

        // Keep generating wall positions until a valid position is found
        do {
            wallXStart = random.nextInt(viewportWidth - wallLength)
            wallYStart = random.nextInt(viewportHeight - wallLength)

            // Check for collisions with existing walls
            var isCollision = false
            for (existingWall in walls) {
                if (isVertical) {
                    if (wallXStart < existingWall.posXEnd + minDistance &&
                        wallXStart + wallLength > existingWall.posXStart - minDistance &&
                        wallYStart < existingWall.posYEnd &&
                        wallYStart + wallLength > existingWall.posYStart
                    ) {
                        isCollision = true
                        break
                    }
                } else {
                    if (wallYStart < existingWall.posYEnd + minDistance &&
                        wallYStart + wallLength > existingWall.posYStart - minDistance &&
                        wallXStart < existingWall.posXEnd &&
                        wallXStart + wallLength > existingWall.posXStart
                    ) {
                        isCollision = true
                        break
                    }
                }
            }
        } while (isCollision)

        // Calculate wall end positions
        val wallXEnd = if (isVertical) wallXStart else wallXStart + wallLength
        val wallYEnd = if (isVertical) wallYStart + wallLength else wallYStart

        // Create and add wall to list
        val wall = Wall(wallXStart, wallYStart, wallXEnd, wallYEnd, wallLength, isVertical)
        walls.add(wall)
    }
    private fun createBall() {
        var ballX: Int
        var ballY: Int
        var isCollision: Boolean

        do {
            isCollision = false
            ballX = random.nextInt(viewportWidth - 160) + 60
            ballY = random.nextInt(viewportHeight - 160) + 60

            // Check for collisions with walls
            for (wall in walls) {
                if (ballX < wall.posXEnd && ballX + 60 > wall.posXStart &&
                    ballY < wall.posYEnd && ballY + 60 > wall.posYStart
                ) {
                    isCollision = true
                    break
                }
            }

            // Check for collisions with existing holes
            for (existingHole in holes) {
                if (ballX < existingHole.posX + 120 && ballX + 60 > existingHole.posX &&
                    ballY < existingHole.posY + 120 && ballY + 60 > existingHole.posY
                ) {
                    isCollision = true
                    break
                }
            }
        } while (isCollision)

        ball = Ball(ballX.toFloat(), ballY.toFloat(), 60)
    }
    private fun createHoles() {
        var holeX: Int
        var holeY: Int
        var isCollision: Boolean

        do {
            isCollision = false
            holeX = random.nextInt(viewportWidth - 180) + 80
            holeY = random.nextInt(viewportHeight - 180) + 80

            // Check for collisions with walls
            for (wall in walls) {
                if (holeX < wall.posXEnd && holeX + 120 > wall.posXStart &&
                    holeY < wall.posYEnd && holeY + 120 > wall.posYStart
                ) {
                    isCollision = true
                    break
                }
            }

            // Check for collisions with existing holes
            for (existingHole in holes) {
                if (holeX < existingHole.posX + 120 && holeX + 120 > existingHole.posX &&
                    holeY < existingHole.posY + 120 && holeY + 120 > existingHole.posY
                ) {
                    isCollision = true
                    break
                }
            }
            // Check for collisions with ball
            if (holeX < ball.posX + 60 && holeX + 120 > ball.posX &&
                holeY < ball.posY + 60 && holeY + 120 > ball.posY)
            {
                isCollision = true
            }

        } while (isCollision)
        hole = Hole(holeX.toFloat(), holeY.toFloat())
        holes.add(hole)
    }
    private fun createFinishHole(){
        var holeX: Int
        var holeY: Int
        var isCollision: Boolean
        do {
            isCollision = false
            holeX = random.nextInt(viewportWidth - 180) + 80
            holeY = random.nextInt(viewportHeight - 180) + 80

            // Check for collisions with walls
            for (wall in walls) {
                if (holeX < wall.posXEnd && holeX + 120 > wall.posXStart &&
                    holeY < wall.posYEnd && holeY + 120 > wall.posYStart
                ) {
                    isCollision = true
                    break
                }
            }
            // Check for collisions with existing holes
            for (existingHole in holes) {
                if (holeX < existingHole.posX + 120 && holeX + 120 > existingHole.posX &&
                    holeY < existingHole.posY + 120 && holeY + 120 > existingHole.posY
                ) {
                    isCollision = true
                    break
                }
            }
            // Check for collisions with ball
            if (holeX < ball.posX + 60 && holeX + 120 > ball.posX &&
                holeY < ball.posY + 60 && holeY + 120 > ball.posY)
            {
                isCollision = true
            }

        } while (isCollision)
        finishHoleX = random.nextInt(viewportWidth - 120) + 60f
        finishHoleY = random.nextInt(viewportHeight - 120) + 60f
    }

    private fun endGame() {
        val distanceFromWinHole = calculateDistance(ball.posX, ball.posY, finishHoleX, finishHoleY)
        val threshold = 100
        val builder: AlertDialog.Builder = AlertDialog.Builder(labCTX)

        if (distanceFromWinHole <= threshold || collisionWithHoles()) {

            builder.setTitle("Game Over")
                .setMessage("Thanks for playing")
                .setPositiveButton("Ok") { _, _ ->
                    (labCTX as Activity).finish()
                }
                .create()

        }
        builder.show()
        gameOver = true
    }

    override fun onSensorChanged(event: SensorEvent) {
        var distance: Float = sqrt(event.values[0].pow(2) + event.values[1].pow(2))
        println(distance)

        val minX = 0 + ball.radius
        val minY = 0 + ball.radius
        val maxX = viewportWidth - ball.radius
        val maxY = viewportHeight - ball.radius

        val nextX = (ball.posX + event.values[0] * (distance * -5)).toInt().coerceIn(minX, maxX).toFloat()
        val nextY = (ball.posY + event.values[1] * distance * 5).toInt().coerceIn(minY, maxY).toFloat()

        if (!isCollidingWithWalls(nextX, nextY, ball.radius.toFloat())) {
            ball.posX = nextX
            ball.posY = nextY
            prevX = nextX
            prevY = nextY
        } else {
            ball.posX = prevX
            ball.posY = prevY
        }
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val p = Paint()
        val p2 = Paint()
        val p3 = Paint()
        p.color = Color.BLACK
        p2.strokeWidth = 20f
        p2.color = Color.LTGRAY
        p3.color = Color.RED

        for (wall in walls) {
            canvas.drawLine(wall.posXStart.toFloat(), wall.posYStart.toFloat(), wall.posXEnd.toFloat(), wall.posYEnd.toFloat(), p2)
        }

        for(hole in holes){
            canvas.drawCircle(hole.posX,hole.posY,80f,p3)
        }

        var p4 = Paint()
        p4.color = Color.BLUE

        canvas.drawCircle(finishHoleX, finishHoleY,finishHoleRadius.toFloat(),p4)
        canvas.drawCircle(ball.posX, ball.posY, ball.radius.toFloat(),p)
        collisionWithHoles()
        endGame()
    }
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//
//        var action: Int = event.action
//        val touchX: Float = event.x
//        val touchY: Float = event.y
//
//        val minX = 0 + ball.radius
//        val minY = 0 + ball.radius
//        val maxX = viewportWidth - ball.radius
//        val maxY = viewportHeight - ball.radius
//
//        when (action) {
//            MotionEvent.ACTION_MOVE -> {
//                val nextX = touchX.coerceIn(minX.toFloat(), maxX.toFloat())
//                val nextY = touchY.coerceIn(minY.toFloat(), maxY.toFloat())
//
//                if (!isCollidingWithWalls(nextX.toInt(), nextY.toInt(), ball.radius)) {
//                    ball.posX = nextX
//                    ball.posY = nextY
//                    prevX = nextX
//                    prevY = nextY
//                } else {
//                    ball.posX = ball.posX - 10
//                    ball.posY = ball.posY - 10
//                }
//                invalidate()
//            }
//        }
//        return true
//    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }
}