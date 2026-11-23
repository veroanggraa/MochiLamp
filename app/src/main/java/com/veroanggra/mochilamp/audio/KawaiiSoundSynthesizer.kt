package com.veroanggra.mochilamp.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

/**
 * Synthesizer audio prosedural murni tanpa dependensi file eksternal (1:1 Web Audio API).
 * Menghasilkan instrumen kalimba/marimba lembut dan tactile switch pop.
 */
class KawaiiSoundSynthesizer {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    private fun playTone(freq: Double, durationSec: Double, volume: Float, popFreq: Double = 0.0) {
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 8.0)
            val toneAngle = 2.0 * Math.PI * freq * t
            var sample = sin(toneAngle) * decay * volume

            if (popFreq > 0.0 && t < 0.035) {
                val popDecay = exp(-t * 40.0)
                val currentPopFreq = popFreq * (1.0 - (t / 0.035) * 0.5)
                val popAngle = 2.0 * Math.PI * currentPopFreq * t
                sample += sin(popAngle) * popDecay * 0.25
            }

            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            audioTrack.notificationMarkerPosition = buffer.size
            audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(track: AudioTrack) { track.release() }
                override fun onPeriodicNotification(track: AudioTrack) {}
            })
        } catch (_: Exception) {}
    }

    fun playSwitchSound(isTurningOn: Boolean) {
        scope.launch {
            if (isTurningOn) {
                playTone(523.25, 0.35, 0.28f, popFreq = 520.0)
                delay(60)
                playTone(659.25, 0.38, 0.26f)
                delay(60)
                playTone(783.99, 0.45, 0.24f)
            } else {
                playTone(392.00, 0.32, 0.26f, popFreq = 380.0)
                delay(80)
                playTone(293.66, 0.42, 0.24f)
            }
        }
    }

    fun playBlushSound() {
        scope.launch {
            playTone(587.33, 0.22, 0.22f)
            delay(50)
            playTone(880.00, 0.28, 0.20f)
        }
    }

    fun playTintPop() {
        scope.launch {
            playTone(659.25, 0.18, 0.18f)
        }
    }
}
