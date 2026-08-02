package com.uibuilder.app.util

import com.uibuilder.app.domain.model.AnimationConfig
import com.uibuilder.app.domain.model.AnimationInterpolator
import com.uibuilder.app.domain.model.AnimationType
import com.uibuilder.app.domain.model.RepeatMode
import com.uibuilder.app.domain.model.SequenceMode
import com.uibuilder.app.domain.model.UiComponent

class JavaScriptGenerator {

    fun generate(
        pageName: String,
        components: List<UiComponent>
    ): String {
        val sb = StringBuilder()
        sb.append("'use strict';\n\n")
        sb.append("document.addEventListener('DOMContentLoaded', function () {\n")
        sb.append("    initInteractions();\n")
        sb.append("    initAnimations();\n")
        sb.append("    initFormHandlers();\n")
        sb.append("});\n\n")

        sb.append("function initInteractions() {\n")
        val interactive = components.filter { it.type in INTERACTIVE_TYPES }
        if (interactive.isEmpty()) {
            sb.append("    // No interactive components found\n")
        } else {
            for (component in interactive) {
                val elementId = component.properties.elementId.ifBlank { defaultIdFor(component) }
                sb.append("    var $elementId = document.getElementById('$elementId');\n")
                sb.append("    if ($elementId) {\n")
                sb.append("        $elementId.addEventListener('click', function () {\n")
                sb.append("            console.log('$elementId clicked');\n")
                sb.append("        });\n")
                sb.append("    }\n")
            }
        }
        sb.append("}\n\n")

        sb.append("function initAnimations() {\n")
        val withAnimations = components.filter { it.animations.isNotEmpty() }
        if (withAnimations.isEmpty()) {
            sb.append("    // No animations configured\n")
        } else {
            for (component in withAnimations) {
                val elementId = component.properties.elementId.ifBlank { defaultIdFor(component) }
                sb.append("    var ${elementId}El = document.getElementById('$elementId');\n")
                sb.append("    if (${elementId}El) {\n")
                sb.append(generateAnimationsForComponent(component, elementId))
                sb.append("    }\n")
            }
        }
        sb.append("}\n\n")

        sb.append("function initFormHandlers() {\n")
        val forms = components.filter { it.type == com.uibuilder.app.domain.model.ComponentType.FORM }
        for (form in forms) {
            val formId = form.properties.elementId.ifBlank { defaultIdFor(form) }
            sb.append("    var $formId = document.getElementById('$formId');\n")
            sb.append("    if ($formId) {\n")
            sb.append("        $formId.addEventListener('submit', function (e) {\n")
            sb.append("            e.preventDefault();\n")
            sb.append("            console.log('Form $formId submitted');\n")
            sb.append("        });\n")
            sb.append("    }\n")
        }
        sb.append("}\n\n")

        sb.append(generateUtilityFunctions())
        return sb.toString()
    }

    private fun generateAnimationsForComponent(
        component: UiComponent,
        elementId: String
    ): String {
        val sb = StringBuilder()
        val animations = component.animations.sortedBy { it.sequenceOrder }
        val sequenceMode = animations.firstOrNull()?.sequenceMode ?: SequenceMode.SEQUENTIAL

        if (animations.size == 1) {
            sb.append(generateSingleAnimation(animations.first(), elementId, "        "))
        } else {
            sb.append("        var ${elementId}Animations = [];\n")
            for (anim in animations) {
                sb.append(generateAnimationEntry(anim, elementId))
            }
            sb.append("        var ${elementId}Sequence = { animations: ${elementId}Animations, mode: '${sequenceMode.name.lowercase()}' };\n")
            sb.append("        playAnimationSequence(${elementId}El, ${elementId}Sequence);\n")
        }
        return sb.toString()
    }

