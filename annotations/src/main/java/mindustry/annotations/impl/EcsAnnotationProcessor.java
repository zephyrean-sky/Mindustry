package mindustry.annotations.impl;

import arc.func.*;
import arc.struct.*;
import com.squareup.javapoet.*;
import mindustry.annotations.*;
import mindustry.annotations.Annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({"mindustry.annotations.Annotations.AutoSystem"})
public class EcsAnnotationProcessor extends BaseProcessor{
    {
        rounds = 2;
    }

    @Override
    public void process(RoundEnvironment env, int round) throws Exception{
        if(round == 0){
            Array<ExecutableElement> types = (Array<ExecutableElement>)Array.with(env.getElementsAnnotatedWith(AutoSystem.class)).select(e -> e instanceof ExecutableElement);

        }else{
            Array<TypeElement> types = (Array<TypeElement>)Array.with(env.getElementsAnnotatedWith(AutoSystem.class)).select(e -> e instanceof TypeElement);
            types.sort(e -> -e.getAnnotation(AutoSystem.class).priority());

            TypeSpec.Builder type = TypeSpec.classBuilder("AutoSystems").addModifiers(Modifier.PUBLIC);
            type.addField(FieldSpec.builder(Prov[].class, "systems", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("{" + types.map(t -> t.asType().toString() + "::new").toString(", ") + "}").build());

            JavaFile.builder(packageName, type.build()).build().writeTo(filer);
        }
    }
}
