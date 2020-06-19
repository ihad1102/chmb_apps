package com.im.ui.video

import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.ui.BaseActivity
import com.im.R
import com.im.databinding.ActivityVideoBinding
import com.im.router.ImRouter

@Route(path = ImRouter.VideoViewActivity)
class VideoActivity : BaseActivity<ActivityVideoBinding>() {
    override var isFullScreen: Boolean = true

    private lateinit var mVideoView: VideoView
    private var mSavedCurrentPosition: Int = 0
    @Autowired @JvmField var videoPath : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.activity_video)
        mVideoView = contentView.videoviewVideo

        val mediaController = MediaController(this)
        mediaController.setAnchorView(mVideoView)

        mVideoView.setMediaController(mediaController)
        mVideoView.setVideoPath(videoPath)
        mVideoView.setOnPreparedListener {
            mVideoView.requestLayout()
            if (mSavedCurrentPosition != 0) {
                mVideoView.seekTo(mSavedCurrentPosition)
                mSavedCurrentPosition = 0
            } else {
                play()
            }
        }
        mVideoView.setOnCompletionListener { mVideoView.keepScreenOn = false }
        showContentView()
    }

    override fun onResume() {
        super.onResume()
        mVideoView.resume()
    }

    override fun onPause() {
        super.onPause()
        mSavedCurrentPosition = mVideoView.currentPosition
        mVideoView.pause()
    }

    override fun onStop() {
        super.onStop()
        pause()
    }

    private fun play() {
        mVideoView.start()
        mVideoView.keepScreenOn = true
    }

    private fun pause() {
        mVideoView.pause()
        mVideoView.keepScreenOn = false
    }
}
