package mindustry.annotations;

import arc.func.*;
import arc.struct.*;
import com.squareup.javapoet.*;
import mindustry.annotations.Annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;

@SuppressWarnings("unchecked")
@SupportedAnnotationTypes({"mindustry.annotations.Annotations.AutoSystem"})
public class EcsAnnotationProcessor extends BaseProcessor{
    @Override
    public void process(RoundEnvironment env) throws Exception{
        Array<TypeElement> types = (Array<TypeElement>)Array.with(env.getElementsAnnotatedWith(AutoSystem.class));
        types.sort(e -> -e.getAnnotation(AutoSystem.class).priority());

        TypeSpec.Builder type = TypeSpec.classBuilder("Systems").addModifiers(Modifier.PUBLIC);
        type.addField(FieldSpec.builder(Prov[].class, "systems", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("{" + types.map(t -> t.asType().toString() + "::new").toString(", ") + "}").build());

        JavaFile.builder(packageName, type.build()).build().writeTo(Utils.filer);
    }
}