    private fun generateSingleAnimation(
        anim: AnimationConfig,
        elementId: String,
        indent: String
    ): String {
        val sb = StringBuilder()
        val easing = easingFor(anim.interpolator)
        sb.append("${indent}${elementId}El.style.transition = 'all ' + ${anim.durationMs} + 'ms $easing';\n")
        sb.append("${indent}setTimeout(function () {\n")
        when (anim.type) {
            AnimationType.FADE_IN -> {
                sb.append("${indent}    ${elementId}El.style.opacity = '1';\n")
            }
            AnimationType.FADE_OUT -> {
                sb.append("${indent}    ${elementId}El.style.opacity = '0';\n")
            }
            AnimationType.SLIDE_UP -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'translateY(0)';\n")
            }
            AnimationType.SLIDE_DOWN -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'translateY(0)';\n")
            }
            AnimationType.SLIDE_LEFT -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'translateX(0)';\n")
            }
            AnimationType.SLIDE_RIGHT -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'translateX(0)';\n")
            }
            AnimationType.BOUNCE -> {
                sb.append("${indent}    ${elementId}El.style.animation = 'bounce ${anim.durationMs}ms $easing';\n")
            }
            AnimationType.PULSE -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'scale(1.1)';\n")
            }
            AnimationType.SHAKE -> {
                sb.append("${indent}    ${elementId}El.style.animation = 'shake ${anim.durationMs}ms $easing';\n")
            }
            AnimationType.ROTATE -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'rotate(360deg)';\n")
            }
            AnimationType.ZOOM_IN -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'scale(1)';\n")
            }
            AnimationType.ZOOM_OUT -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'scale(0.5)';\n")
            }
            AnimationType.FLIP_X -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'rotateX(0deg)';\n")
            }
            AnimationType.FLIP_Y -> {
                sb.append("${indent}    ${elementId}El.style.transform = 'rotateY(0deg)';\n")
            }
        }
        sb.append("${indent}}, ${anim.delayMs});\n")
        return sb.toString()
    }

    private fun generateAnimationEntry(
        anim: AnimationConfig,
        elementId: String
    ): String {
        val easing = easingFor(anim.interpolator)
        val prop = when (anim.type) {
            AnimationType.FADE_IN, AnimationType.FADE_OUT -> "opacity"
            AnimationType.SLIDE_UP, AnimationType.SLIDE_DOWN, AnimationType.BOUNCE -> "transform"
            AnimationType.SLIDE_LEFT, AnimationType.SLIDE_RIGHT, AnimationType.SHAKE -> "transform"
            AnimationType.ROTATE -> "transform"
            AnimationType.PULSE, AnimationType.ZOOM_IN, AnimationType.ZOOM_OUT -> "transform"
            AnimationType.FLIP_X, AnimationType.FLIP_Y -> "transform"
        }
        val targetValue = when (anim.type) {
            AnimationType.FADE_IN -> "1"
            AnimationType.FADE_OUT -> "0"
            AnimationType.SLIDE_UP, AnimationType.SLIDE_DOWN -> "translateY(0)"
            AnimationType.SLIDE_LEFT, AnimationType.SLIDE_RIGHT -> "translateX(0)"
            AnimationType.BOUNCE -> "translateY(0)"
            AnimationType.SHAKE -> "translateX(0)"
            AnimationType.ROTATE -> "rotate(360deg)"
            AnimationType.PULSE, AnimationType.ZOOM_IN -> "scale(1.1)"
            AnimationType.ZOOM_OUT -> "scale(0.5)"
            AnimationType.FLIP_X -> "rotateX(0deg)"
            AnimationType.FLIP_Y -> "rotateY(0deg)"
        }
        return "        ${elementId}Animations.push({ property: '$prop', value: '$targetValue', duration: ${anim.durationMs}, delay: ${anim.delayMs}, easing: '$easing' });\n"
    }

    private fun generateUtilityFunctions(): String {
        val sb = StringBuilder()
        sb.append("function playAnimationSequence(element, sequence) {\n")
        sb.append("    var animations = sequence.animations.slice();\n")
        sb.append("    if (sequence.mode === 'parallel') {\n")
        sb.append("        animations.forEach(function (anim) {\n")
        sb.append("            applyAnimation(element, anim);\n")
        sb.append("        });\n")
        sb.append("    } else {\n")
        sb.append("        playSequentially(element, animations, 0);\n")
        sb.append("    }\n")
        sb.append("}\n\n")

        sb.append("function playSequentially(element, animations, index) {\n")
        sb.append("    if (index >= animations.length) return;\n")
        sb.append("    applyAnimation(element, animations[index]);\n")
        sb.append("    setTimeout(function () {\n")
        sb.append("        playSequentially(element, animations, index + 1);\n")
        sb.append("    }, animations[index].duration + animations[index].delay);\n")
        sb.append("}\n\n")

        sb.append("function applyAnimation(element, anim) {\n")
        sb.append("    element.style.transition = anim.property + ' ' + anim.duration + 'ms ' + anim.easing;\n")
        sb.append("    setTimeout(function () {\n")
        sb.append("        element.style[anim.property] = anim.value;\n")
        sb.append("    }, anim.delay);\n")
        sb.append("}\n\n")

        sb.append("function debounce(fn, delay) {\n")
        sb.append("    var timer;\n")
        sb.append("    return function () {\n")
        sb.append("        var context = this, args = arguments;\n")
        sb.append("        clearTimeout(timer);\n")
        sb.append("        timer = setTimeout(function () { fn.apply(context, args); }, delay);\n")
        sb.append("    };\n")
        sb.append("}\n")
        return sb.toString()
    }

    private fun easingFor(interpolator: AnimationInterpolator): String = when (interpolator) {
        AnimationInterpolator.LINEAR -> "linear"
        AnimationInterpolator.ACCELERATE -> "ease-in"
        AnimationInterpolator.DECELERATE -> "ease-out"
        AnimationInterpolator.ACCELERATE_DECELERATE -> "ease-in-out"
        AnimationInterpolator.BOUNCE -> "cubic-bezier(0.68, -0.55, 0.265, 1.55)"
        AnimationInterpolator.OVERSHOOT -> "cubic-bezier(0.175, 0.885, 0.32, 1.275)"
        AnimationInterpolator.ANTICIPATE -> "cubic-bezier(0.36, 0, 0.66, -0.56)"
        AnimationInterpolator.ANTICIPATE_OVERSHOOT -> "cubic-bezier(0.68, -0.55, 0.265, 1.55)"
    }

    private fun defaultIdFor(component: UiComponent): String {
        val base = component.type.name.lowercase().replace("_", "-")
        return "$base-${component.id.take(6)}"
    }

    companion object {
        private val INTERACTIVE_TYPES = setOf(
            com.uibuilder.app.domain.model.ComponentType.BUTTON,
            com.uibuilder.app.domain.model.ComponentType.LINK,
            com.uibuilder.app.domain.model.ComponentType.CHECKBOX,
            com.uibuilder.app.domain.model.ComponentType.RADIO,
            com.uibuilder.app.domain.model.ComponentType.TOGGLE,
            com.uibuilder.app.domain.model.ComponentType.IMAGE,
            com.uibuilder.app.domain.model.ComponentType.SELECT
        )
    }
}
