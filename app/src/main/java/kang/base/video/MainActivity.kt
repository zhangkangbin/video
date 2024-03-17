package kang.base.video

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        initVideo()

    }
    private var progressBar: SeekBar? = null
    private lateinit var player :ExoPlayer
    var handler: Handler = Handler()
    var updateProgressBarRunnable: Runnable = object : Runnable {
        override fun run() {
            if (player.playbackState == Player.STATE_READY) {
                val duration = player.duration
                val currentPosition = player.currentPosition.toInt()
                val bufferedPosition = player.bufferedPosition.toInt()
                "duration $duration".log()
                "currentPosition $currentPosition".log()
                "bufferedPosition $bufferedPosition".log()
                progressBar?.max = player.duration.toInt()
                progressBar!!.progress = currentPosition
                progressBar!!.secondaryProgress = bufferedPosition

                convertMillisecondsToTimeFormat(player.duration)
            }
            handler.postDelayed(this, 1000) // 每隔一秒执行一次
        }
    }
    @OptIn(UnstableApi::class) private fun initVideo(){
        val map= mutableMapOf<String,String>()
        map["Referer"]="https://www.lokshorts.com/"
        val data= DefaultHttpDataSource.Factory()
        data.setDefaultRequestProperties(map)

        val factory= DefaultMediaSourceFactory(this)
        factory.setDataSourceFactory(data)



        val playerView=findViewById<PlayerView>(R.id.player_view)

        // 初始化进度条
        progressBar = findViewById(R.id.seekBar);
        // 设置进度条的最大值
        progressBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                "onStopTrackingTouch ${seekBar?.progress}".log()
            }
        })

        player= ExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(factory)
            .build()

        playerView.player=player
        val url= MediaItem.fromUri("https://video.lokshorts.com/video/rework/en/255971972d31a" +
                "a1d828bf12866664509_00001/9e3176cc84be1d5674d8c0293244d9a9_001/1.m3u8")

        player.addListener(object :Player.Listener{
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

              //  if(events.contains(STATE_READY))
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                if(playWhenReady){
                    "1===playWhenReady=== ${ player.duration}".log()
                    if( player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)){
                        "2===playWhenReady=== ${ player.duration}".log()
                    //    progressBar?.max = player.duration.toInt()
                    }

                }
            }


        } )
        player.setMediaItem(url)
        player.prepare()

        player.playWhenReady=true


        // 开始更新进度条
        handler.post(updateProgressBarRunnable);
    }
    private fun String.log(){

        Log.d("MainActivity",this)
    }
    fun convertMillisecondsToTimeFormat(milliseconds: Long) {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
      //  val hours = minutes / 60

        val remainingSeconds = seconds % 60

        String.format(
            "%02d:%02d",
            minutes,
            remainingSeconds
        ).log()
    }
}

