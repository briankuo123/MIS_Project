package com.unity3d.player;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Process;

public class MyUnityPlayer extends UnityPlayer{
    public MyUnityPlayer(ContextWrapper contextWrapper){
        super(contextWrapper);
    }

    @Override
    protected void kill(){
    }
}

