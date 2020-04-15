package sk.tuke.mp.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

public class EntityAnalyser {

    private static EntityAnalyser entityAnalyser = new EntityAnalyser();
    private EntityStructure entityStructure = new EntityStructure();

    public EntityAnalyser() {
    }

    public static EntityAnalyser getInstance() {
        return entityAnalyser;
    }

    public EntityStructure analyse(Element element) throws PersistenceException {
        TypeElement typeElement = (TypeElement) element;

        if (element.getAnnotation(Entity.class) != null) {
            if (element.getAnnotation(Table.class) != null) {
                Table tableAnnotation = element.getAnnotation(Table.class);
                entityStructure.setName(tableAnnotation.name());
            } else {
                entityStructure.setName(element.getSimpleName().toString());
            }

            List<? extends Element> fields = typeElement.getEnclosedElements().stream().filter(it -> it.getKind() == ElementKind.FIELD).collect(Collectors.toList());
            List<? extends Element> fieldsWithoutTransient = fields.stream().filter(it -> it.getAnnotation(Transient.class) == null).collect(Collectors.toList());

            fieldsWithoutTransient.forEach(field -> {
                if (field.getAnnotation(ManyToOne.class) != null) {
                    ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                    if (field.getAnnotation(Column.class) != null) {
                        Column columnAnnotation = field.getAnnotation(Column.class);
                        EntityColumn entityColumn = new EntityColumn();
                        entityColumn.setName(columnAnnotation.name());
                        entityColumn.setRealName(field.getSimpleName().toString());
                        entityColumn.setType(field.asType().toString());
                        entityColumn.setLength(columnAnnotation.length());
                        entityColumn.setFetchType(manyToOne.fetch());

                        entityStructure.getForeignKeys().add(entityColumn);

                        EntityColumn entityColumn1 = new EntityColumn();
                        entityColumn1.setName(columnAnnotation.name());
                        entityColumn1.setRealName(field.getSimpleName().toString());
                        entityColumn1.setType(field.asType().toString());
                        entityColumn1.setStore(true);
                        entityColumn1.setLength(255);
                        entityColumn1.setFetchType(manyToOne.fetch());

                        entityColumn.setNullable(!columnAnnotation.nullable());

                        entityStructure.getEntityColumns().add(new EntityColumn());
                    } else {
                        EntityColumn entityColumn = new EntityColumn();
                        entityColumn.setName(field.getSimpleName().toString());
                        entityColumn.setRealName(field.getSimpleName().toString());
                        entityColumn.setType(field.asType().toString());
                        entityColumn.setLength(255);
                        entityColumn.setFetchType(manyToOne.fetch());

                        EntityColumn entityColumn1 = new EntityColumn();
                        entityColumn1.setName(field.getSimpleName().toString());
                        entityColumn1.setRealName(field.getSimpleName().toString());
                        entityColumn1.setType(field.asType().toString());
                        entityColumn1.setStore(true);
                        entityColumn1.setLength(255);
                        entityColumn1.setFetchType(manyToOne.fetch());

                        entityStructure.getForeignKeys().add(entityColumn);
                        entityStructure.getEntityColumns().add(entityColumn1);
                    }
                } else if (field.getAnnotation(Id.class) != null){
                    if (field.getAnnotation(Column.class) != null) {
                        Column columnAnnotation = field.getAnnotation(Column.class);
                        EntityColumn entityColumn = new EntityColumn();
                        entityColumn.setName(columnAnnotation.name());
                        entityColumn.setRealName(field.getSimpleName().toString());
                        entityColumn.setType(field.asType().toString());
                        entityColumn.setLength(columnAnnotation.length());

                        entityStructure.setPrimaryKey(entityColumn);

                        EntityColumn entityColumn1 = new EntityColumn();
                        entityColumn1.setName(columnAnnotation.name());
                        entityColumn1.setRealName(field.getSimpleName().toString());
                        entityColumn1.setType(field.asType().toString());
                        entityColumn1.setStore(false);
                        entityColumn1.setLength(columnAnnotation.length());

                        entityStructure.getEntityColumns().add(entityColumn1);

                        entityColumn.setNullable(!columnAnnotation.nullable());
                    } else {
                        EntityColumn entityColumn = new EntityColumn();
                        entityColumn.setName(field.getSimpleName().toString());
                        entityColumn.setRealName(field.getSimpleName().toString());
                        entityColumn.setType(field.asType().toString());
                        entityColumn.setLength(255);

                        entityStructure.setPrimaryKey(entityColumn);

                        EntityColumn entityColumn1 = new EntityColumn();
                        entityColumn1.setName(field.getSimpleName().toString());
                        entityColumn1.setRealName(field.getSimpleName().toString());
                        entityColumn1.setType(field.asType().toString());
                        entityColumn1.setStore(false);
                        entityColumn1.setLength(255);

                        entityStructure.getEntityColumns().add(entityColumn1);
                    }
                } else {
                    if (field.getAnnotation(Column.class) != null) {
                        Column columnAnnotation = field.getAnnotation(Column.class);
                        EntityColumn entityColumn = new EntityColumn();
                        entityColumn.setName(columnAnnotation.name());
                        entityColumn.setRealName(field.getSimpleName().toString());
                        entityColumn.setType(field.asType().toString());
                        entityColumn.setStore(true);
                        entityColumn.setLength(columnAnnotation.length());

                        entityStructure.getEntityColumns().add(entityColumn);
                        entityColumn.setNullable(!columnAnnotation.nullable());
                    } else {
                        EntityColumn entityColumn = new EntityColumn();
                        entityColumn.setName(field.getSimpleName().toString());
                        entityColumn.setRealName(field.getSimpleName().toString());
                        entityColumn.setType(field.asType().toString());
                        entityColumn.setStore(true);
                        entityColumn.setLength(255);
                        entityStructure.getEntityColumns().add(entityColumn);
                    }
                }
            });
            entityStructure.createSQL();
            if (entityStructure.getForeignKeys().size() > 0) {
                entityStructure.getForeignKeys().forEach(entity -> {
                    entityStructure.referencesSQL(entity.getName());
                });
            }
            return entityStructure;
        } else {
            throw new PersistenceException("Entity can't be persist");
        }
    }
}
