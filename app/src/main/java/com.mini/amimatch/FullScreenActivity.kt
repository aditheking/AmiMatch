package com.mini.amimatch

import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import jp.shts.android.storiesprogressview.StoriesProgressView.StoriesListener

class FullScreenActivity : AppCompatActivity() {

    private lateinit var fullscreenImageView: ImageView
    private lateinit var storiesProgressView: StoriesProgressView

    private var storyUrls: ArrayList<String>? = null
    private var currentStoryIndex: Int = 0
    private var isPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        fullscreenImageView = findViewById(R.id.fullscreenImageView)
        storiesProgressView = findViewById(R.id.storiesProgressView)

        storyUrls = intent.getStringArrayListExtra("storyUrls")
        currentStoryIndex = intent.getIntExtra("position", 0)

        // Set up stories progress view
        storiesProgressView.setStoriesCount(storyUrls?.size ?: 0)
        storiesProgressView.setStoryDuration(3000L)
        storiesProgressView.setStoriesListener(object : StoriesListener {
            override fun onNext() {
                goToNextStory()
            }

            override fun onPrev() {
                goToPreviousStory()
            }

            override fun onComplete() {
            }
        })

        displayStory(currentStoryIndex)

        storiesProgressView.startStories(currentStoryIndex)

        var downTime: Long = 0
        var upTime: Long = 0

        fullscreenImageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downTime = System.currentTimeMillis()
                    isPaused = true
                    storiesProgressView.pause()
                }
                MotionEvent.ACTION_UP -> {
                    upTime = System.currentTimeMillis()
                    if (upTime - downTime < 200) {
                        if (event.x < fullscreenImageView.width / 2) {
                            goToPreviousStory()
                        } else {
                            goToNextStory()
                        }
                    } else {
                        isPaused = false
                        storiesProgressView.resume()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    isPaused = false
                    storiesProgressView.resume()
                }
            }
            true
        }
    }

    override fun onDestroy() {
        storiesProgressView.destroy()
        super.onDestroy()
    }

    private fun displayStory(index: Int) {
        if (storyUrls != null && index >= 0 && index < storyUrls!!.size) {
            Picasso.get().load(storyUrls!![index]).into(fullscreenImageView)
        }
    }

    private fun goToNextStory() {
        currentStoryIndex = (currentStoryIndex + 1) % (storyUrls?.size ?: 1)
        displayStory(currentStoryIndex)
        storiesProgressView.skip()
    }

    private fun goToPreviousStory() {
        currentStoryIndex = if (currentStoryIndex - 1 < 0) {
            (storyUrls?.size ?: 1) - 1
        } else {
            currentStoryIndex - 1
        }
        displayStory(currentStoryIndex)
        storiesProgressView.reverse()
    }
}
