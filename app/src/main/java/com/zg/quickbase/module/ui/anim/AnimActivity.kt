package com.zg.quickbase.module.ui.anim

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityAnimBinding
import com.zg.quickbase.databinding.ActivityHttpBinding

/**
 * 展示两个网络请求demo
 */
class AnimActivity : BaseActivity() {

    lateinit var binding: ActivityAnimBinding
    override fun getRoot(): View {

        binding = ActivityAnimBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.root.setOnClickListener {
            showAnimal()
        }

        binding.ivStandCover.alpha = 0f
        binding.ivStand.alpha = 0f
        binding.ivStandMove.post {
            if (startTx <= 0f) {
                startTx = binding.ivStandMove.translationX
                startTy = binding.ivStandMove.translationY
                showAnimal()
            }
        }

    }

    var startTx = 0f
    var startTy = 0f
    var set: AnimatorSet? = null
    private fun showAnimal() {

        if (set == null) {
            val alphaStandCover = ObjectAnimator.ofFloat(binding.ivStandCover, "alpha", 0f, 0f, 1f)
            val alphaStand = ObjectAnimator.ofFloat(binding.ivStand, "alpha", 0f, 0.5f, 1f)


            val translationMoveX = ObjectAnimator.ofFloat(
                binding.ivStandMove,
                "translationX",
                startTx, 0f,
                0f,
            )
            val translationMoveY = ObjectAnimator.ofFloat(
                binding.ivStandMove,
                "translationY",
                startTy, 0f,
                0f
            )


            set = AnimatorSet()
            set?.run {
                playTogether(alphaStandCover, alphaStand, translationMoveX, translationMoveY)
                duration = 2000
                interpolator = AccelerateInterpolator()
                addListener(object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        // 间隔一秒让餐盘ui显示清楚了再继续循环
                        binding.root.postDelayed({
                            showAnimal()
                        }, 1000)

                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }

                })

            }

        }
        set?.start()


    }

    override fun initViewModel() {

    }

}