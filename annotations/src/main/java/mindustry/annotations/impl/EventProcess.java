package mindustry.annotations.impl;

import mindustry.annotations.*;

import javax.annotation.processing.*;

@SupportedAnnotationTypes("mindustry.annotations.Annotations.Subscribe")
public class EventProcess extends BaseProcessor{

    @Override
    public void process(RoundEnvironment env, int round) throws Exception{

    }
}
