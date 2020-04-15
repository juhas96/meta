package sk.tuke.mp.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes({"sk.tuke.mp.annotations.Column", "sk.tuke.mp.annotations.Entity","sk.tuke.mp.annotations.Id","javax.persistence.Column", "javax.persistence.Table", "javax.persistence.Entity", "javax.persistence.ManyToOne", "javax.persistence.Transient", "javax.persistence.Id"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {

    private Repository repository;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        repository = new Repository();
        System.out.println("ANNOTATION PROCESSOR INITIALIZED");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Entity.class);
        elements.forEach(it -> {
            try {
                repository.entityPersist(it);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try (Writer writer = new FileWriter("processors/src/main/resources/output.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(repository, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
