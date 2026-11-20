package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural 16-bit PCM audio synthesizer for tactile wooden dice knocks,
 * marimba footsteps, bamboo/vine climbs, serpent hisses, and victory drums.
 */
class JungleSoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val sampleRate = 22050

    @Volatile
    var isEnabled: Boolean = true

    private val pentatonicHz = doubleArrayOf(523.25, 587.33, 659.25, 783.99, 880.00, 1046.50)

    fun playDiceRoll() {
        if (!isEnabled) return
        scope.launch {
            val samples = generateDiceRattleSamples()
            playPcmBuffer(samples)
        }
    }

    fun playDiceLand() {
        if (!isEnabled) return
        scope.launch {
            val samples = generateWoodKnockSamples(freq = 165.0, durationMs = 110, gain = 0.7f)
            playPcmBuffer(samples)
        }
    }

    fun playFootstep(stepIndex: Int) {
        if (!isEnabled) return
        scope.launch {
            val freq = pentatonicHz[stepIndex % pentatonicHz.size]
            val samples = generateMarimbaNote(freq = freq, durationMs = 115, gain = 0.45f)
            playPcmBuffer(samples)
        }
    }

    fun playLadderClimb() {
        if (!isEnabled) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
            val stepSamples = (sampleRate * 0.085).toInt()
            val tailSamples = (sampleRate * 0.25).toInt()
            val total = stepSamples * notes.size + tailSamples
            val mix = FloatArray(total)
            notes.forEachIndexed { idx, freq ->
                val noteBuf = generateMarimbaNote(freq, durationMs = 220, gain = 0.42f)
                val offset = idx * stepSamples
                for (i in noteBuf.indices) {
                    if (offset + i < mix.size) {
                        mix[offset + i] += noteBuf[i] / 32767f
                    }
                }
            }
            playPcmBuffer(floatToPcm16(mix))
        }
    }

    fun playSnakeSlide() {
        if (!isEnabled) return
        scope.launch {
            val durationSec = 0.62
            val count = (sampleRate * durationSec).toInt()
            val pcm = ShortArray(count)
            var phase = 0.0
            var noiseState = 0.0
            for (i in 0 until count) {
                val t = i.toDouble() / count
                // Descending slide whistle + filtered sibilant hiss
                val freq = 720.0 * exp(-1.55 * t) + 140.0
                phase += 2.0 * PI * freq / sampleRate
                val env = sin(PI * t).coerceAtLeast(0.0)
                val white = Random.nextDouble(-1.0, 1.0)
                noiseState = noiseState * 0.68 + white * 0.32
                val hiss = (white - noiseState) * 0.42 * (1.0 - t * 0.35)
                val slideTone = sin(phase) * 0.32 * env
                val sample = ((hiss + slideTone) * 0.65).coerceIn(-0.98, 0.98)
                pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcmBuffer(pcm)
        }
    }

    fun playBonusRoll() {
        if (!isEnabled) return
        scope.launch {
            val notes = doubleArrayOf(783.99, 1046.50)
            val stepSamples = (sampleRate * 0.09).toInt()
            val total = stepSamples * 2 + (sampleRate * 0.28).toInt()
            val mix = FloatArray(total)
            notes.forEachIndexed { idx, freq ->
                val noteBuf = generateMarimbaNote(freq, durationMs = 240, gain = 0.48f)
                val offset = idx * stepSamples
                for (i in noteBuf.indices) {
                    if (offset + i < mix.size) {
                        mix[offset + i] += noteBuf[i] / 32767f
                    }
                }
            }
            playPcmBuffer(floatToPcm16(mix))
        }
    }

    fun playVictoryDrums() {
        if (!isEnabled) return
        scope.launch {
            val durationSec = 1.25
            val total = (sampleRate * durationSec).toInt()
            val mix = FloatArray(total)
            val drumOffsets = listOf(0.0, 0.16, 0.32, 0.56, 0.72)
            drumOffsets.forEachIndexed { index, sec ->
                val start = (sec * sampleRate).toInt()
                val baseFreq = if (index % 2 == 0) 95.0 else 130.0
                val drumLen = (sampleRate * 0.22).toInt()
                for (i in 0 until drumLen) {
                    if (start + i >= total) break
                    val t = i.toDouble() / sampleRate
                    val pitchSweep = baseFreq * (1.0 + 1.4 * exp(-t * 28.0))
                    val env = exp(-t * 14.0)
                    val tone = sin(2.0 * PI * pitchSweep * t) * env * 0.55
                    mix[start + i] += tone.toFloat()
                }
            }
            // Celebratory golden fanfare chord at 0.56s
            val chord = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val chordStart = (0.56 * sampleRate).toInt()
            chord.forEach { freq ->
                val buf = generateMarimbaNote(freq, durationMs = 520, gain = 0.28f)
                for (i in buf.indices) {
                    if (chordStart + i < total) {
                        mix[chordStart + i] += buf[i] / 32767f
                    }
                }
            }
            playPcmBuffer(floatToPcm16(mix))
        }
    }

    private fun generateDiceRattleSamples(): ShortArray {
        val total = (sampleRate * 0.42).toInt()
        val mix = FloatArray(total)
        val taps = listOf(0.0 to 360.0, 0.08 to 430.0, 0.17 to 320.0, 0.27 to 490.0)
        taps.forEach { (sec, freq) ->
            val knock = generateWoodKnockSamples(freq = freq, durationMs = 65, gain = 0.48f)
            val offset = (sec * sampleRate).toInt()
            for (i in knock.indices) {
                if (offset + i < total) {
                    mix[offset + i] += knock[i] / 32767f
                }
            }
        }
        return floatToPcm16(mix)
    }

    private fun generateWoodKnockSamples(freq: Double, durationMs: Int, gain: Float): ShortArray {
        val count = (sampleRate * durationMs / 1000.0).toInt()
        val out = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 48.0)
            val body = sin(2.0 * PI * freq * t) + 0.45 * sin(2.0 * PI * (freq * 2.3) * t)
            val click = if (i < 120) Random.nextDouble(-0.4, 0.4) * exp(-t * 180.0) else 0.0
            val sample = ((body * env + click) * gain * 0.65).coerceIn(-0.98, 0.98)
            out[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    private fun generateMarimbaNote(freq: Double, durationMs: Int, gain: Float): ShortArray {
        val count = (sampleRate * durationMs / 1000.0).toInt()
        val out = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 16.0)
            val attack = (i / (sampleRate * 0.004)).coerceAtMost(1.0)
            val wave = sin(2.0 * PI * freq * t) + 0.25 * sin(2.0 * PI * (freq * 4.0) * t) * exp(-t * 36.0)
            val sample = (wave * env * attack * gain).coerceIn(-0.98, 0.98)
            out[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    private fun floatToPcm16(mix: FloatArray): ShortArray {
        val out = ShortArray(mix.size)
        for (i in mix.indices) {
            out[i] = (mix[i].coerceIn(-0.98f, 0.98f) * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    private fun playPcmBuffer(samples: ShortArray) {
        if (samples.isEmpty()) return
        var track: AudioTrack? = null
        try {
            val byteCount = samples.size * 2
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val format = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
            track = AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setAudioFormat(format)
                .setBufferSizeInBytes(byteCount)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            if (track.state != AudioTrack.STATE_INITIALIZED) {
                track.release()
                return
            }
            track.write(samples, 0, samples.size)
            track.play()
            val waitMs = (samples.size * 1000L / sampleRate) + 40L
            Thread.sleep(waitMs)
        } catch (_: Throwable) {
            // Gracefully ignore audio errors on headless/muted environments
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Throwable) {
            }
        }
    }

    fun release() {
        scope.cancel()
    }
}
