package ru.alferatz.ftserver.repository.entity;

import com.google.gson.Gson;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import ru.alferatz.ftserver.model.CoordsObject;

@Converter(autoApply = true)
public class MyConverter implements AttributeConverter<CoordsObject, String> {

  private static final Gson GSON = new Gson();

  @Override
  public String convertToDatabaseColumn(CoordsObject coordsObject) {
    return GSON.toJson(coordsObject);
  }

  @Override
  public CoordsObject convertToEntityAttribute(String dbData) {
    return GSON.fromJson(dbData, CoordsObject.class);
  }
}