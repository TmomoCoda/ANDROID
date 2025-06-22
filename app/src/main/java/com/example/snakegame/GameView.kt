package com.example.snakegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val gridSize = 20
    private val snake = mutableListOf(Point(gridSize / 2, gridSize / 2))
    private var food = Point(Random.nextInt(gridSize), Random.nextInt(gridSize))
    private var direction = Point(1, 0)

    private val cellPaint = Paint().apply {
        color = Color.GREEN
    }
    private val foodPaint = Paint().apply {
        color = Color.RED
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateDelay = 300L
    private val updater = object : Runnable {
        override fun run() {
            update()
            invalidate()
            handler.postDelayed(this, updateDelay)
        }
    }

    private var lastX = 0f
    private var lastY = 0f

    init {
        handler.postDelayed(updater, updateDelay)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellSize = width.coerceAtMost(height) / gridSize.toFloat()
        for (p in snake) {
            canvas.drawRect(
                p.x * cellSize,
                p.y * cellSize,
                (p.x + 1) * cellSize,
                (p.y + 1) * cellSize,
                cellPaint
            )
        }
        canvas.drawRect(
            food.x * cellSize,
            food.y * cellSize,
            (food.x + 1) * cellSize,
            (food.y + 1) * cellSize,
            foodPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.x - lastX
                val dy = event.y - lastY
                if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                    direction = if (dx > 0) Point(1, 0) else Point(-1, 0)
                } else {
                    direction = if (dy > 0) Point(0, 1) else Point(0, -1)
                }
            }
        }
        return true
    }

    private fun update() {
        val newHead = Point(snake.first().x + direction.x, snake.first().y + direction.y)
        if (newHead.x !in 0 until gridSize || newHead.y !in 0 until gridSize || snake.contains(newHead)) {
            handler.removeCallbacks(updater)
            Toast.makeText(context, "Game Over", Toast.LENGTH_SHORT).show()
            return
        }
        snake.add(0, newHead)
        if (newHead == food) {
            placeFood()
        } else {
            snake.removeLast()
        }
    }

    private fun placeFood() {
        do {
            food = Point(Random.nextInt(gridSize), Random.nextInt(gridSize))
        } while (snake.contains(food))
    }
}
