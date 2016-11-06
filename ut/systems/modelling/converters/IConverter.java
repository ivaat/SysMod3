package ut.systems.modelling.converters;

/**
 * Created by taavii on 5.11.2016.
 */
public interface IConverter<SourceType, TargetType> {
    TargetType convert(SourceType sourceType) throws Exception;
}
