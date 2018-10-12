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

import android.os.Bundle;
import android.os.IBinder;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.kristoffersol.materialtimer.databinding.FragmentTimerBinding;
import com.kristoffersol.materialtimer.util.InjectorUtils;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModel;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModelFactory;


public class PomodoroFragment extends Fragment {

    private FragmentTimerBinding mBinding;
    private PomodoroViewModel pomodoroViewModel;
    private AnimatorSet animatorOut,animatorIn;
    private PomodoroListener pomodoroListener;

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
            mBinding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);
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

    private ServiceConnection pomConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public interface PomodoroListener{
        void connectService(ServiceConnection serviceConnection);
        void disconnectService(ServiceConnection connection);
        void publishAction(String action);
    }

    public static PomodoroFragment getInstance(){
        return new PomodoroFragment();
    }

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
    public void onStart(){
        super.onStart();
        pomodoroListener.connectService(pomConnection);
    }

    @Override
    public void onResume(){
        super.onResume();

        if(pomodoroViewModel.getAnimationState()){
            animatorOut.start();
            if(pomodoroViewModel.getTimerRunning()){
                mBinding.playPauseButton.setImageResource(R.drawable.ic_pause_24dp);
            } else {
                mBinding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        pomodoroListener.disconnectService(pomConnection);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            pomodoroListener = (PomodoroListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + "implement PomodoroListener");
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
        pomodoroViewModel.getStateData().observe(this, state -> {});
        pomodoroViewModel.getSessionData().observe(this, state -> {});
        mBinding.playPauseButton.setOnClickListener(view -> {
            if(pomodoroViewModel.getTimerRunning()){
                pomodoroListener.publishAction(PomodoroService.ACTION_PAUSE);
                mBinding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);

            } else {
                pomodoroListener.publishAction(PomodoroService.ACTION_START);
                mBinding.playPauseButton.setImageResource(R.drawable.ic_pause_24dp);

                if(!pomodoroViewModel.getAnimationState()){
                    pomodoroViewModel.setAnimationState(true);
                    animatorOut.start();
                }
            }
        });

        mBinding.stopButton.setOnClickListener(view -> {
            pomodoroListener.publishAction(PomodoroService.ACTION_RESET);
            pomodoroViewModel.setAnimationState(false);
            animatorIn.start();
        });
    }
}
