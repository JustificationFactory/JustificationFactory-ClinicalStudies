package fr.axonic.avek.model.base;

import fr.axonic.avek.model.base.engine.AVarProperty;
import fr.axonic.avek.model.base.engine.DiscretAVar;
import fr.axonic.avek.model.verification.Verify;
import fr.axonic.avek.model.verification.exception.VerificationException;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by cduffau on 11/07/16.
 */
@XmlRootElement
public class ARangedEnum<T extends Enum<T>> extends AEnum<T> implements DiscretAVar<T> {
    List<T> range;

    public ARangedEnum(T value) {
        super(value);
    }

    public ARangedEnum() {
    }

    @Override
    public List<T> getRange() {
        return range;
    }

    @Override
    public void setRange(List<T> range) throws VerificationException {
        setProperty(AVarProperty.RANGE.name(), range);
    }
    @Override
    public ARangedEnum clone() throws CloneNotSupportedException {
        ARangedEnum result = (ARangedEnum) super.clone();

        result.range = range;


        return result;
    }

    @Override
    protected boolean isPropertyVerifiable(String propertyName) {

        boolean result;
        switch (AVarProperty.valueOf(propertyName)) {
            case RANGE: {
                result = true;
            }
            break;
            default: {
                result = super.isPropertyVerifiable(propertyName);
            }
        }
        return result;
    }

    @Override
    protected void setPropertyValue(String propertyName, Object newPropertyValue) {
        switch (AVarProperty.valueOf(propertyName)) {
            case RANGE: {
                range = (List<T>) newPropertyValue;
            }
            break;
            default: {
                super.setPropertyValue(propertyName, newPropertyValue);
            }
        }
    }

    @Override
    protected  Object getPropertyValue(String propertyName){
        Object result;
        switch (AVarProperty.valueOf(propertyName)) {
            case RANGE: {
                result = range;
            }
            break;
            default: {
                result = super.getPropertyValue(propertyName);
            }

        }
        return result;
    }

    @Override
    @Verify
    public void verify(boolean verifyConsistency) throws VerificationException {
        // DO NOTHING
        // There are no constraints to be verified in AVar.
    }
}
