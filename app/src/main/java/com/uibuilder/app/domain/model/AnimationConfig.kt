package com.uibuilder.app.domain.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AnimationType(val displayName: String) : Parcelable {
    FADE_IN("Fade In"),
    FADE_OUT("Fade Out"),
    SLIDE_UP("Slide Up"),
    SLIDE_DOWN("Slide Down"),
    SLIDE_LEFT("Slide Left"),
    SLIDE_RIGHT("Slide Right"),
    BOUNCE("Bounce"),
    PULSE("Pulse"),
    SHAKE("Shake"),
    ROTATE("Rotate"),
    ZOOM_IN("Zoom In"),
    ZOOM_OUT("Zoom Out"),
    FLIP_X("Flip X"),
    FLIP_Y("Flip Y");
}

@Parcelize
enum class AnimationInterpolator(val displayName: String) : Parcelable {
    LINEAR("Linear"),
    ACCELERATE("Accelerate"),
    DECELERATE("Decelerate"),
    ACCELERATE_DECELERATE("AccelerateDecelerate"),
    BOUNCE("Bounce"),
    OVERSHOOT("Overshoot"),
    ANTICIPATE("Anticipate"),
    ANTICIPATE_OVERSHOOT("AnticipateOvershoot")
}

@Parcelize
enum class RepeatMode(val intValue: Int) : Parcelable {
    RESTART(1),
    REVERSE(2)
}

@Parcelize
enum class SequenceMode : Parcelable {
    SEQUENTIAL,
    PARALLEL
}

@Parcelize
@JsonClass(generateAdapter = true)
data class AnimationConfig(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: AnimationType = AnimationType.FADE_IN,
    val durationMs: Int = 500,
    val delayMs: Int = 0,
    val interpolator: AnimationInterpolator = AnimationInterpolator.ACCELERATE_DECELERATE,
    val repeatCount: Int = 0,
    val repeatMode: RepeatMode = RepeatMode.RESTART,
    val sequenceOrder: Int = 0,
    val sequenceMode: SequenceMode = SequenceMode.SEQUENTIAL,
    val keyframes: List<Keyframe> = emptyList(),
    val springConfig: SpringConfig? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Keyframe(
    val fraction: Float,
    val alpha: Float? = null,
    val translationX: Float? = null,
    val translationY: Float? = null,
    val scaleX: Float? = null,
    val scaleY: Float? = null,
    val rotation: Float? = null
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class SpringConfig(
    val dampingRatio: Float = 0.5f,
    val stiffness: Float = 200f,
    val initialVelocity: Float = 0f
) : Parcelable
