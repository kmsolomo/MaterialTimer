/*
 * Copyright 2018 Kristoffer Solomon
 *
 * This file is part of MaterialTimer.
 *
 * MaterialTimer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialTimer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MaterialTimer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kristoffersol.materialtimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.kristoffersol.materialtimer.databinding.FragmentTimerBinding;
import com.kristoffersol.materialtimer.util.InjectorUtils;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModel;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModelFactory;

public class PomodoroFragment extends Fragment {

    private FragmentTimerBinding mBinding;
    private PomodoroViewModel pomodoroViewModel;
    private AnimatorSet animatorOut,animatorIn;

    private void initAnimations(float initialX) {

        //Move to started position
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(mBinding.playPauseButton,"translationX",initialX-150f);
        slideOut.setDuration(500);
        slideOut.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mBinding.stopButton,"alpha",0,1);
        fadeIn.setDuration(500);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator stopButtonMoveOut = ObjectAnimator.ofFloat(mBinding.stopButton,"translationX",initialX+150f);
        stopButtonMoveOut.setDuration(500);
        stopButtonMoveOut.setInterpolator(new AccelerateDecelerateInterpolator());

        //Move back to original position
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(mBinding.playPauseButton,"translationX",initialX);
        slideIn.setDuration(500);
        slideIn.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator stopButtonFadeOut = ObjectAnimator.ofFloat(mBinding.stopButton,"alpha",1,0);
        stopButtonFadeOut.setDuration(500);
        stopButtonFadeOut.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator stopButtonMoveIn = ObjectAnimator.ofFloat(mBinding.stopButton,"translationX",initialX);
        stopButtonFadeOut.setDuration(500);
        stopButtonFadeOut.setInterpolator(new AccelerateDecelerateInterpolator());

        //Setup animation sequence
        animatorOut = new AnimatorSet();
        animatorOut.addListener(animOutListener);
        animatorOut.playTogether(slideOut,fadeIn,stopButtonMoveOut);

        animatorIn = new AnimatorSet();
        animatorIn.addListener(animInListener);
        animatorIn.playTogether(slideIn,stopButtonFadeOut,stopButtonMoveIn);
    }

    private Animator.AnimatorListener animOutListener = new Animator.AnimatorListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public void onAnimationStart(Animator animator) {
            pomodoroViewModel.setStopButtonVisibility(true);
            pomodoroViewModel.setStopButtonClickable(false);
            pomodoroViewModel.setPlayPauseButtonClickable(false);
            mBinding.playPauseButton.setImageResource(R.drawable.ic_pause_24dp);
        }
        @Override
        public void onAnimationEnd(Animator animator) {
            pomodoroViewModel.setPlayPauseButtonClickable(true);
            pomodoroViewModel.setStopButtonClickable(true);
        }
        @Override
        public void onAnimationCancel(Animator animator) {

        }
        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private Animator.AnimatorListener animInListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            pomodoroViewModel.setPlayPauseButtonClickable(false);
            pomodoroViewModel.setStopButtonClickable(false);
        }
        @SuppressLint("RestrictedApi")
        @Override
        public void onAnimationEnd(Animator animator) {
            pomodoroViewModel.setStopButtonVisibility(false);
            pomodoroViewModel.setPlayPauseButtonClickable(true);
        }
        @Override
        public void onAnimationCancel(Animator animator) {

        }
        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
        setupListeners();
        initAnimations(mBinding.playPauseButton.getTranslationX());
    }

    @Override
    public void onResume(){
        super.onResume();

        if(pomodoroViewModel.getTimerRunning()){
            animatorOut.start();
            pomodoroViewModel.setAnimationState(true);
        }

    }

    private void initViewModel() {
        FragmentActivity parent = getActivity();
        if(parent != null){
            PomodoroViewModelFactory factory = InjectorUtils.providePomodoroViewModelFactory();
            pomodoroViewModel = ViewModelProviders.of(parent, factory).get(PomodoroViewModel.class);
            mBinding.setLifecycleOwner(parent);
            mBinding.setViewModel(pomodoroViewModel);
        }
    }

    private void setupListeners() {
        mBinding.playPauseButton.setOnClickListener(view -> {
            if(pomodoroViewModel.getTimerRunning()){
                Intent pauseTimer = new Intent(getActivity(), TimerReceiver.class);
                pauseTimer.setAction(PomodoroService.ACTION_PAUSE);
                mBinding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);
                if(getActivity() != null){
                    getActivity().sendBroadcast(pauseTimer);
                }
                pomodoroViewModel.setTimerRunning(false);
            } else {
                Intent startTimer = new Intent(getActivity(), TimerReceiver.class);
                startTimer.setAction(PomodoroService.ACTION_START);
                mBinding.playPauseButton.setImageResource(R.drawable.ic_pause_24dp);
                if(getActivity() != null){
                    getActivity().sendBroadcast(startTimer);
                }

                if(!pomodoroViewModel.getAnimationState()){
                    pomodoroViewModel.setAnimationState(true);
                    animatorOut.start();
                }
                pomodoroViewModel.setTimerRunning(true);
            }
        });
        mBinding.stopButton.setOnClickListener(view -> {
            Intent stopTime = new Intent(getActivity(), TimerReceiver.class);
            stopTime.setAction(PomodoroService.ACTION_RESET);
            animatorIn.start();
            pomodoroViewModel.setTimerRunning(false);
            pomodoroViewModel.setAnimationState(false);

            if(getActivity() != null){
                getActivity().sendBroadcast(stopTime);
            }
        });
    }

}
